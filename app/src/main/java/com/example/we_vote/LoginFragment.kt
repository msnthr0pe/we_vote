package com.example.we_vote

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.we_vote.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels {
        val app = requireActivity().application as WeVoteApplication
        LoginViewModel.Factory(
            app.container.loginUseCase,
            app.container.getUserUseCase,
            app.container.preferences,
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)

        binding.btnLoginContinue.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPasswordLogin.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(email, password)
            } else {
                Toast.makeText(activity, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LoginViewModel.State.Loading -> binding.progressBar.visibility = View.VISIBLE
                is LoginViewModel.State.Success -> {
                    binding.progressBar.visibility = View.GONE
                    findNavController().navigate(R.id.action_loginFragment_to_mainScreenFragment)
                }
                is LoginViewModel.State.LoginFailed -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), getString(R.string.login_error), Toast.LENGTH_SHORT).show()
                }
                is LoginViewModel.State.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "${getString(R.string.network_error)} ${state.message}", Toast.LENGTH_SHORT).show()
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
