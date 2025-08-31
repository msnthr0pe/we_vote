package com.example.we_vote.ktor

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("login")
    fun login(@Body request: DTOs.CredentialsDTO): Call<Void>

    @POST("getuser")
    suspend fun getUser(@Body email: DTOs.CredentialsDTO): DTOs.UserDTO

    @POST("register")
    fun register(@Body request: DTOs.UserDTO): Call<Void>

}