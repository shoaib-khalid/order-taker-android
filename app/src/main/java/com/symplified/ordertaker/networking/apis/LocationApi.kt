package com.symplified.ordertaker.networking.apis

import com.symplified.ordertaker.models.zones.ZonesResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface LocationApi {

    @Headers("Authorization: Bearer accessToken")
    @GET("tags/tables")
    fun getZones(@Query("storeId") storeId: String) : Call<ZonesResponseBody>
}