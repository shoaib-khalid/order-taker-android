package com.symplified.easydukanpos.data.repository

import com.symplified.easydukanpos.data.dao.TableDao
import com.symplified.easydukanpos.data.dao.ZoneDao
import com.symplified.easydukanpos.models.zones.Table
import com.symplified.easydukanpos.models.zones.Zone
import com.symplified.easydukanpos.models.zones.ZoneWithTables
import com.symplified.easydukanpos.networking.ServiceGenerator
import kotlinx.coroutines.flow.Flow

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