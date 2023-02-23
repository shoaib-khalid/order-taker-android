package com.symplified.ordertaker.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.symplified.ordertaker.App
import com.symplified.ordertaker.data.dao.TableDao
import com.symplified.ordertaker.data.dao.ZoneDao
import com.symplified.ordertaker.models.zones.Table
import com.symplified.ordertaker.models.zones.Zone
import com.symplified.ordertaker.models.zones.ZoneWithTables
import com.symplified.ordertaker.networking.ServiceGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ZoneRepository(
    private val zoneDao: ZoneDao,
    private val tableDao: TableDao
) {
    val allZones: Flow<List<ZoneWithTables>> = zoneDao.getZonesWithTables()
    val allTables: Flow<List<Table>> = tableDao.getAllTables()

    suspend fun fetchZonesAndTables(storeId: String): Boolean {
        try {
            val response =
                ServiceGenerator.createLocationService().getZones(storeId)
            if (response.isSuccessful) {
                response.body()?.let { zoneData ->
                    clear()
                    insert(zoneData.data)
                    return true
                }
            }
        } catch (_: Throwable) {}
        return false
    }

    suspend fun insert(zones: List<Zone>) {
        zoneDao.insert(zones)
        zones.forEach { zone ->
            tableDao.insert(zone.tagTables)
        }
    }

    suspend fun clear() {
        zoneDao.deleteAll()
        tableDao.deleteAll()
    }
}