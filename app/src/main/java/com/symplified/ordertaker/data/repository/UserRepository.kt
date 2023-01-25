package com.symplified.ordertaker.data.repository

import android.util.Log
import com.symplified.ordertaker.data.dao.UserDao
import com.symplified.ordertaker.models.auth.AuthRequest
import com.symplified.ordertaker.models.users.User
import com.symplified.ordertaker.networking.ServiceGenerator
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {
    val user: Flow<User?> = userDao.getUser()
    val currencySymbol: Flow<String?> = userDao.getCurrencySymbol()

    // authenticate user -> getUserById -> getStoreById -> insert into repo -> return true
    suspend fun authenticate(authRequest: AuthRequest): Boolean {
        val authApiService = ServiceGenerator.createAuthService()
        val authResponse = authApiService.authenticate(authRequest)
        if (!authResponse.isSuccessful) {
            Log.d("my-firebase", "authenticate unsuccessful")
            return false
        }

        val sessionData = authResponse.body()!!.data.session
        val userResponse = authApiService.getUserById(sessionData.ownerId)
        if (!userResponse.isSuccessful) {
            Log.d("my-firebase", "getUserById unsuccessful")
            return false
        }

        val userData = userResponse.body()!!.data
        val storeResponse = ServiceGenerator.createProductService().getStoreById(userData.storeId)
        if (!storeResponse.isSuccessful) {
            Log.d("my-firebase", "getStoreById unsuccessful")
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