package com.symplified.ordertaker.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.symplified.ordertaker.models.zones.Table
import kotlinx.coroutines.flow.Flow

@Dao
interface TableDao {
    @Query("SELECT * FROM tables")
    fun getAllTables(): Flow<List<Table>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(table: Table)

    @Query("DELETE FROM tables")
    fun deleteAll()
}