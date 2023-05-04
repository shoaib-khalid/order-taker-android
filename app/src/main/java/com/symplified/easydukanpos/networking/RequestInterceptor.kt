package com.symplified.easydukanpos.networking

import android.util.Log
import com.symplified.easydukanpos.App
import com.symplified.easydukanpos.constants.SharedPrefsKey
import com.symplified.easydukanpos.networking.ServiceGenerator.BASE_URL_PRODUCTION
import com.symplified.easydukanpos.networking.ServiceGenerator.BASE_URL_STAGING
import com.symplified.easydukanpos.networking.ServiceGenerator.USER_SERVICE_PATH
import com.symplified.easydukanpos.networking.apis.AuthApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class RequestInterceptor: Interceptor {

    private var accessToken: String = "accessToken"
    private var refreshToken: String? = null
    private var isLoggedIn: Boolean = false
    private val loginService: AuthApi

    init {
        CoroutineScope(Dispatchers.IO).launch {
            App.userRepository.user.collect { user ->
                isLoggedIn = user != null
                accessToken = user?.accessToken ?: "accessToken"
                refreshToken = user?.refreshToken
            }
        }

        val baseURL: String =
            if (App.sharedPreferences().getBoolean(SharedPrefsKey.IS_STAGING, false))
                BASE_URL_STAGING
            else BASE_URL_PRODUCTION

        loginService = Retrofit.Builder().client(OkHttpClient())
            .baseUrl("$baseURL$USER_SERVICE_PATH")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }

    override fun intercept(chain: Interceptor.Chain): Response {

        val originalRequest = chain.request()
        var request = addTokenToRequest(originalRequest, accessToken)

        var response = chain.proceed(request)
        if (response.code == 401 && isLoggedIn) {
            try {
                val refreshResponse = loginService.refreshAccessToken(refreshToken!!).execute()
                val sessionData = refreshResponse.body()!!.data.session

                CoroutineScope(Dispatchers.IO).launch {
                    App.userRepository.setTokens(sessionData.accessToken, sessionData.refreshToken)
                }

                request = addTokenToRequest(originalRequest, sessionData.accessToken)
                response.close()
                response = chain.proceed(request)
            } catch (e: Throwable) {
                Log.e("request-interceptor", "Failed to refresh token. ${e.localizedMessage}")
            }
        }

        return response
    }

    private fun addTokenToRequest(originalRequest: Request, accessToken: String): Request =
        originalRequest.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .method(originalRequest.method, originalRequest.body)
            .build()
}