package com.example.we_vote

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.we_vote.databinding.FragmentMyApplicationsBinding
import com.example.we_vote.ktor.ApiClient
import com.example.we_vote.ktor.DTOs
import com.example.we_vote.recycler.MyApplicationsAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyApplicationsFragment : Fragment() {

    private var _binding: FragmentMyApplicationsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyApplicationsBinding.inflate(layoutInflater, container, false)

        setupNavigation()
        setupRecycler()

        return binding.root
    }

    private fun setupNavigation() {
        val prefs = requireActivity().getSharedPreferences("credentials", Context.MODE_PRIVATE)
        val access = prefs.getString("access", "user")
        VotingUtil.setBottomBar(access, binding.bottomNav)

        binding.bottomNav.setOnItemSelectedListener { item ->
            VotingUtil.setupNavigation(
                this, item.itemId,
                R.id.action_myApplicationsFragment_to_mainScreenFragment,
                R.id.action_myApplicationsFragment_to_newPollFragment,
                R.id.action_myApplicationsFragment_to_profileFragment,
                R.id.action_myApplicationsFragment_to_archiveFragment,
                R.id.action_myApplicationsFragment_self
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null) {
            binding.bottomNav.menu.findItem(R.id.nav_request)?.isChecked = true
        }
    }

    private fun setupRecycler() {
        val prefs = requireActivity().getSharedPreferences("credentials", Context.MODE_PRIVATE)
        val email = prefs.getString("email", "") ?: ""

        lifecycleScope.launch {
            try {
                val applications = withContext(Dispatchers.IO) {
                    ApiClient.authApi.getUserApplications(DTOs.EmailDTO(email))
                }
                val adapter = MyApplicationsAdapter(applications) { application ->
                    showDetailDialog(application)
                }
                binding.recyclerMyApplications.layoutManager = LinearLayoutManager(requireContext())
                binding.recyclerMyApplications.adapter = adapter
                binding.emptyText.isVisible = applications.isEmpty()
            } catch (e: Exception) {
                Log.e("WE_VOTE", "Error loading applications: ${e.message}")
                Toast.makeText(requireContext(), "Ошибка загрузки заявок: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDetailDialog(application: DTOs.ApplicationDTO) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_application_detail, null)
        dialogView.findViewById<TextView>(R.id.dialog_app_title).text = application.title
        dialogView.findViewById<TextView>(R.id.dialog_option_1).text = application.firstChoice
        dialogView.findViewById<TextView>(R.id.dialog_option_2).text = application.secondChoice
        dialogView.findViewById<TextView>(R.id.dialog_option_3).text = application.thirdChoice

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        dialogView.findViewById<Button>(R.id.dialog_app_close).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
