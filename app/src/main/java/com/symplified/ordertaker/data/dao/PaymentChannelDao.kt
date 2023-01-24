package com.symplified.ordertaker.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.symplified.ordertaker.models.paymentchannel.PaymentChannel
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentChannelDao {
    @Query("SELECT * FROM payment_channels")
    fun getAll(): Flow<List<PaymentChannel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(paymentChannel: PaymentChannel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(paymentChannels: List<PaymentChannel>)

    @Query("DELETE FROM payment_channels")
    fun clear()
}