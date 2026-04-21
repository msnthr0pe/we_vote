package com.example.we_vote

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.we_vote.databinding.FragmentVotingBinding
import com.example.we_vote.ktor.ApiClient
import com.example.we_vote.ktor.DTOs
import kotlinx.coroutines.launch

class VotingFragment : Fragment() {

    private var _binding: FragmentVotingBinding? = null
    private val binding get() = _binding!!
    private var surveyId: Int = 0
    private lateinit var checkBoxes: List<CheckBox>
    private var currentVoteId = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentVotingBinding.inflate(layoutInflater, container, false)

        checkBoxes = listOf(
            binding.firstCheckbox,
            binding.secondCheckbox,
            binding.thirdCheckbox,
        )

        getArgs()
        configureCheckboxLogic()

        binding.sendVoteBtn.setOnClickListener {
            if (currentVoteId == 0) {
                Toast.makeText(activity, "Выберите один из вариантов", Toast.LENGTH_SHORT).show()
            } else {
                binding.progressBarVoting.isVisible = true
                uploadVote()
            }
        }
        return binding.root
    }

    private fun getArgs() {
        val args by navArgs<VotingFragmentArgs>()
        surveyId = args.id
        binding.votingTitle.text = args.title
        binding.firstCheckbox.text = args.firstChoice
        binding.secondCheckbox.text = args.secondChoice
        binding.thirdCheckbox.text = args.thirdChoice
    }

    private fun configureCheckboxLogic() {
        checkBoxes.forEachIndexed { index, element ->
            element.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    currentVoteId = index + 1
                    uncheckOtherCheckboxes(index)
                }
            }
        }
    }

    private fun uncheckOtherCheckboxes(index: Int) {
        checkBoxes.forEachIndexed { id, box ->
            if (index != id) {
                box.isChecked = false
            }
        }
    }

    private fun uploadVote() {
        lifecycleScope.launch {
            try {
                val prefs = requireContext().getSharedPreferences(
                    "credentials",
                    Context.MODE_PRIVATE
                )
                val userEmail = prefs.getString("email", "") ?: ""

                val voteData = DTOs.UsersSurveysDTO(
                    userEmail = userEmail,
                    surveyId = surveyId,
                    vote = currentVoteId
                )

                // Отправляем через Retrofit
                ApiClient.authApi.addUserSurvey(voteData).enqueue(object : retrofit2.Callback<Void> {
                    override fun onResponse(call: retrofit2.Call<Void>, response: retrofit2.Response<Void>) {
                        binding.progressBarVoting.isVisible = false
                        if (response.isSuccessful) {
                            saveVotedSurvey(userEmail, surveyId)
                            Toast.makeText(activity,
                                getString(R.string.successful_voting_message), Toast.LENGTH_SHORT).show()
                            requireActivity().onBackPressedDispatcher.onBackPressed()
                        } else {
                            Toast.makeText(activity, getString(R.string.voting_error), Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                        binding.progressBarVoting.isVisible = false
                        Log.e("VotingFragment", "Error: ${t.message}")
                        Toast.makeText(activity, getString(R.string.voting_error), Toast.LENGTH_SHORT).show()
                    }
                })

            } catch (e: Exception) {
                Log.e("VotingFragment", "Error: ${e.message}")
                Toast.makeText(activity, getString(R.string.voting_error), Toast.LENGTH_SHORT).show()
                binding.progressBarVoting.isVisible = false
            }
        }
    }

    private fun saveVotedSurvey(email: String, surveyId: Int) {
        val prefs = requireContext().getSharedPreferences("voting_state", Context.MODE_PRIVATE)
        val key = "voted_$email"
        val existing = prefs.getStringSet(key, emptySet()) ?: emptySet()
        prefs.edit().putStringSet(key, existing + surveyId.toString()).apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}