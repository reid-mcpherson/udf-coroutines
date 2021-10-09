package com.coroutines.udf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

public typealias Interactor<T, R> = (upstream: Flow<T>) -> Flow<R>

public interface FlowViewModel<STATE : Any, EVENT : Any, EFFECT : Any> {
    public val initialState: STATE
    public val uiState: Flow<STATE>
    public val uiEffect: Flow<EFFECT>
    public fun processUiEvent(event: EVENT)
}

@ExperimentalCoroutinesApi
public abstract class FlowViewModelImpl<STATE : Any, EVENT : Any, ACTION : Any, RESULT : Any, EFFECT : Any>(
    private val flowContext: CoroutineDispatcher = Dispatchers.Default,
    private val scope: CoroutineScope? = null
) : ViewModel(), FlowViewModel<STATE, EVENT, EFFECT> {

    private val events: MutableSharedFlow<EVENT> by lazy {
        val uiEvents = MutableSharedFlow<EVENT>(1)
        eventToActionInteractor(uiEvents)
            .let(actionToResultInteractor)
            .scan(_uiState.value, ::handleResult)
            .onEach { _uiState.value = it }
            .flowOn(flowContext)
            .launchIn(scope ?: viewModelScope)
        uiEvents
    }


    protected abstract val eventToActionInteractor: Interactor<EVENT, ACTION>
    protected abstract val actionToResultInteractor: Interactor<ACTION, RESULT>

    override val uiState: StateFlow<STATE> by lazy { _uiState.asStateFlow() }
    override val uiEffect: SharedFlow<EFFECT> by lazy { _uiEffect.asSharedFlow() }

    private val _uiState by lazy { MutableStateFlow(initialState) }
    private val _uiEffect by lazy { MutableSharedFlow<EFFECT>() }

    protected abstract suspend fun handleResult(previous: STATE, result: RESULT): STATE

    override fun processUiEvent(event: EVENT) {
        events.tryEmit(event)
    }

    protected suspend fun emitEffect(effect: EFFECT): Unit = _uiEffect.emit(effect)
}