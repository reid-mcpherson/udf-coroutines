package com.example.compose.ui.screens.download

import com.example.compose.ui.screens.download.CancelableFlow.Action
import com.example.compose.ui.screens.download.CancelableFlow.JobStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*

interface CancelableFlow<T> {
    val results: SharedFlow<T>

    sealed interface Action {
        object Start : Action
        object Cancel : Action
    }

    sealed interface JobStatus {
        object Idle : JobStatus
        class Working(val job: Job) : JobStatus
    }

    fun createJob(): Job

    fun createControlFlow(upstream: Flow<Action>): Flow<JobStatus>
}

abstract class CancelableFlowImpl<T> : CancelableFlow<T> {

    protected val resultsStream: MutableSharedFlow<T> = MutableSharedFlow()

    override val results: SharedFlow<T> = resultsStream.asSharedFlow()

    override fun createControlFlow(upstream: Flow<Action>): Flow<JobStatus> =
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
                        JobStatus.Idle -> JobStatus.Working(createJob())
                        is JobStatus.Working -> {
                            if (jobStatus.job.isActive) {
                                jobStatus
                            } else JobStatus.Working(
                                createJob()
                            )
                        }
                    }
                }
            }
        }
}