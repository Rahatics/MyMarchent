package com.mymarchent.mymarchent.ui.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mymarchent.mymarchent.data.model.Order
import com.mymarchent.mymarchent.databinding.ItemPaymentBinding

enum class PaymentStatus { MATCHED, UNMATCHED }

data class PaymentListItem(val order: Order, val status: PaymentStatus)

class PaymentAdapter(
    private var items: List<PaymentListItem>,
    private val onApproveClicked: (Order) -> Unit
) : RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val binding = ItemPaymentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PaymentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<PaymentListItem>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    inner class PaymentViewHolder(private val binding: ItemPaymentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PaymentListItem) {
            binding.tvOrderId.text = "Order ID: #${item.order.orderId}"
            binding.tvAmount.text = "৳${item.order.amount}"
            binding.tvTrxId.text = "TrxID: ${item.order.customerTrxId}"

            when (item.status) {
                PaymentStatus.MATCHED -> {
                    binding.tvStatus.text = "Matched ✅"
                    binding.tvStatus.setBackgroundColor(Color.parseColor("#4CAF50")) // Green
                    binding.btnApproveSingle.visibility = View.VISIBLE
                    binding.btnApproveSingle.setOnClickListener {
                        onApproveClicked(item.order)
                    }
                }
                PaymentStatus.UNMATCHED -> {
                    binding.tvStatus.text = "Not Found ❌"
                    binding.tvStatus.setBackgroundColor(Color.parseColor("#F44336")) // Red
                    binding.btnApproveSingle.visibility = View.GONE
                }
            }
        }
    }
}