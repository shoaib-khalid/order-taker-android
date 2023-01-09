package com.symplified.ordertaker.networking.apis

import com.symplified.ordertaker.models.auth.AuthRequestBody
import com.symplified.ordertaker.models.auth.AuthResponseBody
import com.symplified.ordertaker.models.users.UserResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthApi {

    @Headers("Authorization: Bearer accessToken")
    @POST("stores/null/users/authenticate")
    fun authenticate(@Body requestBody: AuthRequestBody): Call<AuthResponseBody>

    @Headers("Authorization: Bearer accessToken")
    @GET("stores/null/users/{userId}")
    fun getUserById(@Path("userId") userId: String): Call<UserResponseBody>
}