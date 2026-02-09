# Unidirectional Data Flow with Coroutines

This repository provides a library implementing a lightweight, coroutine-based Unidirectional Data Flow (UDF) architecture for Kotlin applications, with a focus on Android and Jetpack Compose.

## Overview

The UDF pattern establishes a single, directional flow of data through your application, making state management predictable and easier to debug.

```
Event → Action → Result → State
            ↓
         Effect (side effects)
```

This project is organized into several modules:

*   `core`: A platform-agnostic implementation of the UDF pattern. See the [core module README](./core/README.md) for a detailed explanation.
*   `compose`: Provides helpers and components for using the UDF pattern with Jetpack Compose.
*   `app`: A sample application demonstrating the use of the library.
*   `design`: A design system module with shared UI components.

## Core Concepts

The `core` module provides the foundational components for building features with UDF:

*   **`Feature`**: The main interface for interacting with a UDF component, providing a `StateFlow` of the current state and a `Flow` of side effects.
*   **`StandardFeature`**: A platform-agnostic base class for creating UDF features in any Kotlin application.
*   **`ViewModelFeature`**: An Android-specific implementation that integrates with `ViewModel` and `viewModelScope`.

## Data Flow Types

The pattern uses five main types to model the data flow:

| Type     | Purpose                                           | Example                                                      |
|----------|---------------------------------------------------|--------------------------------------------------------------|
| `STATE`  | Represents the current UI state                   | `data class UiState(val users: List<User>, val loading: Boolean)` |
| `EVENT`  | User interactions or system events                | `sealed class Event { object LoadUsers : Event() }`             |
| `ACTION` | Business logic intents derived from events       | `sealed class Action { object FetchUsers : Action() }`          |
| `RESULT` | Outcomes of processing actions                    | `sealed class Result { data class UsersLoaded(val users: List<User>) : Result() }` |
| `EFFECT` | One-time side effects (e.g., navigation, toasts) | `sealed class Effect { object NavigateHome : Effect() }`        |

## Usage Example (Android ViewModel)

Here's how you can implement a UDF feature using `ViewModelFeature`:

```kotlin
class MyViewModel : ViewModelFeature<State, Event, Action, Result, Effect>() {
    override val initialState = State.Initial

    override val eventToActionInteractor: Interactor<Event, Action> = { events ->
        events.map { event ->
            when (event) {
                Event.LoadUsers -> Action.FetchUsers
            }
        }
    }

    override val actionToResultInteractor: Interactor<Action, Result> = { actions ->
        actions.flatMapMerge { action ->
            when (action) {
                Action.FetchUsers -> flow {
                    emit(Result.Loading)
                    // perform async work
                    emit(Result.UsersLoaded(repository.getUsers()))
                }
            }
        }
    }

    override suspend fun handleResult(previous: State, result: Result): State {
        return when (result) {
            is Result.Loading -> previous.copy(isLoading = true)
            is Result.UsersLoaded -> previous.copy(isLoading = false, users = result.users)
        }
    }
}
```

For more detailed information on the architecture and how to use the `core` library, please see the [core module's README file](./core/README.md).

