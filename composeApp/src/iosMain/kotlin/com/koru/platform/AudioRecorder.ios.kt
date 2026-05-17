package com.koru.platform

import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.AVAudioRecorder
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayAndRecord
import platform.AVFAudio.setActive
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import com.koru.platform.getCurrentInstant

/**
 * iOS implementation of [AudioRecorder].
 *
 * Uses [AVAudioRecorder] from AVFoundation to capture audio.
 */
@OptIn(ExperimentalForeignApi::class)
actual class AudioRecorder actual constructor() {

    private var audioRecorder: AVAudioRecorder? = null
    private var isRecording = false
    private var currentFilePath: String? = null

    /**
     * Starts recording audio.
     * @return true if recording started successfully, false otherwise.
     */
    actual fun startRecording(): Boolean {
        if (isRecording) return false

        try {
            val session = AVAudioSession.sharedInstance()
            session.setCategory(AVAudioSessionCategoryPlayAndRecord, null)
            session.setActive(true, null)

            val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = true,
                error = null
            )
            val fileName = "koru_record_${getCurrentInstant().toEpochMilliseconds()}.m4a"
            val fileUrl = documentDirectory?.URLByAppendingPathComponent(fileName) ?: return false
            currentFilePath = fileUrl.path

            val settings = mapOf<Any?, Any>(
                // In a real app, define proper AVFormatIDKey and other settings
                // platform.AVFAudio.AVFormatIDKey to platform.CoreAudioTypes.kAudioFormatMPEG4AAC
            )

            audioRecorder = AVAudioRecorder(fileUrl, settings, null)
            audioRecorder?.prepareToRecord()
            val started = audioRecorder?.record() ?: false
            
            if (started) {
                isRecording = true
                return true
            }
            return false
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

        audioRecorder?.stop()
        isRecording = false

        try {
            val session = AVAudioSession.sharedInstance()
            session.setActive(false, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return currentFilePath
    }
}
