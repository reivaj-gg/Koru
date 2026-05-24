package com.koru.presentation.utils

import com.koru.presentation.model.VisualNode
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readValue
import platform.CoreGraphics.CGContextFillEllipseInRect
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGRectZero
import platform.UIKit.UIColor
import platform.UIKit.UIGraphicsBeginPDFContextToFile
import platform.UIKit.UIGraphicsBeginPDFPage
import platform.UIKit.UIGraphicsEndPDFContext
import platform.UIKit.UIGraphicsGetCurrentContext

/**
 * iOS implementation of [PdfExporter] using native [UIGraphicsBeginPDFContextToFile].
 */
actual class PdfExporter actual constructor() {
    /**
     * Exports a list of [VisualNode]s to a PDF file at the specified [destinationPath].
     *
     * @param nodes The nodes to draw.
     * @param destinationPath The absolute path where the PDF will be saved.
     * @return true if successful, false otherwise.
     */
    @OptIn(ExperimentalForeignApi::class)
    actual fun exportTree(
        nodes: List<VisualNode>,
        destinationPath: String,
    ): Boolean {
        return try {
            UIGraphicsBeginPDFContextToFile(destinationPath, CGRectZero.readValue(), null)
            UIGraphicsBeginPDFPage()

            val context = UIGraphicsGetCurrentContext()

            nodes.forEach { node ->
                val x = (node.position.x - node.radius).toDouble()
                val y = (node.position.y - node.radius).toDouble()
                val size = (node.radius * 2).toDouble()
                val rect = CGRectMake(x, y, size, size)

                UIColor.darkGrayColor.set()
                CGContextFillEllipseInRect(context, rect)
            }

            UIGraphicsEndPDFContext()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
