package com.symplified.easydukanpos.networking.apis

import com.symplified.easydukanpos.models.bestsellers.BestSellerResponse
import com.symplified.easydukanpos.models.zones.ZonesResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface LocationApi {

    @Headers("Authorization: Bearer accessToken")
    @GET("tags/tables")
    suspend fun getZones(@Query("storeId") storeId: String) : Response<ZonesResponseBody>

    @Headers("Authorization: Bearer accessToken")
    @GET("famous/{storeId}")
    suspend fun getBestSellers(@Path("storeId") storeId: String) : Response<BestSellerResponse>
}