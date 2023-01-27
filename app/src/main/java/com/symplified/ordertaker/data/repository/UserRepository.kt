package com.symplified.ordertaker.data.repository

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.symplified.ordertaker.data.dao.UserDao
import com.symplified.ordertaker.models.auth.AuthRequest
import com.symplified.ordertaker.models.users.User
import com.symplified.ordertaker.networking.ServiceGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class UserRepository(private val userDao: UserDao) {
    val user: Flow<User?> = userDao.getUser()
    val currencySymbol: Flow<String?> = userDao.getCurrencySymbol()

    suspend fun authenticate(authRequest: AuthRequest): Boolean {
        val authApiService = ServiceGenerator.createAuthService()
        val authResponse = authApiService.authenticate(authRequest)
        if (!authResponse.isSuccessful) {
            return false
        }

        val sessionData = authResponse.body()!!.data.session
        val userResponse = authApiService.getUserById(sessionData.ownerId)
        if (!userResponse.isSuccessful) {
            return false
        }

        val userData = userResponse.body()!!.data
        val storeResponse = ServiceGenerator.createProductService().getStoreById(userData.storeId)
        if (!storeResponse.isSuccessful) {
            return false
        }
        val store = storeResponse.body()!!.data

        userDao.clear()
        userDao.insert(User(
            userData.id,
            userData.storeId,
            store.name,
            store.regionCountry.currencySymbol,
            userData.username,
            userData.name,
            sessionData.accessToken,
            sessionData.refreshToken
        ))

        return true
    }

    fun logout() {
        userDao.clear()
    }
}