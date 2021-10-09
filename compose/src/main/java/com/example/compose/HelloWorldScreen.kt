package com.example.compose

import android.os.Bundle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arch.udf.FlowViewModel
import com.arch.udf.FlowViewModelImpl
import com.arch.udf.Interactor
import com.arch.udf.ScreenImpl

class HelloWorldScreen : ScreenImpl<Unit, Unit, Unit, HelloWorldViewModel>() {

    override val viewModelClass = HelloWorldViewModel::class.java

    @Composable
    override fun Screen(viewModel: FlowViewModel<Unit, Unit, Unit>, bundle: Bundle?) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text("Hola World")
        }
    }
}

class HelloWorldViewModel :
    FlowViewModelImpl<Unit, Unit, Unit, Unit, Unit>() {
    override val initialState: Unit = Unit
    override val eventToActionInteractor: Interactor<Unit, Unit> = { it }
    override val actionToResultInteractor: Interactor<Unit, Unit> = { it }

    override suspend fun handleResult(previous: Unit, result: Unit) {}
}