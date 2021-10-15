package com.example.compose.ui.screens.download

import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewModelScope
import com.arch.udf.FlowViewModel
import com.arch.udf.FlowViewModelImpl
import com.arch.udf.Interactor
import com.arch.udf.ScreenImpl
import com.example.compose.ui.screens.download.DownloadScreen.Event
import com.example.compose.ui.screens.download.DownloadScreen.State
import com.example.compose.ui.screens.download.DownloadViewModel.Action
import com.example.compose.ui.screens.download.DownloadViewModel.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import timber.log.Timber

object DownloadScreen : ScreenImpl<State, Event, Unit, DownloadViewModel>() {

    sealed class State {
        object Idle : State()
        data class Downloading(val percent: Int, val showToast: Boolean) : State()
    }

    sealed interface Event {
        data class OnClick(val state: State) : Event
    }


    override val viewModelClass = DownloadViewModel::class.java

    @Composable
    override fun Screen(viewModel: FlowViewModel<State, Event, Unit>, bundle: Bundle?) {
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

            if (state is State.Downloading && state.showToast) {
                Toast()
            }
        }
    }

    @Composable
    private fun Toast() {
        Toast.makeText(LocalContext.current, "50% completed!", Toast.LENGTH_SHORT).show()
    }
}

class DownloadViewModel :
    FlowViewModelImpl<State, Event, Action, Result, Unit>() {

    sealed class Action {
        object StartAction : Action()
        object CancelAction : Action()
    }

    sealed class Result {
        class Downloading(val percent: Int, val showToast: Boolean) : Result()
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
                    State.Idle -> State.Downloading(result.percent, showToast = false)
                    is State.Downloading -> previous.copy(
                        percent = result.percent,
                        showToast = result.showToast
                    )
                }
            }
        }
    }
}