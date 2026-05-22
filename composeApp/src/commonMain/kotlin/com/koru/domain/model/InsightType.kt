package com.koru.domain.model

/**
 * Classifies the nature of an AI-generated insight.
 *
 * The type determines both when an insight is eligible to be generated
 * and how it should be presented to the user.
 *
 * @see Insight
 * @see com.koru.domain.usecase.GetInsightUseCase
 */
enum class InsightType {
    /**
     * A short reflection returned immediately after a trace is saved.
     * Eligible after any single trace — no minimum history required.
     */
    IMMEDIATE,

    /**
     * A recurring thought-habit pattern detected across the user's trace history.
     * Only eligible once the user has accumulated at least 7 traces.
     */
    PATTERN,

    /**
     * An alternative perspective on the captured reaction.
     * Offers a reframe without diagnosing or labelling the user.
     */
    REFRAME,
}
