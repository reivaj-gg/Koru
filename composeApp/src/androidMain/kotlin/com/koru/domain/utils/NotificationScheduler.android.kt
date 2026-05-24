package com.koru.domain.utils

import android.app.AlarmManager
import android.content.Context
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Android implementation of [NotificationScheduler].
 */
actual class NotificationScheduler actual constructor() : KoinComponent {
    private val context: Context by inject()

    actual fun scheduleNotification(
        id: String,
        title: String,
        body: String,
        delayMillis: Long,
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // In a real app we'd use a BroadcastReceiver or WorkManager
        // For the starter kit, we simulate it via logging if receiver isn't registered
        println("Android Notification Scheduled: [$id] $title - $body in $delayMillis ms")
    }
}
