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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import timber.log.Timber

object DownloadScreen : ScreenImpl<State, Event, Effect, DownloadViewModel>() {

    sealed class State {
        object Idle : State()
        data class Downloading(val percent: Int) : State()
    }

    sealed interface Event {
        data class OnClick(val state: State) : Event
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

        MainScreen(state, viewModel::processUiEvent)
    }

    @Composable
    private fun MainScreen(state: State, processUiEvent: (event: Event) -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = if (state is State.Downloading) "Percent Complete = ${state.percent}%" else "Idle")
            Button(onClick = { processUiEvent(Event.OnClick(state)) }) {
                Text(text = if (state is State.Downloading) "Cancel" else "Download Update")
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

    override val initialState: State = State.Idle

    override val eventToActionInteractor: Interactor<Event, Action> = {
        it.map { event ->
            when (event) {
                is Event.OnClick -> {
                    when (event.state) {
                        State.Idle -> Action.StartAction
                        is State.Downloading -> Action.CancelAction
                    }
                }
            }
        }
    }

    override val actionToResultInteractor: Interactor<Action, Result> =
        ActionToResultsInteractor(viewModelScope)

    override suspend fun handleResult(previous: State, result: Result): State {
        Timber.d("Handle Result $result previous State = $previous")
        return when (result) {
            is Result.Idle -> State.Idle
            is Result.Downloading -> {
                when (previous) {
                    State.Idle -> State.Downloading(result.percent)
                    is State.Downloading -> previous.copy(percent = result.percent)
                }
            }
        }
    }

    private class ActionToResultsInteractor(private val scope: CoroutineScope) :
        Interactor<Action, Result> {

        private val downloadFlow = MutableSharedFlow<Result>()

        override fun invoke(upstream: Flow<Action>): Flow<Result> {
            val downloadEffect: Flow<Result> =
                upstream.scan(JobStatus.Idle as JobStatus) { jobStatus, action ->
                    Timber.d("Action = $action jobStatus = $jobStatus")
                    when (action) {
                        Action.CancelAction -> {
                            when (jobStatus) {
                                is JobStatus.Working -> {
                                    jobStatus.job.cancel()
                                    JobStatus.Idle
                                }
                                JobStatus.Idle -> jobStatus
                            }
                        }
                        Action.StartAction -> {
                            val createDownloadJob: () -> Job = {
                                DownloadUpdate()
                                    .map { percent -> Result.Downloading(percent) }
                                    .onEach { percent ->
                                        downloadFlow.emit(percent)
                                    }
                                    .onCompletion {
                                        downloadFlow.emit(Result.Idle)
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
                        flowOf(Result.Idle)
                    } else emptyFlow<Result>()
                }

            return flowOf(downloadEffect, downloadFlow).flattenMerge()
        }

        private sealed class JobStatus {
            object Idle : JobStatus()
            class Working(val job: Job) : JobStatus()
        }
    }
}