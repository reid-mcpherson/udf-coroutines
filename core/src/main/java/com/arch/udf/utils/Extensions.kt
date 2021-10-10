package com.arch.udf.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapConcat

public fun <T : Any> Flow<*>.toSideEffectFlow(): Flow<T> =
    flatMapConcat {
        emptyFlow()
    }