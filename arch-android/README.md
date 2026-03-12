# :arch-android Module — Android ViewModel Integration

The **arch-android** module provides the Android-specific ViewModel wrapper for the UDF pipeline. It depends on `:arch` and adds `androidx.lifecycle.ViewModel` integration.

**Package:** `com.composure.arch`

## Installation

```kotlin
// build.gradle.kts
dependencies {
    // Includes :arch transitively
    implementation("com.github.reid-mcpherson.composure:arch-viewmodel:1.0.0")
}
```

> This module is Android-only. For Kotlin Multiplatform projects, depend
> on `:arch` directly and add ViewModel integration in your Android source set.

## Components

### `ViewModelFeature<STATE, EVENT, ACTION, RESULT, EFFECT>`
Android-specific abstract class extending `ViewModel`. Delegates UDF pipeline logic to an internal `StandardFeature`. The coroutine scope defaults to `viewModelScope + Dispatchers.Default`, ensuring automatic cleanup when the ViewModel is cleared.

Accepts an optional `scope: CoroutineScope` parameter for testing.

## Usage

```kotlin
class MyViewModel : ViewModelFeature<State, Event, Action, Result, Effect>() {
    override val initial: State = State.Idle

    override val eventToAction: Interactor<Event, Action> = { upstream ->
        upstream.map { event -> /* convert event to action */ }
    }

    override val actionToResult: Interactor<Action, Result> = { upstream ->
        upstream.flatMapMerge { action -> /* process action to result */ }
    }

    override suspend fun handleResult(previous: State, result: Result): State {
        // Call emit(effect) here to produce side effects
        return when (result) { /* reduce result into new state */ }
    }
}
```

## Testing

Pass a `TestScope` to override the default `viewModelScope`:

```kotlin
@Test
fun myTest() = runTest {
    val scope = TestScope()
    val viewModel = MyViewModel(scope)
    viewModel.state.test {
        assertThat(awaitItem()).isEqualTo(State.Idle)
        viewModel.process(Event.Load)
        scope.advanceUntilIdle()
        assertThat(awaitItem()).isEqualTo(State.Loaded)
    }
    scope.cancel()
}
```
