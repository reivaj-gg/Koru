package com.koru.domain.utils

import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter

actual class NotificationScheduler actual constructor() {
    actual fun scheduleNotification(
        id: String,
        title: String,
        body: String,
        delayMillis: Long,
    ) {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        val content =
            UNMutableNotificationContent().apply {
                setTitle(title)
                setBody(body)
            }
        val trigger =
            UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
                timeInterval = delayMillis / 1000.0,
                repeats = false,
            )
        val request =
            UNNotificationRequest.requestWithIdentifier(
                identifier = id,
                content = content,
                trigger = trigger,
            )
        center.addNotificationRequest(request) { error ->
            if (error != null) {
                println("Failed to schedule notification: ${error.localizedDescription}")
            }
        }
    }
}
