package com.symplified.ordertaker.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.symplified.ordertaker.App
import com.symplified.ordertaker.models.cartitems.CartItemRequest
import com.symplified.ordertaker.models.cartitems.CartItemWithAddOnsAndSubItems
import com.symplified.ordertaker.models.cartitems.OrderPaymentDetails
import com.symplified.ordertaker.models.cartitems.OrderRequest
import com.symplified.ordertaker.models.paymentchannel.PaymentChannel
import com.symplified.ordertaker.models.users.User
import com.symplified.ordertaker.models.zones.Table
import com.symplified.ordertaker.models.zones.Zone
import com.symplified.ordertaker.networking.ServiceGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartViewModel : ViewModel() {
    val cartItemsWithAddOnsAndSubItems: LiveData<List<CartItemWithAddOnsAndSubItems>> =
        App.cartItemRepository.allItems.asLiveData()
    val paymentChannels: LiveData<List<PaymentChannel>> =
        App.paymentChannelRepository.allPaymentChannels.asLiveData()
    val user: LiveData<User?> = App.userRepository.user.asLiveData()

    private val _selectedPaymentChannel = MutableLiveData<PaymentChannel>().apply {
        value = PaymentChannel("CASH", "Cash")
    }
    val selectedPaymentChannel: LiveData<PaymentChannel> = _selectedPaymentChannel

    private val _isLoadingPaymentChannels: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val isLoadingPaymentChannels: LiveData<Boolean> = _isLoadingPaymentChannels

    private val _isPaymentChannelsReceived = MutableLiveData<Boolean>().apply { value = true }
    val isPaymentChannelsReceived: LiveData<Boolean> = _isPaymentChannelsReceived

    private val _isPlacingOrder = MutableLiveData<Boolean>().apply { value = false }
    val isPlacingOrder: LiveData<Boolean> = _isPlacingOrder

    private val _orderResultMessage: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val orderResultMessage: LiveData<String> = _orderResultMessage

    private val _isOrderSuccessful: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val isOrderSuccessful: LiveData<Boolean> = _isOrderSuccessful

    fun delete(cartItem: CartItemWithAddOnsAndSubItems) = CoroutineScope(Dispatchers.IO).launch {
        App.cartItemRepository.delete(cartItem)
    }

    fun clearAll() = CoroutineScope(Dispatchers.IO).launch {
        App.cartItemRepository.clear()
        App.cartSubItemRepository.clear()
        setSelectedPaymentChannel(PaymentChannel("CASH", "Cash"))
    }

    fun setSelectedPaymentChannel(paymentType: PaymentChannel) {
        viewModelScope.launch {
            _selectedPaymentChannel.value = paymentType
        }
    }

    fun placeOrder(zone: Zone, table: Table) = CoroutineScope(Dispatchers.IO).launch {

        withContext(Dispatchers.Main) { _isPlacingOrder.value = true }

        App.userRepository.user.collect { user ->

            val cartItemRequests: MutableList<CartItemRequest> = mutableListOf()
            cartItemsWithAddOnsAndSubItems.value?.let { cartItemsWithDetails ->
                cartItemsWithDetails.forEach { cartItemWithDetails ->

                    val cartItemRequest = CartItemRequest(
                        itemCode = cartItemWithDetails.cartItem.itemCode,
                        productId = cartItemWithDetails.cartItem.productId,
                        quantity = cartItemWithDetails.cartItem.quantity,
                        cartSubItem = cartItemWithDetails.cartSubItems.ifEmpty { null },
                        cartItemAddOn = cartItemWithDetails.cartItemAddons.ifEmpty { null }
                    )
                    cartItemRequests.add(cartItemRequest)
                }
            }

            val customerNotes =
                "Zone: ${zone.zoneName},\nTable No. ${table.combinationTableNumber}\nServed by: ${user!!.name}"
            val orderRequest = listOf(
                OrderRequest(
                    cartItemRequests,
                    user.storeId,
                    OrderPaymentDetails(_selectedPaymentChannel.value!!.channelCode),
                    customerNotes
                )
            )

            try {
                val response = ServiceGenerator
                    .createOrderService()
                    .placeOrder(table.zoneId, table.id, user.id, orderRequest)
                withContext(Dispatchers.Main) {
                    _isPlacingOrder.value = false
                    if (response.isSuccessful) {
                        clearAll()
                        _orderResultMessage.value = "Order placed successfully"
                        _orderResultMessage.value = ""
                        _isOrderSuccessful.value = true
                        _isOrderSuccessful.value = false
                    } else {
                        _orderResultMessage.value =
                            "An error occurred while placing order. Please try again"
                    }
                }
            } catch (_: Throwable) {
                withContext(Dispatchers.Main) {
                    _isPlacingOrder.value = false
                    _orderResultMessage.value =
                        "An error occurred while placing order. Please try again"
                }
            }
        }
    }

    fun fetchPaymentChannels() = CoroutineScope(Dispatchers.IO).launch {
        withContext(Dispatchers.Main) {
            _isLoadingPaymentChannels.value = true
        }
        val isPaymentChannelsReceived = App.paymentChannelRepository.fetchPaymentChannels()
        Log.d("cartviewmodel", "cartviewmodel: Payment Channels received")
        withContext(Dispatchers.Main) {
            _isPaymentChannelsReceived.value = isPaymentChannelsReceived
            _isLoadingPaymentChannels.value = false
        }
    }

}