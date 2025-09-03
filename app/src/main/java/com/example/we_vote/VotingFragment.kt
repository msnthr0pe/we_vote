package com.example.we_vote

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.example.we_vote.databinding.FragmentVotingBinding
import com.example.we_vote.ktor.ApiClient
import com.example.we_vote.ktor.DTOs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
                binding.progressBarVoting.isVisible = false
            }
        }
        return binding.root
    }

    private fun uploadVote() {
        val prefs = requireContext().getSharedPreferences(
            "credentials",
            Context.MODE_PRIVATE
        )

        val call = ApiClient.authApi.addUserSurvey(
            DTOs.UsersSurveysDTO(
                userEmail = prefs.getString("email", "") ?: "",
                surveyId = surveyId,
                vote = currentVoteId
            )
        )
        call.enqueue(object : Callback<Void>{
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(activity,
                        getString(R.string.successful_voting_message), Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(activity, getString(R.string.voting_error), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Toast.makeText(activity, getString(R.string.voting_error), Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun configureCheckboxLogic() {
        checkBoxes.forEachIndexed {index, element ->
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getArgs() {
        val args by navArgs<VotingFragmentArgs>()

        surveyId = args.id
        binding.votingTitle.text = args.title
        binding.firstCheckbox.text = args.firstChoice
        binding.secondCheckbox.text = args.secondChoice
        binding.thirdCheckbox.text = args.thirdChoice
    }
}