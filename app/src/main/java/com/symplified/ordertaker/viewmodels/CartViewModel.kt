package com.symplified.ordertaker.viewmodels

import androidx.lifecycle.*
import com.symplified.ordertaker.App
import com.symplified.ordertaker.constants.SharedPrefsKey
import com.symplified.ordertaker.data.repository.CartItemRepository
import com.symplified.ordertaker.models.cartitems.*
import com.symplified.ordertaker.models.zones.Table
import com.symplified.ordertaker.models.zones.Zone
import com.symplified.ordertaker.networking.ServiceGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartViewModel : ViewModel() {
    val cartItemsWithAddOnsAndSubItems: LiveData<List<CartItemWithAddOnsAndSubItems>> =
        App.cartItemRepository.allItems.asLiveData()

    fun delete(cartItem: CartItemWithAddOnsAndSubItems) = CoroutineScope(Dispatchers.IO).launch {
        App.cartItemRepository.delete(cartItem)
    }

    fun clearAll() = CoroutineScope(Dispatchers.IO).launch {
        App.cartItemRepository.clear()
        App.cartSubItemRepository.clear()
    }

    private val _selectedPaymentType = MutableLiveData<String>().apply { value = "CASH" }
    val selectedPaymentType: LiveData<String> = _selectedPaymentType
    fun setCurrentPaymentType(paymentType: String) {
        _selectedPaymentType.value = paymentType
    }

    private val _isPlacingOrder = MutableLiveData<Boolean>().apply { value = false }
    val isPlacingOrder: LiveData<Boolean> = _isPlacingOrder
    private val _orderResultMessage: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val orderResultMessage: LiveData<String> = _orderResultMessage
    fun placeOrder(zone: Zone, table: Table) {
        val sharedPrefs = App.sharedPreferences()
        val staffId = sharedPrefs.getString(SharedPrefsKey.USER_ID, "")!!
        val storeId = sharedPrefs.getString(SharedPrefsKey.STORE_ID, "")!!
        _isPlacingOrder.value = true

        val cartItemsWithSubItemsRequest: MutableList<CartItemWithAddOnsAndSubItemsRequest> =
            mutableListOf()
        cartItemsWithAddOnsAndSubItems.value?.let { cartItems ->
            cartItems.forEach { cartItem ->
//                cartItemsWithSubItemsRequest.add(
//                    CartItemWithAddOnsAndSubItemsRequest(
//                        itemCode = cartItem.itemCode,
//                        productId = cartItem.productId,
//                        quantity = cartItem.quantity,
//                        cartSubItem = listOf()
//                    )
//                )
            }
        }

        var customerNotes = "Zone: ${zone.zoneName},\nTable No. ${table.combinationTableNumber}"
        App.sharedPreferences().getString(SharedPrefsKey.USERNAME, "")?.let { username ->
            if (username.isNotBlank()) {
                customerNotes = "$customerNotes,\nServed by: $username"
            }
        }
        val orderRequest = listOf(
            OrderRequest(
                cartItemsWithSubItemsRequest,
                storeId,
                OrderPaymentDetails(_selectedPaymentType.value!!),
                customerNotes
            )
        )

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
                        if (response.isSuccessful) {
                            clearAll()
                            _orderResultMessage.value = "Order placed successfully"
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

}

class CartViewModelFactory(private val repository: CartItemRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            return CartViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}