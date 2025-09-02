package com.example.we_vote

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.we_vote.databinding.FragmentVotingBinding

class VotingFragment : Fragment() {

    private var _binding: FragmentVotingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentVotingBinding.inflate(layoutInflater, container, false)

        getArgs()
        configureCheckboxLogic()

        return binding.root
    }

    private fun configureCheckboxLogic() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getArgs() {
        val args by navArgs<VotingFragmentArgs>()

        binding.votingTitle.text = args.title
        binding.firstCheckbox.text = args.firstChoice
        binding.secondCheckbox.text = args.secondChoice
        binding.thirdCheckbox.text = args.thirdChoice
    }
}