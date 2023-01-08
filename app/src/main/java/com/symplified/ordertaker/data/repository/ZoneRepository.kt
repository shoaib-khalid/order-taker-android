package com.symplified.ordertaker.data.repository

import com.symplified.ordertaker.data.ZoneDao
import com.symplified.ordertaker.models.zones.Zone
import com.symplified.ordertaker.models.zones.ZoneWithTables
import kotlinx.coroutines.flow.Flow

class ZoneRepository(private val zoneDao: ZoneDao) {
    val allZones: Flow<List<ZoneWithTables>> = zoneDao.getZonesWithTables()

    suspend fun insert(zone: Zone) {
        zoneDao.insert(zone)
    }
}