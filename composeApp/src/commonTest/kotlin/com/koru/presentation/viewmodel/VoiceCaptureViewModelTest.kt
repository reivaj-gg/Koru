package com.koru.presentation.viewmodel

import app.cash.turbine.test
import com.koru.domain.model.Trace
import com.koru.domain.repository.PermissionHelper
import com.koru.domain.repository.VoiceCaptureRepository
import com.koru.domain.usecase.SaveTraceUseCase
import com.koru.domain.usecase.StartVoiceCaptureUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

// ─── Fakes ────────────────────────────────────────────────────────────────────

private class FakePermissionHelper(
    private val hasPermission: Boolean = true,
    private val grantOnRequest: Boolean = true,
) : PermissionHelper {
    override fun hasMicrophonePermission(): Boolean = hasPermission

    override suspend fun requestMicrophonePermission(): Boolean = grantOnRequest
}

private class FakeVoiceCaptureRepository : VoiceCaptureRepository {
    override fun startCapture(): Flow<com.koru.domain.model.TranscriptionState> = emptyFlow()

    override fun stopCapture() = Unit
}

private class FakeSaveTraceRepository : com.koru.domain.repository.TraceRepository {
    private val saved = mutableListOf<Trace>()
    var shouldFail: Boolean = false

    fun savedTraces(): List<Trace> = saved.toList()

    override fun observeAll(): Flow<List<Trace>> = kotlinx.coroutines.flow.flowOf(saved.toList())

    override suspend fun save(trace: Trace): Result<String> {
        kotlinx.coroutines.delay(10)
        if (shouldFail) return Result.failure(RuntimeException("Storage error"))
        saved.add(trace)
        return Result.success(trace.id)
    }

    override suspend fun search(
        semanticQuery: String,
        limit: Int,
    ): Result<List<Trace>> = Result.success(emptyList())

    override suspend fun delete(traceId: String): Result<Unit> {
        saved.removeAll { it.id == traceId }
        return Result.success(Unit)
    }

    override suspend fun getPendingSyncs(): Result<List<Trace>> {
        return Result.success(emptyList())
    }

    override suspend fun markAsSynced(traceId: String): Result<Unit> {
        return Result.success(Unit)
    }
}

// ─── Helper ───────────────────────────────────────────────────────────────────

private fun buildViewModel(
    permissionHelper: PermissionHelper = FakePermissionHelper(),
    saveRepository: FakeSaveTraceRepository = FakeSaveTraceRepository(),
): Triple<VoiceCaptureViewModel, FakeSaveTraceRepository, FakeVoiceCaptureRepository> {
    val captureRepo = FakeVoiceCaptureRepository()
    val vm =
        VoiceCaptureViewModel(
            startVoiceCaptureUseCase = StartVoiceCaptureUseCase(captureRepo),
            permissionHelper = permissionHelper,
            saveTraceUseCase = SaveTraceUseCase(saveRepository),
        )
    return Triple(vm, saveRepository, captureRepo)
}

// ─── Tests ────────────────────────────────────────────────────────────────────

class VoiceCaptureViewModelTest : com.koru.presentation.utils.MainDispatcherRule() {
    @Test
    fun given_valid_transcription_when_SaveTranscription_then_trace_is_persisted_and_TraceSaved_is_emitted() =
        runTest {
            val (vm, repo) = buildViewModel()

            vm.effects.test {
                vm.handleIntent(VoiceIntent.SaveTranscription("I felt anxious during the meeting"))

                val effect = awaitItem()
                assertIs<VoiceEffect.TraceSaved>(effect)

                assertEquals(1, repo.savedTraces().size)
                assertEquals("I felt anxious during the meeting", repo.savedTraces().first().content)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun given_valid_transcription_when_save_completes_then_state_resets_to_initial() =
        runTest {
            val (vm) = buildViewModel()

            vm.state.test {
                awaitItem() // initial state

                vm.handleIntent(VoiceIntent.SaveTranscription("Something meaningful happened today"))

                val savingState = awaitItem()
                assertTrue(savingState.isSaving)

                val resetState = awaitItem()
                assertFalse(resetState.isSaving)
                assertEquals("", resetState.transcription)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun given_transcription_shorter_than_3_chars_when_SaveTranscription_then_ShowToast_is_emitted_and_nothing_is_saved() =
        runTest {
            val (vm, repo) = buildViewModel()

            vm.effects.test {
                vm.handleIntent(VoiceIntent.SaveTranscription("hi"))

                val effect = awaitItem()
                assertIs<VoiceEffect.ShowToast>(effect)
                assertEquals("Transcription too short to save", effect.message)

                assertTrue(repo.savedTraces().isEmpty())

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun given_blank_transcription_when_SaveTranscription_then_ShowToast_is_emitted_and_nothing_is_saved() =
        runTest {
            val (vm, repo) = buildViewModel()

            vm.effects.test {
                vm.handleIntent(VoiceIntent.SaveTranscription("  "))

                val effect = awaitItem()
                assertIs<VoiceEffect.ShowToast>(effect)
                assertTrue(repo.savedTraces().isEmpty())

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun given_storage_failure_when_SaveTranscription_then_ShowToast_with_error_and_isSaving_resets() =
        runTest {
            val failingRepo = FakeSaveTraceRepository().also { it.shouldFail = true }
            val (vm) = buildViewModel(saveRepository = failingRepo)

            vm.effects.test {
                vm.handleIntent(VoiceIntent.SaveTranscription("Something important happened"))

                val effect = awaitItem()
                assertIs<VoiceEffect.ShowToast>(effect)
                assertTrue(effect.message.isNotBlank())

                cancelAndIgnoreRemainingEvents()
            }

            assertFalse(vm.state.value.isSaving)
        }

    @Test
    fun given_isSaving_true_when_SaveTranscription_then_second_save_is_ignored() =
        runTest {
            val (vm, repo) = buildViewModel()

            vm.effects.test {
                vm.handleIntent(VoiceIntent.SaveTranscription("First valid trace to save"))
                // Second intent fired before first completes — should be ignored
                vm.handleIntent(VoiceIntent.SaveTranscription("Duplicate save attempt"))

                val effect = awaitItem()
                assertIs<VoiceEffect.TraceSaved>(effect)
                cancelAndIgnoreRemainingEvents()
            }

            assertEquals(1, repo.savedTraces().size)
        }

    @Test
    fun given_permission_denied_when_StartRecording_then_PERMISSION_DENIED_error_is_set_in_state() =
        runTest {
            val noPermission = FakePermissionHelper(hasPermission = false, grantOnRequest = false)
            val (vm) = buildViewModel(permissionHelper = noPermission)

            vm.state.test {
                awaitItem() // initial

                vm.handleIntent(VoiceIntent.StartRecording)

                val errorState = awaitItem()
                assertEquals(com.koru.domain.model.VoiceError.PERMISSION_DENIED, errorState.activeError)

                cancelAndIgnoreRemainingEvents()
            }
        }
}
