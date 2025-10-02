package com.example.we_vote

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.we_vote.databinding.FragmentArchivePollBinding
import kotlin.getValue

class ArchivePollFragment : Fragment() {

    private var _binding: FragmentArchivePollBinding? = null
    private val binding get() = _binding!!
    private lateinit var access: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentArchivePollBinding.inflate(layoutInflater, container, false)

        setupNavigation()
        getArgs()

        return binding.root
    }

    private fun setupNavigation() {
        val prefs = requireActivity().getSharedPreferences("credentials",
            Context.MODE_PRIVATE)
        access = prefs.getString("access", "user").toString()
        VotingUtil.setBottomBar(access, binding.bottomNav)

        binding.bottomNav.setOnItemSelectedListener { item ->
            VotingUtil.setupNavigation(this, item.itemId,
                R.id.action_archivePollFragment_to_mainScreenFragment,
                R.id.action_archivePollFragment_to_newPollFragment,
                R.id.action_archivePollFragment_to_profileFragment,
                R.id.action_archivePollFragment_to_archiveFragment)
        }
    }

    private fun getArgs() {
        val args by navArgs<ArchivePollFragmentArgs>()

        with (binding) {
            archivedTitle.text = args.title
            archivedFirstChoice.titleText = args.firstChoice
            archivedFirstChoice.progress =  args.firstChoiceValue
            archivedSecondChoice.titleText = args.secondChoice
            archivedSecondChoice.progress = args.secondChoiceValue
            archivedThirdChoice.titleText = args.thirdChoice
            archivedThirdChoice.progress = args.thirdChoiceValue
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