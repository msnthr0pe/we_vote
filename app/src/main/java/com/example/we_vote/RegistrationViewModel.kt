package com.example.we_vote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.we_vote.domain.model.User
import com.example.we_vote.domain.usecase.city.GetCitiesUseCase
import com.example.we_vote.domain.usecase.user.RegisterUseCase
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val registerUseCase: RegisterUseCase,
    private val getCitiesUseCase: GetCitiesUseCase,
) : ViewModel() {

    sealed class State {
        object Idle : State()
        object Loading : State()
        object Success : State()
        data class Error(val message: String) : State()
    }

    private val _state = MutableLiveData<State>(State.Idle)
    val state: LiveData<State> = _state

    private val _cities = MutableLiveData<List<String>>()
    val cities: LiveData<List<String>> = _cities

    init {
        viewModelScope.launch {
            try { _cities.value = getCitiesUseCase() } catch (_: Exception) { _cities.value = emptyList() }
        }
    }

    fun register(name: String, email: String, dob: String, city: String, password: String) {
        viewModelScope.launch {
            _state.value = State.Loading
            try {
                registerUseCase(User(name, email, dob, city, password, "user"))
                _state.value = State.Success
            } catch (e: Exception) {
                _state.value = State.Error(e.message ?: "")
            }
        }
    }

    class Factory(
        private val registerUseCase: RegisterUseCase,
        private val getCitiesUseCase: GetCitiesUseCase,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return RegistrationViewModel(registerUseCase, getCitiesUseCase) as T
        }
    }
}
