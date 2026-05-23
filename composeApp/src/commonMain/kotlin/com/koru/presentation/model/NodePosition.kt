package com.koru.presentation.model

/**
 * Represents the absolute 2D visual position of a node on the Canvas.
 *
 * This class is purely mathematical and has no UI framework dependencies.
 *
 * @property x The horizontal coordinate.
 * @property y The vertical coordinate.
 */
data class NodePosition(
    val x: Float,
    val y: Float,
) {
    init {
        require(!x.isNaN()) { "x coordinate cannot be NaN" }
        require(!y.isNaN()) { "y coordinate cannot be NaN" }
    }
}
