package com.koru.presentation.viewmodel

import com.koru.domain.model.Insight
import com.koru.presentation.model.VisualNode
import com.koru.presentation.utils.BezierCurve

/**
 * Immutable state of the main screen.
 *
 * Exclusively uses immutable collections (implemented here with standard Kotlin [List])
 * and immutable primitive types via [val].
 *
 * @property nodes Immutable list of mathematically processed nodes ready to be drawn.
 * @property branches Immutable list of calculated Bézier curves connecting the nodes.
 * @property isLoading Indicates if the tree is currently loading or analyzing data.
 * @property error Error message to display if data loading fails.
 */
data class HomeState(
    val nodes: List<VisualNode> = emptyList(),
    val branches: List<BezierCurve> = emptyList(),
    val latestInsight: Insight? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
) {
    init {
        require(error == null || error.isNotBlank()) { "Error message cannot be blank if provided" }
    }
}

/**
 * Exclusive intents that the user can dispatch from HomeScreen.
 */
sealed class HomeIntent {
    /**
     * Triggered when tapping a specific node calculated mathematically by hit-testing.
     *
     * @property traceId The identifier of the trace impacted by the user.
     */
    data class TapNode(
        val traceId: String,
    ) : HomeIntent()

    /**
     * Triggered when requesting to start manual or voice capture.
     */
    data object OpenCapture : HomeIntent()

    /**
     * Triggered when the screen needs to initialize or reload data from the database.
     */
    data object LoadTree : HomeIntent()

    /**
     * Triggered when the user requests an AI insight based on their recent traces.
     *
     * @property traceId The context trace id.
     * @property query The semantic query to fetch traces.
     */
    data class RequestInsight(
        val traceId: String,
        val query: String,
    ) : HomeIntent()
}

/**
 * Immutable side effects that the UI must consume exactly once.
 */
sealed class HomeEffect {
    /**
     * Instructs the UI to navigate to the detail screen of a particular node.
     *
     * @property traceId The id of the trace to examine.
     */
    data class NavigateToTraceDetail(
        val traceId: String,
    ) : HomeEffect()

    /**
     * Displays a temporary notice or banner detailing a non-fatal issue.
     *
     * @property message The localized or direct text of the error.
     */
    data class ShowError(
        val message: String,
    ) : HomeEffect()
}
