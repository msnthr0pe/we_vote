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
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.edit
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.we_vote.databinding.FragmentPersonalInfoChangeBinding
import com.example.we_vote.ktor.ApiClient
import com.example.we_vote.ktor.DTOs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PersonalInfoChangeFragment : Fragment() {

    private var _binding: FragmentPersonalInfoChangeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentPersonalInfoChangeBinding.inflate(layoutInflater, container, false)

        setupNavigation()
        setupDataChange()

        return binding.root
    }

    private fun setupNavigation() {
        val prefs = requireActivity().getSharedPreferences("credentials",
            Context.MODE_PRIVATE)
        val access = prefs.getString("access", "user")
        VotingUtil.setBottomBar(access, binding.bottomNav)
        binding.bottomNav.menu.findItem(R.id.nav_profile).isChecked = true

        binding.bottomNav.setOnItemSelectedListener { item ->
            VotingUtil.setupNavigation(this, item.itemId,
                R.id.action_personalInfoChangeFragment_to_mainScreenFragment,
                R.id.action_personalInfoChangeFragment_to_newPollFragment,
                R.id.action_personalInfoChangeFragment_to_profileFragment,
                R.id.action_personalInfoChangeFragment_to_archiveFragment)
        }
    }

    private fun setupDataChange() {
        val currentUserData = getUserData()

        binding.etNameChange.setText(currentUserData.name)
        binding.etDateChange.setText(currentUserData.dob)
        binding.etCityChange.setText(currentUserData.city)

        binding.confirmChangeButton.setOnClickListener {
            val updatedUserData = DTOs.UserDTO(
                name = binding.etNameChange.text.toString().ifEmpty { currentUserData.name },
                email = currentUserData.email,
                dob = binding.etDateChange.text.toString().ifEmpty { currentUserData.dob },
                city = binding.etCityChange.text.toString().ifEmpty { currentUserData.city },
                password = binding.etPasswordChange.text.toString().ifEmpty { currentUserData.password },
                access = currentUserData.access,
            )
            showConfirmDialog(updatedUserData)
        }
    }

    private fun showConfirmDialog(updatedUserData: DTOs.UserDTO) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_window, null)
        dialogView.findViewById<TextView>(R.id.dialog_message).text = getString(R.string.are_you_sure_update)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        dialogView.findViewById<Button>(R.id.dialog_confirm).setOnClickListener {
            dialog.dismiss()
            executeQuery(updatedUserData)
        }
        dialogView.findViewById<Button>(R.id.dialog_cancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun getUserData() : DTOs.UserDTO {
        val prefs = requireActivity().getSharedPreferences("credentials",
            Context.MODE_PRIVATE)
        val name = prefs.getString("name", "") ?: ""
        val email = prefs.getString("email", "") ?: ""
        val dob = prefs.getString("dob", "") ?: ""
        val city = prefs.getString("city", "") ?: ""
        val password = ""
        val access = prefs.getString("access", "") ?: ""
        return DTOs.UserDTO(name, email, dob, city, password, access)
    }

    private fun updatePrefs(userDTO: DTOs.UserDTO) {
        val prefs = requireContext().getSharedPreferences(
            "credentials",
            Context.MODE_PRIVATE
        )
        prefs.edit {
            putString("email", userDTO.email)
            putString("name", userDTO.name)
            putString("dob", userDTO.dob)
            putString("city", userDTO.city)
            putString("access", userDTO.access)
            apply()
        }
    }

    private fun executeQuery(userDTO: DTOs.UserDTO) {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    ApiClient.authApi.updateUser(userDTO).execute()
                }
                if (response.isSuccessful) {
                    updatePrefs(userDTO)
                    Toast.makeText(requireContext(), getString(R.string.account_updated), Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_personalInfoChangeFragment_to_profileFragment)
                } else {
                    Toast.makeText(requireContext(), "Ошибка при обновлении данных", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("WE_VOTE", "Update user error", e)
                Toast.makeText(requireContext(), "Ошибка сети: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNav.menu.findItem(R.id.nav_profile).isChecked = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}