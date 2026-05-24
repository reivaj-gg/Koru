package com.koru.presentation.utils

import com.koru.presentation.model.VisualNode

/**
 * Native PDF exporter for the Tree Canvas.
 * Uses expect/actual to leverage platform-specific PDF generation APIs.
 */
expect class PdfExporter() {
    /**
     * Exports a list of [VisualNode]s to a PDF file at the specified [destinationPath].
     *
     * @param nodes The nodes to draw.
     * @param destinationPath The absolute path where the PDF will be saved.
     * @return true if successful, false otherwise.
     */
    fun exportTree(
        nodes: List<VisualNode>,
        destinationPath: String,
    ): Boolean
}
