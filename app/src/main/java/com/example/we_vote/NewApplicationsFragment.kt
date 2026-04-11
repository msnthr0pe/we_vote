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
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.we_vote.databinding.FragmentNewApplicationsBinding
import com.example.we_vote.ktor.ApplicationStatus
import com.example.we_vote.ktor.DTOs
import com.example.we_vote.recycler.NewApplicationsAdapter

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
                R.id.action_newApplicationsFragment_to_archiveFragment
            )
        }
    }

    private fun setupRecycler() {
        applications = mutableListOf(
            DTOs.ApplicationDTO(
                id = 1,
                title = "Нужно ли добавить велодорожки вдоль набережной?",
                firstChoice = "Да, обязательно",
                secondChoice = "Нет, это лишнее",
                thirdChoice = "Нужно изучить вопрос",
                status = ApplicationStatus.PENDING,
                userEmail = "user1@example.com"
            ),
            DTOs.ApplicationDTO(
                id = 2,
                title = "Стоит ли открыть детскую площадку в центральном парке?",
                firstChoice = "Да",
                secondChoice = "Нет",
                thirdChoice = "Не важно",
                status = ApplicationStatus.ACCEPTED,
                userEmail = "user2@example.com"
            ),
            DTOs.ApplicationDTO(
                id = 3,
                title = "Следует ли запретить движение транспорта в историческом центре?",
                firstChoice = "Да, запретить полностью",
                secondChoice = "Разрешить только общественный транспорт",
                thirdChoice = "Оставить как есть",
                status = ApplicationStatus.REJECTED,
                userEmail = "user3@example.com"
            ),
            DTOs.ApplicationDTO(
                id = 4,
                title = "Нужно ли организовать бесплатный Wi-Fi в общественных пространствах города?",
                firstChoice = "Да, везде",
                secondChoice = "Только в парках",
                thirdChoice = "Нет необходимости",
                status = ApplicationStatus.PENDING,
                userEmail = "user4@example.com"
            )
        )

        adapter = NewApplicationsAdapter(
            items = applications,
            onItemClick = { application -> showDetailDialog(application) },
            onAccept = { application, position -> updateStatus(application, position, ApplicationStatus.ACCEPTED) },
            onReject = { application, position -> updateStatus(application, position, ApplicationStatus.REJECTED) }
        )

        binding.recyclerNewApplications.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerNewApplications.adapter = adapter
        binding.emptyText.isVisible = applications.isEmpty()
    }

    private fun updateStatus(application: DTOs.ApplicationDTO, position: Int, newStatus: ApplicationStatus) {
        val updated = application.copy(status = newStatus)
        applications[position] = updated
        adapter.notifyItemChanged(position)
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
