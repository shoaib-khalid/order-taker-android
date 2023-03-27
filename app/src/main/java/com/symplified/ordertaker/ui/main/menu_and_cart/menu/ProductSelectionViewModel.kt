package com.symplified.ordertaker.ui.main.menu_and_cart.menu

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.symplified.ordertaker.App
import com.symplified.ordertaker.models.cartitems.CartItem
import com.symplified.ordertaker.models.cartitems.CartItemAddOn
import com.symplified.ordertaker.models.cartitems.CartSubItem
import com.symplified.ordertaker.models.products.ProductWithDetails
import com.symplified.ordertaker.models.products.addons.ProductAddOnDetails
import com.symplified.ordertaker.models.products.options.PackageOptionDetailsWithProductAndInventories
import com.symplified.ordertaker.models.products.options.ProductPackage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.elementAtOrNull
import kotlin.collections.firstOrNull
import kotlin.collections.forEach
import kotlin.collections.indexOfFirst
import kotlin.collections.isNotEmpty
import kotlin.collections.map
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.removeAll
import kotlin.collections.set
import kotlin.collections.toSet

class ProductSelectionViewModel : ViewModel() {

    val currencySymbol: LiveData<String?> = App.userRepository.currencySymbol.asLiveData()

    private val _productWithDetails: MutableLiveData<ProductWithDetails> by lazy { MutableLiveData<ProductWithDetails>() }
    val productWithDetails: LiveData<ProductWithDetails> = _productWithDetails

    private val _productQuantity = MutableLiveData<Int>().apply { value = 1 }
    val productQuantity: LiveData<Int> = _productQuantity

    private val _addOnGroupsCountMap =
        MutableLiveData<MutableMap<String, GroupSelectionStats>>().apply { value = mutableMapOf() }
    val addOnGroupsCountMap: LiveData<MutableMap<String, GroupSelectionStats>> =
        _addOnGroupsCountMap

    private val _packageOptionsCountMap =
        MutableLiveData<MutableMap<String, GroupSelectionStats>>().apply { value = mutableMapOf() }
    val packageOptionsCountMap: LiveData<MutableMap<String, GroupSelectionStats>> =
        _packageOptionsCountMap

    private var cartItem: CartItem? = null
    private val cartItemAddOns: MutableList<CartItemAddOn> = mutableListOf()

    private val _cartSubItems =
        MutableLiveData<MutableList<CartSubItem>>().apply { value = mutableListOf() }
    val cartSubItems: LiveData<MutableList<CartSubItem>> = _cartSubItems

    private val _isCartItemValid = MutableLiveData<Boolean>().apply { value = true }
    val isCartItemValid: LiveData<Boolean> = _isCartItemValid

    fun setSelectedProduct(productWithDetails: ProductWithDetails) {
        cartItem = null
        _productWithDetails.value = productWithDetails
        _productQuantity.value = 1
        cartItemAddOns.clear()
        _cartSubItems.value = mutableListOf()

        if (productWithDetails.productVariantsWithVariantsAvailable.isEmpty()) {
            productWithDetails.productInventoriesWithItems.firstOrNull()
                ?.let { inventoryWithItems ->
                    cartItem = CartItem(
                        itemName = productWithDetails.product.name,
                        itemPrice = inventoryWithItems.productInventory.dineInPrice,
                        itemCode = inventoryWithItems.productInventory.itemCode,
                        productId = productWithDetails.product.id
                    )
                }
        }

        val addOnCountMap: MutableMap<String, GroupSelectionStats> = mutableMapOf()
        productWithDetails.productAddOnGroupsWithDetails.forEach { addOnGroupWithDetails ->
            addOnGroupWithDetails.productAddOnGroup.let { addOnGroup ->
                addOnCountMap[addOnGroup.id] = GroupSelectionStats(
                    addOnGroup.minAllowed,
                    addOnGroup.maxAllowed
                )
            }
        }
        _addOnGroupsCountMap.value = addOnCountMap

        val packageOptionsCountMap: MutableMap<String, GroupSelectionStats> = mutableMapOf()
        productWithDetails.productPackages.forEach { productPackageWithOptionDetails ->
            productPackageWithOptionDetails.productPackage.let { productPackage ->
                packageOptionsCountMap[productPackage.id] = GroupSelectionStats(
                    productPackage.minAllow,
                    productPackage.totalAllow,
                    productPackage.allowSameItem
                )
            }
        }
        _packageOptionsCountMap.value = packageOptionsCountMap

        validate()
    }

    fun incrementProductQuantity() {
        cartItem?.let { cartItem ->
            _productQuantity.value = ++cartItem.quantity
        }
    }

    fun decrementProductQuantity() {
        cartItem?.let { cartItem ->
            if (cartItem.quantity > 1) {
                _productQuantity.value = --cartItem.quantity
            }
        }
    }

    fun selectAddOn(
        addOn: ProductAddOnDetails,
        groupId: String
    ) {
        cartItemAddOns.add(
            CartItemAddOn(
                productAddOnId = addOn.id,
                name = addOn.name,
                price = addOn.dineInPrice
            )
        )

        _addOnGroupsCountMap.value?.let { addOnCountMap ->
            addOnCountMap[groupId]?.let { it.selected++ }
            _addOnGroupsCountMap.value = addOnCountMap
        }
        validate()
    }

    fun removeAddOn(
        addOn: ProductAddOnDetails,
        groupId: String
    ) {
        cartItemAddOns.removeAll { addOnInList -> addOnInList.productAddOnId == addOn.id }

        _addOnGroupsCountMap.value?.let { addOnCountMap ->
            addOnCountMap[groupId]?.let { it.selected-- }
            _addOnGroupsCountMap.value = addOnCountMap
        }
        validate()
    }

    fun addCartSubItem(
        packageGroup: ProductPackage,
        option: PackageOptionDetailsWithProductAndInventories
    ) {
        val groupSelectionStats = _packageOptionsCountMap.value!![packageGroup.id]!!

        if (groupSelectionStats.selected < groupSelectionStats.maxAllowed
            && option.product != null
            && option.productInventories.isNotEmpty()
        ) {
            val cartSubItemToAdd = CartSubItem(
                productId = option.product.id,
                SKU = option.productInventories[0].sku,
                productName = option.product.name,
                itemCode = option.productInventories[0].itemCode,
                productPrice = option.productInventories[0].dineInPrice,
                optionId = option.optionDetails.id,
                packageGroupId = packageGroup.id
            )

            _cartSubItems.value?.let { cartSubItems ->

                val cartSubItemInList =
                    cartSubItems.firstOrNull { it.optionId == cartSubItemToAdd.optionId }
                if (cartSubItemInList != null && packageGroup.allowSameItem) {
                    cartSubItemInList.quantity++
                    _packageOptionsCountMap.value?.let { packageOptionsCountMap ->
                        packageOptionsCountMap[packageGroup.id]!!.selected += 1
                        _packageOptionsCountMap.value = packageOptionsCountMap
                    }
                } else if (cartSubItemInList == null) {
                    cartSubItems.add(cartSubItemToAdd)
                    _packageOptionsCountMap.value?.let { packageOptionsCountMap ->
                        packageOptionsCountMap[packageGroup.id]!!.selected += 1
                        _packageOptionsCountMap.value = packageOptionsCountMap
                    }
                }
                _cartSubItems.value = cartSubItems
            }
        }
        validate()
    }

    fun decrementCartSubItem(
        packageGroup: ProductPackage,
        option: PackageOptionDetailsWithProductAndInventories
    ) {
        _cartSubItems.value?.let { cartSubItems ->
            val indexOfCartItem =
                cartSubItems.indexOfFirst { it.optionId == option.optionDetails.id }
            if (indexOfCartItem != -1) {
                if (--cartSubItems[indexOfCartItem].quantity <= 0) {
                    cartSubItems.removeAt(indexOfCartItem)
                }
                _cartSubItems.value = cartSubItems

                _packageOptionsCountMap.value?.let { packageOptionsCountMap ->
                    packageOptionsCountMap[packageGroup.id]?.let { groupSelectionStats ->
                        groupSelectionStats.selected =
                            if (groupSelectionStats.selected <= 0) 0
                            else groupSelectionStats.selected - 1
                    }
                    _packageOptionsCountMap.value = packageOptionsCountMap
                }
            }
        }
        validate()
    }

    private fun validate() {
        var isCartItemValid = cartItem != null

        if (_productWithDetails.value!!.product.isCustomPrice) {
            isCartItemValid = cartItem!!.itemPrice > 0.00
        }

        _addOnGroupsCountMap.value!!.forEach { (_, selectionStats) ->
            if (selectionStats.selected < selectionStats.minAllowed) {
                isCartItemValid = false
            }
        }

        _packageOptionsCountMap.value!!.forEach { (_, selectionStats) ->
            if (selectionStats.selected < selectionStats.minAllowed) {
                isCartItemValid = false
            }
        }

        _isCartItemValid.value = isCartItemValid
    }

    fun setCartItemWithVariant(inventoryIndex: Int) {
        productWithDetails.value?.let { productWithDetails ->
            productWithDetails.productInventoriesWithItems.elementAtOrNull(inventoryIndex)
                ?.let { inventoryWithItems ->
                    cartItem = CartItem(
                        itemName = "${productWithDetails.product.name} - " +
                                inventoryWithItems.inventoryItems[0].productVariantAvailable.value,
                        itemPrice = inventoryWithItems.productInventory.dineInPrice,
                        itemCode = inventoryWithItems.productInventory.itemCode,
                        productId = productWithDetails.product.id,
                        quantity = _productQuantity.value!!
                    )
                    validate()
                }
        }
    }

    fun addToCart() = CoroutineScope(Dispatchers.IO).launch {


        val existingCartItems =
            App.cartItemRepository.getCartItems(cartItem!!.itemCode, cartItem!!.productId)
//            .collect().let { existingCartItems ->

        var sameCartItemExists = false
        val existingCartItem = existingCartItems.firstOrNull {
            it.cartItem.itemCode == cartItem?.itemCode
                    && it.cartItem.productId == cartItem?.productId
                    && it.cartItem.itemPrice == cartItem?.itemPrice
                    && it.cartItemAddons.map(::mapForComparison).toSet() == cartItemAddOns.map(::mapForComparison).toSet()
                    && it.cartSubItems.map(::mapForComparison).toSet() == _cartSubItems.value!!.map(::mapForComparison).toSet()
        }
        Log.d("abcd", "CartItem already exists: ${existingCartItem != null}")
        if (existingCartItem != null) {
            existingCartItem.cartItem.quantity += cartItem!!.quantity
            App.cartItemRepository.insert(existingCartItem.cartItem)
        } else {
            App.cartItemRepository.insert(cartItem!!, cartItemAddOns, _cartSubItems.value!!)

        }

        existingCartItems.forEach {
            if (it.cartItem.itemCode == cartItem?.itemCode && it.cartItem.productId == cartItem?.productId) {
                val areAddonsSame = it.cartItemAddons.map(::mapForComparison).toSet() ==
                        cartItemAddOns.map(::mapForComparison).toSet()

                val doSubItemsMatch = it.cartSubItems.map(::mapForComparison).toSet() ==
                        _cartSubItems.value!!.map(::mapForComparison).toSet()

                Log.d("abcd", "areAddonsSame: $areAddonsSame")
                Log.d("abcd", "doSubItemsMatch: $doSubItemsMatch")
            }
        }
//            }
    }

    fun setCustomPrice(price: Double) {
        cartItem?.itemPrice = price
        validate()
    }

    private fun mapForComparison(subItem: CartSubItem) = CartSubItem(
        SKU = subItem.SKU,
        productName = subItem.productName,
        itemCode = subItem.itemCode,
        productPrice = subItem.productPrice,
        productId = subItem.productId,
        optionId = subItem.optionId,
        packageGroupId = subItem.packageGroupId,
        quantity = subItem.quantity
    )

    private fun mapForComparison(addOn: CartItemAddOn) = addOn.productAddOnId

    data class GroupSelectionStats(
        val minAllowed: Int,
        val maxAllowed: Int,
        val allowSameItem: Boolean = false,
        var selected: Int = 0
    )
}