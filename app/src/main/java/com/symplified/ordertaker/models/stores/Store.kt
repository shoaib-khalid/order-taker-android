package com.symplified.ordertaker.models.stores

data class Store(
    val name: String,
    val regionCountry: StoreRegion,
    val regionVertical: RegionVertical
)
