package com.example.we_vote

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.we_vote.databinding.FragmentLoginBinding
import com.example.we_vote.ktor.ApiClient
import com.example.we_vote.ktor.DTOs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        binding.btnLoginContinue.setOnClickListener {
            with(binding) {
                val login = etEmail.text.toString()
                val password = etPasswordLogin.text.toString()
                if (login.isNotEmpty() && password.isNotEmpty()) {
                    binding.progressBar.visibility = View.VISIBLE
                    loginUser(login, password)
                } else {
                    Toast.makeText(activity, "Заполните все поля", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
        }

        return binding.root
    }

    private fun loginUser(login: String, password: String) {
        val call =
            ApiClient.authApi.login(DTOs.CredentialsDTO(login, password))

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    lifecycleScope.launch {
                        try {
                            val userDTO = withContext(Dispatchers.IO) {
                                ApiClient.authApi.getUser(DTOs.CredentialsDTO(login, password))
                            }

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

                            binding.progressBar.visibility = View.GONE
                            findNavController().navigate(R.id.action_loginFragment_to_mainScreenFragment)

                        } catch (e: Exception) {
                            Log.e("MYCLIENT", getString(R.string.user_data_error, e.message))
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                } else {
                    Toast.makeText(requireContext(),
                        getString(R.string.login_error), Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "${getString(R.string.network_error)  } ${t.message}", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}