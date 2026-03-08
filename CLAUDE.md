# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build all modules
./gradlew assemble

# Run all tests
./gradlew test

# Run tests for a specific module
./gradlew :core:test
./gradlew :compose:test

# Run a single test class
./gradlew :core:test --tests "com.arch.udf.CoreFeatureTest"

# Run a single test by method name (use * for spaces in backtick names)
./gradlew :core:test --tests "com.arch.udf.StandardFeatureTest.*when an action is received*"

# Clean build
./gradlew clean
```

## Architecture

This is an Android library implementing **Unidirectional Data Flow (UDF)** with Kotlin Coroutines. The data flow is strictly one-directional:

```
Event → [eventToAction] → Action → [actionToResult] → Result → [handleResult] → State
                                                                      ↓
                                                                   Effect (optional, one-time)
```

### Module Structure

- **`:core`** (`com.arch.udf`) — Platform-agnostic UDF library. Has `-Xexplicit-api=strict` enforced, so all public declarations require explicit visibility modifiers.
- **`:compose`** (`com.example.compose`) — Sample Android app demonstrating the library with Jetpack Compose navigation and a download progress feature.

### Core Types

All five generic type parameters appear throughout the codebase:

| Type | Role |
|------|------|
| `STATE` | Immutable UI state, exposed as `StateFlow` |
| `EVENT` | User/system inputs, sent via `feature.process(event)` |
| `ACTION` | Business intents derived from events via `eventToAction` interactor |
| `RESULT` | Outcomes of actions, produced by `actionToResult` interactor |
| `EFFECT` | One-time side effects (navigation, toasts) emitted via `emit(effect)` |

### Key Classes

- **`Feature<STATE, EVENT, EFFECT>`** — Public-facing interface. Consumers only see `state: StateFlow`, `effects: Flow`, and `process(event)`.
- **`CoreFeature`** — Internal interface extending `Feature` with the full 5-type pipeline. Not for external implementation.
- **`StandardFeature`** — Platform-agnostic abstract class. Takes a `CoroutineScope` in its constructor. The processing pipeline is set up lazily. Events are buffered with replay=1 via `MutableSharedFlow`.
- **`ViewModelFeature`** — Android-specific abstract class extending `ViewModel`. Delegates to an internal `StandardFeature`. Defaults to `viewModelScope + Dispatchers.Default`. Accepts an optional `scope` parameter for testing.
- **`StandardScreen<STATE, EVENT, EFFECT, VIEW_MODEL>`** — Abstract Compose class. Subclasses implement `Content(feature)`. The `invoke()` operator resolves the ViewModel automatically; for zero-arg or DI-provided ViewModels use `inline operator fun <reified T> invoke()`.
- **`Interactor<T, R>`** — Type alias: `(upstream: Flow<T>) -> Flow<R>`. The two interactors are the primary extension points for implementing business logic.

### Implementing a Feature

Extend `ViewModelFeature` (Android) or `StandardFeature` (platform-agnostic):

```kotlin
class MyViewModel : ViewModelFeature<State, Event, Action, Result, Effect>() {
    override val initial: State = State.Idle
    override val eventToAction: Interactor<Event, Action> = { upstream -> upstream.map { ... } }
    override val actionToResult: Interactor<Action, Result> = { upstream -> upstream.flatMapMerge { ... } }
    override suspend fun handleResult(previous: State, result: Result): State {
        // Call emit(effect) here to produce side effects
        return when (result) { ... }
    }
}
```

### Testing Patterns

Tests use `TestScope` for virtual time control, Turbine for Flow assertions, MockK for mocking, and Truth for assertions.

```kotlin
@Test
fun myTest() = runTest {
    val scope = TestScope()
    val feature = MyFeature(scope)
    feature.state.test {
        assertThat(awaitItem()).isEqualTo(State.Initial)
        feature.process(Event.Load)
        scope.advanceUntilIdle()
        assertThat(awaitItem()).isEqualTo(State.Loaded)
    }
    scope.cancel()
}
```

- `CoreFeatureTest` is an abstract base test class shared between `StandardFeatureTest` and `AndroidFeatureTest` to test both implementations with the same suite.
- Interactors can be tested independently by passing `flowOf(...)` directly into them.
- Effects are tested by nesting `subject.effects.test { ... }` inside `subject.state.test { ... }`.
