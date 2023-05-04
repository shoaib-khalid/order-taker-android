package com.symplified.easydukanpos.data.dao

import androidx.room.*
import com.symplified.easydukanpos.models.zones.Zone
import com.symplified.easydukanpos.models.zones.ZoneWithTables
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