package com.koru.domain.utils

/**
 * Returns the current time in milliseconds since the Unix epoch for Android.
 */
actual fun currentEpochMillis(): Long = java.lang.System.currentTimeMillis()
