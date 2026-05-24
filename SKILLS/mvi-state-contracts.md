---
id: mvi-state-contracts
name: MVI State Contracts
description: Strict MVI contracts for ViewModels and UI
---

# MVI State Contracts

Every screen in the application must define a strict MVI contract.

## 1. State (Immutable Truth)
```kotlin
data class ScreenState(
    val isLoading: Boolean = false,
    val data: List<Item> = emptyList(), // Always use immutable collections
    val error: String? = null
)
```

## 2. Intent (User Actions)
```kotlin
sealed class ScreenIntent {
    data object LoadData : ScreenIntent()
    data class TapItem(val id: String) : ScreenIntent()
}
```

## 3. Effect (One-time Events)
```kotlin
sealed class ScreenEffect {
    data class NavigateToDetail(val id: String) : ScreenEffect()
    data class ShowToast(val message: String) : ScreenEffect()
}
```

## ViewModel Rules
- The ViewModel holds `private val _state = MutableStateFlow(ScreenState())` and exposes `val state = _state.asStateFlow()`.
- It holds `private val _effects = MutableSharedFlow<ScreenEffect>()` and exposes `val effects = _effects.asSharedFlow()`.
- All interactions go through `fun handleIntent(intent: ScreenIntent)`.
- UI must **never** call business logic directly or mutate state.
