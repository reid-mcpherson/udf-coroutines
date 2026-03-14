# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build all modules
./gradlew assemble

# Run all tests
./gradlew test

# Run tests for a specific module
./gradlew :arch:jvmTest
./gradlew :arch-android:test
./gradlew :ui:test
./gradlew :compose:test

# Run a single test class
# :arch uses jvmTest (KMP JVM target); other Android modules use testDebugUnitTest
./gradlew :arch:jvmTest --tests "com.composure.arch.StandardFeatureTest"
./gradlew :arch-android:testDebugUnitTest --tests "com.composure.arch.AndroidFeatureTest"

# Run a single test by method name (use * for spaces in backtick names)
./gradlew :arch:jvmTest --tests "com.composure.arch.StandardFeatureTest.*when an action is received*"

# Check all Kotlin files for lint issues
./gradlew ktlintCheck

# Auto-fix lint issues
./gradlew ktlintFormat

# Clean build
./gradlew clean
```

## Architecture

This is a **Kotlin Multiplatform** library implementing **Unidirectional Data Flow (UDF)** with Kotlin Coroutines. The data flow is strictly one-directional:

```
Event → [eventToAction] → Action → [actionToResult] → Result → [handleResult] → State
                                                                      ↓
                                                                   Effect (optional, one-time)
```

### Module Structure

- **`:arch`** (`com.composure.arch`) — Kotlin Multiplatform UDF core (`Feature`, `StandardFeature`, `Interactor`). Targets: Android, iOS (iosArm64, iosSimulatorArm64), JVM. Published to Maven Central.
- **`:arch-android`** (`com.composure.arch`) — Android-specific ViewModel integration (`ViewModelFeature`). Depends on `:arch`. Published to Maven Central.
- **`:ui`** (`com.composure.ui`) — Jetpack Compose screen binding helpers (`Screen`, `StandardScreen`). Depends on `:arch-android`. Not published.
- **`:compose`** (`com.example.compose`) — Sample Android app demonstrating the library with Jetpack Compose navigation and a download progress feature. Not published.

All three library modules (`:arch`, `:arch-android`, `:ui`) enforce `-Xexplicit-api=strict` — all public declarations require explicit visibility modifiers.

### Code Style & Toolchain

- **Line limit:** 120 characters (enforced by ktlint via `.editorconfig`). Long lines will fail `ktlintCheck`.
- **JDK:** Use JDK 21 to match CI and avoid toolchain mismatches.

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

- `CoreFeatureTest` (`com.composure.arch`) is an abstract base test class shared between `StandardFeatureTest` (`:arch`) and `AndroidFeatureTest` (`:arch-android`) to test both implementations with the same suite.
- Interactors can be tested independently by passing `flowOf(...)` directly into them.
- Effects are tested by nesting `subject.effects.test { ... }` inside `subject.state.test { ... }`.
