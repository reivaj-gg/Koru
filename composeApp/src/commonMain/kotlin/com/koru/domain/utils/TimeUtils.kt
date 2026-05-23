package com.koru.domain.utils

/**
 * Returns the current time in milliseconds since the Unix epoch.
 */
fun currentEpochMillis(): Long = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
