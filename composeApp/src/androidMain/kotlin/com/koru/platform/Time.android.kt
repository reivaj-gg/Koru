package com.koru.platform

import kotlinx.datetime.Instant
import kotlinx.datetime.Clock

actual fun getCurrentInstant(): Instant = Clock.System.now()
