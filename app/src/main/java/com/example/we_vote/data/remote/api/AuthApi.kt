package com.example.we_vote.data.remote.api

import com.example.we_vote.data.remote.dto.ApplicationDto
import com.example.we_vote.data.remote.dto.ApplicationIdDto
import com.example.we_vote.data.remote.dto.ApplicationStatusUpdateDto
import com.example.we_vote.data.remote.dto.CityDto
import com.example.we_vote.data.remote.dto.CredentialsDto
import com.example.we_vote.data.remote.dto.EmailDto
import com.example.we_vote.data.remote.dto.SurveyDto
import com.example.we_vote.data.remote.dto.SurveyIdDto
import com.example.we_vote.data.remote.dto.SurveyVotesDto
import com.example.we_vote.data.remote.dto.TitleDto
import com.example.we_vote.data.remote.dto.UserDto
import com.example.we_vote.data.remote.dto.UsersSurveysDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("login")
    suspend fun login(@Body request: CredentialsDto): Response<Void>

    @POST("getuser")
    suspend fun getUser(@Body request: CredentialsDto): UserDto

    @POST("register")
    suspend fun register(@Body request: UserDto): Response<Void>

    @POST("getsurveys")
    suspend fun getSurveys(@Body request: CityDto): List<SurveyDto>

    @POST("getarchivedsurveys")
    suspend fun getArchivedSurveys(@Body request: CityDto): List<SurveyDto>

    @GET("getcities")
    suspend fun getCities(): List<CityDto>

    @POST("addcity")
    suspend fun addCity(@Body request: CityDto): Response<Void>

    @POST("deletecity")
    suspend fun deleteCity(@Body request: CityDto): Response<Void>

    @POST("addusersurvey")
    suspend fun addUserSurvey(@Body request: UsersSurveysDto): Response<Void>

    @POST("archivesurvey")
    suspend fun archiveSurvey(@Body request: TitleDto): Response<Void>

    @POST("addsurvey")
    suspend fun addSurvey(@Body request: SurveyDto): Response<Void>

    @POST("updateuser")
    suspend fun updateUser(@Body request: UserDto): Response<Void>

    @POST("getsurveyvotes")
    suspend fun getSurveyVotes(@Body request: SurveyIdDto): SurveyVotesDto

    @POST("deletesurveyinfo")
    suspend fun deleteSurvey(@Body request: SurveyIdDto): Response<Void>

    @POST("getapplications")
    suspend fun getApplications(@Body request: EmailDto): List<ApplicationDto>

    @POST("getuserapplications")
    suspend fun getUserApplications(@Body request: EmailDto): List<ApplicationDto>

    @POST("updateapplication")
    suspend fun updateApplication(@Body request: ApplicationStatusUpdateDto): Response<Void>

    @POST("addapplication")
    suspend fun addApplication(@Body request: ApplicationDto): Response<Void>

    @POST("deleteapplication")
    suspend fun deleteApplication(@Body request: ApplicationIdDto): Response<Void>
}
