package com.koru.presentation.utils

import com.koru.domain.model.Trace
import com.koru.presentation.model.NodePosition
import com.koru.presentation.model.VisualNode
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Pure mathematical class responsible for calculating node positions
 * and resolving spatial interactions (hit-testing).
 *
 * Zero dependencies on UI or Compose, guaranteeing that the entire layout is
 * deterministically unit testable.
 */
class TreeLayoutCalculator {
    /**
     * Calculates the spatial positions of a list of traces simulating an organic pattern (sine curve).
     *
     * @param traces List of [Trace] from the domain.
     * @param canvasWidth Available width of the canvas.
     * @param startY Top margin where the tree begins.
     * @param ySpacing Vertical distance between each node.
     * @param nodeRadius Uniform radius for the generated nodes.
     * @return List of [VisualNode] with absolute mathematical coordinates.
     */
    fun calculateLayout(
        traces: List<Trace>,
        canvasWidth: Float,
        startY: Float = 150f,
        ySpacing: Float = 250f,
        nodeRadius: Float = 60f,
    ): List<VisualNode> {
        return traces.mapIndexed { index, trace ->
            // Normalize X using a sine function to generate a smooth zig-zag curve.
            // Multiply by 0.25f so the oscillation stays between 25% and 75% of the width.
            val normalizedX = (sin(index.toFloat()) * 0.25f) + 0.5f
            val x = canvasWidth * normalizedX
            val y = startY + (index * ySpacing)

            VisualNode(
                traceId = trace.id,
                contentSnippet = trace.content.take(SNIPPET_LENGTH),
                position = NodePosition(x, y),
                radius = nodeRadius,
                emotionTag = trace.emotionTag,
            )
        }
    }

    /**
     * Resolves hit-testing using Euclidean distance.
     * Iterating in reverse, assuming that the last elements of the list are drawn on top.
     *
     * @param touchX Horizontal coordinate of the touch event.
     * @param touchY Vertical coordinate of the touch event.
     * @param nodes List of visual nodes currently on screen.
     * @return The [VisualNode.traceId] of the touched node, or null if empty space was touched.
     */
    fun findHitNode(
        touchX: Float,
        touchY: Float,
        nodes: List<VisualNode>,
    ): String? {
        for (node in nodes.reversed()) {
            val dx = touchX - node.position.x
            val dy = touchY - node.position.y
            val distance = sqrt((dx * dx) + (dy * dy))

            if (distance <= node.radius) {
                return node.traceId
            }
        }
        return null
    }

    private companion object {
        private const val SNIPPET_LENGTH = 30
    }
}
