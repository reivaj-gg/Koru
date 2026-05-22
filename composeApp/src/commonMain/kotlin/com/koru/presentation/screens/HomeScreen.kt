package com.koru.presentation.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
 * Pantalla principal que renderiza el árbol de trazos.
 *
 * Es una UI "tonta" (Dumb UI) que se limita a observar [TreeState] y dibujar los nodos usando
 * un Canvas optimizado para evitar recomposiciones. Todo el hit-testing se delega
 * matemáticamente a [TreeLayoutCalculator].
 */
@Suppress("FunctionName")
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    calculator: TreeLayoutCalculator = koinInject(),
    onNavigateToTrace: (String) -> Unit = {},
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.handleIntent(HomeIntent.LoadTree)
        viewModel.effects.collect { effect ->
            when (effect) {
                is HomeEffect.NavigateToTraceDetail -> onNavigateToTrace(effect.traceId)
                is HomeEffect.ShowError -> {
                    // Aquí se integraría con el SnackbarHost del Scaffold
                }
            }
        }
    }

    Scaffold { paddingValues ->
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
 * Componente interno que aísla el renderizado del Canvas para maximizar el rendimiento
 * y minimizar las lecturas de estado que provocan jank.
 */
@Suppress("FunctionName")
@Composable
private fun TreeCanvas(
    nodes: List<VisualNode>,
    calculator: TreeLayoutCalculator,
    onNodeTap: (String) -> Unit,
) {
    // Mapeo puro y visual de emociones a colores
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
                        // Delegación estricta del hit-testing a la clase matemática pura.
                        // Cero matemática en la UI.
                        val hitTraceId = calculator.findHitNode(offset.x, offset.y, nodes)
                        if (hitTraceId != null) {
                            onNodeTap(hitTraceId)
                        }
                    }
                },
    ) {
        if (nodes.isEmpty()) return@Canvas

        // 1. Dibujar ramas (curvas Bézier suaves entre nodos)
        if (nodes.size > 1) {
            val path = Path()
            path.moveTo(nodes.first().position.x, nodes.first().position.y)

            for (i in 1 until nodes.size) {
                val prev = nodes[i - 1]
                val current = nodes[i]

                val controlPointY = (prev.position.y + current.position.y) / 2f

                path.cubicTo(
                    x1 = prev.position.x,
                    y1 = controlPointY,
                    x2 = current.position.x,
                    y2 = controlPointY,
                    x3 = current.position.x,
                    y3 = current.position.y,
                )
            }

            drawPath(
                path = path,
                color = branchColor,
                style = Stroke(width = 8f),
            )
        }

        // 2. Dibujar nodos
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
