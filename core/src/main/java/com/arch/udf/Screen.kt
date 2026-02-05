package com.arch.udf

import androidx.compose.runtime.Composable

/**
 * Represents a single screen in the application, following the Unidirectional Data Flow (UDF) pattern.
 *
 * This interface defines the contract for a screen, which is composed of a [Content] function
 * that takes a specific [VIEW_MODEL] instance. The screen is responsible for observing state,
 * sending events, and handling side effects, all orchestrated by its associated ViewModel.
 *
 * @param STATE The type of the state object that represents the screen's UI.
 * @param EVENT The type of the events that can be triggered by user interactions on the screen.
 * @param EFFECT The type of the side effects that can be emitted by the ViewModel (e.g., navigation, toasts).
 * @param VIEW_MODEL The type of the ViewModel, which must implement [Feature] to manage the screen's logic.
 */
public interface Screen<STATE : Any, EVENT : Any, EFFECT : Any, VIEW_MODEL : Feature<STATE, EVENT, EFFECT>> {

    @Composable
    public fun Content(viewModel: VIEW_MODEL)

}