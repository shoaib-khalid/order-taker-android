package com.symplified.ordertaker.networking

import com.symplified.ordertaker.networking.apis.AuthApi
import com.symplified.ordertaker.networking.apis.LocationApi
import com.symplified.ordertaker.networking.apis.OrderApi
import com.symplified.ordertaker.networking.apis.ProductApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object ServiceGenerator {
    private const val BASE_URL = "https://api.symplified.it/"

    private const val USER_SERVICE_PATH = "user-service/v1/"
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

    private fun createRetrofitInstance(servicePath: String): Retrofit =
        Retrofit.Builder()
            .client(OkHttpClient())
            .baseUrl("${BASE_URL}${servicePath}")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}