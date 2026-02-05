package com.arch.udf

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan


/**
 * A standard, platform-agnostic implementation of the [CoreFlowFeature] interface, providing the
 * core logic for a Unidirectional Data Flow (UDF) architecture. This class orchestrates the
 * entire data flow cycle: from receiving an [EVENT], converting it to an [ACTION], processing
 * the action to produce a [RESULT], and finally reducing the result into a new [STATE].
 * It also manages one-time side [EFFECT]s.
 *
 * This class is designed to be the engine of a UDF feature, handling the state management and
 * event processing pipeline using Kotlin Coroutines and Flow.
 *
 * The processing pipeline is set up lazily and is launched within the provided [CoroutineScope].
 * Events are processed sequentially to ensure state consistency.
 *
 * @param STATE The type of the state object, representing the UI state.
 * @param EVENT The type of events that can be processed, typically originating from user interactions.
 * @param ACTION The type of actions derived from events, representing business logic intents.
 * @param RESULT The type of results produced by processing actions, representing outcomes of business logic.
 * @param EFFECT The type of side effects for one-time events (e.g., navigation, showing a toast).
 * @param scope The [CoroutineScope] in which the feature's event processing pipeline will run.
 */
public abstract class StandardFlowFeature<STATE : Any, EVENT : Any, ACTION : Any, RESULT : Any, EFFECT : Any>(
    private val scope: CoroutineScope
) : CoreFlowFeature<STATE, EVENT, ACTION, RESULT, EFFECT> {

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

