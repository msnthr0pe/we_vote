package com.example.we_vote

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.we_vote.databinding.FragmentArchiveBinding
import com.example.we_vote.recycler.ArchiveAdapter

class ArchiveFragment : Fragment() {

    private var _binding: FragmentArchiveBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ArchiveViewModel by viewModels {
        val app = requireActivity().application as WeVoteApplication
        ArchiveViewModel.Factory(
            app.container.getArchivedSurveysUseCase,
            app.container.getSurveyVotesUseCase,
            app.container.deleteSurveyUseCase,
            app.container.preferences,
        )
    }

    private lateinit var adapter: ArchiveAdapter
    private lateinit var access: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentArchiveBinding.inflate(layoutInflater, container, false)

        access = (requireActivity().application as WeVoteApplication).container.preferences.getAccess()
        setupNavigation()
        setupRecycler()
        observeState()

        viewModel.loadArchive()

        return binding.root
    }

    private fun setupRecycler() {
        adapter = ArchiveAdapter(emptyList(), access) { survey, position ->
            showDeleteDialog(survey.id, position)
        }
        binding.recyclerArchive.layoutManager = LinearLayoutManager(activity)
        binding.recyclerArchive.adapter = adapter
    }

    private fun observeState() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ArchiveViewModel.State.Loading -> binding.archiveProgressBar.isVisible = true
                is ArchiveViewModel.State.Success -> {
                    binding.archiveProgressBar.isVisible = false
                    adapter.updateItems(state.items)
                    binding.emptyArchiveText.isVisible = state.items.isEmpty()
                }
                is ArchiveViewModel.State.Error -> {
                    binding.archiveProgressBar.isVisible = false
                    Toast.makeText(requireContext(), "Ошибка: ${state.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.deleteResult.observe(viewLifecycleOwner) { result ->
            result.onFailure {
                Toast.makeText(requireContext(), "Ошибка при удалении", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDeleteDialog(surveyId: Int, position: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_window, null)
        dialogView.findViewById<TextView>(R.id.dialog_message).text = getString(R.string.are_you_sure_delete)
        val btnConfirm = dialogView.findViewById<Button>(R.id.dialog_confirm)
        btnConfirm.text = getString(R.string.delete)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        btnConfirm.setOnClickListener {
            dialog.dismiss()
            viewModel.deleteSurvey(surveyId, position)
        }
        dialogView.findViewById<Button>(R.id.dialog_cancel).setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    private fun setupNavigation() {
        VotingUtil.setBottomBar(access, binding.bottomNav)

        val requestAction = if (access == "admin" || access == "developer")
            R.id.action_archiveFragment_to_newApplicationsFragment
        else
            R.id.action_archiveFragment_to_myApplicationsFragment

        binding.bottomNav.setOnItemSelectedListener { item ->
            VotingUtil.setupNavigation(
                this, item.itemId,
                R.id.action_archiveFragment_to_mainScreenFragment,
                R.id.action_archiveFragment_to_newPollFragment,
                R.id.action_archiveFragment_to_profileFragment,
                R.id.action_archiveFragment_self,
                requestAction,
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null) binding.bottomNav.menu.findItem(R.id.nav_archive).isChecked = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
