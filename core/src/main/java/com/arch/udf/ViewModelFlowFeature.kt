package com.arch.udf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.plus

/**
 * An Android-specific implementation of a UDF feature that is integrated with the AndroidX `ViewModel`.
 * This class provides a convenient way to create a feature whose lifecycle is automatically managed
 * by the `viewModelScope`.
 *
 * It delegates the core UDF logic to an instance of [StandardFlowFeature], effectively acting as a
 * bridge between the Android ViewModel architecture and the platform-agnostic UDF implementation.
 * Subclasses must provide the concrete definitions for the UDF components (`initialState`, interactors,
 * and the result handler).
 *
 * The feature's coroutine scope defaults to `viewModelScope + Dispatchers.Default`, ensuring that
 * all background processing is cancelled when the `ViewModel` is cleared and that computations
 * run on a background thread by default.
 *
 * @param STATE The type of the state object, representing the UI state.
 * @param EVENT The type of events from the UI (e.g., button clicks).
 * @param ACTION The type of actions derived from events, representing business logic intents.
 * @param RESULT The type of results produced by processing actions.
 * @param EFFECT The type of side effects for one-time events (e.g., navigation, toasts).
 * @param scope An optional [CoroutineScope] to run the feature in. If not provided, it defaults to
 *              the `viewModelScope` combined with `Dispatchers.Default`.
 */
public abstract class ViewModelFlowFeature<STATE : Any, EVENT : Any, ACTION : Any, RESULT : Any, EFFECT : Any>(
    scope: CoroutineScope? = null
) : ViewModel(),
    CoreFlowFeature<STATE, EVENT, ACTION, RESULT, EFFECT> {

    protected val scope: CoroutineScope = scope ?: featureScope

    private val standardFlowFeature: StandardFlowFeature<STATE, EVENT, ACTION, RESULT, EFFECT> =
        object : StandardFlowFeature<STATE, EVENT, ACTION, RESULT, EFFECT>(
            this@ViewModelFlowFeature.scope
        ) {
            override val initialState: STATE
                get() = this@ViewModelFlowFeature.initialState
            override val eventToActionInteractor: Interactor<EVENT, ACTION>
                get() = this@ViewModelFlowFeature.eventToActionInteractor
            override val actionToResultInteractor: Interactor<ACTION, RESULT>
                get() = this@ViewModelFlowFeature.actionToResultInteractor

            override suspend fun handleResult(previous: STATE, result: RESULT): STATE =
                this@ViewModelFlowFeature.handleResult(previous, result)
        }

    override val state: StateFlow<STATE>
        get() = standardFlowFeature.state

    override val effect: Flow<EFFECT>
        get() = standardFlowFeature.effect

    override fun processUiEvent(event: EVENT): Unit =
        standardFlowFeature.processUiEvent(event)

    override suspend fun emit(effect: EFFECT): Unit =
        standardFlowFeature.emit(effect)

    private val ViewModel.featureScope: CoroutineScope
        get() = viewModelScope + Dispatchers.Default
}