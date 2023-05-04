package com.symplified.easydukanpos.data.repository

import com.symplified.easydukanpos.App
import com.symplified.easydukanpos.data.dao.UserDao
import com.symplified.easydukanpos.models.auth.AuthRequest
import com.symplified.easydukanpos.models.users.User
import com.symplified.easydukanpos.networking.ServiceGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class UserRepository(private val userDao: UserDao) {
    val user: Flow<User?> = userDao.getUser()
    val currencySymbol: Flow<String?> = userDao.getCurrencySymbol()

    suspend fun authenticate(authRequest: AuthRequest): Boolean {
        try {
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
            val storeResponse =
                ServiceGenerator.createProductService().getStoreById(userData.storeId)
            if (!storeResponse.isSuccessful) {
                return false
            }
            val store = storeResponse.body()!!.data

            userDao.clear()
            userDao.insert(
                User(
                    userData.id,
                    userData.storeId,
                    store.name,
                    store.regionCountry.currencySymbol,
                    userData.username,
                    userData.name,
                    sessionData.accessToken,
                    sessionData.refreshToken,
                    store.regionVertical.businessType
                )
            )

            CoroutineScope(Dispatchers.IO).launch {
                launch { App.zoneRepository.fetchZonesAndTables(userData.storeId) }
                launch { App.productRepository.fetchCategories(userData.storeId) }
                launch { App.productRepository.fetchProducts(userData.storeId) }
                launch { App.paymentChannelRepository.fetchPaymentChannels() }
                launch { App.productRepository.fetchBestSellers(userData.storeId) }
                launch { App.productRepository.fetchOpenItemProducts(userData.storeId) }
            }

            return true
        } catch (_: Throwable) {
        }
        return false
    }

    fun logout() {
        userDao.clear()
    }

    fun getAccessToken(): String? = userDao.getAccessToken()

    fun getRefreshToken(): String? = userDao.getRefreshToken()

    fun setTokens(accessToken: String, refreshToken: String) =
        userDao.setTokens(accessToken, refreshToken)

    fun getStoreId() = userDao.getStoreId()
}