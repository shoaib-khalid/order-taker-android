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
import com.symplified.ordertaker.models.products.ProductWithPackagesAndAddOns
import com.symplified.ordertaker.models.products.addons.ProductAddOnResponseBody
import com.symplified.ordertaker.models.products.options.ProductPackageResponseBody
import com.symplified.ordertaker.networking.ServiceGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductViewModel : ViewModel() {
    private val _products: MutableLiveData<List<ProductWithPackagesAndAddOns>> by lazy {
        MutableLiveData<List<ProductWithPackagesAndAddOns>>()
    }
    val products: LiveData<List<ProductWithPackagesAndAddOns>> = _products

    private val _isLoadingProducts = MutableLiveData<Boolean>().apply { value = false }
    val isLoadingProducts: LiveData<Boolean> = _isLoadingProducts

    fun setCurrentCategory(category: Category) {
//        _currentCategory.value = category
        _products.value = listOf()
        _isLoadingProducts.value = true

        val storeId = App.sharedPreferences().getString(SharedPrefsKey.STORE_ID, "")!!
        val productApi = ServiceGenerator.createProductService()
        productApi
            .getProductsByCategoryId(storeId, category.id)
            .clone()
            .enqueue(
                object : Callback<ProductResponseBody> {
                    override fun onResponse(
                        call: Call<ProductResponseBody>,
                        response: Response<ProductResponseBody>
                    ) {
                        response.body()?.let { productResponseBody ->
                            _products.value = productResponseBody.data.content.map {
                                ProductWithPackagesAndAddOns(product = it)
                            }

//                            _products.value = productResponseBody.data.content
                            productResponseBody.data.content.forEach { product: Product ->
                                if (product.hasAddOn) {
                                    productApi.getProductAddOns(product.id).clone()
                                        .enqueue(
                                            object : Callback<ProductAddOnResponseBody> {
                                                override fun onResponse(
                                                    call: Call<ProductAddOnResponseBody>,
                                                    response: Response<ProductAddOnResponseBody>
                                                ) {
                                                    TODO("Add product addons to list")
                                                }

                                                override fun onFailure(
                                                    call: Call<ProductAddOnResponseBody>,
                                                    t: Throwable
                                                ) {
                                                }
                                            }
                                        )
                                }

                                if (product.isPackage) {
                                    productApi.getProductOptions(storeId, product.id).clone()
                                        .enqueue(
                                            object : Callback<ProductPackageResponseBody> {
                                                override fun onResponse(
                                                    call: Call<ProductPackageResponseBody>,
                                                    response: Response<ProductPackageResponseBody>
                                                ) {
                                                    TODO("Add productpackages to list")
                                                }

                                                override fun onFailure(
                                                    call: Call<ProductPackageResponseBody>,
                                                    t: Throwable
                                                ) {
                                                }
                                            }
                                        )
                                }
                            }
                        }
                        _isLoadingProducts.value = false
                    }

                    override fun onFailure(call: Call<ProductResponseBody>, t: Throwable) {
                        _isLoadingProducts.value = false
                    }
                }
            )
    }

    fun clearProducts() {
        _products.value = listOf()
    }
}