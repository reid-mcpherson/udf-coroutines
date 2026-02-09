package com.example.compose.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arch.udf.Feature
import com.arch.udf.Interactor
import com.arch.udf.StandardScreen
import com.arch.udf.ViewModelFeature

object Screen3 : StandardScreen<Unit, Unit, Unit, Screen3Feature>() {

    @Composable
    override fun Content(feature: Feature<Unit, Unit, Unit>) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text("Screen 3")
        }
    }
}

class Screen3Feature :
    ViewModelFeature<Unit, Unit, Unit, Unit, Unit>() {
    override val initial: Unit = Unit
    override val eventToAction: Interactor<Unit, Unit> = { flow -> flow }
    override val actionToResult: Interactor<Unit, Unit> = { flow -> flow }

    override suspend fun handleResult(previous: Unit, result: Unit) {}
}