package com.symplified.ordertaker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.symplified.ordertaker.models.zones.Zone
import com.symplified.ordertaker.models.zones.ZoneWithTables
import kotlinx.coroutines.flow.Flow

@Dao
interface ZoneDao {
    @Transaction
    @Query("SELECT * FROM zones")
    fun getZonesWithTables(): Flow<List<ZoneWithTables>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(zone: Zone)

    @Query("DELETE FROM zones")
    fun deleteAll()
}