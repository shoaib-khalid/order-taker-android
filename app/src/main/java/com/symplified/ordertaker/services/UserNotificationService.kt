package com.symplified.ordertaker.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.symplified.ordertaker.App
import com.symplified.ordertaker.R
import com.symplified.ordertaker.models.auth.TokenRefreshRequest
import com.symplified.ordertaker.networking.ServiceGenerator
import com.symplified.ordertaker.ui.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserNotificationService: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            App.userRepository.user.collect { user ->
                if (user != null) {
                    ServiceGenerator.createAuthService()
                        .refreshFirebaseToken(
                            user.storeId, user.id, TokenRefreshRequest(token)
                        )
                }
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val stackBuilder = TaskStackBuilder.create(applicationContext)
            .addNextIntentWithParentStack(Intent(applicationContext, MainActivity::class.java))

        val pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notificationManager: NotificationManager = applicationContext.getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(NotificationChannel("Logout", "Logout", NotificationManager.IMPORTANCE_DEFAULT))
        }

        val builder = NotificationCompat.Builder(applicationContext, "Logout")
            .setContentIntent(pendingIntent)
            .setContentTitle("Shift Ended")
            .setContentText("Your shift has ended.")
            .setSmallIcon(R.drawable.ic_notification)
            .build()
        notificationManager.notify(1234, builder)

        App.userRepository.logout()
    }
}