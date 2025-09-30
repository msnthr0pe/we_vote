package com.example.we_vote

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.we_vote.databinding.FragmentArchiveBinding
import com.example.we_vote.ktor.ApiClient
import com.example.we_vote.ktor.DTOs
import com.example.we_vote.recycler.SurveyAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ArchiveFragment : Fragment() {

    private var _binding: FragmentArchiveBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SurveyAdapter
    private lateinit var access: String
    private lateinit var surveys: MutableList<DTOs.SurveyDTO>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentArchiveBinding.inflate(layoutInflater, container, false)

        setupNavigation()
        setupArchiveRecycler()

        return binding.root
    }

    private fun setupNavigation() {
        val prefs = requireActivity().getSharedPreferences("credentials",
            Context.MODE_PRIVATE)
        access = prefs.getString("access", "user").toString()
        VotingUtil.setBottomBar(access, binding.bottomNav)

        binding.bottomNav.setOnItemSelectedListener { item ->
            VotingUtil.setupNavigation(this, item.itemId,
                R.id.action_archiveFragment_to_mainScreenFragment,
                R.id.action_archiveFragment_to_newPollFragment,
                R.id.action_archiveFragment_to_profileFragment,
                R.id.action_archiveFragment_self)
        }
    }

    private fun setupArchiveRecycler() {
        recyclerView = binding.recyclerArchive
        recyclerView.layoutManager = LinearLayoutManager(activity)
        getSurveysForRecycler()
    }

    private fun getSurveysForRecycler() {
        binding.archiveProgressBar.isVisible = true
        lifecycleScope.launch {
            try {
                surveys = withContext(Dispatchers.IO) {
                    ApiClient.authApi.getSurveys()
                }

                adapter = SurveyAdapter(surveys, access, { survey ->

                }, {survey, position, surveyAmount ->  })
                recyclerView.adapter = adapter

            } catch (e: Exception) {
                Log.e("WE_VOTE", "Ошибка: ${e.message}")
            }
            binding.archiveProgressBar.isVisible = false
        }
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNav.menu.findItem(R.id.nav_archive).isChecked = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}