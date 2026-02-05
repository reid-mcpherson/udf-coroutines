package com.arch.udf

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * An abstract base class for a standard screen in a UDF (Unidirectional Data Flow) architecture.
 * This class simplifies the connection between a Composable screen and a `ViewModel` that implements
 * the `FlowFeature` interface.
 *
 * It follows the pattern where the UI is a function of the state (`UI = f(state)`). The screen observes
 * state changes from the `ViewModel` and sends user-initiated events back to it.
 *
 * Subclasses must implement the [Screen] method, which defines the actual UI content.
 *
 * This class provides two ways to render the screen's content:
 * 1.  [Content] (inline reified): A convenience function that automatically resolves the `ViewModel`
 *     using `viewModel<T>()`. This is suitable for `ViewModel`s with no-argument constructors or
 *     those using dependency injection frameworks like Hilt.
 * 2.  [Content] (override): An override of the `Screen` interface method, allowing a `ViewModel`
 *     instance to be passed in explicitly. This is useful for manual `ViewModel` creation or in
 *     previews.
 *
 * @param STATE The type of the state object representing the UI state.
 * @param EVENT The type of the event object sent from the UI to the `ViewModel`.
 * @param EFFECT The type of the side effect object emitted by the `ViewModel` for one-time events.
 * @param VIEW_MODEL The type of the `ViewModel`, which must implement `FlowFeature` and extend `ViewModel`.
 */
public abstract class StandardScreen<STATE : Any, EVENT : Any, EFFECT : Any, VIEW_MODEL> :
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