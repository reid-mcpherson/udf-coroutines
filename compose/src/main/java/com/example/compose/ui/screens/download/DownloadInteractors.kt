package com.example.compose.ui.screens.download

import com.arch.udf.Interactor
import com.example.compose.repository.DownloadUpdate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import timber.log.Timber

class EventToActionsInteractor : Interactor<DownloadScreen.Event, DownloadViewModel.Action> {
    override fun invoke(upstream: Flow<DownloadScreen.Event>): Flow<DownloadViewModel.Action> =
        upstream.map { event ->
            when (event) {
                is DownloadScreen.Event.OnClick -> {
                    when (event.state) {
                        DownloadScreen.State.Idle -> DownloadViewModel.Action.StartAction
                        is DownloadScreen.State.Downloading -> DownloadViewModel.Action.CancelAction
                    }
                }
            }
        }
}

class ActionToResultsInteractor(
    private val scope: CoroutineScope
) : Interactor<DownloadViewModel.Action, DownloadViewModel.Result> {

    private val downloadFlow = MutableSharedFlow<DownloadViewModel.Result>()

    override fun invoke(upstream: Flow<DownloadViewModel.Action>): Flow<DownloadViewModel.Result> {
        val downloadEffect: Flow<DownloadViewModel.Result> =
            upstream.scan(JobStatus.Idle as JobStatus) { jobStatus, action ->
                Timber.d("Action = $action jobStatus = $jobStatus")
                when (action) {
                    DownloadViewModel.Action.CancelAction -> {
                        when (jobStatus) {
                            is JobStatus.Working -> {
                                jobStatus.job.cancel()
                                JobStatus.Idle
                            }
                            JobStatus.Idle -> jobStatus
                        }
                    }
                    DownloadViewModel.Action.StartAction -> {
                        val createDownloadJob: () -> Job = {
                            DownloadUpdate()
                                .map { percent ->
                                    DownloadViewModel.Result.Downloading(
                                        percent,
                                        percent == 50
                                    )
                                }
                                .onEach { percent ->
                                    downloadFlow.emit(percent)
                                }
                                .onCompletion {
                                    downloadFlow.emit(DownloadViewModel.Result.Idle)
                                }.launchIn(scope)
                        }
                        when (jobStatus) {
                            JobStatus.Idle -> JobStatus.Working(createDownloadJob())
                            is JobStatus.Working -> {
                                if (jobStatus.job.isActive) {
                                    jobStatus
                                } else JobStatus.Working(createDownloadJob())
                            }
                        }
                    }
                }
            }.flatMapConcat { jobStatus ->
                if (jobStatus == JobStatus.Idle) {
                    flowOf(DownloadViewModel.Result.Idle)
                } else emptyFlow<DownloadViewModel.Result>()
            }

        return flowOf(downloadEffect, downloadFlow).flattenMerge()
    }

    private sealed class JobStatus {
        object Idle : JobStatus()
        class Working(val job: Job) : JobStatus()
    }
}