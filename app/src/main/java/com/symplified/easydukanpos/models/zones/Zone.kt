package com.symplified.easydukanpos.models.zones

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "zones")
data class Zone (
    @PrimaryKey
    val id: Int,
    val tagId: Int,
    val zoneName: String,
    @Ignore
    val tagTables: List<Table> = listOf()
) {
    constructor(
        id: Int,
        tagId: Int,
        zoneName: String
    ): this(id, tagId, zoneName, listOf())
}