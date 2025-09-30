package com.example.we_vote.ktor

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("login")
    fun login(@Body request: DTOs.CredentialsDTO): Call<Void>

    @POST("getuser")
    suspend fun getUser(@Body email: DTOs.CredentialsDTO): DTOs.UserDTO

    @POST("register")
    fun register(@Body request: DTOs.UserDTO): Call<Void>

    @GET("getsurveys")
    suspend fun getSurveys(): MutableList<DTOs.SurveyDTO>

    @GET("getarchivedsurveys")
    suspend fun getArchivedSurveys(): MutableList<DTOs.SurveyDTO>

    @POST("addusersurvey")
    fun addUserSurvey(@Body request: DTOs.UsersSurveysDTO): Call<Void>

    @POST("archivesurvey")
    fun archiveSurvey(@Body request: DTOs.TitleDTO): Call<Void>

    @POST("addsurvey")
    fun addSurvey(@Body request: DTOs.SurveyDTO): Call<Void>

    @POST("updateuser")
    fun updateUser(@Body request: DTOs.UserDTO): Call<Void>

    @POST("getsurveyvotes")
    fun getSurveyVotes(@Body request: DTOs.SurveyIdRequest): Call<DTOs.SurveyVotesDTO>

}