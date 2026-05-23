package com.koru.domain.utils

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

/**
 * Returns the current epoch time in milliseconds using iOS [NSDate].
 */
actual fun currentEpochMillis(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()
