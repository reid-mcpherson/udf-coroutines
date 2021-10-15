package com.example.compose.ui.screens.download

import com.arch.udf.Interactor
import com.example.compose.repository.DownloadUpdate
import com.example.compose.ui.screens.download.CancelableFlow.Action
import com.example.compose.ui.screens.download.CancelableFlow.JobStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*

class EventToActionsInteractor : Interactor<DownloadScreen.Event, DownloadViewModel.Action> {
    override fun invoke(upstream: Flow<DownloadScreen.Event>): Flow<DownloadViewModel.Action> =
        upstream.map { event ->
            when (event) {
                is DownloadScreen.Event.OnClick -> {
                    when (event.state) {
                        DownloadScreen.State.Idle -> DownloadViewModel.Action.Start
                        is DownloadScreen.State.Downloading -> DownloadViewModel.Action.Cancel
                    }
                }
            }
        }
}

class ActionToResultsInteractor(
    scope: CoroutineScope,
    private val cancelableDownloadFlow: CancelableFlow<DownloadViewModel.Result> = DownloadCancelableFlow(
        scope
    )
) : Interactor<DownloadViewModel.Action, DownloadViewModel.Result> {

    override fun invoke(upstream: Flow<DownloadViewModel.Action>): Flow<DownloadViewModel.Result> {
        val actions = upstream.map { action ->
            when (action) {
                DownloadViewModel.Action.Start -> Action.Start
                DownloadViewModel.Action.Cancel -> Action.Cancel
            }
        }

        val controlFlow =
            cancelableDownloadFlow.createControlFlow(actions).flatMapConcat { jobStatus ->
                if (jobStatus == JobStatus.Idle) {
                    flowOf(DownloadViewModel.Result.Idle)
                } else emptyFlow<DownloadViewModel.Result>()
            }

        return flowOf(controlFlow, cancelableDownloadFlow.results).flattenMerge()
    }
}

class DownloadCancelableFlow(private val scope: CoroutineScope) :
    CancelableFlowImpl<DownloadViewModel.Result>() {
    override fun createJob(): Job =
        DownloadUpdate()
            .map { percent ->
                DownloadViewModel.Result.Downloading(
                    percent,
                    percent == 50
                )
            }
            .onEach { percent ->
                resultsStream.emit(percent)
            }
            .onCompletion {
                resultsStream.emit(DownloadViewModel.Result.Idle)
            }.launchIn(scope)
}

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