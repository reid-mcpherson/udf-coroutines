package com.arch.udf

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

public interface Screen<STATE : Any, EVENT : Any, EFFECT : Any, VIEW_MODEL : FlowFeature<STATE, EVENT, EFFECT>> {

    @Composable
    public fun Content(viewModel: VIEW_MODEL)

}

public abstract class ScreenImpl<STATE : Any, EVENT : Any, EFFECT : Any, VIEW_MODEL> :
    Screen<STATE, EVENT, EFFECT, VIEW_MODEL> where VIEW_MODEL : FlowFeature<STATE, EVENT, EFFECT>, VIEW_MODEL : ViewModel {

    @Composable
    protected abstract fun Screen(viewModel: FlowFeature<STATE, EVENT, EFFECT>)

    @Composable
    //This will only work for view models with
    //zero argument constructors.
    public inline fun <reified T : VIEW_MODEL> Content() {
        Content(viewModel = viewModel<T>())
    }

    @Composable
    override fun Content(viewModel: VIEW_MODEL) {
        Screen(viewModel)
    }
}