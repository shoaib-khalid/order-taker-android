package com.symplified.ordertaker.networking

import android.content.Context
import android.util.Log
import com.symplified.ordertaker.R
import com.symplified.ordertaker.networking.apis.AuthApi
import com.symplified.ordertaker.networking.apis.LocationApi
import com.symplified.ordertaker.networking.apis.ProductApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ServiceGenerator {
    companion object {

        fun createAuthService(context: Context) =
            createRetrofitInstance(
                context,
                context.getString(R.string.auth_service_path)
            ).create(AuthApi::class.java)

        fun createLocationService(context: Context) =
            createRetrofitInstance(
                context,
                context.getString(R.string.location_service_path)
            ).create(LocationApi::class.java)

        fun createProductService(context: Context) =
            createRetrofitInstance(
                context,
                context.getString(R.string.product_service_path)
            ).create(ProductApi::class.java)

        private fun createRetrofitInstance(
            context: Context,
            servicePath: String
        ): Retrofit {
            val baseUrlAndPath = "${context.getString(R.string.base_url)}${servicePath}"
            Log.d("zones", "Base URL and path: $baseUrlAndPath$servicePath")
            return Retrofit.Builder()
                .client(OkHttpClient())
                .baseUrl(baseUrlAndPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}