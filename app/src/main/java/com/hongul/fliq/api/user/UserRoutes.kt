package com.hongul.fliq.api.user

import com.hongul.fliq.api.user.dto.CreateUserBody
import com.hongul.fliq.api.user.dto.CreateUserResponse
import com.hongul.fliq.api.user.dto.DeleteUserResponse
import com.hongul.fliq.api.user.dto.GetUserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserRoutes {
    @POST("user/upload")
    suspend fun createUser(@Body createUserBody: CreateUserBody): Response<CreateUserResponse>

    @GET("user/{id}")
    suspend fun getUser(@Path("id") id: Long): Response<GetUserResponse?>

    @DELETE("user/{id}")
    suspend fun deleteUser(@Path("id") id: Long): Response<DeleteUserResponse>
}