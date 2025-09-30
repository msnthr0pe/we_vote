package com.example.we_vote

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.we_vote.databinding.FragmentArchivePollBinding

class ArchivePollFragment : Fragment() {

    private var _binding: FragmentArchivePollBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentArchivePollBinding.inflate(layoutInflater, container, false)

        setupNavigation()

        return binding.root
    }

    private fun setupNavigation() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            VotingUtil.setupNavigation(this, item.itemId,
                R.id.action_archivePollFragment_to_mainScreenFragment,
                R.id.action_archivePollFragment_to_newPollFragment,
                R.id.action_archivePollFragment_to_profileFragment,
                R.id.action_archivePollFragment_to_archiveFragment)
        }
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