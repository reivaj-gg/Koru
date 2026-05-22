package com.koru.domain.usecase

import com.koru.domain.model.TranscriptionState
import com.koru.domain.repository.VoiceCaptureRepository
import kotlinx.coroutines.flow.Flow

/**
 * Orquestador de la captura de voz en el dominio.
 *
 * Encapsula la lógica de inicio de captura delegando en el repositorio
 * correspondiente inyectado por Koin.
 *
 * @param repository La implementación de [VoiceCaptureRepository] en la capa Data.
 */
class StartVoiceCaptureUseCase(
    private val repository: VoiceCaptureRepository,
) {
    /**
     * Inicia el flujo de captura y reconocimiento de voz.
     *
     * @return Un stream de [TranscriptionState] con actualizaciones en tiempo real.
     */
    operator fun invoke(): Flow<TranscriptionState> = repository.startCapture()
}
