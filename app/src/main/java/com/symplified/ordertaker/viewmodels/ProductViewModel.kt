package com.symplified.ordertaker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.symplified.ordertaker.App
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
    fun setCurrentCategory(category: Category) {
//        _currentCategory.value = category
        ServiceGenerator.createProductService()
            .getProductsByCategoryId(App.testStoreId, category.id)
            .clone()
            .enqueue(
                object: Callback<ProductResponseBody> {
                    override fun onResponse(
                        call: Call<ProductResponseBody>,
                        response: Response<ProductResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let { productResponseBody ->
                                _products.value = productResponseBody.data.content
                            }
                        }
                    }

                    override fun onFailure(call: Call<ProductResponseBody>, t: Throwable) {
                    }
                }
            )
    }
}