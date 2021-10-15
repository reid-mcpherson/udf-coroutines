package com.example.compose.ui.screens

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

object Screen3 : ScreenImpl<Unit, Unit, Unit, Screen3ViewModel>() {

    override val viewModelClass = Screen3ViewModel::class.java

    @Composable
    override fun Screen(viewModel: FlowViewModel<Unit, Unit, Unit>, bundle: Bundle?) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text("Screen 3")
        }
    }
}

class Screen3ViewModel :
    FlowViewModelImpl<Unit, Unit, Unit, Unit, Unit>() {
    override val initialState: Unit = Unit
    override val eventToActionInteractor: Interactor<Unit, Unit> = { it }
    override val actionToResultInteractor: Interactor<Unit, Unit> = { it }

    override suspend fun handleResult(previous: Unit, result: Unit) {}

    override fun onCleared() {
        println("onCleared 3")
        super.onCleared()
    }

}