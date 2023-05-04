package com.symplified.easydukanpos.models.paymentchannel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payment_channels")
data class PaymentChannel(
    @PrimaryKey
    val channelCode: String,
    val channelName: String
)
