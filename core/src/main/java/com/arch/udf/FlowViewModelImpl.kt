package com.arch.udf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.plus

internal interface FlowViewModelCore<STATE : Any, EVENT : Any, ACTION : Any, RESULT : Any, EFFECT : Any> :
    FlowViewModel<STATE, EVENT, EFFECT> {
    val initialState: STATE
    val eventToActionInteractor: Interactor<EVENT, ACTION>
    val actionToResultInteractor: Interactor<ACTION, RESULT>
    suspend fun handleResult(previous: STATE, result: RESULT): STATE
    suspend fun emit(effect: EFFECT)
}

public abstract class FlowViewModelImpl<STATE : Any, EVENT : Any, ACTION : Any, RESULT : Any, EFFECT : Any>(
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
            .launchIn(scope)
        events
    }

    override fun processUiEvent(event: EVENT) {
        events.tryEmit(event)
    }

    public override suspend fun emit(effect: EFFECT): Unit = _effect.emit(effect)
}

public abstract class FlowViewModelAndroid<STATE : Any, EVENT : Any, ACTION : Any, RESULT : Any, EFFECT : Any>(
    scope: CoroutineScope? = null
) : ViewModel(),
    FlowViewModelCore<STATE, EVENT, ACTION, RESULT, EFFECT> {

    protected val scope: CoroutineScope = scope ?: featureScope

    private val flowViewModelImpl: FlowViewModelImpl<STATE, EVENT, ACTION, RESULT, EFFECT> =
        object : FlowViewModelImpl<STATE, EVENT, ACTION, RESULT, EFFECT>(
            this@FlowViewModelAndroid.scope
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

    private val ViewModel.featureScope: CoroutineScope
        get() = viewModelScope + Dispatchers.Default
}