package com.symplified.ordertaker.networking.apis

import com.symplified.ordertaker.models.cartitems.OrderRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface OrderApi {

    @Headers("Authorization: Bearer accessToken")
    @POST("orders/placeGroupOrder?isStaffOrder=true")
    fun placeOrder(
        @Query("zoneId") zoneId: Int,
        @Query("tableId") tableId: Int,
        @Query("staffId") staffId: String,
        @Body requestBody: List<OrderRequest>
    ) : Call<ResponseBody>
}