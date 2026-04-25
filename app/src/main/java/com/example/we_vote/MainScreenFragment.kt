package com.example.we_vote

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.we_vote.databinding.FragmentMainScreenBinding
import com.example.we_vote.domain.model.Survey
import com.example.we_vote.recycler.HistoryAdapter
import com.example.we_vote.recycler.MainSurveyAdapter

class MainScreenFragment : Fragment() {

    private var _binding: FragmentMainScreenBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainScreenViewModel by viewModels {
        val app = requireActivity().application as WeVoteApplication
        MainScreenViewModel.Factory(
            app.container.getSurveysUseCase,
            app.container.getSurveyVotesUseCase,
            app.container.archiveSurveyUseCase,
            app.container.preferences,
        )
    }

    private lateinit var adapter: MainSurveyAdapter
    private lateinit var access: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMainScreenBinding.inflate(layoutInflater, container, false)

        access = (requireActivity().application as WeVoteApplication).container.preferences.getAccess()

        setupNavigation()
        setupRecycler()
        setupSearch()
        setupSearchHistory()
        observeState()

        viewModel.loadSurveys()

        return binding.root
    }

    private fun setupRecycler() {
        val votedIds = viewModel.preferences.getVotedIds()
        adapter = MainSurveyAdapter(
            surveys = emptyList(),
            access = access,
            votedIds = votedIds,
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
            onArchiveClick = { survey, _, _ ->
                showArchiveDialog(survey)
            },
            onFetchResults = { surveyId, onResult ->
                viewModel.fetchSurveyVotes(surveyId, onResult)
            },
        )
        binding.mainRecycler.layoutManager = LinearLayoutManager(activity)
        binding.mainRecycler.adapter = adapter

        binding.btnRetry.setOnClickListener { viewModel.loadSurveys() }
    }

    private fun observeState() {
        viewModel.surveysState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is MainScreenViewModel.SurveysState.Loading -> {
                    binding.progressBarMain.isVisible = true
                    binding.layoutError.visibility = View.GONE
                }
                is MainScreenViewModel.SurveysState.Success -> {
                    binding.progressBarMain.isVisible = false
                    binding.layoutError.visibility = View.GONE
                    adapter.updateList(state.surveys)
                    binding.noResults.isVisible = state.surveys.isEmpty()
                    binding.mainRecycler.isVisible = state.surveys.isNotEmpty()
                }
                is MainScreenViewModel.SurveysState.Error -> {
                    binding.progressBarMain.isVisible = false
                    binding.layoutError.visibility = View.VISIBLE
                }
            }
        }

        viewModel.archiveResult.observe(viewLifecycleOwner) { success ->
            if (!success) Toast.makeText(requireContext(), "Ошибка архивирования", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSearch() {
        binding.btnClearSearch.setOnClickListener {
            binding.searchEditText.text.clear()
            binding.btnClearSearch.visibility = View.GONE
            hideKeyboard()
        }

        binding.searchEditText.setOnEditorActionListener { _, _, _ ->
            val query = binding.searchEditText.text.toString()
            saveToHistory(query)
            setupHistory()
            false
        }

        binding.searchEditText.doOnTextChanged { s, _, _, _ ->
            if (_binding == null) return@doOnTextChanged
            binding.btnClearSearch.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            if (!s.isNullOrEmpty()) {
                binding.historyDropdown.visibility = View.GONE
                binding.mainRecycler.isVisible = true
                viewModel.filter(s.toString())
            } else {
                viewModel.filter("")
                if (binding.searchEditText.hasFocus()) {
                    setupHistory()
                    binding.historyDropdown.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupSearchHistory() {
        binding.clearSearchHistory.setOnClickListener {
            if (_binding != null) {
                viewModel.preferences.clearSearchHistory()
                setupHistory()
            }
        }

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (_binding == null) return@setOnFocusChangeListener
            if (hasFocus && binding.searchEditText.text.isNullOrEmpty()) {
                setupHistory()
                binding.historyDropdown.visibility = View.VISIBLE
            } else if (!hasFocus && binding.searchEditText.text.isNullOrEmpty()) {
                binding.historyDropdown.visibility = View.GONE
            }
        }
    }

    private fun setupHistory() {
        if (_binding == null) return
        val history = viewModel.preferences.getSearchHistory()
        binding.clearSearchHistory.isVisible = history.isNotEmpty()
        binding.historyPlaceholder.isVisible = history.isEmpty()
        binding.historyRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.historyRecycler.adapter = HistoryAdapter(history) { selected ->
            binding.searchEditText.setText(selected)
            binding.searchEditText.setSelection(selected.length)
            binding.searchEditText.clearFocus()
            binding.historyDropdown.visibility = View.GONE
        }
    }

    private fun saveToHistory(query: String) {
        if (query.isBlank()) return
        val history = viewModel.preferences.getSearchHistory()
        history.remove(query)
        history.add(0, query)
        viewModel.preferences.saveSearchHistory(history.take(5))
    }

    private fun showArchiveDialog(survey: Survey) {
        if (!isAdded) return
        val dialogView = layoutInflater.inflate(R.layout.dialog_window, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        dialogView.findViewById<Button>(R.id.dialog_confirm).setOnClickListener {
            dialog.dismiss()
            viewModel.archiveSurvey(survey)
        }
        dialogView.findViewById<Button>(R.id.dialog_cancel).setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }

    private fun setupNavigation() {
        VotingUtil.setBottomBar(access, binding.bottomNav)
        binding.bottomNav.menu.findItem(R.id.nav_home)?.isChecked = true

        val requestAction = if (access == "admin" || access == "developer")
            R.id.action_mainScreenFragment_to_newApplicationsFragment
        else
            R.id.action_mainScreenFragment_to_myApplicationsFragment

        binding.bottomNav.setOnItemSelectedListener { item ->
            VotingUtil.setupNavigation(
                this, item.itemId,
                R.id.action_mainScreenFragment_self,
                R.id.action_mainScreenFragment_to_newPollFragment,
                R.id.action_mainScreenFragment_to_profileFragment,
                R.id.action_mainScreenFragment_to_archiveFragment,
                requestAction,
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null) binding.bottomNav.menu.findItem(R.id.nav_home)?.isChecked = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
