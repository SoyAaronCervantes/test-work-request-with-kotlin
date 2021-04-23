package com.soyaaroncervantes.demoworkmanager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlin.Exception

class UploadManager(context: Context, workerParams: WorkerParameters) : Worker( context, workerParams ) {
    override fun doWork(): Result {
        val message = inputData.getString("message") ?: "Default message"
        return try {
            // sendNotification( message,  )
            Result.success()
        } catch ( ex: Exception ) {
            Result.failure()
        }
    }

    private fun sendNotification(message: String, title: String ) {
        val notificationManager =
          applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel( "default", "Default", NotificationManager.IMPORTANCE_DEFAULT )
            notificationManager.createNotificationChannel( notificationChannel )
        }

        val notificationCompat = NotificationCompat
          .Builder(applicationContext, "default")
          .setContentTitle(title)
          .setContentText(message)
          .setPriority(NotificationCompat.PRIORITY_MAX)

        notificationManager.notify(0, notificationCompat.build() )

    }
}