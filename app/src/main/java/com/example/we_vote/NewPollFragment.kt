package com.example.we_vote

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.we_vote.databinding.FragmentNewPollBinding

class NewPollFragment : Fragment() {

    private var _binding: FragmentNewPollBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentNewPollBinding.inflate(layoutInflater, container, false)

        val prefs = requireActivity().getSharedPreferences("credentials",
            Context.MODE_PRIVATE)
        val access = prefs.getString("access", "user")
        VotingUtil.setBottomBar(access, binding.bottomNav)
        binding.bottomNav.menu.findItem(R.id.nav_new_poll).isChecked = true

        binding.bottomNav.setOnItemSelectedListener { item ->
            VotingUtil.setupNavigation(this, item.itemId,
                R.id.action_newPollFragment_to_mainScreenFragment,
                R.id.action_newPollFragment_self,
                R.id.action_newPollFragment_to_profileFragment,
                R.id.action_newPollFragment_to_archiveFragment)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNav.menu.findItem(R.id.nav_new_poll).isChecked = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}