package com.symplified.easydukanpos.models.bestsellers

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "best_sellers")
data class BestSeller(
    @PrimaryKey
    val id: String
)