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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.we_vote.databinding.FragmentPersonalInfoChangeBinding
import com.example.we_vote.domain.model.User

class PersonalInfoChangeFragment : Fragment() {

    private var _binding: FragmentPersonalInfoChangeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PersonalInfoChangeViewModel by viewModels {
        val app = requireActivity().application as WeVoteApplication
        PersonalInfoChangeViewModel.Factory(app.container.updateUserUseCase, app.container.preferences)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPersonalInfoChangeBinding.inflate(layoutInflater, container, false)

        setupForm()

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PersonalInfoChangeViewModel.State.Loading -> Unit
                is PersonalInfoChangeViewModel.State.Success -> {
                    Toast.makeText(requireContext(), getString(R.string.account_updated), Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_personalInfoChangeFragment_to_profileFragment)
                }
                is PersonalInfoChangeViewModel.State.Error -> {
                    Toast.makeText(requireContext(), "Ошибка сети: ${state.message}", Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }

        return binding.root
    }

    private fun setupForm() {
        val current = viewModel.preferences.getUserSnapshot()
        binding.etNameChange.setText(current.name)
        binding.etDateChange.setText(current.dob)
        binding.etCityChange.setText(current.city)

        binding.confirmChangeButton.setOnClickListener {
            val updated = User(
                name = binding.etNameChange.text.toString().ifEmpty { current.name },
                email = current.email,
                dob = binding.etDateChange.text.toString().ifEmpty { current.dob },
                city = binding.etCityChange.text.toString().ifEmpty { current.city },
                password = binding.etPasswordChange.text.toString().ifEmpty { current.password },
                access = current.access,
            )
            showConfirmDialog(updated)
        }
    }

    private fun showConfirmDialog(user: User) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_window, null)
        dialogView.findViewById<TextView>(R.id.dialog_message).text = getString(R.string.are_you_sure_update)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        dialogView.findViewById<Button>(R.id.dialog_confirm).setOnClickListener {
            dialog.dismiss()
            viewModel.updateUser(user)
        }
        dialogView.findViewById<Button>(R.id.dialog_cancel).setOnClickListener { dialog.dismiss() }

        dialog.show()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
