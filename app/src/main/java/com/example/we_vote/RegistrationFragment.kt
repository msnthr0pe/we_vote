package com.example.we_vote

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.we_vote.databinding.FragmentRegistrationBinding

class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegistrationViewModel by viewModels {
        val app = requireActivity().application as WeVoteApplication
        RegistrationViewModel.Factory(app.container.registerUseCase)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRegistrationBinding.inflate(layoutInflater, container, false)

        binding.btBack.setOnClickListener { findNavController().navigateUp() }

        binding.btnRegister.setOnClickListener {
            val name = binding.etNameRegister.text.toString()
            val email = binding.etEmailRegister.text.toString()
            val dob = binding.etDateRegister.text.toString()
            val city = binding.etCityRegister.text.toString()
            val password = binding.etPasswordRegister.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && dob.isNotEmpty()
                && city.isNotEmpty() && password.isNotEmpty()
            ) {
                viewModel.register(name, email, dob, city, password)
            } else {
                Toast.makeText(activity, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is RegistrationViewModel.State.Loading -> binding.progressBar.visibility = View.VISIBLE
                is RegistrationViewModel.State.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), getString(R.string.account_created), Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                is RegistrationViewModel.State.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), getString(R.string.account_creation_error), Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
