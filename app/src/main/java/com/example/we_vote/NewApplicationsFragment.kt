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

    private lateinit var applications: MutableList<DTOs.ApplicationDTO>
    private lateinit var adapter: NewApplicationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewApplicationsBinding.inflate(layoutInflater, container, false)

        setupNavigation()
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

    private fun setupRecycler() {
        lifecycleScope.launch {
            try {
                applications = withContext(Dispatchers.IO) {
                    ApiClient.authApi.getApplications()
                }.toMutableList()

                adapter = NewApplicationsAdapter(
                    items = applications,
                    onItemClick = { application -> showDetailDialog(application) },
                    onAccept = { application, position -> updateStatus(application, position, ApplicationStatus.ACCEPTED) },
                    onReject = { application, position -> updateStatus(application, position, ApplicationStatus.REJECTED) }
                )

                binding.recyclerNewApplications.layoutManager = LinearLayoutManager(requireContext())
                binding.recyclerNewApplications.adapter = adapter
                binding.emptyText.isVisible = applications.isEmpty()
            } catch (e: Exception) {
                Log.e("WE_VOTE", "Error loading applications: ${e.message}")
                Toast.makeText(requireContext(), "Ошибка загрузки заявок: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateStatus(application: DTOs.ApplicationDTO, position: Int, newStatus: ApplicationStatus) {
        val call = ApiClient.authApi.updateApplication(
            DTOs.ApplicationStatusUpdateDTO(id = application.id, status = newStatus.name)
        )
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    val updated = application.copy(status = newStatus)
                    applications[position] = updated
                    adapter.notifyItemChanged(position)
                    if (newStatus == ApplicationStatus.ACCEPTED) {
                        publishSurvey(application)
                    }
                } else {
                    Toast.makeText(requireContext(), "Ошибка обновления статуса", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
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
