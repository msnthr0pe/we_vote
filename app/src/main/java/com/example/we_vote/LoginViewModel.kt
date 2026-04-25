package com.example.we_vote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.we_vote.data.local.PreferencesManager
import com.example.we_vote.domain.usecase.user.GetUserUseCase
import com.example.we_vote.domain.usecase.user.LoginUseCase
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val preferences: PreferencesManager,
) : ViewModel() {

    sealed class State {
        object Idle : State()
        object Loading : State()
        object Success : State()
        object LoginFailed : State()
        data class Error(val message: String) : State()
    }

    private val _state = MutableLiveData<State>(State.Idle)
    val state: LiveData<State> = _state

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = State.Loading
            try {
                val success = loginUseCase(email, password)
                if (success) {
                    val user = getUserUseCase(email, password)
                    preferences.saveUser(user)
                    _state.value = State.Success
                } else {
                    _state.value = State.LoginFailed
                }
            } catch (e: Exception) {
                _state.value = State.Error(e.message ?: "")
            }
        }
    }

    class Factory(
        private val loginUseCase: LoginUseCase,
        private val getUserUseCase: GetUserUseCase,
        private val preferences: PreferencesManager,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(loginUseCase, getUserUseCase, preferences) as T
        }
    }
}
