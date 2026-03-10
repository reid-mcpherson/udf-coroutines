# :arch Module — Platform-Agnostic UDF Core

The **arch** module provides the platform-agnostic Unidirectional Data Flow (UDF) pipeline. It has no Android framework dependency beyond the Android library plugin used for build configuration.

**Package:** `com.composure.arch`

## Installation

```kotlin
// build.gradle.kts
dependencies {
    implementation("com.github.reid-mcpherson.composure:arch:1.0.0")
}
```

## Components

### `Feature<STATE, EVENT, EFFECT>`
The public-facing interface that consumers depend on. Exposes:
- `state: StateFlow<STATE>` — current UI state
- `effects: Flow<EFFECT>` — one-time side effects
- `process(event: EVENT)` — sends an event into the pipeline

### `Interactor<T, R>`
A type alias: `(upstream: Flow<T>) -> Flow<R>`. The two interactors are the primary extension points for implementing business logic.

### `StandardFeature<STATE, EVENT, ACTION, RESULT, EFFECT>`
Platform-agnostic abstract class. Takes a `CoroutineScope` in its constructor. The processing pipeline is set up lazily. Events are buffered with replay=1 via `MutableSharedFlow`.

## Data Flow

```
Event → [eventToAction] → Action → [actionToResult] → Result → [handleResult] → State
                                                                      ↓
                                                                   Effect (optional)
```

## Usage

```kotlin
class MyFeature(scope: CoroutineScope) :
    StandardFeature<State, Event, Action, Result, Effect>(scope) {

    override val initial: State = State.Idle

    override val eventToAction: Interactor<Event, Action> = { upstream ->
        upstream.map { event -> /* convert event to action */ }
    }

    override val actionToResult: Interactor<Action, Result> = { upstream ->
        upstream.flatMapMerge { action -> /* process action to result */ }
    }

    override suspend fun handleResult(previous: State, result: Result): State {
        return when (result) { /* reduce result into new state */ }
    }
}
```

## Testing

```kotlin
@Test
fun myTest() = runTest {
    val scope = TestScope()
    val feature = MyFeature(scope)
    feature.state.test {
        assertThat(awaitItem()).isEqualTo(State.Idle)
        feature.process(Event.Load)
        scope.advanceUntilIdle()
        assertThat(awaitItem()).isEqualTo(State.Loaded)
    }
    scope.cancel()
}
```
