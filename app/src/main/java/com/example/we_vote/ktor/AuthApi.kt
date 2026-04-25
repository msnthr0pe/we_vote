package com.example.we_vote.ktor

import retrofit2.Call
import retrofit2.Response
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

    // 👇 УБЕРИТЕ "suspend" - оставьте только Call<Void>
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

    @POST("deletesurveyinfo")
    fun deleteSurveyInfo(@Body request: DTOs.SurveyIdRequest): Call<Void>

    @GET("getapplications")
    suspend fun getApplications(): MutableList<DTOs.ApplicationDTO>

    @POST("getuserapplications")
    suspend fun getUserApplications(@Body request: DTOs.EmailDTO): MutableList<DTOs.ApplicationDTO>

    @POST("updateapplication")
    fun updateApplication(@Body request: DTOs.ApplicationStatusUpdateDTO): Call<Void>

    @POST("addapplication")
    fun addApplication(@Body request: DTOs.ApplicationDTO): Call<Void>

    @POST("deleteapplication")
    fun deleteApplication(@Body request: DTOs.ApplicationIdDTO): Call<Void>
}