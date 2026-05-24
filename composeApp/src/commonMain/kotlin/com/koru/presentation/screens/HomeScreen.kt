package com.koru.presentation.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.koru.domain.model.EmotionTag
import com.koru.presentation.model.VisualNode
import com.koru.presentation.utils.TreeLayoutCalculator
import com.koru.presentation.viewmodel.HomeEffect
import com.koru.presentation.viewmodel.HomeIntent
import com.koru.presentation.viewmodel.HomeViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * Main screen that renders the trace tree.
 *
 * This is a Dumb UI that only observes [HomeState] and draws nodes using
 * an optimized Canvas to avoid recompositions. All hit-testing is delegated
 * mathematically to [TreeLayoutCalculator].
 */
@Suppress("ktlint:standard:function-naming")
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    calculator: TreeLayoutCalculator = koinInject(),
    onNavigateToTrace: (String) -> Unit = {},
    onNavigateToCapture: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.handleIntent(HomeIntent.LoadTree)
        viewModel.effects.collect { effect ->
            when (effect) {
                is HomeEffect.NavigateToTraceDetail -> onNavigateToTrace(effect.traceId)
                is HomeEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                // If OpenCapture intent gets an effect (optional), handle it here
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            androidx.compose.material3.FloatingActionButton(onClick = onNavigateToCapture) {
                Text("+") // Simple placeholder icon
            }
        },
    ) { paddingValues ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            if (state.isLoading && state.nodes.isEmpty()) {
                CircularProgressIndicator()
            } else if (state.error != null && state.nodes.isEmpty()) {
                Text(
                    text = state.error ?: "Error desconocido",
                    color = MaterialTheme.colorScheme.error,
                )
            } else {
                TreeCanvas(
                    nodes = state.nodes,
                    branches = state.branches,
                    calculator = calculator,
                    onNodeTap = { traceId ->
                        viewModel.handleIntent(HomeIntent.TapNode(traceId))
                    },
                )
            }
        }
    }
}

/**
 * Internal component isolating Canvas rendering to maximize performance
 * and minimize state reads that cause jank.
 */
@Suppress("ktlint:standard:function-naming")
@Composable
private fun TreeCanvas(
    nodes: List<VisualNode>,
    branches: List<com.koru.presentation.utils.BezierCurve>,
    calculator: TreeLayoutCalculator,
    onNodeTap: (String) -> Unit,
) {
    // Pure visual mapping from emotions to colors
    val colorMap =
        mapOf(
            EmotionTag.CLARITY to Color(0xFF4CAF50),
            EmotionTag.TENSION to Color(0xFFF44336),
            EmotionTag.SURPRISE to Color(0xFFFFEB3B),
            EmotionTag.RESISTANCE to Color(0xFFFF9800),
            EmotionTag.GRATITUDE to Color(0xFF03A9F4),
        )

    val defaultColor = Color.Gray
    val branchColor = Color.LightGray

    Canvas(
        modifier =
            Modifier
                .fillMaxSize()
                .pointerInput(nodes) {
                    detectTapGestures { offset ->
                        // Strict delegation of hit-testing to the pure mathematical class.
                        // Zero math in the UI.
                        val hitTraceId = calculator.findHitNode(offset.x, offset.y, nodes)
                        if (hitTraceId != null) {
                            onNodeTap(hitTraceId)
                        }
                    }
                },
    ) {
        if (nodes.isEmpty()) return@Canvas

        // 1. Draw branches (smooth Bézier curves between nodes)
        if (branches.isNotEmpty()) {
            val path = Path()
            path.moveTo(branches.first().startX, branches.first().startY)

            for (curve in branches) {
                path.cubicTo(
                    x1 = curve.cp1X,
                    y1 = curve.cp1Y,
                    x2 = curve.cp2X,
                    y2 = curve.cp2Y,
                    x3 = curve.endX,
                    y3 = curve.endY,
                )
            }

            drawPath(
                path = path,
                color = branchColor,
                style = Stroke(width = 8f),
            )
        }

        // 2. Draw nodes
        for (node in nodes) {
            val nodeColor = node.emotionTag?.let { colorMap[it] } ?: defaultColor

            drawCircle(
                color = nodeColor,
                radius = node.radius,
                center = Offset(node.position.x, node.position.y),
            )
        }
    }
}
