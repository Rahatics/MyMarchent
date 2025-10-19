package com.mymarchent.mymarchent.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mymarchent.mymarchent.databinding.FragmentSettingsBinding
import com.mymarchent.mymarchent.ui.login.LoginActivity

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupClickListeners()

        viewModel.loadUserDetails()
    }

    private fun setupObservers() {
        viewModel.apiKey.observe(viewLifecycleOwner) { apiKey ->
            binding.tvApiKey.text = apiKey ?: "Not Found"
        }
    }

    private fun setupClickListeners() {
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            // Navigate back to LoginActivity and clear the task stack
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}