package com.example.compose.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arch.udf.*

object Screen2 : StandardScreen<Unit, Unit, Unit, Screen2Feature>() {

    @Composable
    override fun Screen(viewModel: Feature<Unit, Unit, Unit>) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text("Screen 2")
        }
    }
}

class Screen2Feature :
    ViewModelFeature<Unit, Unit, Unit, Unit, Unit>() {
    override val initialState: Unit = Unit
    override val eventToActionInteractor: Interactor<Unit, Unit> = { flow -> flow }
    override val actionToResultInteractor: Interactor<Unit, Unit> = { flow -> flow }

    override suspend fun handleResult(previous: Unit, result: Unit) {}

    override fun onCleared() {
        println("onCleared 2")
        super.onCleared()
    }
}