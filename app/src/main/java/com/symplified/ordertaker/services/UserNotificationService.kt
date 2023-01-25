package com.symplified.ordertaker.services

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.symplified.ordertaker.App
import com.symplified.ordertaker.models.auth.TokenRefreshRequest
import com.symplified.ordertaker.networking.ServiceGenerator
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
                            user.storeId,
                            user.id,
                            TokenRefreshRequest(token)
                        )
                }
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("my-firebase", "onMessageReceived: ${message.from} ${message.data}")
        App.userRepository.logout()
    }
}