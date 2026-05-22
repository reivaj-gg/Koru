package com.koru.presentation.utils

import com.koru.domain.model.Trace
import com.koru.presentation.model.NodePosition
import com.koru.presentation.model.VisualNode
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Clase matemática pura responsable de calcular las posiciones de los nodos
 * y resolver interacciones espaciales (hit-testing).
 *
 * Cero dependencias de UI o Compose, garantizando que todo el layout es
 * unit testable de manera determinista.
 */
class TreeLayoutCalculator {
    /**
     * Calcula las posiciones espaciales de una lista de trazos simulando un patrón orgánico (curva sinodal).
     *
     * @param traces Lista de [Trace] provenientes del dominio.
     * @param canvasWidth Ancho disponible del canvas.
     * @param startY Margen superior donde comienza el árbol.
     * @param ySpacing Distancia vertical entre cada nodo.
     * @param nodeRadius Radio uniforme para los nodos generados.
     * @return Lista de [VisualNode] con coordenadas matemáticas absolutas.
     */
    fun calculateLayout(
        traces: List<Trace>,
        canvasWidth: Float,
        startY: Float = 150f,
        ySpacing: Float = 250f,
        nodeRadius: Float = 60f,
    ): List<VisualNode> {
        return traces.mapIndexed { index, trace ->
            // Normalizamos X usando una función seno para generar una curva zig-zag fluida.
            // Multiplicamos por 0.25f para que la oscilación se mantenga entre el 25% y 75% del ancho.
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
     * Resuelve el hit-testing utilizando distancia Euclidiana.
     * Iterando en reverso, asume que los últimos elementos de la lista se dibujan por encima.
     *
     * @param touchX Coordenada horizontal del evento táctil.
     * @param touchY Coordenada vertical del evento táctil.
     * @param nodes Lista de nodos visuales actualmente en pantalla.
     * @return El [VisualNode.traceId] del nodo tocado, o null si el toque fue al vacío.
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
