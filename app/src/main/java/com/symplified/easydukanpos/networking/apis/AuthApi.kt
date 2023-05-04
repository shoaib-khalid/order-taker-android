package com.symplified.easydukanpos.networking.apis

import com.symplified.easydukanpos.models.auth.AuthRequest
import com.symplified.easydukanpos.models.auth.AuthResponseBody
import com.symplified.easydukanpos.models.auth.TokenRefreshRequest
import com.symplified.easydukanpos.models.users.UserResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface AuthApi {

    @Headers("Authorization: Bearer accessToken")
    @POST("stores/null/users/authenticate")
    suspend fun authenticate(@Body requestBody: AuthRequest): Response<AuthResponseBody>

    @Headers(
        "Content-Type: application/plain"
    )
    @POST("clients/session/refresh")
    fun refreshAccessToken(@Body refreshToken: String): Call<AuthResponseBody>

    @Headers("Authorization: Bearer accessToken")
    @GET("stores/null/users/{userId}")
    suspend fun getUserById(@Path("userId") userId: String): Response<UserResponseBody>

    @Headers("Authorization: Bearer accessToken")
    @PUT("stores/{storeId}/users/refreshFcmToken/{id}")
    suspend fun updateFirebaseToken(
        @Path("storeId") storeId: String,
        @Path("id") userId: String,
        @Body tokenRefreshRequest: TokenRefreshRequest
    ): Response<Void>
}