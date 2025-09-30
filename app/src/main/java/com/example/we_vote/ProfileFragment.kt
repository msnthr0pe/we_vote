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
        val name = prefs.getString("name", "")
        val dob = prefs.getString("dob", "")
        val city = prefs.getString("city", "")

        with (binding) {
            nameUser.text = name
            birthDate.text = dob
            userCity.text = city
        }
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
        val prefs = requireActivity().getSharedPreferences("credentials",
            Context.MODE_PRIVATE)
        val access = prefs.getString("access", "user")
        VotingUtil.setBottomBar(access, binding.bottomNav)
        binding.bottomNav.menu.findItem(R.id.nav_profile).isChecked = true

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
        binding.bottomNav.menu.findItem(R.id.nav_profile).isChecked = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}