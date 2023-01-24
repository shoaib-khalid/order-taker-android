package com.symplified.ordertaker.data.repository

import android.util.Log
import com.symplified.ordertaker.data.dao.PaymentChannelDao
import com.symplified.ordertaker.models.paymentchannel.PaymentChannel
import com.symplified.ordertaker.networking.ServiceGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PaymentChannelRepository(private val paymentChannelDao: PaymentChannelDao) {
    val allPaymentChannels: Flow<List<PaymentChannel>> = paymentChannelDao.getAll()

    suspend fun fetchPaymentChannels(): Boolean {
        try {
            val paymentChannelsResponse = ServiceGenerator.createOrderService().getPaymentChannels()
            if (!paymentChannelsResponse.isSuccessful) {
                return false
            }
            paymentChannelsResponse.body()?.let { responseBody ->
                paymentChannelDao.insert(responseBody.data)
            }
            Log.d("cartviewmodel", "paymentchannelrepository: Payment Channels received")
            return true
        } catch (_:Throwable) {
            return false
        }
    }
}