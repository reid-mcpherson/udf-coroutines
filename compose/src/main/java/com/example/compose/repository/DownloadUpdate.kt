package com.example.compose.repository

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import timber.log.Timber

object DownloadUpdate {
    operator fun invoke(): Flow<Int> {
        return flow {
            var i = 0
            while (currentCoroutineContext().isActive && i <= 100) {
                delay(100)
                Timber.d("Emitting $i")
                emit(i)
                i++
            }
        }
    }
}