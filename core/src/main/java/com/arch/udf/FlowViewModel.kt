package com.arch.udf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

public typealias Interactor<T, R> = (upstream: Flow<T>) -> Flow<R>

public interface FlowViewModel<STATE : Any, EVENT : Any, EFFECT : Any> {
    public val initialState: STATE
    public val state: StateFlow<STATE>
    public val effect: Flow<EFFECT>
    public fun processUiEvent(event: EVENT)
}

public abstract class FlowViewModelImpl<STATE : Any, EVENT : Any, ACTION : Any, RESULT : Any, EFFECT : Any>(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val scope: CoroutineScope? = null
) : ViewModel(), FlowViewModel<STATE, EVENT, EFFECT> {

    private val _effect by lazy { MutableSharedFlow<EFFECT>() }
    override val effect: SharedFlow<EFFECT> by lazy { _effect.asSharedFlow() }

    private val events = MutableSharedFlow<EVENT>(replay = 1)

    override val state: StateFlow<STATE> by lazy {
        events
            .let(eventToActionInteractor)
            .let(actionToResultInteractor)
            .scan(initialState, ::handleResult)
            .flowOn(dispatcher)
            .stateIn(scope ?: viewModelScope, SharingStarted.Lazily, initialState)
    }


//    private val events: MutableSharedFlow<EVENT> by lazy {
//        val uiEvents = MutableSharedFlow<EVENT>(1)
//        eventToActionInteractor(uiEvents)
//            .let(actionToResultInteractor)
//            .scan(_uiState.value, ::handleResult)
//            .onEach { _uiState.value = it }
//            .flowOn(flowContext)
//            .launchIn(scope ?: viewModelScope)
//        uiEvents
//    }

    protected abstract val eventToActionInteractor: Interactor<EVENT, ACTION>
    protected abstract val actionToResultInteractor: Interactor<ACTION, RESULT>

    protected abstract suspend fun handleResult(previous: STATE, result: RESULT): STATE

    override fun processUiEvent(event: EVENT) {
        val succeeded = events.tryEmit(event)
        println("$succeeded")
    }

    protected suspend fun emitEffect(effect: EFFECT): Unit = _effect.emit(effect)
}