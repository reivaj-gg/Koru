package com.koru.platform

/**
 * Platform-agnostic interface for recording audio.
 *
 * Implemented natively via expect/actual using `AudioRecord` on Android
 * and `AVAudioRecorder` on iOS.
 */
expect class AudioRecorder() {
    /**
     * Starts recording audio.
     * @return true if recording started successfully, false otherwise.
     */
    fun startRecording(): Boolean

    /**
     * Stops recording and returns the file path or URI of the recorded audio.
     * @return the path to the recorded audio file, or null if recording failed.
     */
    fun stopRecording(): String?
}
