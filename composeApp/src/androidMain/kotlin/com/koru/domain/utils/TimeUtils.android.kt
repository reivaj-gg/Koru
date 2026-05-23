package com.koru.domain.utils

/**
 * Returns the current epoch time in milliseconds using Android/JVM [System.currentTimeMillis].
 */
actual fun currentEpochMillis(): Long = System.currentTimeMillis()
