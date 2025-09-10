package com.example.we_vote

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.we_vote.databinding.FragmentMainScreenBinding
import com.example.we_vote.ktor.ApiClient
import com.example.we_vote.ktor.DTOs
import com.example.we_vote.recycler.SurveyAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.core.graphics.drawable.toDrawable

class MainScreenFragment : Fragment() {

    private var _binding: FragmentMainScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SurveyAdapter
    private lateinit var access: String
    private lateinit var surveys: MutableList<DTOs.SurveyDTO>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentMainScreenBinding.inflate(layoutInflater, container, false)

        setupNavigation()
        setupRecycler()

        return binding.root
    }

    private fun setupNavigation() {
        val prefs = requireActivity().getSharedPreferences("credentials",
            Context.MODE_PRIVATE)
        access = prefs.getString("access", "user") ?: "user"
        VotingUtil.setBottomBar(access, binding.bottomNav)
        binding.bottomNav.menu.findItem(R.id.nav_home).isChecked = true
        binding.bottomNav.setOnItemSelectedListener { item ->
            VotingUtil.setupNavigation(this, item.itemId,
                R.id.action_mainScreenFragment_self,
                R.id.action_mainScreenFragment_to_newPollFragment,
                R.id.action_mainScreenFragment_to_profileFragment,
                R.id.action_mainScreenFragment_to_archiveFragment)
        }
    }

    private fun setupRecycler() {
        recyclerView = binding.mainRecycler
        recyclerView.layoutManager = LinearLayoutManager(activity)
        lifecycleScope.launch {
            try {

                surveys = withContext(Dispatchers.IO) {
                    ApiClient.authApi.getSurveys()
                }

                adapter = SurveyAdapter(surveys, access, { survey ->
                    val action = MainScreenFragmentDirections.actionMainScreenFragmentToVotingFragment(
                        id = survey.id,
                        title = survey.title,
                        firstChoice = survey.firstChoice,
                        secondChoice = survey.secondChoice,
                        thirdChoice = survey.thirdChoice,
                    )
                    findNavController().navigate(action)
                }, {survey, position, surveyAmount -> showEditDialog(survey, position, surveyAmount) })
                recyclerView.adapter = adapter

            } catch (e: Exception) {
                Log.e("CLIENT", "Ошибка: ${e.message}")
            }
            binding.progressBarMain.isVisible = false
        }

    }

    private fun showEditDialog(survey: DTOs.SurveyDTO, position: Int, surveyAmount: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_window, null)
        val btnConfirm = dialogView.findViewById<Button>(R.id.dialog_confirm)
        val btnCancel = dialogView.findViewById<Button>(R.id.dialog_cancel)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        btnConfirm.setOnClickListener {
            dialog.dismiss()
            archiveSurvey(survey.title)
            surveys.removeAt(position)
            adapter.notifyItemRemoved(position)
            adapter.notifyItemRangeChanged(position, surveyAmount)
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        /*dialog.window?.setBackgroundDrawable(
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_edittext_dialog)
        )*/

        dialog.show()
    }

    private fun archiveSurvey(title: String) {
        val call = ApiClient.authApi.archiveSurvey(DTOs.TitleDTO(title))
        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>,
            ) {
                if (!response.isSuccessful) {
                    Toast.makeText(requireContext(), "Ошибка", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Toast.makeText(requireContext(), "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNav.menu.findItem(R.id.nav_home).isChecked = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}