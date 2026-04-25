package com.example.we_vote

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.we_vote.databinding.FragmentVotingBinding

class VotingFragment : Fragment() {

    private var _binding: FragmentVotingBinding? = null
    private val binding get() = _binding!!

    private lateinit var checkBoxes: List<CheckBox>
    private var currentVoteId = 0

    private val viewModel: VotingViewModel by viewModels {
        val app = requireActivity().application as WeVoteApplication
        VotingViewModel.Factory(app.container.voteSurveyUseCase, app.container.preferences)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentVotingBinding.inflate(layoutInflater, container, false)

        checkBoxes = listOf(binding.firstCheckbox, binding.secondCheckbox, binding.thirdCheckbox)

        val args by navArgs<VotingFragmentArgs>()
        binding.votingTitle.text = args.title
        binding.firstCheckbox.text = args.firstChoice
        binding.secondCheckbox.text = args.secondChoice
        binding.thirdCheckbox.text = args.thirdChoice

        checkBoxes.forEachIndexed { index, box ->
            box.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    currentVoteId = index + 1
                    checkBoxes.forEachIndexed { i, b -> if (i != index) b.isChecked = false }
                }
            }
        }

        binding.sendVoteBtn.setOnClickListener {
            if (currentVoteId == 0) {
                Toast.makeText(activity, "Выберите один из вариантов", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.vote(args.id, currentVoteId)
            }
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is VotingViewModel.State.Loading -> binding.progressBarVoting.isVisible = true
                is VotingViewModel.State.Success -> {
                    binding.progressBarVoting.isVisible = false
                    Toast.makeText(activity, getString(R.string.successful_voting_message), Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
                is VotingViewModel.State.Error -> {
                    binding.progressBarVoting.isVisible = false
                    Toast.makeText(activity, getString(R.string.voting_error), Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
