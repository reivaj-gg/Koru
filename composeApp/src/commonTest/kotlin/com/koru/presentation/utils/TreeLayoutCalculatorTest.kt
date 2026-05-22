package com.koru.presentation.utils

import com.koru.domain.model.EmotionTag
import com.koru.domain.model.Trace
import com.koru.presentation.model.NodePosition
import com.koru.presentation.model.VisualNode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Pruebas unitarias para la lógica matemática pura del árbol.
 * Sigue el patrón TDD (Red -> Green -> Refactor).
 */
class TreeLayoutCalculatorTest {
    private val calculator = TreeLayoutCalculator()
    private val fakeTraces =
        listOf(
            Trace(
                id = "trace-1",
                content = "Primer pensamiento",
                context = null,
                capturedAt = kotlin.time.Instant.fromEpochMilliseconds(0),
                emotionTag = EmotionTag.CLARITY,
            ),
            Trace(
                id = "trace-2",
                content = "Segundo pensamiento un poco más largo para probar el truncado",
                context = null,
                capturedAt = kotlin.time.Instant.fromEpochMilliseconds(0),
                emotionTag = EmotionTag.RESISTANCE,
            ),
        )

    @Test
    fun given_traces_when_calculateLayout_then_assigns_valid_positions() {
        val canvasWidth = 1000f
        val startY = 100f
        val ySpacing = 200f
        val radius = 50f

        val visualNodes =
            calculator.calculateLayout(
                traces = fakeTraces,
                canvasWidth = canvasWidth,
                startY = startY,
                ySpacing = ySpacing,
                nodeRadius = radius,
            )

        assertEquals(2, visualNodes.size)

        // Primer nodo (index 0): sin(0) = 0. normalizedX = 0.5. x = 500
        val node1 = visualNodes[0]
        assertEquals("trace-1", node1.traceId)
        assertEquals(500f, node1.position.x)
        assertEquals(100f, node1.position.y)
        assertEquals(radius, node1.radius)
        assertEquals(EmotionTag.CLARITY, node1.emotionTag)
        assertEquals("Primer pensamiento", node1.contentSnippet)

        // Segundo nodo (index 1): sin(1) = 0.84147. normalizedX = (0.84147 * 0.25) + 0.5 = 0.7103. x = ~710.3
        val node2 = visualNodes[1]
        assertEquals("trace-2", node2.traceId)
        assertEquals(100f + 200f, node2.position.y) // 300f
        assertEquals(EmotionTag.RESISTANCE, node2.emotionTag)
        // El snippet debe estar truncado a 30 caracteres
        assertEquals(30, node2.contentSnippet.length)
        assertEquals("Segundo pensamiento un poco má", node2.contentSnippet)
    }

    @Test
    fun given_exact_touch_when_findHitNode_then_returns_traceId() {
        val visualNodes =
            listOf(
                VisualNode("trace-1", "A", NodePosition(100f, 100f), 50f, null),
            )

        // Toque en el centro exacto
        val hitId = calculator.findHitNode(100f, 100f, visualNodes)
        assertEquals("trace-1", hitId)

        // Toque en el borde (100 + 50)
        val hitIdEdge = calculator.findHitNode(150f, 100f, visualNodes)
        assertEquals("trace-1", hitIdEdge)
    }

    @Test
    fun given_outside_touch_when_findHitNode_then_returns_null() {
        val visualNodes =
            listOf(
                VisualNode("trace-1", "A", NodePosition(100f, 100f), 50f, null),
            )

        // Toque fuera del radio (100 + 51)
        val hitId = calculator.findHitNode(151f, 100f, visualNodes)
        assertNull(hitId)
    }

    @Test
    fun given_overlapping_nodes_when_findHitNode_then_returns_top_most() {
        val visualNodes =
            listOf(
                VisualNode("trace-bottom", "A", NodePosition(100f, 100f), 50f, null),
                VisualNode("trace-top", "B", NodePosition(120f, 100f), 50f, null),
            )

        // Un toque en X=110 impacta a ambos nodos. Debería devolver el último de la lista (top-most).
        val hitId = calculator.findHitNode(110f, 100f, visualNodes)
        assertNotNull(hitId)
        assertEquals("trace-top", hitId)
    }
}
