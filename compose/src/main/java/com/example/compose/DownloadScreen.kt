package com.example.compose

import android.os.Bundle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewModelScope
import com.arch.udf.FlowViewModel
import com.arch.udf.FlowViewModelImpl
import com.arch.udf.Interactor
import com.arch.udf.ScreenImpl
import com.example.compose.DownloadScreen.Effect
import com.example.compose.DownloadScreen.Event
import com.example.compose.DownloadScreen.State
import com.example.compose.DownloadViewModel.Action
import com.example.compose.DownloadViewModel.Result
import com.example.compose.repository.DownloadUpdate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import timber.log.Timber

object DownloadScreen : ScreenImpl<State, Event, Effect, DownloadViewModel>() {

    data class State(val isDownloading: Boolean)

    sealed interface Event {
        data class OnClick(val isDownloading: Boolean) : Event
    }

    sealed interface Effect {
        object Toast : Effect
    }

    override val viewModelClass = DownloadViewModel::class.java

    @Composable
    override fun Screen(viewModel: FlowViewModel<State, Event, Effect>, bundle: Bundle?) {
        val state: State by viewModel
            .state
            .collectAsState()

        MainScreen(state.isDownloading, viewModel::processUiEvent)
    }

    @Composable
    private fun MainScreen(isDownloading: Boolean, processUiEvent: (event: Event) -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { processUiEvent(Event.OnClick(isDownloading)) }) {
                Text(text = if (isDownloading) "Cancel" else "Download Update")
            }
        }
    }
}

class DownloadViewModel :
    FlowViewModelImpl<State, Event, Action, Result, Effect>() {

    sealed class Action {
        object StartAction : Action()
        object CancelAction : Action()
    }

    sealed class Result {
        class Downloading(val percent: Int) : Result()
        object Idle : Result()
    }

    override val initialState: State = State(isDownloading = false)

    override val eventToActionInteractor: Interactor<Event, Action> = {
        it.map { event ->
            when (event) {
                is Event.OnClick -> {
                    if (event.isDownloading) {
                        Action.CancelAction
                    } else Action.StartAction
                }
            }
        }
    }

    override val actionToResultInteractor: Interactor<Action, Result> =
        ActionToResultsInteractor(viewModelScope)

    override suspend fun handleResult(previous: State, result: Result): State {
        Timber.d("Handle Result $result previous State = $previous")
        return when (result) {
            is Result.Idle -> previous.copy(isDownloading = false)
            is Result.Downloading -> previous.copy(isDownloading = true)
        }
    }

    override fun onCleared() {
        println("Cleared!")
        super.onCleared()
    }

    private class ActionToResultsInteractor(private val scope: CoroutineScope) :
        Interactor<Action, Result> {
        @FlowPreview
        @ExperimentalCoroutinesApi
        override fun invoke(upstream: Flow<Action>): Flow<Result> {
            Timber.d("Actions to Results invoked!")
            return upstream.scan(JobStatus.Idle as JobStatus) { jobStatus, action ->
                Timber.d("Action = $action jobStatus = $jobStatus")
                when (action) {
                    Action.CancelAction -> {
                        when (jobStatus) {
                            is JobStatus.Working -> {
                                JobStatus.Idle
                            }
                            JobStatus.Idle -> jobStatus
                        }
                    }
                    Action.StartAction -> {
                        when (jobStatus) {
                            JobStatus.Idle -> {
                                Timber.d("Returning download flow")
                                 val downloadFlow = DownloadUpdate()
                                    .map { percent -> Result.Downloading(percent) }
                                JobStatus.Working(downloadFlow)
                            }
                            is JobStatus.Working -> jobStatus
                        }
                    }
                }
            }.flatMapMerge { it.flow }
        }

        private sealed class JobStatus(val flow: Flow<Result>) {
            object Idle : JobStatus(flowOf(Result.Idle))
            class Working(flow: Flow<Result>) : JobStatus(flow)
        }

    }
}