package com.example.we_vote

import android.app.AlertDialog
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
import com.example.we_vote.databinding.FragmentNewApplicationsBinding
import com.example.we_vote.domain.model.ApplicationStatus
import com.example.we_vote.domain.model.SurveyApplication
import com.example.we_vote.recycler.NewApplicationsAdapter

class NewApplicationsFragment : Fragment() {

    private var _binding: FragmentNewApplicationsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewApplicationsViewModel by viewModels {
        val app = requireActivity().application as WeVoteApplication
        NewApplicationsViewModel.Factory(
            app.container.getApplicationsUseCase,
            app.container.updateApplicationStatusUseCase,
            app.container.addSurveyUseCase,
        )
    }

    private lateinit var adapter: NewApplicationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentNewApplicationsBinding.inflate(layoutInflater, container, false)

        setupNavigation()
        setupFilter()
        setupRecycler()
        observeState()

        viewModel.loadApplications()

        return binding.root
    }

    private fun setupRecycler() {
        adapter = NewApplicationsAdapter(
            items = mutableListOf(),
            onItemClick = { app -> showDetailDialog(app) },
            onAccept = { app, _ -> viewModel.updateStatus(app, ApplicationStatus.ACCEPTED) },
            onReject = { app, _ -> viewModel.updateStatus(app, ApplicationStatus.REJECTED) },
        )
        binding.recyclerNewApplications.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerNewApplications.adapter = adapter
    }

    private fun observeState() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is NewApplicationsViewModel.State.Loading -> Unit
                is NewApplicationsViewModel.State.Success -> {
                    adapter.updateList(state.applications)
                    binding.emptyText.isVisible = state.applications.isEmpty()
                }
                is NewApplicationsViewModel.State.Error -> {
                    Toast.makeText(requireContext(), "Ошибка: ${state.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupFilter() {
        binding.filterChipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener
            val filter = when (checkedIds.first()) {
                R.id.chip_accepted -> ApplicationStatus.ACCEPTED
                R.id.chip_rejected -> ApplicationStatus.REJECTED
                else               -> ApplicationStatus.PENDING
            }
            viewModel.applyFilter(filter)
        }
    }

    private fun showDetailDialog(application: SurveyApplication) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_application_detail, null)
        dialogView.findViewById<TextView>(R.id.dialog_app_title).text = application.title
        dialogView.findViewById<TextView>(R.id.dialog_option_1).text = application.firstChoice
        dialogView.findViewById<TextView>(R.id.dialog_option_2).text = application.secondChoice
        dialogView.findViewById<TextView>(R.id.dialog_option_3).text = application.thirdChoice

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        dialogView.findViewById<Button>(R.id.dialog_app_close).setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    private fun setupNavigation() {
        val access = (requireActivity().application as WeVoteApplication).container.preferences.getAccess()
        VotingUtil.setBottomBar(access, binding.bottomNav)

        binding.bottomNav.setOnItemSelectedListener { item ->
            VotingUtil.setupNavigation(
                this, item.itemId,
                R.id.action_newApplicationsFragment_to_mainScreenFragment,
                R.id.action_newApplicationsFragment_to_newPollFragment,
                R.id.action_newApplicationsFragment_to_profileFragment,
                R.id.action_newApplicationsFragment_to_archiveFragment,
                R.id.action_newApplicationsFragment_self,
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null) binding.bottomNav.menu.findItem(R.id.nav_request)?.isChecked = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
