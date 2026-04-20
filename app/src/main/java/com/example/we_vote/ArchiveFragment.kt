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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.we_vote.databinding.FragmentArchiveBinding
import com.example.we_vote.ktor.ApiClient
import com.example.we_vote.ktor.DTOs
import com.example.we_vote.recycler.SurveyAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
                    ApiClient.authApi.getArchivedSurveys()
                }

                adapter = SurveyAdapter(surveys, access, getString(R.string.results),
                    getString(R.string.delete_from_archive),{ survey ->

                    getVotingStatistics(survey) { votingStatistics ->
                        val action = ArchiveFragmentDirections.actionArchiveFragmentToArchivePollFragment(
                            id = survey.id,
                            title = survey.title,
                            firstChoice = survey.firstChoice,
                            firstChoiceValue = votingStatistics?.votesPercentage?.get(1) ?: 0,
                            secondChoice = survey.secondChoice,
                            secondChoiceValue = votingStatistics?.votesPercentage?.get(2) ?: 0,
                            thirdChoice = survey.thirdChoice,
                            thirdChoiceValue = votingStatistics?.votesPercentage?.get(3) ?: 0,
                        )
                        findNavController().navigate(action)
                    }

                }, {survey, position, surveyAmount ->  showEditDialog(survey, position, surveyAmount)})
                recyclerView.adapter = adapter

            } catch (e: Exception) {
                Log.e("WE_VOTE", "Ошибка: ${e.message}")
            }
            binding.archiveProgressBar.isVisible = false
        }
    }

    private fun showEditDialog(survey: DTOs.SurveyDTO, position: Int, surveyAmount: Int) {
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
            surveys.removeAt(position)
            adapter.notifyItemRemoved(position)
            adapter.notifyItemRangeChanged(position, surveyAmount)
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun deleteSurvey(idSurvey: Int) {
        val call = ApiClient.authApi.deleteSurveyInfo(DTOs.SurveyIdRequest(idSurvey))
        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void?>,
                response: Response<Void?>,
            ) {
                if (!response.isSuccessful) {
                    Toast.makeText(requireContext(), "Ошибка при удалении", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Toast.makeText(requireContext(), "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getVotingStatistics(surveyDTO: DTOs.SurveyDTO, onResult: (DTOs.SurveyVotesDTO?) -> Unit) {
        val call = ApiClient.authApi.getSurveyVotes(DTOs.SurveyIdRequest(surveyDTO.id))

        call.enqueue(object : Callback<DTOs.SurveyVotesDTO> {
            override fun onResponse(
                call: Call<DTOs.SurveyVotesDTO?>,
                response: Response<DTOs.SurveyVotesDTO?>,
            ) {
                if (response.isSuccessful) {
                    onResult(response.body())
                } else {
                    onResult(null)
                }
            }

            override fun onFailure(
                call: Call<DTOs.SurveyVotesDTO?>,
                t: Throwable,
            ) {
                Toast.makeText(activity, "Ошибка: ${t.message}", Toast.LENGTH_SHORT).show()
                onResult(null)
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