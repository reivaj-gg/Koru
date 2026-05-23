---
name: mvi-state-contracts
description: "State, Intent, and Effect patterns for ViewModels in Compose Multiplatform."
---

# MVI State Contracts

## The Principle
Unidirectional Data Flow. The UI is a dumb reflection of the `State`. The UI communicates with the ViewModel exclusively via `Intent`. The ViewModel communicates one-off events via `Effect`.

## The Triad
Every screen must define three sealed/data constructs:

1. **State (`HomeState`)**: A `data class` representing the immutable snapshot of the UI.
2. **Intent (`HomeIntent`)**: A `sealed class` representing user actions.
3. **Effect (`HomeEffect`)**: A `sealed class` for one-time side effects (navigation, toasts).

## ViewModel Implementation
```kotlin
private val _state = MutableStateFlow(HomeState())
val state: StateFlow<HomeState> = _state.asStateFlow()

private val _effects = MutableSharedFlow<HomeEffect>()
val effects: SharedFlow<HomeEffect> = _effects.asSharedFlow()

fun handleIntent(intent: HomeIntent) {
    when (intent) {
        is HomeIntent.SaveTrace -> executeSave(intent.content)
    }
}
```

Never mutate state directly from the UI. Never put business logic in the Composable.
