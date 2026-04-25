package com.example.we_vote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.we_vote.domain.model.Survey
import com.example.we_vote.domain.model.SurveyVotes
import com.example.we_vote.domain.usecase.survey.DeleteSurveyUseCase
import com.example.we_vote.domain.usecase.survey.GetArchivedSurveysUseCase
import com.example.we_vote.domain.usecase.survey.GetSurveyVotesUseCase
import kotlinx.coroutines.launch

class ArchiveViewModel(
    private val getArchivedSurveysUseCase: GetArchivedSurveysUseCase,
    private val getSurveyVotesUseCase: GetSurveyVotesUseCase,
    private val deleteSurveyUseCase: DeleteSurveyUseCase,
) : ViewModel() {

    data class ArchiveItem(val survey: Survey, val votes: SurveyVotes?)

    sealed class State {
        object Loading : State()
        data class Success(val items: List<ArchiveItem>) : State()
        data class Error(val message: String) : State()
    }

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    private val _deleteResult = MutableLiveData<Result<Unit>>()
    val deleteResult: LiveData<Result<Unit>> = _deleteResult

    private val currentItems = mutableListOf<ArchiveItem>()

    fun loadArchive() {
        viewModelScope.launch {
            _state.value = State.Loading
            try {
                val surveys = getArchivedSurveysUseCase()
                currentItems.clear()
                for (survey in surveys) {
                    val votes = getSurveyVotesUseCase(survey.id)
                    currentItems.add(ArchiveItem(survey, votes))
                }
                _state.value = State.Success(currentItems.toList())
            } catch (e: Exception) {
                _state.value = State.Error(e.message ?: "")
            }
        }
    }

    fun deleteSurvey(surveyId: Int, position: Int) {
        viewModelScope.launch {
            try {
                deleteSurveyUseCase(surveyId)
                currentItems.removeAt(position)
                _state.value = State.Success(currentItems.toList())
                _deleteResult.value = Result.success(Unit)
            } catch (e: Exception) {
                _deleteResult.value = Result.failure(e)
            }
        }
    }

    class Factory(
        private val getArchivedSurveysUseCase: GetArchivedSurveysUseCase,
        private val getSurveyVotesUseCase: GetSurveyVotesUseCase,
        private val deleteSurveyUseCase: DeleteSurveyUseCase,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ArchiveViewModel(getArchivedSurveysUseCase, getSurveyVotesUseCase, deleteSurveyUseCase) as T
        }
    }
}
