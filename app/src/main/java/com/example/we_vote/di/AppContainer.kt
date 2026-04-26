package com.example.we_vote.di

import android.content.Context
import com.example.we_vote.data.local.PreferencesManager
import com.example.we_vote.data.remote.ApiClient
import com.example.we_vote.data.repository.ApplicationRepositoryImpl
import com.example.we_vote.data.repository.CityRepositoryImpl
import com.example.we_vote.data.repository.SurveyRepositoryImpl
import com.example.we_vote.data.repository.UserRepositoryImpl
import com.example.we_vote.domain.usecase.application.AddApplicationUseCase
import com.example.we_vote.domain.usecase.application.DeleteApplicationUseCase
import com.example.we_vote.domain.usecase.application.GetApplicationsUseCase
import com.example.we_vote.domain.usecase.application.GetUserApplicationsUseCase
import com.example.we_vote.domain.usecase.application.UpdateApplicationStatusUseCase
import com.example.we_vote.domain.usecase.city.AddCityUseCase
import com.example.we_vote.domain.usecase.city.DeleteCityUseCase
import com.example.we_vote.domain.usecase.city.GetCitiesUseCase
import com.example.we_vote.domain.usecase.survey.AddSurveyUseCase
import com.example.we_vote.domain.usecase.survey.ArchiveSurveyUseCase
import com.example.we_vote.domain.usecase.survey.DeleteSurveyUseCase
import com.example.we_vote.domain.usecase.survey.GetArchivedSurveysUseCase
import com.example.we_vote.domain.usecase.survey.GetSurveyVotesUseCase
import com.example.we_vote.domain.usecase.survey.GetSurveysUseCase
import com.example.we_vote.domain.usecase.survey.VoteSurveyUseCase
import com.example.we_vote.domain.usecase.user.GetUserUseCase
import com.example.we_vote.domain.usecase.user.LoginUseCase
import com.example.we_vote.domain.usecase.user.RegisterUseCase
import com.example.we_vote.domain.usecase.user.UpdateUserUseCase

class AppContainer(context: Context) {

    val preferences = PreferencesManager(context)

    private val api = ApiClient.authApi

    private val surveyRepository = SurveyRepositoryImpl(api)
    private val userRepository = UserRepositoryImpl(api)
    private val applicationRepository = ApplicationRepositoryImpl(api)
    private val cityRepository = CityRepositoryImpl(api)

    // City use cases
    val getCitiesUseCase = GetCitiesUseCase(cityRepository)
    val addCityUseCase = AddCityUseCase(cityRepository)
    val deleteCityUseCase = DeleteCityUseCase(cityRepository)

    // Survey use cases
    val getSurveysUseCase = GetSurveysUseCase(surveyRepository)
    val getArchivedSurveysUseCase = GetArchivedSurveysUseCase(surveyRepository)
    val getSurveyVotesUseCase = GetSurveyVotesUseCase(surveyRepository)
    val voteSurveyUseCase = VoteSurveyUseCase(surveyRepository)
    val archiveSurveyUseCase = ArchiveSurveyUseCase(surveyRepository)
    val addSurveyUseCase = AddSurveyUseCase(surveyRepository)
    val deleteSurveyUseCase = DeleteSurveyUseCase(surveyRepository)

    // User use cases
    val loginUseCase = LoginUseCase(userRepository)
    val getUserUseCase = GetUserUseCase(userRepository)
    val registerUseCase = RegisterUseCase(userRepository)
    val updateUserUseCase = UpdateUserUseCase(userRepository)

    // Application use cases
    val getApplicationsUseCase = GetApplicationsUseCase(applicationRepository)
    val getUserApplicationsUseCase = GetUserApplicationsUseCase(applicationRepository)
    val addApplicationUseCase = AddApplicationUseCase(applicationRepository)
    val updateApplicationStatusUseCase = UpdateApplicationStatusUseCase(applicationRepository)
    val deleteApplicationUseCase = DeleteApplicationUseCase(applicationRepository)
}
