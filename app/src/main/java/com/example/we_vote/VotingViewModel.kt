package com.example.we_vote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.we_vote.data.local.PreferencesManager
import com.example.we_vote.domain.usecase.survey.VoteSurveyUseCase
import kotlinx.coroutines.launch

class VotingViewModel(
    private val voteSurveyUseCase: VoteSurveyUseCase,
    private val preferences: PreferencesManager,
) : ViewModel() {

    sealed class State {
        object Idle : State()
        object Loading : State()
        object Success : State()
        data class Error(val message: String) : State()
    }

    private val _state = MutableLiveData<State>(State.Idle)
    val state: LiveData<State> = _state

    fun vote(surveyId: Int, voteOption: Int) {
        viewModelScope.launch {
            _state.value = State.Loading
            try {
                val email = preferences.getEmail()
                voteSurveyUseCase(email, surveyId, voteOption)
                preferences.saveVotedId(surveyId)
                _state.value = State.Success
            } catch (e: Exception) {
                _state.value = State.Error(e.message ?: "")
            }
        }
    }

    class Factory(
        private val voteSurveyUseCase: VoteSurveyUseCase,
        private val preferences: PreferencesManager,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return VotingViewModel(voteSurveyUseCase, preferences) as T
        }
    }
}
