package com.arch.udf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

//TODO: Better name for this?
internal interface FlowViewModelCore<STATE : Any, EVENT : Any, ACTION : Any, RESULT : Any, EFFECT : Any> :
    FlowViewModel<STATE, EVENT, EFFECT> {
    val initialState: STATE
    val eventToActionInteractor: Interactor<EVENT, ACTION>
    val actionToResultInteractor: Interactor<ACTION, RESULT>
    suspend fun handleResult(previous: STATE, result: RESULT): STATE
    suspend fun emitEffect(effect: EFFECT)
}

public abstract class FlowViewModelImpl<STATE : Any, EVENT : Any, ACTION : Any, RESULT : Any, EFFECT : Any>(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val scope: CoroutineScope
) : FlowViewModelCore<STATE, EVENT, ACTION, RESULT, EFFECT> {

    private val _effect by lazy { MutableSharedFlow<EFFECT>() }
    override val effect: SharedFlow<EFFECT> by lazy { _effect.asSharedFlow() }

    private val _state by lazy { MutableStateFlow(initialState) }
    override val state: StateFlow<STATE> by lazy { _state.asStateFlow() }

    private val events: MutableSharedFlow<EVENT> by lazy {
        val events = MutableSharedFlow<EVENT>(1)
        events
            .let(eventToActionInteractor)
            .let(actionToResultInteractor)
            .scan(_state.value, ::handleResult)
            .onEach { newState -> _state.value = newState }
            .flowOn(dispatcher)
            .launchIn(scope)
        events
    }

    override fun processUiEvent(event: EVENT) {
        events.tryEmit(event)
    }

    public override suspend fun emitEffect(effect: EFFECT): Unit = _effect.emit(effect)
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

    override suspend fun emitEffect(effect: EFFECT): Unit =
        flowViewModelImpl.emitEffect(effect)
}