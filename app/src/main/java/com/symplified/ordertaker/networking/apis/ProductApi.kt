package com.symplified.ordertaker.networking.apis

import com.symplified.ordertaker.models.categories.CategoryResponseBody
import com.symplified.ordertaker.models.products.ProductResponseBody
import com.symplified.ordertaker.models.products.addons.ProductAddOnResponseBody
import com.symplified.ordertaker.models.products.options.ProductPackageResponseBody
import com.symplified.ordertaker.models.stores.StoreResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApi {

    @Headers("Authorization: Bearer accessToken")
    @GET("store-categories?pageSize=1000000&sortByCol=sequenceNumber&sortingOrder=ASC")
    fun getCategories(@Query("storeId") storeId: String): Call<CategoryResponseBody>

    @Headers("Authorization: Bearer accessToken")
    @GET("stores/{storeId}/products?pageSize=1000000&sortByCol=created&sortingOrder=DESC&status=ACTIVE,OUTOFSTOCK&platformType=dinein")
    fun getProductsByStoreIdAndCategoryId(
        @Path("storeId") storeId: String,
        @Query("categoryId") categoryId: String
    ): Call<ProductResponseBody>

    @Headers("Authorization: Bearer accessToken")
    @GET("stores/{storeId}/products?pageSize=1000000&sortByCol=created&sortingOrder=DESC&status=ACTIVE,OUTOFSTOCK&platformType=dinein")
    suspend fun getProductsByStoreId(
        @Path("storeId") storeId: String,
    ): Response<ProductResponseBody>

    @Headers("Authorization: Bearer accessToken")
    @GET("stores/{storeId}/package/{productId}/options")
    suspend fun getProductOptions(
        @Path("storeId") storeId: String,
        @Path("productId") productId: String
    ): Response<ProductPackageResponseBody>

    @Headers("Authorization: Bearer accessToken")
    @GET("product-addon")
    suspend fun getProductAddOns(@Query("productId") productId: String): Response<ProductAddOnResponseBody>

    @Headers("Authorization: Bearer accessToken")
    @GET("stores/{storeId}")
    fun getStoreById(@Path("storeId") storeId: String) : Call<StoreResponseBody>
}