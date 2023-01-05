package com.symplified.ordertaker.data.repository

import com.symplified.ordertaker.data.TableDao
import com.symplified.ordertaker.models.zones.Table
import kotlinx.coroutines.flow.Flow

class TableRepository(private val tableDao: TableDao) {
    val allTables: Flow<List<Table>> = tableDao.getAllTables()

    suspend fun insert(table: Table) {
        tableDao.insert(table)
    }
}