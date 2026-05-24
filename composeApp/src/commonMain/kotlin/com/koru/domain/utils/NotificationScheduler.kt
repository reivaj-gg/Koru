package com.koru.domain.utils

/**
 * Schedules local notifications natively to support habit stacking and AI pattern alerts.
 */
expect class NotificationScheduler() {
    /**
     * Schedules a local notification.
     *
     * @param id The unique identifier for the notification.
     * @param title The notification title.
     * @param body The notification body.
     * @param delayMillis The delay in milliseconds before the notification triggers.
     */
    fun scheduleNotification(
        id: String,
        title: String,
        body: String,
        delayMillis: Long,
    )
}
