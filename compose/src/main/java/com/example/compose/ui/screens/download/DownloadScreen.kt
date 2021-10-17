package com.example.compose.ui.screens.download

import android.os.Bundle
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewModelScope
import com.arch.udf.FlowViewModel
import com.arch.udf.FlowViewModelImpl
import com.arch.udf.Interactor
import com.arch.udf.ScreenImpl
import com.example.compose.ui.screens.download.DownloadScreen.CompletedEffect
import com.example.compose.ui.screens.download.DownloadScreen.Event
import com.example.compose.ui.screens.download.DownloadScreen.State
import com.example.compose.ui.screens.download.DownloadViewModel.Action
import com.example.compose.ui.screens.download.DownloadViewModel.Result
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import timber.log.Timber

object DownloadScreen : ScreenImpl<State, Event, CompletedEffect, DownloadViewModel>() {

    sealed class State {
        object Idle : State()
        data class Downloading(val percent: Int, val showToast: Boolean) : State()
    }

    sealed interface Event {
        data class OnClick(val state: State) : Event
    }

    object CompletedEffect

    override val viewModelClass = DownloadViewModel::class.java

    @Composable
    override fun Screen(viewModel: FlowViewModel<State, Event, CompletedEffect>, bundle: Bundle?) {
        val state: State by viewModel
            .state
            .collectAsState()

        var dialogState by remember { mutableStateOf(false) }

        LaunchedEffect(true) {
            viewModel
                .effect
                .map { effect ->
                    when (effect) {
                        CompletedEffect -> dialogState = true
                    }
                }
                .collect()
        }

        MainScreen(state, viewModel::processUiEvent)
        CompletedDialog(dialogState, onClose = { dialogState = false })
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

    @Composable
    private fun CompletedDialog(dialogState: Boolean, onClose: () -> Unit) {
        if (dialogState) {
            AlertDialog(
                onDismissRequest = onClose,
                title = { Text("Update Complete!") },
                text = { Text("Click OK to Continue.")},
                confirmButton = {
                    Button(onClick = onClose){
                        Text("OK")
                    }
                }
            )
        }
    }
}

class DownloadViewModel :
    FlowViewModelImpl<State, Event, Action, Result, CompletedEffect>() {

    sealed class Action {
        object Start : Action()
        object Cancel : Action()
    }

    sealed class Result {
        data class Downloading(val percent: Int, val showToast: Boolean) : Result()
        object Completed : Result()
        object Idle : Result()
    }

    override val initialState: State = State.Idle

    override val eventToActionInteractor: Interactor<Event, Action> = EventToActionsInteractor()

    override val actionToResultInteractor: Interactor<Action, Result> =
        ActionToResultsInteractor(viewModelScope)

    override suspend fun handleResult(previous: State, result: Result): State {
        Timber.d("Handle Result $result previous State = $previous")
        return when (result) {
            Result.Idle -> State.Idle
            is Result.Downloading -> {
                when (previous) {
                    State.Idle -> State.Downloading(result.percent, showToast = false)
                    is State.Downloading -> previous.copy(
                        percent = result.percent,
                        showToast = result.showToast
                    )
                }
            }
            Result.Completed -> {
                emitEffect(CompletedEffect)
                previous
            }
        }
    }
}