package com.example.compose.ui.screens.download

import com.arch.udf.Interactor
import com.example.compose.repository.DownloadUpdate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*

class EventToActionsInteractor : Interactor<DownloadScreen.Event, DownloadFeature.Action> {
    override fun invoke(
        upstream: Flow<DownloadScreen.Event>
    ): Flow<DownloadFeature.Action> =
        upstream.map { event ->
            when (event) {
                is DownloadScreen.Event.OnClick -> {
                    when (event.state) {
                        DownloadScreen.State.Idle -> DownloadFeature.Action.Start
                        is DownloadScreen.State.Downloading -> DownloadFeature.Action.Cancel
                    }
                }
            }
        }
}

class ActionToResultsInteractor(
    private val scope: CoroutineScope,
    private val createJob: (MutableSharedFlow<DownloadFeature.Result>) -> Job = { callbackFlow ->
        createDownloadJob(callbackFlow, scope)
    }
) :
    Interactor<DownloadFeature.Action, DownloadFeature.Result> {

    companion object {
        fun createDownloadJob(
            callbackFlow: MutableSharedFlow<DownloadFeature.Result>,
            scope: CoroutineScope
        ): Job = DownloadUpdate()
            .map { percent ->
                DownloadFeature.Result.Downloading(
                    percent,
                    percent == 50
                )
            }
            .onEach { percent ->
                callbackFlow.emit(percent)
            }
            .onCompletion {
                callbackFlow.emit(DownloadFeature.Result.Completed)
                callbackFlow.emit(DownloadFeature.Result.Idle)
            }.launchIn(scope)
    }

    override fun invoke(
        upstream: Flow<DownloadFeature.Action>
    ): Flow<DownloadFeature.Result> {
        val controlFlow = upstream.map { action ->
            when (action) {
                DownloadFeature.Action.Start -> Action.Start
                DownloadFeature.Action.Cancel -> Action.Cancel
            }
        }

        val (jobStatusFlow, callbackFlow) =
            controlFlow.toCancelableFlow(createJob)

        val jobStatusResult = jobStatusFlow.flatMapConcat { jobStatus ->
            if (jobStatus == JobStatus.Idle) {
                flowOf(DownloadFeature.Result.Idle)
            } else emptyFlow<DownloadFeature.Result>()
        }

        return flowOf(jobStatusResult, callbackFlow).flattenMerge()
    }
}
