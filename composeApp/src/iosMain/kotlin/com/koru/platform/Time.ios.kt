package com.koru.platform

import kotlinx.datetime.Instant
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

actual fun getCurrentInstant(): Instant {
    val epochSeconds = NSDate().timeIntervalSince1970
    return Instant.fromEpochMilliseconds((epochSeconds * 1000).toLong())
}
