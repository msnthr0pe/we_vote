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
import com.example.we_vote.databinding.FragmentNewApplicationsBinding
import com.example.we_vote.ktor.ApiClient
import com.example.we_vote.ktor.ApplicationStatus
import com.example.we_vote.ktor.DTOs
import com.example.we_vote.recycler.NewApplicationsAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewApplicationsFragment : Fragment() {

    private var _binding: FragmentNewApplicationsBinding? = null
    private val binding get() = _binding!!

    private val allApplications: MutableList<DTOs.ApplicationDTO> = mutableListOf()
    private lateinit var adapter: NewApplicationsAdapter
    private var currentFilter = ApplicationStatus.PENDING

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewApplicationsBinding.inflate(layoutInflater, container, false)

        setupNavigation()
        setupFilter()
        setupRecycler()

        return binding.root
    }

    private fun setupNavigation() {
        val prefs = requireActivity().getSharedPreferences("credentials", Context.MODE_PRIVATE)
        val access = prefs.getString("access", "admin")
        VotingUtil.setBottomBar(access, binding.bottomNav)

        binding.bottomNav.setOnItemSelectedListener { item ->
            VotingUtil.setupNavigation(
                this, item.itemId,
                R.id.action_newApplicationsFragment_to_mainScreenFragment,
                R.id.action_newApplicationsFragment_to_newPollFragment,
                R.id.action_newApplicationsFragment_to_profileFragment,
                R.id.action_newApplicationsFragment_to_archiveFragment,
                R.id.action_newApplicationsFragment_self
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null) {
            binding.bottomNav.menu.findItem(R.id.nav_request)?.isChecked = true
        }
    }

    private fun setupFilter() {
        binding.filterChipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener
            currentFilter = when (checkedIds.first()) {
                R.id.chip_accepted -> ApplicationStatus.ACCEPTED
                R.id.chip_rejected -> ApplicationStatus.REJECTED
                else               -> ApplicationStatus.PENDING
            }
            applyFilter()
        }
    }

    private fun applyFilter() {
        if (!::adapter.isInitialized) return
        val filtered = allApplications.filter { it.status == currentFilter }
        adapter.updateList(filtered)
        binding.emptyText.isVisible = filtered.isEmpty()
    }

    private fun setupRecycler() {
        adapter = NewApplicationsAdapter(
            items = mutableListOf(),
            onItemClick = { application -> showDetailDialog(application) },
            onAccept = { application, _ -> updateStatus(application, ApplicationStatus.ACCEPTED) },
            onReject = { application, _ -> updateStatus(application, ApplicationStatus.REJECTED) }
        )
        binding.recyclerNewApplications.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerNewApplications.adapter = adapter

        lifecycleScope.launch {
            try {
                val loaded = withContext(Dispatchers.IO) {
                    ApiClient.authApi.getApplications()
                }
                allApplications.clear()
                allApplications.addAll(loaded)
                applyFilter()
            } catch (e: Exception) {
                Log.e("WE_VOTE", "Error loading applications: ${e.message}")
                Toast.makeText(requireContext(), "Ошибка загрузки заявок: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateStatus(application: DTOs.ApplicationDTO, newStatus: ApplicationStatus) {
        val call = ApiClient.authApi.updateApplication(
            DTOs.ApplicationStatusUpdateDTO(id = application.id, status = newStatus.name)
        )
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (!isAdded || _binding == null) return
                if (response.isSuccessful) {
                    val idx = allApplications.indexOfFirst { it.id == application.id }
                    if (idx != -1) allApplications[idx] = application.copy(status = newStatus)
                    applyFilter()
                    if (newStatus == ApplicationStatus.ACCEPTED) publishSurvey(application)
                } else {
                    Toast.makeText(requireContext(), "Ошибка обновления статуса", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                if (!isAdded || _binding == null) return
                Toast.makeText(requireContext(), "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun publishSurvey(application: DTOs.ApplicationDTO) {
        val call = ApiClient.authApi.addSurvey(
            DTOs.SurveyDTO(-1, application.title, application.firstChoice, application.secondChoice, application.thirdChoice)
        )
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (!response.isSuccessful) {
                    Toast.makeText(requireContext(), "Заявка принята, но опрос не опубликован", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "Ошибка публикации опроса: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
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
