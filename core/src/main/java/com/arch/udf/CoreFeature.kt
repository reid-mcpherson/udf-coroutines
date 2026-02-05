package com.arch.udf

/**
 * Core internal interface defining the structure and components of a Unidirectional Data Flow (UDF) feature.
 * It orchestrates the flow from an incoming event to a new state or a side effect. This interface
 * is not meant for direct implementation by consumers but is used by [StandardFeature] and [ViewModelFeature].
 *
 * @param STATE The type of the state object.
 * @param EVENT The type of the event that can be processed. Typically user interactions or system events.
 * @param ACTION The type of the action that is derived from an event. Actions represent the intent to change the state.
 * @param RESULT The type of the result produced by processing an action. Results are the direct inputs for state reduction.
 * @param EFFECT The type of the side effect that can be emitted. Side effects are for one-time events like showing a toast or navigating.
 */
internal interface CoreFeature<STATE : Any, EVENT : Any, ACTION : Any, RESULT : Any, EFFECT : Any> :
    Feature<STATE, EVENT, EFFECT> {
    val initialState: STATE
    val eventToActionInteractor: Interactor<EVENT, ACTION>
    val actionToResultInteractor: Interactor<ACTION, RESULT>
    suspend fun handleResult(previous: STATE, result: RESULT): STATE
    suspend fun emit(effect: EFFECT)
}