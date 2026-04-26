package com.example.we_vote

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.we_vote.databinding.FragmentNewPollBinding

class NewPollFragment : Fragment() {

    private var _binding: FragmentNewPollBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewPollViewModel by viewModels {
        val app = requireActivity().application as WeVoteApplication
        NewPollViewModel.Factory(
            app.container.addSurveyUseCase,
            app.container.addApplicationUseCase,
            app.container.preferences,
            app.container.getCitiesUseCase,
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentNewPollBinding.inflate(layoutInflater, container, false)

        setupNavigation()

        val access = (requireActivity().application as WeVoteApplication).container.preferences.getAccess()
        val isAdmin = access == "admin" || access == "developer"

        if (isAdmin) {
            binding.cityCard.visibility = View.VISIBLE
            viewModel.cities.observe(viewLifecycleOwner) { cities ->
                val adapter = buildCityAdapter(cities)
                binding.cityDropdown.setAdapter(adapter)
                binding.cityDropdown.setOnClickListener { binding.cityDropdown.showDropDown() }
            }
        }

        binding.addSurveyBtn.setOnClickListener {
            val title = binding.newTitle.text.toString()
            val first = binding.newFirstChoice.text.toString()
            val second = binding.newSecondChoice.text.toString()
            val third = binding.newThirdChoice.text.toString()
            val city = if (isAdmin) binding.cityDropdown.text.toString() else ""

            val fieldsValid = title.isNotEmpty() && first.isNotEmpty()
                    && second.isNotEmpty() && third.isNotEmpty()
            val cityValid = !isAdmin || city.isNotEmpty()

            if (fieldsValid && cityValid) {
                viewModel.submit(title, first, second, third, city)
            } else {
                Toast.makeText(activity, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is NewPollViewModel.State.Loading -> binding.progressBar.visibility = View.VISIBLE
                is NewPollViewModel.State.SurveyPublished -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), getString(R.string.survey_created), Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
                is NewPollViewModel.State.ApplicationSubmitted -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), getString(R.string.application_sent), Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
                is NewPollViewModel.State.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "${getString(R.string.network_error)} ${state.message}", Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }

        return binding.root
    }

    private fun setupNavigation() {
        val access = (requireActivity().application as WeVoteApplication).container.preferences.getAccess()
        VotingUtil.setBottomBar(access, binding.bottomNav)
        binding.bottomNav.menu.findItem(R.id.nav_new_poll).isChecked = true

        val requestAction = if (access == "admin" || access == "developer")
            R.id.action_newPollFragment_to_newApplicationsFragment
        else
            R.id.action_newPollFragment_to_myApplicationsFragment

        binding.bottomNav.setOnItemSelectedListener { item ->
            VotingUtil.setupNavigation(
                this, item.itemId,
                R.id.action_newPollFragment_to_mainScreenFragment,
                R.id.action_newPollFragment_self,
                R.id.action_newPollFragment_to_profileFragment,
                R.id.action_newPollFragment_to_archiveFragment,
                requestAction,
            )
        }
    }

    private fun buildCityAdapter(cities: List<String>) =
        object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line, cities) {
            override fun getFilter() = object : Filter() {
                override fun performFiltering(c: CharSequence?) = FilterResults().also {
                    it.values = cities; it.count = cities.size
                }
                override fun publishResults(c: CharSequence?, r: FilterResults?) = notifyDataSetChanged()
            }
        }

    override fun onResume() {
        super.onResume()
        if (_binding != null) binding.bottomNav.menu.findItem(R.id.nav_new_poll).isChecked = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
