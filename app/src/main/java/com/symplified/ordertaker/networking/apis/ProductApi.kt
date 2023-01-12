package com.symplified.ordertaker.networking.apis

import com.symplified.ordertaker.models.HttpResponse
import com.symplified.ordertaker.models.categories.CategoryResponseBody
import com.symplified.ordertaker.models.products.ProductResponseBody
import com.symplified.ordertaker.models.products.addons.ProductAddOnResponseBody
import com.symplified.ordertaker.models.products.options.ProductPackageResponseBody
import com.symplified.ordertaker.models.stores.StoreResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApi {

    @Headers("Authorization: Bearer accessToken")
    @GET("store-categories?page=0&pageSize=100&sortByCol=sequenceNumber&sortingOrder=ASC")
    fun getCategories(@Query("storeId") storeId: String): Call<CategoryResponseBody>

    @Headers("Authorization: Bearer accessToken")
    @GET("stores/{storeId}/products?page=0&size=1000000&sortByCol=created&sortingOrder=DESC&status=ACTIVE,OUTOFSTOCK&platformType=dinein")
    fun getProductsByCategoryId(
        @Path("storeId") storeId: String,
        @Query("categoryId") categoryId: String
    ): Call<ProductResponseBody>

    @Headers("Authorization: Bearer accessToken")
    @GET("stores/{storeId}/package/{productId}/options")
    fun getProductOptions(
        @Path("storeId") storeId: String,
        @Path("productId") productId: String
    ): Call<ProductPackageResponseBody>

    @Headers("Authorization: Bearer accessToken")
    @GET("product-addon")
    fun getProductAddOns(@Query("productId") productId: String): Call<ProductAddOnResponseBody>

    @GET("stores/{storeId}")
    fun getStoreById(@Path("storeId") storeId: String) : Call<StoreResponseBody>
}