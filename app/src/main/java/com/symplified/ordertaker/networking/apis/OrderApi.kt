package com.symplified.ordertaker.networking.apis

import com.symplified.ordertaker.models.order.OrderRequest
import com.symplified.ordertaker.models.order.OrderResponse
import com.symplified.ordertaker.models.paymentchannel.PaymentChannelsResponse
import retrofit2.Response
import retrofit2.http.*

interface OrderApi {

    @Headers("Authorization: Bearer accessToken")
    @POST("orders/placeGroupOrder?isStaffOrder=true")
    suspend fun placeOrder(
        @Query("staffId") staffId: String,
        @Body requestBody: List<OrderRequest>
    ) : Response<OrderResponse>

    @Headers("Authorization: Bearer accessToken")
    @POST("orders/placeGroupOrder?isStaffOrder=true")
    suspend fun placeOrderWithZoneIdAndTableId(
        @Query("zoneId") zoneId: Int,
        @Query("tableId") tableId: Int,
        @Query("staffId") staffId: String,
        @Body requestBody: List<OrderRequest>
    ) : Response<OrderResponse>

    @Headers("Authorization: Bearer accessToken")
    @GET("qrorder/paymentChannel")
    suspend fun getPaymentChannels(): Response<PaymentChannelsResponse>
}