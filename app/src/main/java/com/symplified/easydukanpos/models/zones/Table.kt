package com.symplified.easydukanpos.models.zones

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tables")
data class Table(
    @PrimaryKey
    val id: Int,
    val zoneId: Int,
    val combinationTableNumber: String
)