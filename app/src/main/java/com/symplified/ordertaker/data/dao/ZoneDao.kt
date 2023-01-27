package com.symplified.ordertaker.data.dao

import androidx.room.*
import com.symplified.ordertaker.models.zones.Zone
import com.symplified.ordertaker.models.zones.ZoneWithTables
import kotlinx.coroutines.flow.Flow

@Dao
interface ZoneDao {
    @Transaction
    @Query("SELECT * FROM zones")
    fun getZonesWithTables(): Flow<List<ZoneWithTables>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(zone: Zone)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(zones: List<Zone>)

    @Query("DELETE FROM zones")
    suspend fun deleteAll()
}