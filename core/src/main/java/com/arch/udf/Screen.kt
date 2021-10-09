package com.arch.udf

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

public interface Screen<STATE : Any, EVENT : Any, EFFECT : Any, VIEW_MODEL : FlowViewModel<STATE, EVENT, EFFECT>> {

    @Composable
    public fun Content(bundle: Bundle?)

}

public abstract class ScreenImpl<STATE : Any, EVENT : Any, EFFECT : Any, VIEW_MODEL> :
    Screen<STATE, EVENT, EFFECT, VIEW_MODEL> where VIEW_MODEL : FlowViewModel<STATE, EVENT, EFFECT>, VIEW_MODEL : ViewModel {

    protected abstract val viewModelClass: Class<VIEW_MODEL>

    @Composable
    protected abstract fun Screen(viewModel: FlowViewModel<STATE, EVENT, EFFECT>, bundle: Bundle?)

    @Composable
    public override fun Content(bundle: Bundle?) {
        Screen(
            viewModel = viewModel(modelClass = viewModelClass),
            bundle = bundle
        )
    }
}