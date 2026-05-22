package com.koru.presentation.model

import com.koru.domain.model.EmotionTag

/**
 * Representa un [com.koru.domain.model.Trace] mapeado a sus coordenadas visuales exactas.
 *
 * @property traceId El identificador único del rastro original.
 * @property contentSnippet Un fragmento corto del texto para previsualizar.
 * @property position Coordenadas absolutas calculadas.
 * @property radius Radio de interacción y dibujo del nodo.
 * @property emotionTag Tag emocional asociado, usado para determinar el color/estilo visual.
 */
data class VisualNode(
    val traceId: String,
    val contentSnippet: String,
    val position: NodePosition,
    val radius: Float,
    val emotionTag: EmotionTag?,
)
