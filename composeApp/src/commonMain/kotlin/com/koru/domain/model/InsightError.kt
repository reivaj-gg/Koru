package com.koru.domain.model

/**
 * Represents the typed failure states of the AI insight pipeline.
 *
 * All errors are surfaced as [InsightError] subtypes so the UI can
 * react specifically to each condition without relying on raw exceptions.
 *
 * @see com.koru.domain.repository.InsightRepository
 * @see com.koru.domain.usecase.GetInsightUseCase
 */
sealed class InsightError {
    /**
     * The Gemini API did not respond within the configured timeout window.
     *
     * The UI should display a non-blocking message and allow the user
     * to continue using the app — the insight will not be retried automatically.
     */
    data object Timeout : InsightError()

    /**
     * The device has no active network connection.
     *
     * Insights are queued and will be generated once connectivity is restored.
     */
    data object NoConnection : InsightError()

    /**
     * An unexpected error occurred during the insight generation pipeline.
     *
     * @param message A developer-facing description of the failure cause.
     */
    data class Unknown(val message: String?) : InsightError()
}
