package com.symplified.ordertaker.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.symplified.ordertaker.App
import com.symplified.ordertaker.constants.SharedPrefsKey
import com.symplified.ordertaker.models.cartitems.*
import com.symplified.ordertaker.models.paymentchannel.PaymentChannel
import com.symplified.ordertaker.models.zones.Table
import com.symplified.ordertaker.models.zones.Zone
import com.symplified.ordertaker.networking.ServiceGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartViewModel : ViewModel() {
    val cartItemsWithAddOnsAndSubItems: LiveData<List<CartItemWithAddOnsAndSubItems>> =
        App.cartItemRepository.allItems.asLiveData()
    val paymentChannels: LiveData<List<PaymentChannel>> =
        App.paymentChannelRepository.allPaymentChannels.asLiveData()

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

    fun placeOrder(zone: Zone, table: Table) {
        val sharedPrefs = App.sharedPreferences()
        val staffId = sharedPrefs.getString(SharedPrefsKey.USER_ID, "")!!
        val storeId = sharedPrefs.getString(SharedPrefsKey.STORE_ID, "")!!
        _isPlacingOrder.value = true

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

        var customerNotes = "Zone: ${zone.zoneName},\nTable No. ${table.combinationTableNumber}"
        val username = App.sharedPreferences().getString(SharedPrefsKey.USERNAME, "")!!
        if (username.isNotBlank()) {
            customerNotes = "$customerNotes,\nServed by: $username"
        }
        val orderRequest = listOf(
            OrderRequest(
                cartItemRequests,
                storeId,
                OrderPaymentDetails(
                    _selectedPaymentChannel.value!!.channelCode
                ),
                customerNotes
            )
        )
        Log.d("place-order", orderRequest.toString())

        ServiceGenerator
            .createOrderService()
            .placeOrder(
                table.zoneId,
                table.id,
                staffId,
                orderRequest
            ).clone()
            .enqueue(
                object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        Log.d("place-order", response.raw().toString())
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
                        _isPlacingOrder.value = false
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        _isPlacingOrder.value = false
                        _orderResultMessage.value =
                            "An error occurred while placing order. Please try again"
                    }

                }
            )
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