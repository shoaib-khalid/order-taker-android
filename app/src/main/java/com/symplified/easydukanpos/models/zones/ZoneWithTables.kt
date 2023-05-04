package com.symplified.easydukanpos.models.zones

import androidx.room.Embedded
import androidx.room.Relation

data class ZoneWithTables(
    @Embedded val zone: Zone,
    @Relation(
        parentColumn = "id",
        entityColumn = "zoneId"
    )
    val tables: List<Table>
)