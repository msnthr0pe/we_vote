package com.example.we_vote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.we_vote.data.local.PreferencesManager
import com.example.we_vote.domain.model.User
import com.example.we_vote.domain.usecase.user.UpdateUserUseCase
import kotlinx.coroutines.launch

class PersonalInfoChangeViewModel(
    private val updateUserUseCase: UpdateUserUseCase,
    val preferences: PreferencesManager,
) : ViewModel() {

    sealed class State {
        object Idle : State()
        object Loading : State()
        object Success : State()
        data class Error(val message: String) : State()
    }

    private val _state = MutableLiveData<State>(State.Idle)
    val state: LiveData<State> = _state

    fun updateUser(user: User) {
        viewModelScope.launch {
            _state.value = State.Loading
            try {
                updateUserUseCase(user)
                preferences.saveUser(user)
                _state.value = State.Success
            } catch (e: Exception) {
                _state.value = State.Error(e.message ?: "")
            }
        }
    }

    class Factory(
        private val updateUserUseCase: UpdateUserUseCase,
        private val preferences: PreferencesManager,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return PersonalInfoChangeViewModel(updateUserUseCase, preferences) as T
        }
    }
}
