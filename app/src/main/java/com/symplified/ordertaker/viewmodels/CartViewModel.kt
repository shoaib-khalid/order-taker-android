package com.symplified.ordertaker.viewmodels

import androidx.lifecycle.*
import com.symplified.ordertaker.App
import com.symplified.ordertaker.models.cartitems.CartItemAddOnRequest
import com.symplified.ordertaker.models.cartitems.CartItemRequest
import com.symplified.ordertaker.models.cartitems.CartItemWithAddOnsAndSubItems
import com.symplified.ordertaker.models.cartitems.CartSubItemRequest
import com.symplified.ordertaker.models.order.OrderPaymentDetails
import com.symplified.ordertaker.models.order.OrderRequest
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

    private val _isPaymentChannelsReceived: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val isPaymentChannelsReceived: LiveData<Boolean> = _isPaymentChannelsReceived

    private val _isPlacingOrder: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
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

            val orderRequest = listOf(
                OrderRequest(
                    cartItemRequests,
                    user.storeId,
                    OrderPaymentDetails(_selectedPaymentChannel.value!!.channelCode),
                    customerNotes
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
                       _orderResultMessage.value = "Order placed successfully"
                       _orderResultMessage.value = ""

                       _isOrderSuccessful.value = true
                       _isOrderSuccessful.value = false

                       clearAll()
                   } else {
                       _orderResultMessage.value =
                           "An error occurred while placing order. Please try again"
                   }
                }
            } catch (e: Throwable) {
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
        withContext(Dispatchers.Main) {
            _isPaymentChannelsReceived.value = isPaymentChannelsReceived
            _isLoadingPaymentChannels.value = false
        }
    }

}