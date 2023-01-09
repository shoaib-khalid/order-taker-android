package com.symplified.ordertaker.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.symplified.ordertaker.App
import com.symplified.ordertaker.constants.SharedPrefsKey
import com.symplified.ordertaker.models.categories.Category
import com.symplified.ordertaker.models.products.Product
import com.symplified.ordertaker.models.products.ProductResponseBody
import com.symplified.ordertaker.networking.ServiceGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductViewModel: ViewModel() {
    private val _products: MutableLiveData<List<Product>> by lazy {
        MutableLiveData<List<Product>>()
    }
    val products: LiveData<List<Product>> = _products

    private val _isLoadingProducts = MutableLiveData<Boolean>().apply { value = false }
    val isLoadingProducts : LiveData<Boolean> = _isLoadingProducts

    fun setCurrentCategory(category: Category) {
//        _currentCategory.value = category
        _products.value = listOf()
        _isLoadingProducts.value = true

        val storeId = App.sharedPreferences().getString(SharedPrefsKey.STORE_ID, "")!!
        ServiceGenerator.createProductService()
            .getProductsByCategoryId(storeId, category.id)
            .clone()
            .enqueue(
                object: Callback<ProductResponseBody> {
                    override fun onResponse(
                        call: Call<ProductResponseBody>,
                        response: Response<ProductResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            _products.value = response.body()!!.data.content
                        }
                        _isLoadingProducts.value = false
                    }

                    override fun onFailure(call: Call<ProductResponseBody>, t: Throwable) {
                        _isLoadingProducts.value = false
                    }
                }
            )
    }

    fun clearProducts() { _products.value = listOf() }
}