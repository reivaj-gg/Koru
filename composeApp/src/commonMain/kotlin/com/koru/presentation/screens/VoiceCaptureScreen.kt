package com.koru.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.koru.presentation.viewmodel.VoiceCaptureViewModel
import com.koru.presentation.viewmodel.VoiceEffect
import com.koru.presentation.viewmodel.VoiceIntent
import org.koin.compose.viewmodel.koinViewModel

/**
 * Screen for recording voice traces and reviewing/editing the transcription.
 */
@Suppress("ktlint:standard:function-naming")
@Composable
fun VoiceCaptureScreen(
    viewModel: VoiceCaptureViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is VoiceEffect.TraceSaved -> onNavigateBack()
                is VoiceEffect.ShowToast -> snackbarHostState.showSnackbar(effect.message)
                is VoiceEffect.RequestPermissions -> {
                    // Handled inside ViewModel via MOKO Permissions or Platform Helpers.
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            if (state.transcription.isNotEmpty()) {
                var text by remember(state.transcription) { mutableStateOf(state.transcription) }
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Your Thought") },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f),
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.handleIntent(VoiceIntent.SaveTranscription(text)) },
                    enabled = !state.isSaving,
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        Text("Save Trace")
                    }
                }
            } else {
                Text(
                    text = if (state.isRecording) "Listening..." else "Hold to Record",
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(modifier = Modifier.height(32.dp))
                Box(
                    modifier =
                        Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(if (state.isRecording) Color.Red else Color.LightGray)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        viewModel.handleIntent(VoiceIntent.StartRecording)
                                        tryAwaitRelease()
                                        viewModel.handleIntent(VoiceIntent.StopRecording)
                                    },
                                )
                            },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(if (state.isRecording) "Release" else "Hold")
                }
            }
        }
    }
}
