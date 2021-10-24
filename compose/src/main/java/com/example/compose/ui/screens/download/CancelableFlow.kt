package com.example.compose.ui.screens.download

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*

interface CancelableFlow<T> {
    val results: SharedFlow<T>

    fun createJob(scope: CoroutineScope): Job

    fun createControlFlow(upstream: Flow<Action>, scope: CoroutineScope): Flow<JobStatus>
}

abstract class CancelableFlowImpl<T> : CancelableFlow<T> {

    protected val resultsStream: MutableSharedFlow<T> = MutableSharedFlow()

    override val results: SharedFlow<T> = resultsStream.asSharedFlow()

    override fun createControlFlow(upstream: Flow<Action>, scope: CoroutineScope): Flow<JobStatus> =
        upstream.scan(JobStatus.Idle as JobStatus) { jobStatus, action ->
            when (action) {
                Action.Cancel -> {
                    when (jobStatus) {
                        is JobStatus.Working -> {
                            jobStatus.job.cancel()
                            JobStatus.Idle
                        }
                        JobStatus.Idle -> jobStatus
                    }
                }
                Action.Start -> {
                    when (jobStatus) {
                        JobStatus.Idle -> JobStatus.Working(createJob(scope))
                        is JobStatus.Working -> {
                            if (jobStatus.job.isActive) {
                                jobStatus
                            } else JobStatus.Working(
                                createJob(scope)
                            )
                        }
                    }
                }
            }
        }
}

sealed interface Action {
    object Start : Action
    object Cancel : Action
}

sealed interface JobStatus {
    object Idle : JobStatus
    class Working(val job: Job) : JobStatus
}

fun <T> Flow<Action>.toCancelableFlow(
    createJob: (mutableCallbackFlow: MutableSharedFlow<T>) -> Job,
): Pair<Flow<JobStatus>, SharedFlow<T>> {
    val callbackFlow: MutableSharedFlow<T> = MutableSharedFlow()
    return scan(JobStatus.Idle as JobStatus) { jobStatus, action ->
        when (action) {
            Action.Cancel -> {
                when (jobStatus) {
                    is JobStatus.Working -> {
                        jobStatus.job.cancel()
                        JobStatus.Idle
                    }
                    JobStatus.Idle -> jobStatus
                }
            }
            Action.Start -> {
                when (jobStatus) {
                    JobStatus.Idle -> JobStatus.Working(createJob(callbackFlow))
                    is JobStatus.Working -> {
                        if (jobStatus.job.isActive) {
                            jobStatus
                        } else JobStatus.Working(
                            createJob(callbackFlow)
                        )
                    }
                }
            }
        }
    } to callbackFlow
}