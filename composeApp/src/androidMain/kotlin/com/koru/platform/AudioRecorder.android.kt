package com.koru.platform

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

/**
 * Android implementation of [AudioRecorder].
 *
 * Uses [AudioRecord] to capture raw audio data and saves it to a temporary file.
 */
actual class AudioRecorder actual constructor() {

    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private var recordingJob: Job? = null
    private var outputFile: File? = null

    /**
     * Starts recording audio.
     * @return true if recording started successfully, false otherwise.
     */
    actual fun startRecording(): Boolean {
        if (isRecording) return false

        // In a real app, you MUST ensure RECORD_AUDIO permission is granted before this point.
        val sampleRate = 44100
        val channelConfig = AudioFormat.CHANNEL_IN_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                return false
            }

            // Using a temporary file for the spike
            outputFile = File.createTempFile("koru_record_", ".pcm")
            
            audioRecord?.startRecording()
            isRecording = true

            recordingJob = CoroutineScope(Dispatchers.IO).launch {
                writeAudioDataToFile(bufferSize)
            }

            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    /**
     * Stops recording and returns the file path or URI of the recorded audio.
     * @return the path to the recorded audio file, or null if recording failed.
     */
    actual fun stopRecording(): String? {
        if (!isRecording) return null

        isRecording = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        
        recordingJob?.cancel()
        recordingJob = null

        return outputFile?.absolutePath
    }

    private fun writeAudioDataToFile(bufferSize: Int) {
        val data = ByteArray(bufferSize)
        val os = FileOutputStream(outputFile)

        try {
            while (isRecording) {
                val read = audioRecord?.read(data, 0, bufferSize) ?: 0
                if (read > 0) {
                    os.write(data, 0, read)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                os.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
