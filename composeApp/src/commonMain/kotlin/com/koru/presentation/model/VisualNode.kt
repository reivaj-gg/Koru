package com.koru.presentation.model

import com.koru.domain.model.EmotionTag

/**
 * Represents a [com.koru.domain.model.Trace] mapped to its exact visual coordinates.
 *
 * @property traceId The unique identifier of the original trace.
 * @property contentSnippet A short snippet of the text for preview.
 * @property position Absolute calculated coordinates.
 * @property radius Interaction and drawing radius of the node.
 * @property emotionTag Associated emotion tag, used to determine visual color/style.
 */
data class VisualNode(
    val traceId: String,
    val contentSnippet: String,
    val position: NodePosition,
    val radius: Float,
    val emotionTag: EmotionTag?,
) {
    init {
        require(traceId.isNotBlank()) { "VisualNode traceId must not be blank" }
        require(contentSnippet.isNotBlank()) { "VisualNode contentSnippet must not be blank" }
        require(radius > 0f) { "VisualNode radius must be positive" }
    }
}
