package com.example.we_vote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.we_vote.data.local.PreferencesManager

class ProfileViewModel(
    private val preferences: PreferencesManager,
) : ViewModel() {

    data class ProfileData(val name: String, val city: String)

    private val _profile = MutableLiveData<ProfileData>()
    val profile: LiveData<ProfileData> = _profile

    fun loadProfile() {
        _profile.value = ProfileData(
            name = preferences.getName(),
            city = preferences.getCity(),
        )
    }

    class Factory(
        private val preferences: PreferencesManager,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(preferences) as T
        }
    }
}
