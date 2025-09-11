package com.example.we_vote

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.we_vote.databinding.FragmentRegistrationBinding
import com.example.we_vote.ktor.ApiClient
import com.example.we_vote.ktor.DTOs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentRegistrationBinding.inflate(layoutInflater, container, false)

        setupUserCreation()

        return binding.root
    }

    private fun setupUserCreation() {
        binding.btnRegister.setOnClickListener {
            with(binding) {
                val name = etNameRegister.text.toString()
                val email = etEmailRegister.text.toString()
                val dob = etDateRegister.text.toString()
                val city = etCityRegister.text.toString()
                val password = etPasswordRegister.text.toString()

                if (
                    name.isNotEmpty() &&
                    email.isNotEmpty() &&
                    dob.isNotEmpty() &&
                    city.isNotEmpty() &&
                    password.isNotEmpty()
                ) {
                    binding.progressBar.visibility = View.VISIBLE
                    registerNewUser(name, email, dob, city, password)
                } else {
                    Toast.makeText(activity, "Заполните все поля", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun registerNewUser(
        name: String,
        email: String,
        dob: String,
        city: String,
        password: String
    ) {
        val call = ApiClient.authApi.register(DTOs.UserDTO(
            name, email, dob, city, password, "user")
        )
        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(),
                        getString(R.string.account_created), Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(requireContext(),
                        getString(R.string.account_creation_error), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Toast.makeText(requireContext(), "${getString(R.string.network_error)} ${t.message}", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}