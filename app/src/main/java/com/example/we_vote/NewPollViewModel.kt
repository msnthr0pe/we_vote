package com.example.we_vote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.we_vote.data.local.PreferencesManager
import com.example.we_vote.domain.model.ApplicationStatus
import com.example.we_vote.domain.model.Survey
import com.example.we_vote.domain.model.SurveyApplication
import com.example.we_vote.domain.usecase.application.AddApplicationUseCase
import com.example.we_vote.domain.usecase.survey.AddSurveyUseCase
import kotlinx.coroutines.launch

class NewPollViewModel(
    private val addSurveyUseCase: AddSurveyUseCase,
    private val addApplicationUseCase: AddApplicationUseCase,
    private val preferences: PreferencesManager,
) : ViewModel() {

    sealed class State {
        object Idle : State()
        object Loading : State()
        object SurveyPublished : State()
        object ApplicationSubmitted : State()
        data class Error(val message: String) : State()
    }

    private val _state = MutableLiveData<State>(State.Idle)
    val state: LiveData<State> = _state

    fun submit(title: String, first: String, second: String, third: String) {
        val access = preferences.getAccess()
        viewModelScope.launch {
            _state.value = State.Loading
            try {
                if (access == "admin" || access == "developer") {
                    addSurveyUseCase(Survey(-1, title, first, second, third))
                    _state.value = State.SurveyPublished
                } else {
                    val email = preferences.getEmail()
                    addApplicationUseCase(
                        SurveyApplication(-1, title, first, second, third, ApplicationStatus.PENDING, email)
                    )
                    _state.value = State.ApplicationSubmitted
                }
            } catch (e: Exception) {
                _state.value = State.Error(e.message ?: "")
            }
        }
    }

    class Factory(
        private val addSurveyUseCase: AddSurveyUseCase,
        private val addApplicationUseCase: AddApplicationUseCase,
        private val preferences: PreferencesManager,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return NewPollViewModel(addSurveyUseCase, addApplicationUseCase, preferences) as T
        }
    }
}
