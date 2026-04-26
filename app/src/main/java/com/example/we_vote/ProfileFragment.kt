package com.example.we_vote

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.we_vote.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels {
        val app = requireActivity().application as WeVoteApplication
        ProfileViewModel.Factory(app.container.preferences)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)

        setupNavigation()
        setupButtons()
        viewModel.loadProfile()

        viewModel.profile.observe(viewLifecycleOwner) { data ->
            binding.nameUser.text = data.name
            binding.userCity.text = data.city
        }

        return binding.root
    }

    private fun setupButtons() {
        binding.btnLogout.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_titleFragment)
        }
        binding.alterData.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_personalInfoChangeFragment)
        }

    }

    private fun setupNavigation() {
        val access = (requireActivity().application as WeVoteApplication).container.preferences.getAccess()
        VotingUtil.setBottomBar(access, binding.bottomNav)

        val requestAction = if (access == "admin" || access == "developer")
            R.id.action_profileFragment_to_newApplicationsFragment
        else
            R.id.action_profileFragment_to_myApplicationsFragment

        binding.bottomNav.setOnItemSelectedListener { item ->
            VotingUtil.setupNavigation(
                this, item.itemId,
                R.id.action_profileFragment_to_mainScreenFragment,
                R.id.action_profileFragment_to_newPollFragment,
                R.id.action_profileFragment_self,
                R.id.action_profileFragment_to_archiveFragment,
                requestAction,
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null) {
            binding.bottomNav.menu.findItem(R.id.nav_profile)?.isChecked = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
