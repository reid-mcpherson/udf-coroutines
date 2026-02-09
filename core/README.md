# Core Module - Unidirectional Data Flow (UDF) Architecture

The **core** module provides a lightweight, coroutine-based implementation of the Unidirectional Data Flow (UDF) architecture pattern. It abstracts away the complexity of state management and event processing while ensuring a clean, predictable data flow for your application.

## Overview

The UDF pattern establishes a single, directional flow of data through your application:

```
Event → Action → Result → State
            ↓
         Effect (side effects)
```

This module provides:
- **Platform-agnostic implementation** with `StandardFeature` for any Kotlin application
- **Android-specific integration** with `ViewModelFeature` for seamless ViewModel integration
- **Composable UI support** via `StandardScreen` for building UI screens
- **Flow-based processing** using Kotlin Coroutines for reactive data handling

## Core Components

### 1. **Feature Interface**
The main public interface for consuming UDF features.

```kotlin
public interface Feature<STATE : Any, EVENT : Any, EFFECT : Any> {
    val state: StateFlow<STATE>
    val effects: Flow<EFFECT>
    fun processUiEvent(event: EVENT)
}
```

**Key Properties:**
- `state`: A `StateFlow` representing the current UI state
- `effects`: A `Flow` for one-time side effects (navigation, toasts, etc.)

### 2. **StandardFeature**
A platform-agnostic implementation of the UDF pattern for any Kotlin application.

```kotlin
abstract class StandardFeature<STATE, EVENT, ACTION, RESULT, EFFECT>(
    scope: CoroutineScope
)
```

**Usage:**
Extend this class and provide your own implementations:

```kotlin
class MyFeature(scope: CoroutineScope) : StandardFeature<State, Event, Action, Result, Effect>(scope) {
    override val initialState = State.Initial
    
    override val eventToActionInteractor: Interactor<Event, Action> = { events ->
        events.map { event -> /* convert event to action */ }
    }
    
    override val actionToResultInteractor: Interactor<Action, Result> = { actions ->
        actions.map { action -> /* process action to result */ }
    }
    
    override suspend fun handleResult(previous: State, result: Result): State {
        return when (result) {
            // reduce result into new state
        }
    }
}
```

### 3. **ViewModelFeature**
An Android-specific implementation that integrates with `ViewModel` and leverages `viewModelScope`.

```kotlin
abstract class ViewModelFeature<STATE, EVENT, ACTION, RESULT, EFFECT>() : ViewModel(), CoreFeature<...>
```

**Usage:**
```kotlin
class MyViewModel : ViewModelFeature<State, Event, Action, Result, Effect>() {
    override val initialState = State.Initial
    override val eventToActionInteractor: Interactor<Event, Action> = { /* ... */ }
    override val actionToResultInteractor: Interactor<Action, Result> = { /* ... */ }
    override suspend fun handleResult(previous: State, result: Result): State { /* ... */ }
}
```

**Benefits:**
- Lifecycle-aware coroutine scope (`viewModelScope`)
- Automatic cleanup when ViewModel is cleared
- Seamless integration with Android architecture components

### 4. **StandardScreen**
An abstract base class for building Jetpack Compose screens following the UDF pattern.

```kotlin
abstract class StandardScreen<STATE, EVENT, EFFECT, VIEW_MODEL>() 
    where VIEW_MODEL : Feature<STATE, EVENT, EFFECT>, VIEW_MODEL : ViewModel
```

**Usage:**
```kotlin
class MyScreen : StandardScreen<State, Event, Effect, MyViewModel>() {
    @Composable
    override fun Screen(viewModel: Feature<State, Event, Effect>) {
        // Observe state and emit events
        val state by viewModel.state.collectAsState()
        LaunchedEffect(Unit) {
            viewModel.effects.collect { effect ->
                when (effect) {
                    // handle side effects
                }
            }
        }
        // Build UI
    }
}
```

## Data Types

The UDF pattern uses five generic types to describe your feature:

| Type | Purpose | Example |
|------|---------|---------|
| `STATE` | Represents the current UI state | `data class UiState(val users: List<User>, val loading: Boolean)` |
| `EVENT` | User interactions or system events | `sealed class Event { object LoadUsers : Event() }` |
| `ACTION` | Business logic intents derived from events | `sealed class Action { object FetchUsers : Action() }` |
| `RESULT` | Outcomes of processing actions | `sealed class Result { data class UsersLoaded(val users: List<User>) : Result() }` |
| `EFFECT` | One-time side effects (navigation, toasts) | `sealed class Effect { object NavigateHome : Effect() }` |

## Processing Pipeline

1. **Event Processing**: User interactions trigger `EVENT`s
2. **Event-to-Action**: `eventToActionInteractor` transforms events to `ACTION`s
3. **Action Processing**: `actionToResultInteractor` processes actions to produce `RESULT`s
4. **State Reduction**: `handleResult` reduces results into new `STATE`s
5. **Side Effects**: `emit()` can be called to produce one-time `EFFECT`s

```
EVENT
  ↓
[eventToActionInteractor]
  ↓
ACTION
  ↓
[actionToResultInteractor]
  ↓
RESULT
  ↓
[handleResult]
  ↓
STATE + EFFECT (optional)
```

## Interactors

Interactors are functions that transform `Flow` objects. They're used to connect different processing stages:

```kotlin
typealias Interactor<T, R> = (upstream: Flow<T>) -> Flow<R>
```

**Example:**
```kotlin
val eventToActionInteractor: Interactor<Event, Action> = { events ->
    events.map { event ->
        when (event) {
            Event.LoadUsers -> Action.FetchUsers
            is Event.UserClicked -> Action.ShowUserDetails(event.id)
        }
    }
}
```

## Key Features

### ✨ Reactive by Default
- Uses Kotlin Flow for composable, cancellable async operations
- State changes are exposed as `StateFlow` for predictable reactivity

### 🔄 Unidirectional Data Flow
- Single source of truth for state
- Clear, predictable data transformations
- Easier to test and reason about

### 🎯 Type-Safe
- Leverages Kotlin's type system for compile-time safety
- No runtime type casting needed

### 📱 Android Integration
- `ViewModelFeature` provides lifecycle awareness
- Automatic coroutine cancellation on ViewModel clear
- Works seamlessly with Jetpack Compose

### 🧪 Testable
- Easy to unit test with mocked interactors
- Can use `TestScope` and virtual time for deterministic testing

## Testing

The core module includes comprehensive test utilities for validating your features:

```kotlin
@Test
fun testStateChange() = runTest {
    val scope = TestScope()
    val feature = MyFeature(scope)
    
    feature.state.test {
        assertThat(awaitItem()).isEqualTo(State.Initial)
        feature.processUiEvent(Event.LoadData)
        scope.advanceUntilIdle()
        assertThat(awaitItem()).isEqualTo(State.Loaded)
    }
}
```

## Dependencies

The core module relies on:
- **Kotlin Coroutines** (`kotlinx-coroutines-core`, `kotlinx-coroutines-android`)
- **AndroidX Lifecycle** for ViewModel integration
- **Jetpack Compose** for UI components
- **Timber** for logging

## Best Practices

1. **Keep State Immutable**: Use `data class` for state objects
2. **Use Sealed Classes**: For `EVENT`, `ACTION`, `RESULT`, and `EFFECT`
3. **Sequential Event Processing**: Events are processed in order for state consistency
4. **Handle Side Effects**: Always observe and process the `effects` flow in your UI
5. **Test Interactors**: Test event-to-action and action-to-result transformations independently
6. **Avoid State Leaks**: Properly cancel scopes and subscriptions

## Example Implementation

See the test file `FlowViewModelCoreTest.kt` for complete working examples demonstrating:
- Basic state changes
- Asynchronous operations
- Side effect emission
- Time-based testing with virtual time

## License

This module is part of the UDF-Coroutines project.

