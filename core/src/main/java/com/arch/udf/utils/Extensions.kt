package com.arch.udf.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.plus

public fun <T : Any> Flow<*>.toSideEffectFlow(): Flow<T> =
    flatMapConcat {
        emptyFlow()
    }


