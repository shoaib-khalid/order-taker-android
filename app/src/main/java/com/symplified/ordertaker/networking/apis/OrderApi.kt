package com.symplified.ordertaker.networking.apis

import com.symplified.ordertaker.models.cartitems.OrderRequest
import com.symplified.ordertaker.models.paymentchannel.PaymentChannelsResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface OrderApi {

    @Headers("Authorization: Bearer accessToken")
    @POST("orders/placeGroupOrder?isStaffOrder=true")
    fun placeOrder(
        @Query("zoneId") zoneId: Int,
        @Query("tableId") tableId: Int,
        @Query("staffId") staffId: String,
        @Body requestBody: List<OrderRequest>
    ) : Call<ResponseBody>

    @Headers("Authorization: Bearer accessToken")
    @GET("qrorder/paymentChannel")
    suspend fun getPaymentChannels(): Response<PaymentChannelsResponse>
}