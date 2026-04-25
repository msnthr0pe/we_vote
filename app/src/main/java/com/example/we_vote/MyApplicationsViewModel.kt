package com.example.we_vote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.we_vote.data.local.PreferencesManager
import com.example.we_vote.domain.model.SurveyApplication
import com.example.we_vote.domain.usecase.application.DeleteApplicationUseCase
import com.example.we_vote.domain.usecase.application.GetUserApplicationsUseCase
import kotlinx.coroutines.launch

class MyApplicationsViewModel(
    private val getUserApplicationsUseCase: GetUserApplicationsUseCase,
    private val deleteApplicationUseCase: DeleteApplicationUseCase,
    private val preferences: PreferencesManager,
) : ViewModel() {

    sealed class State {
        object Loading : State()
        data class Success(val applications: List<SurveyApplication>) : State()
        data class Error(val message: String) : State()
    }

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    private val _cancelResult = MutableLiveData<Result<Int>>()
    val cancelResult: LiveData<Result<Int>> = _cancelResult

    private val currentItems = mutableListOf<SurveyApplication>()

    fun loadApplications() {
        viewModelScope.launch {
            _state.value = State.Loading
            try {
                val email = preferences.getEmail()
                val apps = getUserApplicationsUseCase(email)
                currentItems.clear()
                currentItems.addAll(apps)
                _state.value = State.Success(currentItems.toList())
            } catch (e: Exception) {
                _state.value = State.Error(e.message ?: "")
            }
        }
    }

    fun cancelApplication(id: Int, position: Int) {
        viewModelScope.launch {
            try {
                deleteApplicationUseCase(id)
                currentItems.removeAt(position)
                _state.value = State.Success(currentItems.toList())
                _cancelResult.value = Result.success(position)
            } catch (e: Exception) {
                _cancelResult.value = Result.failure(e)
            }
        }
    }

    class Factory(
        private val getUserApplicationsUseCase: GetUserApplicationsUseCase,
        private val deleteApplicationUseCase: DeleteApplicationUseCase,
        private val preferences: PreferencesManager,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return MyApplicationsViewModel(getUserApplicationsUseCase, deleteApplicationUseCase, preferences) as T
        }
    }
}
