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
import com.example.we_vote.databinding.FragmentMyApplicationsBinding
import com.example.we_vote.domain.model.SurveyApplication
import com.example.we_vote.recycler.MyApplicationsAdapter

class MyApplicationsFragment : Fragment() {

    private var _binding: FragmentMyApplicationsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyApplicationsViewModel by viewModels {
        val app = requireActivity().application as WeVoteApplication
        MyApplicationsViewModel.Factory(
            app.container.getUserApplicationsUseCase,
            app.container.deleteApplicationUseCase,
            app.container.preferences,
        )
    }

    private lateinit var adapter: MyApplicationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMyApplicationsBinding.inflate(layoutInflater, container, false)

        setupNavigation()
        setupRecycler()
        observeState()

        viewModel.loadApplications()

        return binding.root
    }

    private fun setupRecycler() {
        adapter = MyApplicationsAdapter(
            items = emptyList(),
            onItemClick = { app -> showDetailDialog(app) },
            onCancel = { app, position -> showCancelConfirmDialog(app, position) },
        )
        binding.recyclerMyApplications.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerMyApplications.adapter = adapter
    }

    private fun observeState() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is MyApplicationsViewModel.State.Loading -> Unit
                is MyApplicationsViewModel.State.Success -> {
                    adapter.updateItems(state.applications)
                    binding.emptyText.isVisible = state.applications.isEmpty()
                }
                is MyApplicationsViewModel.State.Error -> {
                    Toast.makeText(requireContext(), "Ошибка: ${state.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.cancelResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), getString(R.string.application_cancelled), Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(requireContext(), "Ошибка отмены заявки", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showCancelConfirmDialog(application: SurveyApplication, position: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_window, null)
        dialogView.findViewById<TextView>(R.id.dialog_message).text = getString(R.string.cancel_application_confirm)
        val btnConfirm = dialogView.findViewById<Button>(R.id.dialog_confirm)
        btnConfirm.text = getString(R.string.cancel_application)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        btnConfirm.setOnClickListener {
            dialog.dismiss()
            viewModel.cancelApplication(application.id, position)
        }
        dialogView.findViewById<Button>(R.id.dialog_cancel).setOnClickListener { dialog.dismiss() }

        dialog.show()
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
                R.id.action_myApplicationsFragment_to_mainScreenFragment,
                R.id.action_myApplicationsFragment_to_newPollFragment,
                R.id.action_myApplicationsFragment_to_profileFragment,
                R.id.action_myApplicationsFragment_to_archiveFragment,
                R.id.action_myApplicationsFragment_self,
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
