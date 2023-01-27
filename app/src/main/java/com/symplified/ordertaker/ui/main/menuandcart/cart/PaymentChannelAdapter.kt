package com.symplified.ordertaker.ui.main.menuandcart.cart

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.symplified.ordertaker.R
import com.symplified.ordertaker.models.paymentchannel.PaymentChannel

class PaymentChannelAdapter(
    private val onPaymentTypeClickListener: OnPaymentTypeClickListener
) : RecyclerView.Adapter<PaymentChannelAdapter.ViewHolder>() {

    private val paymentChannels: MutableList<PaymentChannel> =
        mutableListOf()
    private var selectedPosition = RecyclerView.NO_POSITION

    interface OnPaymentTypeClickListener {
        fun onPaymentTypeClicked(paymentChannel: PaymentChannel)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.payment_channel_text_view)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.row_payment_type, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val isSelected = (selectedPosition == position)
//        viewHolder.itemView.isSelected = isSelected
        viewHolder.textView.isSelected = isSelected

        viewHolder.textView.setTextColor(Color.parseColor(if (isSelected) "#FFFFFFFF" else "#FF000000"))

        viewHolder.textView.text = paymentChannels[position].channelName
        viewHolder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = viewHolder.adapterPosition
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            onPaymentTypeClickListener.onPaymentTypeClicked(paymentChannels[selectedPosition])
        }
    }

    override fun getItemCount() = paymentChannels.size

    fun updatePaymentChannels(updatedPaymentChannels: List<PaymentChannel>) {
        if (paymentChannels.isNotEmpty()) {
            val originalSize = paymentChannels.size
            paymentChannels.clear()
            notifyItemRangeRemoved(0, originalSize)
        }
        paymentChannels.addAll(updatedPaymentChannels)
        notifyItemRangeInserted(0, paymentChannels.size)
    }

    fun selectPaymentChannel(selectedPaymentChannel: PaymentChannel) {
        clearSelectedPaymentChannel()
        val indexOfSelected = paymentChannels.indexOf(selectedPaymentChannel)
        if (indexOfSelected != -1) {
            selectedPosition = indexOfSelected
            notifyItemChanged(indexOfSelected)
        }
    }

    fun clearSelectedPaymentChannel() {
        val previouslySelected = selectedPosition
        selectedPosition = RecyclerView.NO_POSITION
        if (previouslySelected != RecyclerView.NO_POSITION) {
            notifyItemChanged(previouslySelected)
        }
    }
}