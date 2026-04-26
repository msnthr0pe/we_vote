package com.example.we_vote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.we_vote.data.local.PreferencesManager
import com.example.we_vote.domain.model.Survey
import com.example.we_vote.domain.model.SurveyVotes
import com.example.we_vote.domain.usecase.survey.ArchiveSurveyUseCase
import com.example.we_vote.domain.usecase.survey.GetSurveyVotesUseCase
import com.example.we_vote.domain.usecase.survey.GetSurveysUseCase
import kotlinx.coroutines.launch

class MainScreenViewModel(
    private val getSurveysUseCase: GetSurveysUseCase,
    private val getSurveyVotesUseCase: GetSurveyVotesUseCase,
    private val archiveSurveyUseCase: ArchiveSurveyUseCase,
    val preferences: PreferencesManager,
) : ViewModel() {

    sealed class SurveysState {
        object Loading : SurveysState()
        data class Success(val surveys: List<Survey>) : SurveysState()
        object Error : SurveysState()
    }

    private val _surveysState = MutableLiveData<SurveysState>()
    val surveysState: LiveData<SurveysState> = _surveysState

    private val _archiveResult = MutableLiveData<Boolean>()
    val archiveResult: LiveData<Boolean> = _archiveResult

    private var allSurveys: List<Survey> = emptyList()

    fun loadSurveys() {
        viewModelScope.launch {
            _surveysState.value = SurveysState.Loading
            try {
                val access = preferences.getAccess()
                val city = if (access == "developer") "" else preferences.getCity()
                allSurveys = getSurveysUseCase(city)
                _surveysState.value = SurveysState.Success(allSurveys)
            } catch (e: Exception) {
                _surveysState.value = SurveysState.Error
            }
        }
    }

    fun filter(query: String) {
        val filtered = if (query.isEmpty()) allSurveys
        else allSurveys.filter { it.title.contains(query, ignoreCase = true) }
        _surveysState.value = SurveysState.Success(filtered)
    }

    fun archiveSurvey(survey: Survey) {
        viewModelScope.launch {
            try {
                archiveSurveyUseCase(survey.title)
                allSurveys = allSurveys.filter { it.id != survey.id }
                _surveysState.value = SurveysState.Success(allSurveys)
            } catch (e: Exception) {
                _archiveResult.value = false
            }
        }
    }

    fun fetchSurveyVotes(surveyId: Int, onResult: (SurveyVotes?) -> Unit) {
        viewModelScope.launch {
            onResult(getSurveyVotesUseCase(surveyId))
        }
    }

    class Factory(
        private val getSurveysUseCase: GetSurveysUseCase,
        private val getSurveyVotesUseCase: GetSurveyVotesUseCase,
        private val archiveSurveyUseCase: ArchiveSurveyUseCase,
        private val preferences: PreferencesManager,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return MainScreenViewModel(getSurveysUseCase, getSurveyVotesUseCase, archiveSurveyUseCase, preferences) as T
        }
    }
}
