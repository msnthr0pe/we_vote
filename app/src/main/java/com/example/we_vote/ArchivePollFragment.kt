package com.example.we_vote

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.we_vote.databinding.FragmentArchivePollBinding
import com.example.we_vote.ktor.ApiClient
import com.example.we_vote.ktor.DTOs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.getValue

class ArchivePollFragment : Fragment() {

    private var _binding: FragmentArchivePollBinding? = null
    private val binding get() = _binding!!
    private lateinit var access: String
    private lateinit var currentIdSurvey: Integer



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentArchivePollBinding.inflate(layoutInflater, container, false)

        setupNavigation()
        getArgs()
        setupButtons()


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
            currentIdSurvey = args.id as Integer
            archivedTitle.text = args.title
            archivedFirstChoice.titleText = args.firstChoice
            archivedFirstChoice.progress =  args.firstChoiceValue
            archivedSecondChoice.titleText = args.secondChoice
            archivedSecondChoice.progress = args.secondChoiceValue
            archivedThirdChoice.titleText = args.thirdChoice
            archivedThirdChoice.progress = args.thirdChoiceValue
        }

    }

    private fun setupButtons() {
        binding.archivePollDelete.setOnClickListener {
            showEditDialog()
        }
    }

    private fun showEditDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_window, null)
        dialogView.findViewById<TextView>(R.id.dialog_message).text = getString(R.string.are_you_sure_delete)
        val btnConfirm = dialogView.findViewById<Button>(R.id.dialog_confirm)
        val btnCancel = dialogView.findViewById<Button>(R.id.dialog_cancel)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        btnConfirm.setOnClickListener {
            dialog.dismiss()
            deleteSurvey()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun deleteSurvey() {
        val call = ApiClient.authApi.deleteSurveyInfo(DTOs.SurveyIdRequest(currentIdSurvey.toInt()))
        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void?>,
                response: Response<Void?>,
            ) {
                if (response.isSuccessful) {
                    val action = ArchivePollFragmentDirections.actionArchivePollFragmentToArchiveFragment()
                    findNavController().navigate(action)
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
            }

        })
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