package com.symplified.ordertaker.data.repository

import com.symplified.ordertaker.data.dao.UserDao
import com.symplified.ordertaker.models.auth.AuthRequestBody
import com.symplified.ordertaker.models.users.User
import com.symplified.ordertaker.networking.ServiceGenerator
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {
    val user: Flow<User?> = userDao.getUser()

    // authenticate user -> getUserById -> getStoreById -> insert into repo -> return true
    suspend fun authenticate(authRequest: AuthRequestBody): Boolean {
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