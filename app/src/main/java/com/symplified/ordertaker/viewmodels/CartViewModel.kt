package com.symplified.ordertaker.viewmodels

import androidx.lifecycle.*
import com.symplified.ordertaker.App
import com.symplified.ordertaker.constants.SharedPrefsKey
import com.symplified.ordertaker.models.cartitems.CartItemAddOnRequest
import com.symplified.ordertaker.models.cartitems.CartItemRequest
import com.symplified.ordertaker.models.cartitems.CartItemWithAddOnsAndSubItems
import com.symplified.ordertaker.models.cartitems.CartSubItemRequest
import com.symplified.ordertaker.models.order.OrderPaymentDetails
import com.symplified.ordertaker.models.order.OrderRequest
import com.symplified.ordertaker.models.paymentchannel.PaymentOption
import com.symplified.ordertaker.models.users.User
import com.symplified.ordertaker.models.zones.Table
import com.symplified.ordertaker.models.zones.Zone
import com.symplified.ordertaker.networking.ServiceGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartViewModel : ViewModel() {
    val cartItemsWithAddOnsAndSubItems: LiveData<List<CartItemWithAddOnsAndSubItems>> =
        App.cartItemRepository.allItems.asLiveData()
    val user: LiveData<User?> = App.userRepository.user.asLiveData()

    private val _selectedPaymentOption = MutableLiveData<PaymentOption>().apply {
        value = PaymentOption.CASH
    }
    val selectedPaymentOption: LiveData<PaymentOption> = _selectedPaymentOption

    private val _isPlacingOrder: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val isPlacingOrder: LiveData<Boolean> = _isPlacingOrder

    private val _orderResult: MutableStateFlow<OrderResult?> =
        MutableStateFlow(null)
    val orderResult: StateFlow<OrderResult?> = _orderResult

    fun delete(cartItem: CartItemWithAddOnsAndSubItems) = CoroutineScope(Dispatchers.IO).launch {
        App.cartItemRepository.delete(cartItem)
    }

    fun clearAll() = CoroutineScope(Dispatchers.IO).launch {
        App.cartItemRepository.clear()
        App.cartSubItemRepository.clear()
        setSelectedPaymentOption(PaymentOption.CASH)
        _orderResult.value = null
    }

    fun setSelectedPaymentOption(paymentType: PaymentOption) = viewModelScope.launch {
        _selectedPaymentOption.value = paymentType
    }

    fun placeOrder(
        zone: Zone? = null,
        table: Table? = null
    ) = CoroutineScope(Dispatchers.IO).launch {
        user.value?.let { user ->

            withContext(Dispatchers.Main) { _isPlacingOrder.value = true }

            val cartItemRequests: MutableList<CartItemRequest> = mutableListOf()
            val cartItemsWithDetails = cartItemsWithAddOnsAndSubItems.value!!
            cartItemsWithDetails.forEach { cartItemWithDetails ->
                val cartSubItemsRequestList: MutableList<CartSubItemRequest> = mutableListOf()
                cartItemWithDetails.cartSubItems.forEach { subItem ->
                    for (i in 1..subItem.quantity) {
                        cartSubItemsRequestList.add(
                            CartSubItemRequest(
                                SKU = subItem.SKU,
                                productName = subItem.productName,
                                productId = subItem.productId,
                                itemCode = subItem.itemCode
                            )
                        )
                    }
                }

                val cartItemAddOnRequestList: List<CartItemAddOnRequest> =
                    cartItemWithDetails.cartItemAddons.map { addOn ->
                        CartItemAddOnRequest(addOn.productAddOnId)
                    }

                val cartItemRequest = CartItemRequest(
                    itemCode = cartItemWithDetails.cartItem.itemCode,
                    productId = cartItemWithDetails.cartItem.productId,
                    quantity = cartItemWithDetails.cartItem.quantity,
                    productPrice = cartItemWithDetails.cartItem.itemPrice,
                    cartSubItem = cartSubItemsRequestList.ifEmpty { null },
                    cartItemAddOn = cartItemAddOnRequestList.ifEmpty { null }
                )
                cartItemRequests.add(cartItemRequest)
            }

            var customerNotes = ""
            if (zone != null) {
                customerNotes = "${customerNotes}Zone: ${zone.zoneName},\n"
            }
            if (table != null) {
                customerNotes = "${customerNotes}Table No. ${table.combinationTableNumber}\n"
            }
            customerNotes = "${customerNotes}Served by: ${user.name}"

            val selectedPaymentType = _selectedPaymentOption.value!!
            val orderRequest = listOf(
                OrderRequest(
                    storeId = user.storeId,
                    orderPaymentDetails = OrderPaymentDetails(selectedPaymentType.name),
                    customerNotes = customerNotes,
                    cartItems = cartItemRequests
                )
            )

            try {
                val orderApi = ServiceGenerator.createOrderService()
                val response =
                    if (table != null)
                        orderApi.placeOrderWithZoneIdAndTableId(
                            table.zoneId,
                            table.id,
                            user.id,
                            orderRequest
                        )
                    else orderApi.placeOrder(user.id, orderRequest)
                withContext(Dispatchers.Main) {
                    _isPlacingOrder.value = false
                }
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val orderId = response.body()?.data?.orderList?.getOrNull(0)?.id
                        val basePaymentUrl = when (
                            App.sharedPreferences().getBoolean(SharedPrefsKey.IS_STAGING, false)
                        ) {
                            true -> STAGING_PAYMENT_URL
                            false -> PRODUCTION_PAYMENT_URL
                        }

                        _orderResult.value =
                            OrderResult.Success(
                                paymentUrl = if (orderId != null && selectedPaymentType != PaymentOption.CASH)
                                    "$basePaymentUrl${selectedPaymentType.endpoint}?storeId=" +
                                            "${user.storeId}&orderId=$orderId"
                                else null
                            )

                        clearAll()
                    } else {
                        _orderResult.value = OrderResult.Failure()
                    }
                }
            } catch (e: Throwable) {
                withContext(Dispatchers.Main) {
                    _orderResult.value = OrderResult.Failure()
                    _isPlacingOrder.value = false
                }
            }
        }
    }

    companion object {
        private const val STAGING_PAYMENT_URL = "https://paymentv2.dev-my.symplified.ai/"
        private const val PRODUCTION_PAYMENT_URL = "https://paymentv2.deliverin.my/"
    }
}

sealed class OrderResult(open val message: String) {
    data class Success(
        override val message: String = "Order placed successfully",
        val paymentUrl: String? = null
    ) : OrderResult(message)

    data class Failure(override val message: String = "An error occurred while placing order. Please try again") :
        OrderResult(message)
}