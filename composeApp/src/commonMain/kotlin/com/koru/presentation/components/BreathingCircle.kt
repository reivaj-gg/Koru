package com.koru.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

/**
 * A reactive UI component that pulses based on audio amplitude and user interaction.
 *
 * It serves as the primary gesture target for starting and stopping voice capture.
 *
 * @param isRecording Whether the circle should show the active recording state.
 * @param amplitude Real-time audio amplitude (0.0 to 1.0) to modulate the pulse.
 * @param onHold Triggered when the user starts pressing the circle.
 * @param onRelease Triggered when the user releases the circle.
 */
@Suppress("ktlint:standard:function-naming")
@Composable
fun BreathingCircle(
    isRecording: Boolean,
    amplitude: Float,
    onHold: () -> Unit,
    onRelease: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition()

    val basePulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
    )

    // Modulate radius by both base pulse and real-time audio amplitude
    val scale =
        if (isRecording) {
            basePulse + (amplitude * 0.5f)
        } else {
            basePulse
        }

    val circleColor = if (isRecording) Color(0xFF6200EE) else Color.Gray

    Box(
        contentAlignment = Alignment.Center,
        modifier =
            modifier
                .size(200.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            onHold()
                            try {
                                awaitRelease()
                            } finally {
                                onRelease()
                            }
                        },
                    )
                },
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = (size.minDimension / 3f) * scale

            // Outer glow / aura
            drawCircle(
                color = circleColor.copy(alpha = 0.2f),
                radius = radius * 1.3f,
                style = Fill,
            )

            // Main circle
            drawCircle(
                color = circleColor,
                radius = radius,
                style = Fill,
            )

            // Border
            drawCircle(
                color = Color.White.copy(alpha = 0.5f),
                radius = radius,
                style = Stroke(width = 2.dp.toPx()),
            )
        }
    }
}
