[![Build & Test](https://github.com/reid-mcpherson/composure/actions/workflows/workflow.yml/badge.svg?branch=main)](https://github.com/reid-mcpherson/composure/actions/workflows/workflow.yml)

# Composure

This repository provides a library implementing a lightweight, coroutine-based Unidirectional Data Flow (UDF) architecture for Kotlin applications, with a focus on Android and Jetpack Compose.

## Overview

The UDF pattern establishes a single, directional flow of data through your application, making state management predictable and easier to debug.

```
Event → Action → Result → State
            ↓
         Effect (side effects)
```

This project is organized into several modules:

*   `arch`: Platform-agnostic UDF core (`Feature`, `StandardFeature`, `Interactor`). See the [arch module README](./arch/README.md).
*   `arch-android`: Android-specific ViewModel integration (`ViewModelFeature`). See the [arch-android module README](./arch-android/README.md).
*   `ui`: Jetpack Compose screen binding helpers (`Screen`, `StandardScreen`). See the [ui module README](./ui/README.md).
*   `compose`: Sample Android application demonstrating the library.

## Installation

Add JitPack to your `settings.gradle.kts`:

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        maven { url = uri("https://jitpack.io") }
    }
}
```

Then add the desired dependency to your module's `build.gradle.kts`:

```kotlin
// build.gradle.kts
dependencies {
    // Platform-agnostic UDF core
    implementation("com.github.reid-mcpherson.composure:arch:1.0.0")

    // Android ViewModel integration (includes :arch)
    implementation("com.github.reid-mcpherson.composure:arch-android:1.0.0")
}
```

## Core Concepts

*   **`Feature`** (`com.composure.arch`): The main interface for interacting with a UDF component, providing a `StateFlow` of the current state and a `Flow` of side effects.
*   **`StandardFeature`** (`com.composure.arch`): A platform-agnostic base class for creating UDF features in any Kotlin application.
*   **`ViewModelFeature`** (`com.composure.arch`): An Android-specific implementation that integrates with `ViewModel` and `viewModelScope`.
*   **`StandardScreen`** (`com.composure.ui`): An abstract Compose screen base class that wires a `ViewModelFeature` to a composable UI.

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

```kotlin
class MyViewModel : ViewModelFeature<State, Event, Action, Result, Effect>() {
    override val initial = State.Initial

    override val eventToAction: Interactor<Event, Action> = { events ->
        events.map { event ->
            when (event) {
                Event.LoadUsers -> Action.FetchUsers
            }
        }
    }

    override val actionToResult: Interactor<Action, Result> = { actions ->
        actions.flatMapMerge { action ->
            when (action) {
                Action.FetchUsers -> flow {
                    emit(Result.Loading)
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
