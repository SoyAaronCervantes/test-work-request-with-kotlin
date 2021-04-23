package com.soyaaroncervantes.demoworkmanager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class UploadManager(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
  override fun doWork(): Result {
    val message = inputData.getString("message") ?: "Default message"
    val title = inputData.getString("title") ?: "Here comes Jhonny!"
    return try {
      sendNotification(message, title)
      Result.success()
    } catch (ex: Exception) {
      Log.e("[Error Worker]", "$ex")
      Result.failure()
    }
  }

  private fun sendNotification(message: String, title: String) {
    val notificationManager =
      applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      val notificationChannel =
        NotificationChannel("default", "Default", NotificationManager.IMPORTANCE_DEFAULT)
      notificationManager.createNotificationChannel(notificationChannel)
    }

    val notificationCompat = NotificationCompat
      .Builder(applicationContext, "default")
      .setSmallIcon( R.drawable.ic_air )
      .setContentTitle( title )
      .setContentText( message )
      .setStyle( NotificationCompat.BigTextStyle().bigText( applicationContext.getString( R.string.app_name ) ) )
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    notificationManager.notify(0, notificationCompat.build())

  }
}