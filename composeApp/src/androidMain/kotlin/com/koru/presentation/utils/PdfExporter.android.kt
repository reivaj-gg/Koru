package com.koru.presentation.utils

import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.koru.presentation.model.VisualNode
import java.io.FileOutputStream

/**
 * Android implementation of [PdfExporter] using native [PdfDocument].
 */
actual class PdfExporter actual constructor() {
    /**
     * Exports a list of [VisualNode]s to a PDF file at the specified [destinationPath].
     *
     * @param nodes The nodes to draw.
     * @param destinationPath The absolute path where the PDF will be saved.
     * @return true if successful, false otherwise.
     */
    actual fun exportTree(
        nodes: List<VisualNode>,
        destinationPath: String,
    ): Boolean {
        return try {
            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas

            val circlePaint =
                Paint().apply {
                    color = Color.DKGRAY
                    style = Paint.Style.FILL
                    isAntiAlias = true
                }

            val textPaint =
                Paint().apply {
                    color = Color.WHITE
                    textSize = 10f
                    textAlign = Paint.Align.CENTER
                    isAntiAlias = true
                }

            nodes.forEach { node ->
                val x = node.position.x
                val y = node.position.y
                canvas.drawCircle(x, y, node.radius, circlePaint)
                canvas.drawText(node.contentSnippet, x, y + 4f, textPaint)
            }

            document.finishPage(page)
            FileOutputStream(destinationPath).use { out ->
                document.writeTo(out)
            }
            document.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
