package com.symplified.easydukanpos.data.repository

import com.symplified.easydukanpos.data.dao.PaymentChannelDao
import com.symplified.easydukanpos.models.paymentchannel.PaymentChannel
import com.symplified.easydukanpos.networking.ServiceGenerator
import kotlinx.coroutines.flow.Flow

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
            return true
        } catch (_:Throwable) {
            return false
        }
    }

    fun clear() {
        paymentChannelDao.clear()
    }
}