package com.arch.udf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

internal interface FlowViewModelCore<STATE : Any, EVENT : Any, ACTION : Any, RESULT : Any, EFFECT : Any> :
    FlowViewModel<STATE, EVENT, EFFECT> {
    val initialState: STATE
    val eventToActionInteractor: Interactor<EVENT, ACTION>
    val actionToResultInteractor: Interactor<ACTION, RESULT>
    suspend fun handleResult(previous: STATE, result: RESULT): STATE
    suspend fun emit(effect: EFFECT)
}

public abstract class FlowViewModelImpl<STATE : Any, EVENT : Any, ACTION : Any, RESULT : Any, EFFECT : Any>(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val scope: CoroutineScope
) : FlowViewModelCore<STATE, EVENT, ACTION, RESULT, EFFECT> {

    private val _effect: MutableSharedFlow<EFFECT> = MutableSharedFlow()
    override val effect: SharedFlow<EFFECT> = _effect.asSharedFlow()

    private val events: MutableSharedFlow<EVENT> = MutableSharedFlow(1)

    override val state: StateFlow<STATE> by lazy {
        events
            .let(eventToActionInteractor)
            .let(actionToResultInteractor)
            .scan(initialState, ::handleResult)
            .flowOn(dispatcher)
            .stateIn(scope, SharingStarted.Eagerly, initialState)
    }

    override fun processUiEvent(event: EVENT) {
        events.tryEmit(event)
    }

    public override suspend fun emit(effect: EFFECT): Unit = _effect.emit(effect)
}

public abstract class FlowViewModelAndroid<STATE : Any, EVENT : Any, ACTION : Any, RESULT : Any, EFFECT : Any>(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
    _scope: CoroutineScope? = null
) : ViewModel(),
    FlowViewModelCore<STATE, EVENT, ACTION, RESULT, EFFECT> {

    protected val scope: CoroutineScope = _scope ?: viewModelScope

    private val flowViewModelImpl: FlowViewModelImpl<STATE, EVENT, ACTION, RESULT, EFFECT> =
        object : FlowViewModelImpl<STATE, EVENT, ACTION, RESULT, EFFECT>(
            dispatcher,
            scope
        ) {
            override val initialState: STATE
                get() = this@FlowViewModelAndroid.initialState
            override val eventToActionInteractor: Interactor<EVENT, ACTION>
                get() = this@FlowViewModelAndroid.eventToActionInteractor
            override val actionToResultInteractor: Interactor<ACTION, RESULT>
                get() = this@FlowViewModelAndroid.actionToResultInteractor

            override suspend fun handleResult(previous: STATE, result: RESULT): STATE =
                this@FlowViewModelAndroid.handleResult(previous, result)
        }

    override val state: StateFlow<STATE>
        get() = flowViewModelImpl.state

    override val effect: Flow<EFFECT>
        get() = flowViewModelImpl.effect

    override fun processUiEvent(event: EVENT): Unit =
        flowViewModelImpl.processUiEvent(event)

    override suspend fun emit(effect: EFFECT): Unit =
        flowViewModelImpl.emit(effect)
}