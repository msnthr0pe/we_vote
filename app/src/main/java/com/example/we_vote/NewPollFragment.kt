package com.example.we_vote

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.we_vote.databinding.FragmentNewPollBinding
import com.example.we_vote.ktor.ApiClient
import com.example.we_vote.ktor.DTOs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewPollFragment : Fragment() {

    private var _binding: FragmentNewPollBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentNewPollBinding.inflate(layoutInflater, container, false)

        setupNavigation()
        setupSurveyCreation()

        return binding.root
    }

    private fun setupSurveyCreation() {
        binding.addSurveyBtn.setOnClickListener {
            with(binding) {
                val title = newTitle.text.toString()
                val firstChoice = newFirstChoice.text.toString()
                val secondChoice = newSecondChoice.text.toString()
                val thirdChoice = newThirdChoice.text.toString()

                if (
                    title.isNotEmpty() &&
                    firstChoice.isNotEmpty() &&
                    secondChoice.isNotEmpty() &&
                    thirdChoice.isNotEmpty()
                ) {
                    binding.progressBar.visibility = View.VISIBLE
                    addNewSurvey(title, firstChoice, secondChoice, thirdChoice)
                } else {
                    Toast.makeText(activity, "Заполните все поля", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addNewSurvey(
        title: String,
        firstChoice: String,
        secondChoice: String,
        thirdChoice: String,
    ) {
        val call = ApiClient.authApi.addSurvey(DTOs.SurveyDTO(
            -1, title, firstChoice, secondChoice, thirdChoice)
        )
        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(),
                        getString(R.string.survey_created), Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(requireContext(),
                        getString(R.string.survey_creation_error), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Toast.makeText(requireContext(), "${getString(R.string.network_error)} ${t.message}", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }

        })
    }

    fun setupNavigation() {
        val prefs = requireActivity().getSharedPreferences("credentials",
            Context.MODE_PRIVATE)
        val access = prefs.getString("access", "user")
        VotingUtil.setBottomBar(access, binding.bottomNav)
        binding.bottomNav.menu.findItem(R.id.nav_new_poll).isChecked = true

        binding.bottomNav.setOnItemSelectedListener { item ->
            VotingUtil.setupNavigation(this, item.itemId,
                R.id.action_newPollFragment_to_mainScreenFragment,
                R.id.action_newPollFragment_self,
                R.id.action_newPollFragment_to_profileFragment,
                R.id.action_newPollFragment_to_archiveFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNav.menu.findItem(R.id.nav_new_poll).isChecked = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}