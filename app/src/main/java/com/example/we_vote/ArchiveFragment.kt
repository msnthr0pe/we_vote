package com.example.we_vote

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.we_vote.databinding.FragmentArchiveBinding
import com.example.we_vote.ktor.ApiClient
import com.example.we_vote.ktor.DTOs
import com.example.we_vote.recycler.ArchiveAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume

class ArchiveFragment : Fragment() {

    private var _binding: FragmentArchiveBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ArchiveAdapter
    private lateinit var access: String

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
        val prefs = requireActivity().getSharedPreferences("credentials", Context.MODE_PRIVATE)
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
                val surveys = withContext(Dispatchers.IO) {
                    ApiClient.authApi.getArchivedSurveys()
                }.sortedByDescending { it.id }

                val items = surveys.map { survey ->
                    val stats = fetchSurveyStats(survey)
                    ArchiveAdapter.ArchiveItem(survey, stats)
                }

                adapter = ArchiveAdapter(items, access) { survey, position, _ ->
                    showDeleteDialog(survey, position)
                }
                recyclerView.adapter = adapter

            } catch (e: Exception) {
                Log.e("WE_VOTE", "Ошибка: ${e.message}")
            }
            binding.archiveProgressBar.isVisible = false
        }
    }

    private suspend fun fetchSurveyStats(survey: DTOs.SurveyDTO): DTOs.SurveyVotesDTO? =
        suspendCancellableCoroutine { continuation ->
            val call = ApiClient.authApi.getSurveyVotes(DTOs.SurveyIdRequest(survey.id))
            call.enqueue(object : Callback<DTOs.SurveyVotesDTO> {
                override fun onResponse(call: Call<DTOs.SurveyVotesDTO?>, response: Response<DTOs.SurveyVotesDTO?>) {
                    continuation.resume(if (response.isSuccessful) response.body() else null)
                }
                override fun onFailure(call: Call<DTOs.SurveyVotesDTO?>, t: Throwable) {
                    continuation.resume(null)
                }
            })
            continuation.invokeOnCancellation { call.cancel() }
        }

    private fun showDeleteDialog(survey: DTOs.SurveyDTO, position: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_window, null)
        val btnConfirm = dialogView.findViewById<Button>(R.id.dialog_confirm)
        val btnCancel = dialogView.findViewById<Button>(R.id.dialog_cancel)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        btnConfirm.setOnClickListener {
            dialog.dismiss()
            deleteSurvey(survey.id)
            adapter.removeItem(position)
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun deleteSurvey(idSurvey: Int) {
        val call = ApiClient.authApi.deleteSurveyInfo(DTOs.SurveyIdRequest(idSurvey))
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                if (!response.isSuccessful) {
                    Toast.makeText(requireContext(), "Ошибка при удалении", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Toast.makeText(requireContext(), "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
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
