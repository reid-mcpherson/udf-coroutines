package com.example.compose.ui.screens.download

import com.arch.udf.Interactor
import com.example.compose.repository.DownloadUpdate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*

class EventToActionsInteractor : Interactor<DownloadScreen.Event, DownloadViewModel.Action> {
    override fun invoke(
        upstream: Flow<DownloadScreen.Event>
    ): Flow<DownloadViewModel.Action> =
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
    private val scope: CoroutineScope,
    private val createJob: (MutableSharedFlow<DownloadViewModel.Result>) -> Job = { callbackFlow ->
        createDownloadJob(callbackFlow, scope)
    }
) :
    Interactor<DownloadViewModel.Action, DownloadViewModel.Result> {

    companion object {
        fun createDownloadJob(
            callbackFlow: MutableSharedFlow<DownloadViewModel.Result>,
            scope: CoroutineScope
        ): Job = DownloadUpdate()
            .map { percent ->
                DownloadViewModel.Result.Downloading(
                    percent,
                    percent == 50
                )
            }
            .onEach { percent ->
                callbackFlow.emit(percent)
            }
            .onCompletion {
                callbackFlow.emit(DownloadViewModel.Result.Completed)
                callbackFlow.emit(DownloadViewModel.Result.Idle)
            }.launchIn(scope)
    }

    override fun invoke(
        upstream: Flow<DownloadViewModel.Action>
    ): Flow<DownloadViewModel.Result> {
        val controlFlow = upstream.map { action ->
            when (action) {
                DownloadViewModel.Action.Start -> Action.Start
                DownloadViewModel.Action.Cancel -> Action.Cancel
            }
        }

        val (jobStatusFlow, callbackFlow) =
            controlFlow.toCancelableFlow(createJob)

        val jobStatusResult = jobStatusFlow.flatMapConcat { jobStatus ->
            if (jobStatus == JobStatus.Idle) {
                flowOf(DownloadViewModel.Result.Idle)
            } else emptyFlow<DownloadViewModel.Result>()
        }

        return flowOf(jobStatusResult, callbackFlow).flattenMerge()
    }
}
