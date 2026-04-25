package com.example.we_vote.data.local

import android.content.Context
import androidx.core.content.edit
import com.example.we_vote.domain.model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferencesManager(private val context: Context) {

    private val credPrefs get() =
        context.getSharedPreferences("credentials", Context.MODE_PRIVATE)
    private val votingPrefs get() =
        context.getSharedPreferences("voting_state", Context.MODE_PRIVATE)
    private val historyPrefs get() =
        context.getSharedPreferences("search_history", Context.MODE_PRIVATE)

    fun saveUser(user: User) {
        credPrefs.edit {
            putString("email", user.email)
            putString("name", user.name)
            putString("dob", user.dob)
            putString("city", user.city)
            putString("access", user.access)
        }
    }

    fun getEmail(): String = credPrefs.getString("email", "") ?: ""
    fun getName(): String = credPrefs.getString("name", "") ?: ""
    fun getCity(): String = credPrefs.getString("city", "") ?: ""
    fun getDob(): String = credPrefs.getString("dob", "") ?: ""
    fun getAccess(): String = credPrefs.getString("access", "user") ?: "user"

    fun getUserSnapshot(): User = User(
        name = getName(),
        email = getEmail(),
        dob = getDob(),
        city = getCity(),
        password = "",
        access = getAccess(),
    )

    fun getVotedIds(): MutableSet<Int> {
        val email = getEmail()
        return votingPrefs.getStringSet("voted_$email", emptySet())
            .orEmpty()
            .mapNotNullTo(mutableSetOf()) { it.toIntOrNull() }
    }

    fun saveVotedId(surveyId: Int) {
        val email = getEmail()
        val key = "voted_$email"
        val existing = votingPrefs.getStringSet(key, emptySet()) ?: emptySet()
        votingPrefs.edit { putStringSet(key, existing + surveyId.toString()) }
    }

    fun getSearchHistory(): MutableList<String> {
        val json = historyPrefs.getString("history", null) ?: return mutableListOf()
        return Gson().fromJson(json, object : TypeToken<MutableList<String>>() {}.type)
    }

    fun saveSearchHistory(history: List<String>) {
        historyPrefs.edit { putString("history", Gson().toJson(history)) }
    }

    fun clearSearchHistory() {
        historyPrefs.edit { remove("history") }
    }
}
