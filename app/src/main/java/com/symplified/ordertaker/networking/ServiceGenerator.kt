package com.symplified.ordertaker.networking

import com.symplified.ordertaker.App
import com.symplified.ordertaker.constants.SharedPrefsKey
import com.symplified.ordertaker.networking.apis.AuthApi
import com.symplified.ordertaker.networking.apis.LocationApi
import com.symplified.ordertaker.networking.apis.OrderApi
import com.symplified.ordertaker.networking.apis.ProductApi
import kotlinx.coroutines.flow.collect
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceGenerator {

    const val BASE_URL_STAGING = "https://api.symplified.it/"
    const val BASE_URL_PRODUCTION = "https://api.symplified.biz/"

    const val USER_SERVICE_PATH = "user-service/v1/"

    private const val LOCATION_SERVICE_PATH = "location-service/v1/"
    private const val PRODUCT_SERVICE_PATH = "product-service/v1/"
    private const val ORDER_SERVICE_PATH = "order-service/v1/"

    fun createAuthService(): AuthApi =
        createRetrofitInstance(USER_SERVICE_PATH).create(AuthApi::class.java)

    fun createLocationService(): LocationApi =
        createRetrofitInstance(LOCATION_SERVICE_PATH).create(LocationApi::class.java)

    fun createProductService(): ProductApi =
        createRetrofitInstance(PRODUCT_SERVICE_PATH).create(ProductApi::class.java)

    fun createOrderService(): OrderApi =
        createRetrofitInstance(ORDER_SERVICE_PATH).create(OrderApi::class.java)

    private fun createRetrofitInstance(servicePath: String): Retrofit {

        val baseUrl =
            if (App.sharedPreferences().getBoolean(SharedPrefsKey.IS_STAGING, false))
                BASE_URL_STAGING
            else BASE_URL_PRODUCTION

        val httpClient = OkHttpClient.Builder()
            .addInterceptor(RequestInterceptor())
            .build()

        return Retrofit.Builder()
            .client(OkHttpClient())
            .baseUrl("${baseUrl}${servicePath}")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}