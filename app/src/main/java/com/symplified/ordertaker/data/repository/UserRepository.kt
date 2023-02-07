package com.symplified.ordertaker.data.repository

import com.symplified.ordertaker.App
import com.symplified.ordertaker.data.dao.BEST_SELLERS_CATEGORY_ID
import com.symplified.ordertaker.data.dao.BEST_SELLERS_CATEGORY_NAME
import com.symplified.ordertaker.data.dao.UserDao
import com.symplified.ordertaker.models.auth.AuthRequest
import com.symplified.ordertaker.models.categories.Category
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
                    sessionData.refreshToken
                )
            )

            CoroutineScope(Dispatchers.IO).launch {
                launch {
                    App.zoneRepository.fetchZonesAndTables(userData.storeId)
                }
                launch {
                    App.productRepository.insertAddOnGroups(
                        Category(
                            BEST_SELLERS_CATEGORY_ID,
                            BEST_SELLERS_CATEGORY_NAME
                        )
                    )
                }
                launch {
                    App.productRepository.fetchCategories(userData.storeId)
                }
                launch {
                    App.productRepository.fetchProducts(userData.storeId)
                }
                launch {
                    App.paymentChannelRepository.fetchPaymentChannels()
                }
                launch {
                    App.productRepository.fetchBestSellers(userData.storeId)
                }
            }

            return true
        } catch (_: Throwable) {}
        return false
    }

    fun logout() {
        userDao.clear()
    }

    fun getAccessToken(): String? = userDao.getAccessToken()

    fun getRefreshToken(): String? = userDao.getRefreshToken()

    fun setTokens(accessToken: String, refreshToken: String) = userDao.setTokens(accessToken, refreshToken)
}