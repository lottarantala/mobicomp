package com.example.mobicomp.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.mobicomp.R

class NotificationWorker(val context: Context) {
    private val CHANNEL_ID = "reminder_id"
    private val NOTIFICATION_ID = 1

    private fun createNotificationChannel() {
        val name = "Reminder Channel"
        val descriptionText = "Reminder Channel Description"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = descriptionText
            }
            val notificationManager = context
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun createNotification(message: String, reminderTime: String) {

        createNotificationChannel()

        val intent = Intent()
        intent.setClassName(context, "com.example.mobicomp.ui.MainActivity").apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("reminder_message", message)
            }

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
            .setContentTitle("Reminder: $message")
            .setContentText(reminderTime)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        with(NotificationManagerCompat.from(context)) {
            notify(
                NOTIFICATION_ID, notification.build()
            )
        }
    }

}