package com.example.compose

import android.os.Bundle
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import com.arch.udf.FlowViewModel
import com.arch.udf.FlowViewModelImpl
import com.arch.udf.Interactor
import com.arch.udf.ScreenImpl
import com.example.compose.DownloadScreen.Effect
import com.example.compose.DownloadScreen.Event
import com.example.compose.DownloadScreen.State
import com.example.compose.DownloadViewModel.Action
import com.example.compose.DownloadViewModel.Result
import kotlinx.coroutines.flow.*

object DownloadScreen : ScreenImpl<State, Event, Effect, DownloadViewModel>() {

    data class State(val darkMode: Boolean)

    sealed interface Event {
        data class OnClick(val isSelected: Boolean) : Event
        object Toast : Event
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

        MainScreen(state.darkMode, viewModel::processUiEvent)
    }

    @Composable
    private fun MainScreen(isDarkMode: Boolean, processUiEvent: (event: Event) -> Unit) {
        val darkColor = if (isDarkMode) Color.Black else Color.White
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(darkColor),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { processUiEvent(Event.OnClick(true)) }) {
                Text(text = "Dark Mode")
            }

            Button(onClick = { processUiEvent(Event.OnClick(false)) }) {
                Text(text = "Light Mode")
            }

            Button(onClick = { processUiEvent(Event.Toast) }) {
                Text(text = "Toast!")
            }
        }
    }
}

class DownloadViewModel :
    FlowViewModelImpl<State, Event, Action, Result, Effect>() {

    sealed class Action {
        data class ButtonClickAction(val isSelected: Boolean) : Action()
    }

    sealed class Result {
        data class ButtonClickResult(val isDarkMode: Boolean) : Result()
    }

    override val initialState: State = State(false)

    override val eventToActionInteractor: Interactor<Event, Action> = {
        it.filterIsInstance<Event.OnClick>()
            .map { onClickEvent ->
                Action.ButtonClickAction(onClickEvent.isSelected)
            }
    }
    override val actionToResultInteractor: Interactor<Action, Result> = {
        it.filterIsInstance<Action.ButtonClickAction>()
            .map { buttonClickAction ->
                Result.ButtonClickResult(buttonClickAction.isSelected)
            }
    }

    override suspend fun handleResult(previous: State, result: Result): State =
        when (result) {
            is Result.ButtonClickResult -> previous.copy(darkMode = result.isDarkMode)
        }

    override fun onCleared() {
        println("Cleared!")
        super.onCleared()
    }
}