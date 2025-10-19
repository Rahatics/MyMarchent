package com.mymarchent.mymarchent.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mymarchent.mymarchent.data.model.Order
import com.mymarchent.mymarchent.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var paymentAdapter: PaymentAdapter
    private var matchedOrders: List<Order> = emptyList()

    private val requestPermissionLauncher = 
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireContext(), "Permission granted. Syncing now...", Toast.LENGTH_SHORT).show()
                viewModel.performSync()
            } else {
                Toast.makeText(requireContext(), "SMS permission is required to match payments.", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClickListeners()
        setupObservers()
        setupSwipeToRefresh()
    }

    private fun setupRecyclerView() {
        paymentAdapter = PaymentAdapter(emptyList()) { order ->
            viewModel.approveSingleOrder(order)
        }
        binding.rvPayments.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = paymentAdapter
        }
    }

    private fun setupClickListeners() {
        binding.btnSync.setOnClickListener {
            checkAndRequestSmsPermission()
        }

        binding.btnApproveAll.setOnClickListener {
            viewModel.approveMatchedOrders(matchedOrders)
        }
    }

    private fun setupSwipeToRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.performSync()
        }
    }
    
    private fun checkAndRequestSmsPermission() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED -> {
                viewModel.performSync()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS) -> {
                showSmsPermissionRationaleDialog()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_SMS)
            }
        }
    }

    private fun showSmsPermissionRationaleDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Permission Required")
            .setMessage("To automatically match payments, this app needs to read your payment confirmation SMS messages.")
            .setPositiveButton("Grant Permission") { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.READ_SMS)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (paymentAdapter.itemCount == 0 && !binding.swipeRefreshLayout.isRefreshing) {
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
            binding.swipeRefreshLayout.isRefreshing = isLoading
            binding.btnSync.isEnabled = !isLoading
            binding.btnApproveAll.isEnabled = !isLoading
        }

        viewModel.syncResult.observe(viewLifecycleOwner) { result ->
            matchedOrders = result.matchedOrders

            val combinedList = mutableListOf<PaymentListItem>()
            combinedList.addAll(result.matchedOrders.map { PaymentListItem(it, PaymentStatus.MATCHED) })
            combinedList.addAll(result.unmatchedOrders.map { PaymentListItem(it, PaymentStatus.UNMATCHED) })

            paymentAdapter.updateData(combinedList)
            
            // Show empty state if the list is empty
            if (combinedList.isEmpty()) {
                binding.tvEmptyState.visibility = View.VISIBLE
                binding.rvPayments.visibility = View.GONE
            } else {
                binding.tvEmptyState.visibility = View.GONE
                binding.rvPayments.visibility = View.VISIBLE
            }

            binding.btnApproveAll.visibility = if (result.matchedOrders.size > 1) View.VISIBLE else View.GONE
        }

        viewModel.awaitingConfirmationCount.observe(viewLifecycleOwner) { count ->
            binding.tvAwaitingCount.text = count.toString()
        }

        viewModel.confirmedTodayCount.observe(viewLifecycleOwner) { count ->
            binding.tvConfirmedTodayCount.text = count.toString()
        }

        viewModel.approvalStatus.observe(viewLifecycleOwner) { status ->
            Toast.makeText(requireContext(), status, Toast.LENGTH_LONG).show()
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_LONG).show()
            // Also show empty state on error
            binding.tvEmptyState.text = error
            binding.tvEmptyState.visibility = View.VISIBLE
            binding.rvPayments.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}