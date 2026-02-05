package com.arch.udf

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

public typealias Interactor<T, R> = (upstream: Flow<T>) -> Flow<R>

public interface FlowFeature<STATE : Any, EVENT : Any, EFFECT : Any> {
    public val state: StateFlow<STATE>
    public val effect: Flow<EFFECT>
    public fun processUiEvent(event: EVENT)
}