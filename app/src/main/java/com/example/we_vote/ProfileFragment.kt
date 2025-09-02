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

        binding.btnLogout.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_titleFragment)
        }

        val prefs = requireActivity().getSharedPreferences("credentials",
            Context.MODE_PRIVATE)
        val access = prefs.getString("access", "user")
        VotingUtil.setBottomBar(access, binding.bottomNav)
        binding.bottomNav.menu.findItem(R.id.nav_profile).isChecked = true

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    findNavController().navigate(R.id.action_profileFragment_to_mainScreenFragment)
                    true
                }
                R.id.nav_new_poll -> {
                    findNavController().navigate(R.id.action_profileFragment_to_newPollFragment)
                    true
                }
                R.id.nav_profile -> {
                    findNavController().navigate(R.id.action_profileFragment_self)
                    true
                }
                R.id.nav_archive -> {
                    findNavController().navigate(R.id.action_profileFragment_to_archiveFragment)
                    true
                }
                else -> false
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}