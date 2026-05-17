package com.koru.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.koru.presentation.components.CaptureSheet
import com.koru.presentation.viewmodel.HomeEffect
import com.koru.presentation.viewmodel.HomeIntent
import com.koru.presentation.viewmodel.HomeViewModel

/**
 * Main dashboard screen for Koru.
 *
 * Displays the current context and allows capturing new traces.
 * Connects to [HomeViewModel] using the MVI pattern.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeEffect.TraceSaved -> {
                    snackbarHostState.showSnackbar("Trace saved successfully")
                }
                is HomeEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Koru") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.dispatch(HomeIntent.OpenCapture) }
            ) {
                // To keep it simple without adding a specific icon dependency, we'll use text or basic icon
                Text("+")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading) {
                Text("Loading...")
            } else {
                Text("Tap the + button to capture a thought.")
            }
        }

        if (state.isCapturing) {
            CaptureSheet(
                onDismiss = { viewModel.dispatch(HomeIntent.CloseCapture) },
                onSave = { content ->
                    viewModel.dispatch(HomeIntent.SaveTrace(content))
                }
            )
        }
    }
}
