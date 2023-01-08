package com.symplified.ordertaker.networking.apis

import com.symplified.ordertaker.models.HttpResponse
import com.symplified.ordertaker.models.categories.CategoryResponseBody
import com.symplified.ordertaker.models.products.ProductResponseBody
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
}