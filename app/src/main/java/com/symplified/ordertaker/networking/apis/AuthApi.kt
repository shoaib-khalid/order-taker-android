package com.symplified.ordertaker.networking.apis

import com.symplified.ordertaker.models.auth.AuthRequestBody
import com.symplified.ordertaker.models.auth.AuthResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApi {

    @Headers("Authorization: Bearer accessToken")
    @POST("stores/null/users/authenticate")
    fun authenticate(@Body requestBody: AuthRequestBody): Call<AuthResponseBody>
}