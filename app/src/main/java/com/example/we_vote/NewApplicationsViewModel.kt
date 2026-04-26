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
import com.example.we_vote.domain.usecase.application.GetApplicationsUseCase
import com.example.we_vote.domain.usecase.application.UpdateApplicationStatusUseCase
import com.example.we_vote.domain.usecase.survey.AddSurveyUseCase
import kotlinx.coroutines.launch

class NewApplicationsViewModel(
    private val getApplicationsUseCase: GetApplicationsUseCase,
    private val updateApplicationStatusUseCase: UpdateApplicationStatusUseCase,
    private val addSurveyUseCase: AddSurveyUseCase,
    private val preferences: PreferencesManager,
) : ViewModel() {

    sealed class State {
        object Loading : State()
        data class Success(val applications: List<SurveyApplication>) : State()
        data class Error(val message: String) : State()
    }

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    private val allApplications = mutableListOf<SurveyApplication>()
    var currentFilter: ApplicationStatus = ApplicationStatus.PENDING
        private set

    fun loadApplications() {
        viewModelScope.launch {
            _state.value = State.Loading
            try {
                val apps = getApplicationsUseCase(preferences.getEmail())
                allApplications.clear()
                allApplications.addAll(apps)
                applyFilter(currentFilter)
            } catch (e: Exception) {
                _state.value = State.Error(e.message ?: "")
            }
        }
    }

    fun applyFilter(filter: ApplicationStatus) {
        currentFilter = filter
        val filtered = allApplications.filter { it.status == filter }
        _state.value = State.Success(filtered)
    }

    fun updateStatus(application: SurveyApplication, newStatus: ApplicationStatus) {
        viewModelScope.launch {
            try {
                updateApplicationStatusUseCase(application.id, newStatus)
                val idx = allApplications.indexOfFirst { it.id == application.id }
                if (idx != -1) allApplications[idx] = application.copy(status = newStatus)
                if (newStatus == ApplicationStatus.ACCEPTED) {
                    publishSurvey(application)
                }
                applyFilter(currentFilter)
            } catch (e: Exception) {
                _state.value = State.Error(e.message ?: "")
            }
        }
    }

    private suspend fun publishSurvey(application: SurveyApplication) {
        runCatching {
            addSurveyUseCase(
                Survey(-1, application.title, application.firstChoice, application.secondChoice, application.thirdChoice, application.userCity)
            )
        }
    }

    class Factory(
        private val getApplicationsUseCase: GetApplicationsUseCase,
        private val updateApplicationStatusUseCase: UpdateApplicationStatusUseCase,
        private val addSurveyUseCase: AddSurveyUseCase,
        private val preferences: PreferencesManager,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return NewApplicationsViewModel(getApplicationsUseCase, updateApplicationStatusUseCase, addSurveyUseCase, preferences) as T
        }
    }
}
