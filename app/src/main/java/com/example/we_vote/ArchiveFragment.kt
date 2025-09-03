package com.example.we_vote

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.we_vote.databinding.FragmentArchiveBinding

class ArchiveFragment : Fragment() {

    private var _binding: FragmentArchiveBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentArchiveBinding.inflate(layoutInflater, container, false)

        val prefs = requireActivity().getSharedPreferences("credentials",
            Context.MODE_PRIVATE)
        val access = prefs.getString("access", "user")
        VotingUtil.setBottomBar(access, binding.bottomNav)

        binding.bottomNav.setOnItemSelectedListener { item ->
            VotingUtil.setupNavigation(this, item.itemId,
                R.id.action_archiveFragment_to_mainScreenFragment,
                R.id.action_archiveFragment_to_newPollFragment,
                R.id.action_archiveFragment_to_profileFragment,
                R.id.action_archiveFragment_self)
        }

        return binding.root
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