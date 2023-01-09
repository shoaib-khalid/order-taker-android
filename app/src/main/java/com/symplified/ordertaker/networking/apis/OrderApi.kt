package com.symplified.ordertaker.networking.apis

import com.symplified.ordertaker.models.cartitems.OrderRequest
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface OrderApi {

    @Headers("Authorization: Bearer accessToken")
    @POST("orders/placeGroupOrder?isStaffOrder=true")
    fun placeOrder(
        @Query("zone") zoneName: String,
        @Query("tableNo") tableNo: String,
        @Query("staffId") staffId: String,
        @Body requestBody: OrderRequest
    )
}