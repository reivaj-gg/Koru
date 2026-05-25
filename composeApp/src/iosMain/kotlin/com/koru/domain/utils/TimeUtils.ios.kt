package com.koru.domain.utils

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

/**
 * Returns the current time in milliseconds since the Unix epoch for iOS.
 */
actual fun currentEpochMillis(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()
