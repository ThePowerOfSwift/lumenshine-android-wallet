package com.soneso.stellargate.presentation.notification

import android.content.Context
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.soneso.stellargate.R

/**
 * Controller for push notifications in status bar.
 * Created by cristi.paval on 3/12/18.
 */
class SgPushController(private val context: Context) {

    private val notificationManager = NotificationManagerCompat.from(context)

    fun showGeneralNotification(message: String) {
        val notificationBuilder = NotificationCompat.Builder(context, GENERAL_CHANNEL)
        notificationBuilder.setContentText(message)
        notificationBuilder.setStyle(NotificationCompat.BigTextStyle().bigText(message))
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
//        notificationBuilder.setContentIntent(intentToOpenTheApp())
        notificationBuilder.setAutoCancel(true)

        val notification = notificationBuilder.build()
        notificationManager.notify(generateNotificationId(), notification)
    }

    private fun generateNotificationId() = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()

    companion object {
        const val TAG = "SgPushController"
        private const val GENERAL_CHANNEL = "$TAG.GENERAL_CHANNEL"
    }
}