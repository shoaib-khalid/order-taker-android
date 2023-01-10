package com.symplified.ordertaker.models.products.addons

data class ProductAddOnDetails(
    val id: String,
    val addonTemplateItemId: String,
    val groupId: String,
    val name: String,
    val productId: String,
    val dineInPrice: Double,
    val status: String,
    val productAddonGroupId: String
)
