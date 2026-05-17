package com.koru.presentation.viewmodel

/**
 * Immutable snapshot of [HomeScreen] UI truth.
 * Produced exclusively by [HomeViewModel].
 */
data class HomeState(
    val isCapturing: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
)

/**
 * User-initiated actions on [HomeScreen].
 * ViewModels receive intents — they never expose mutating functions directly.
 */
sealed class HomeIntent {
    data object OpenCapture : HomeIntent()
    data object CloseCapture : HomeIntent()
    data class SaveTrace(val content: String) : HomeIntent()
}

/**
 * One-time side effects emitted by [HomeViewModel].
 * Consumed exactly once by the UI (navigation, toasts, haptics).
 */
sealed class HomeEffect {
    data object TraceSaved : HomeEffect()
    data class ShowError(val message: String) : HomeEffect()
}
