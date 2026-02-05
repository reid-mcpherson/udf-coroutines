package com.arch.udf

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

public typealias Interactor<T, R> = (upstream: Flow<T>) -> Flow<R>

/**
 * A Unidirectional Data Flow (UDF) feature that processes UI events,
 * manages state, and emits side effects. This interface defines the public
 * contract for a self-contained feature component.
 *
 * @param STATE The type of the state exposed by this feature. It represents the
 *              UI state at any given point in time.
 * @param EVENT The type of the events that this feature can process. These are
 *              typically user interactions from the UI.
 * @param EFFECT The type of the side effects that this feature can produce.
 *               These are one-time events, such as navigation, showing a toast,
 *               or displaying a dialog.
 */
public interface Feature<STATE : Any, EVENT : Any, EFFECT : Any> {
    public val state: StateFlow<STATE>
    public val effect: Flow<EFFECT>
    public fun processUiEvent(event: EVENT)
}