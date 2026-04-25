package com.example.we_vote

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.we_vote.databinding.FragmentMainScreenBinding
import com.example.we_vote.ktor.ApiClient
import com.example.we_vote.ktor.DTOs
import com.example.we_vote.recycler.MainSurveyAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.core.graphics.drawable.toDrawable
import androidx.core.widget.doOnTextChanged
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import androidx.core.content.edit
import com.example.we_vote.recycler.HistoryAdapter

class MainScreenFragment : Fragment() {

    private var _binding: FragmentMainScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MainSurveyAdapter
    private lateinit var access: String
    private lateinit var surveys: MutableList<DTOs.SurveyDTO>
    private val searchQuery = MutableStateFlow("")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentMainScreenBinding.inflate(layoutInflater, container, false)

        setupNavigation()
        setupMainRecycler()
        setupSearch()
        setupSearchHistory()

        return binding.root
    }

    private fun setupSearchHistory() {
        binding.clearSearchHistory.setOnClickListener {
            if (_binding != null) {
                clearSearchHistory()
                setupHistory()
            }
        }

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (_binding != null) {
                if (hasFocus && binding.searchEditText.text.isNullOrEmpty()) {
                    setupHistory()
                    binding.historyDropdown.visibility = View.VISIBLE
                } else if (!hasFocus && binding.searchEditText.text.isNullOrEmpty()) {
                    binding.historyDropdown.visibility = View.GONE
                }
            }
        }
    }

    private fun makeMainRecyclerVisible() {
        if (_binding != null) {
            binding.historyDropdown.visibility = View.GONE
            binding.mainRecycler.isVisible = true
        }
    }

    private fun setupHistory() {
        if (_binding == null) return

        val history = loadSearchHistory()
        val hasHistory = history.isNotEmpty()
        binding.clearSearchHistory.visibility = if (hasHistory) View.VISIBLE else View.GONE
        binding.historyPlaceholder.visibility = if (hasHistory) View.GONE else View.VISIBLE
        binding.historyRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.historyRecycler.adapter = HistoryAdapter(history) { selected ->
            binding.searchEditText.setText(selected)
            binding.searchEditText.setSelection(selected.length)
            binding.searchEditText.clearFocus()
            binding.historyDropdown.visibility = View.GONE
        }
    }

    @OptIn(FlowPreview::class)
    private fun setupSearch() {
        val clearButton = binding.btnClearSearch
        clearButton.setOnClickListener {
            if (_binding != null) {
                binding.searchEditText.text.clear()
                clearButton.visibility = View.GONE
                hideKeyboard()
            }
        }

        binding.searchEditText.setOnEditorActionListener { _, _, _ ->
            val query = binding.searchEditText.text.toString()
            getHistory(query)
            setupHistory()
            false
        }

        binding.searchEditText.doOnTextChanged { s, start, before, count ->
            lifecycleScope.launch {
                searchQuery.value = s.toString()
                searchQuery
                    .debounce(1000)
                    .distinctUntilChanged()
                    .flowOn(Dispatchers.Default)
                    .collectLatest { query ->
                        if (isAdded && _binding != null) {
                            filterCards(query)
                        }
                    }
            }
            if (_binding != null) {
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                if (!s.isNullOrEmpty()) {
                    binding.historyDropdown.visibility = View.GONE
                    binding.mainRecycler.isVisible = true
                } else if (binding.searchEditText.hasFocus()) {
                    setupHistory()
                    binding.historyDropdown.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }

    private fun getHistory(query: String) {
        if (query != "") {
            val history = loadSearchHistory()
            history.remove(query)
            history.add(0, query)
            val trimmed = history.take(5)
            saveSearchHistory(trimmed)
        }
    }

    private fun filterCards(query: String) {
        // Проверяем, что фрагмент еще активен
        if (!isAdded || _binding == null) return

        val filtered = if (query.isEmpty()) {
            surveys
        } else {
            surveys.filter { it.title.contains(query, ignoreCase = true) }
        }

        try {
            adapter.updateList(filtered)
            binding.noResults.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
            recyclerView.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE
        } catch (_: Exception) {
            Log.e("WE_VOTE", "Exception occurred")
        }
    }

    private fun setupNavigation() {
        val prefs = requireActivity().getSharedPreferences("credentials",
            Context.MODE_PRIVATE)
        access = prefs.getString("access", "user") ?: "user"
        VotingUtil.setBottomBar(access, binding.bottomNav)

        binding.bottomNav.menu.findItem(R.id.nav_home)?.isChecked = true

        val requestAction = if (access == "admin" || access == "developer")
            R.id.action_mainScreenFragment_to_newApplicationsFragment
        else
            R.id.action_mainScreenFragment_to_myApplicationsFragment

        binding.bottomNav.setOnItemSelectedListener { item ->
            VotingUtil.setupNavigation(this, item.itemId,
                R.id.action_mainScreenFragment_self,
                R.id.action_mainScreenFragment_to_newPollFragment,
                R.id.action_mainScreenFragment_to_profileFragment,
                R.id.action_mainScreenFragment_to_archiveFragment,
                requestAction)
        }
    }

    private fun setupMainRecycler() {
        recyclerView = binding.mainRecycler
        recyclerView.layoutManager = LinearLayoutManager(activity)
        getSurveysForRecycler()

        binding.btnRetry.setOnClickListener {
            getSurveysForRecycler()
        }
    }

    private fun getSurveysForRecycler() {
        // Проверяем, что фрагмент еще активен
        if (!isAdded || _binding == null) return

        binding.progressBarMain.isVisible = true
        lifecycleScope.launch {
            try {
                surveys = withContext(Dispatchers.IO) {
                    ApiClient.authApi.getSurveys()
                }

                // Проверяем, что фрагмент еще активен после получения данных
                if (!isAdded || _binding == null) return@launch

                val votedIds = loadVotedIds()
                adapter = MainSurveyAdapter(
                    surveys, access, votedIds,
                    onVoteClick = { survey ->
                        val action = MainScreenFragmentDirections.actionMainScreenFragmentToVotingFragment(
                            id = survey.id,
                            title = survey.title,
                            firstChoice = survey.firstChoice,
                            secondChoice = survey.secondChoice,
                            thirdChoice = survey.thirdChoice,
                        )
                        findNavController().navigate(action)
                    },
                    onArchiveClick = { survey, position, surveyAmount ->
                        showEditDialog(survey, position, surveyAmount)
                    },
                    onFetchResults = { surveyId, onResult ->
                        fetchSurveyResults(surveyId, onResult)
                    }
                )

                recyclerView.adapter = adapter
                binding.layoutError.visibility = View.GONE
                binding.noResults.isVisible = surveys.isEmpty()

            } catch (e: Exception) {
                // Проверяем, что фрагмент еще активен
                if (isAdded && _binding != null) {
                    Log.e("WE_VOTE", "Ошибка: ${e.message}")
                    binding.layoutError.visibility = View.VISIBLE
                }
            } finally {
                if (isAdded && _binding != null) {
                    binding.progressBarMain.isVisible = false
                }
            }
        }
    }

    private fun loadVotedIds(): MutableSet<Int> {
        val email = requireActivity()
            .getSharedPreferences("credentials", Context.MODE_PRIVATE)
            .getString("email", "") ?: ""
        val prefs = requireContext().getSharedPreferences("voting_state", Context.MODE_PRIVATE)
        return prefs.getStringSet("voted_$email", emptySet())
            .orEmpty()
            .mapNotNullTo(mutableSetOf()) { it.toIntOrNull() }
    }

    private fun fetchSurveyResults(surveyId: Int, onResult: (DTOs.SurveyVotesDTO?) -> Unit) {
        val call = ApiClient.authApi.getSurveyVotes(DTOs.SurveyIdRequest(surveyId))
        call.enqueue(object : Callback<DTOs.SurveyVotesDTO> {
            override fun onResponse(call: Call<DTOs.SurveyVotesDTO?>, response: Response<DTOs.SurveyVotesDTO?>) {
                onResult(if (response.isSuccessful) response.body() else null)
            }
            override fun onFailure(call: Call<DTOs.SurveyVotesDTO?>, t: Throwable) {
                onResult(null)
            }
        })
    }

    private fun showEditDialog(survey: DTOs.SurveyDTO, position: Int, surveyAmount: Int) {
        if (!isAdded) return

        val dialogView = layoutInflater.inflate(R.layout.dialog_window, null)
        val btnConfirm = dialogView.findViewById<Button>(R.id.dialog_confirm)
        val btnCancel = dialogView.findViewById<Button>(R.id.dialog_cancel)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        btnConfirm.setOnClickListener {
            dialog.dismiss()
            archiveSurvey(survey.title)
            surveys.removeAt(position)
            adapter.notifyItemRemoved(position)
            adapter.notifyItemRangeChanged(position, surveyAmount)
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun archiveSurvey(title: String) {
        val call = ApiClient.authApi.archiveSurvey(DTOs.TitleDTO(title))
        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>,
            ) {
                if (!response.isSuccessful) {
                    Toast.makeText(requireContext(), "Ошибка", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Toast.makeText(requireContext(), "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getHistoryPrefs(): SharedPreferences {
        return requireContext().getSharedPreferences("search_history", Context.MODE_PRIVATE)
    }

    private fun loadSearchHistory(): MutableList<String> {
        val json = getHistoryPrefs().getString("history", null) ?: return mutableListOf()
        return Gson().fromJson(json, object : TypeToken<MutableList<String>>() {}.type)
    }

    private fun saveSearchHistory(history: List<String>) {
        val json = Gson().toJson(history)
        getHistoryPrefs().edit { putString("history", json) }
    }

    private fun clearSearchHistory() {
        getHistoryPrefs().edit { remove("history") }
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null) {
            binding.bottomNav.menu.findItem(R.id.nav_home)?.isChecked = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}