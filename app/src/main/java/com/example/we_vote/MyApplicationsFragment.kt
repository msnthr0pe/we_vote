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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyApplicationsFragment : Fragment() {

    private var _binding: FragmentMyApplicationsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MyApplicationsAdapter

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

                adapter = MyApplicationsAdapter(
                    items = applications,
                    onItemClick = { application -> showDetailDialog(application) },
                    onCancel = { application, position -> showCancelConfirmDialog(application, position) }
                )
                binding.recyclerMyApplications.layoutManager = LinearLayoutManager(requireContext())
                binding.recyclerMyApplications.adapter = adapter
                binding.emptyText.isVisible = applications.isEmpty()
            } catch (e: Exception) {
                Log.e("WE_VOTE", "Error loading applications: ${e.message}")
                Toast.makeText(requireContext(), "Ошибка загрузки заявок: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showCancelConfirmDialog(application: DTOs.ApplicationDTO, position: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_window, null)
        dialogView.findViewById<TextView>(R.id.dialog_message).text =
            getString(R.string.cancel_application_confirm)
        val btnConfirm = dialogView.findViewById<Button>(R.id.dialog_confirm)
        btnConfirm.text = getString(R.string.cancel_application)
        val btnCancel = dialogView.findViewById<Button>(R.id.dialog_cancel)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        btnConfirm.setOnClickListener {
            dialog.dismiss()
            cancelApplication(application, position)
        }
        btnCancel.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    private fun cancelApplication(application: DTOs.ApplicationDTO, position: Int) {
        val call = ApiClient.authApi.deleteApplication(
            DTOs.ApplicationIdDTO(id = application.id)
        )
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (!isAdded || _binding == null) return
                if (response.isSuccessful) {
                    adapter.removeAt(position)
                    binding.emptyText.isVisible = adapter.itemCount == 0
                    Toast.makeText(requireContext(), getString(R.string.application_cancelled), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Ошибка отмены заявки", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                if (!isAdded || _binding == null) return
                Toast.makeText(requireContext(), "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
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
