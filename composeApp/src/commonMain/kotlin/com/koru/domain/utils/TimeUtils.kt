package com.koru.domain.utils

import kotlinx.datetime.Clock

/**
 * Returns the current time in milliseconds since the Unix epoch.
 *
 * Uses [Clock.System] from `kotlinx-datetime` so this function lives
 * entirely in `commonMain` — no `expect/actual` needed, maximising
 * shared code (contest criterion #2).
 */
fun currentEpochMillis(): Long = Clock.System.now().toEpochMilliseconds()
