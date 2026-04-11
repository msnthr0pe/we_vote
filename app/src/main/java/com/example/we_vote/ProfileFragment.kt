package com.example.we_vote

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.we_vote.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)

        setupNavigation()
        setupButtons()
        getUserData()

        return binding.root
    }

    private fun getUserData() {
        val prefs = requireActivity().getSharedPreferences("credentials",
            Context.MODE_PRIVATE)
        val access = prefs.getString("access", "user")
        
        if (access == "developer") {
            // Тестовые данные для разработчика
            binding.nameUser.text = "Dev Test User"
            binding.userCity.text = "Test City"
        } else {
            val name = prefs.getString("name", "")
            val city = prefs.getString("city", "")
            binding.nameUser.text = name
            binding.userCity.text = city
        }
    }

    private fun setupButtons() {
        binding.btnLogout.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_titleFragment)
        }

        binding.alterData.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_personalInfoChangeFragment)
        }

        binding.myRequests.setOnClickListener {
            val prefs = requireActivity().getSharedPreferences("credentials", Context.MODE_PRIVATE)
            val access = prefs.getString("access", "user")
            if (access == "admin" || access == "developer") {
                findNavController().navigate(R.id.action_profileFragment_to_newApplicationsFragment)
            } else {
                findNavController().navigate(R.id.action_profileFragment_to_myApplicationsFragment)
            }
        }
    }

    private fun setupNavigation() {
        val prefs = requireActivity().getSharedPreferences("credentials",
            Context.MODE_PRIVATE)
        val access = prefs.getString("access", "user")
        VotingUtil.setBottomBar(access, binding.bottomNav)
        
        binding.bottomNav.setOnItemSelectedListener { item ->
            VotingUtil.setupNavigation(this, item.itemId,
                R.id.action_profileFragment_to_mainScreenFragment,
                R.id.action_profileFragment_to_newPollFragment,
                R.id.action_profileFragment_self,
                R.id.action_profileFragment_to_archiveFragment)
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
