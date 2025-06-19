package com.example.timemate2

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val moduleName = intent.getStringExtra("module_name") ?: "Unknown Module"
        val lecturer = intent.getStringExtra("lecturer_name") ?: "Unknown Lecturer"
        val date = intent.getStringExtra("date") ?: "Unknown Date"
        val time = intent.getStringExtra("time") ?: "Unknown Time"

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "appointment_channel"

        // Create notification channel (Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Appointment Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for appointment reminder notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent to open AppointmentActivity when notification is tapped
        val notificationIntent = Intent(context, AppointmentActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Replace with your own icon if you want
            .setContentTitle("Appointment Reminder: $moduleName")
            .setContentText("Lecturer: $lecturer\nDate: $date\nTime: $time")
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "Module: $moduleName\nLecturer: $lecturer\nDate: $date\nTime: $time"
                )
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Use a unique notification ID so multiple reminders don't overwrite each other
        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notification)
    }
}
