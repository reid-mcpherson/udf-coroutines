# :ui Module — Jetpack Compose Screen Helpers

The **ui** module provides Compose screen binding helpers that connect `ViewModelFeature` instances to composable UI. It depends on `:arch-android`.

**Package:** `com.composure.ui`

## Components

### `Screen<STATE, EVENT, EFFECT, VIEW_MODEL>`
Interface defining a composable screen that receives a `VIEW_MODEL` instance. Consumers only need to implement `invoke(viewModel: VIEW_MODEL)`.

### `StandardScreen<STATE, EVENT, EFFECT, VIEW_MODEL>`
Abstract base class implementing `Screen`. Subclasses implement `Content(feature)` to define the UI.

Provides two invocation styles:
1. **Reified inline** — `invoke<MyViewModel>()` resolves the ViewModel automatically via `viewModel<T>()`. Works for zero-arg constructors or DI-provided ViewModels.
2. **Explicit** — `invoke(viewModel)` passes a ViewModel instance directly (useful for tests and previews).

## Usage

```kotlin
object MyScreen : StandardScreen<State, Event, Effect, MyViewModel>() {

    @Composable
    override fun Content(feature: Feature<State, Event, Effect>) {
        val state by feature.state.collectAsState()
        LaunchedEffect(Unit) {
            feature.effects.collect { effect ->
                // handle side effects
            }
        }
        // Build UI using state and feature::process
    }
}

// In your NavHost:
MyScreen<MyViewModel>()   // resolves ViewModel automatically
```
