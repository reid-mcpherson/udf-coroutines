package com.arch.udf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import timber.log.Timber

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

    private val _state by lazy { MutableStateFlow(initialState) }
    override val state: StateFlow<STATE> by lazy { _state.asStateFlow() }

    init {
        Timber.d("init $this")
    }

    private val events: MutableSharedFlow<EVENT> by lazy {
        val events = MutableSharedFlow<EVENT>(1)
        events
            .let(eventToActionInteractor)
            .let(actionToResultInteractor)
            .scan(_state.value, ::handleResult)
            .onEach { newState -> _state.value = newState }
            .flowOn(dispatcher)
            .launchIn(scope ?: viewModelScope)
        events
    }

    protected abstract val eventToActionInteractor: Interactor<EVENT, ACTION>
    protected abstract val actionToResultInteractor: Interactor<ACTION, RESULT>

    protected abstract suspend fun handleResult(previous: STATE, result: RESULT): STATE

    override fun processUiEvent(event: EVENT) {
        events.tryEmit(event)
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("onCleared $this")
    }

    protected suspend fun emitEffect(effect: EFFECT): Unit = _effect.emit(effect)
}