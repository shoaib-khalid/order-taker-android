package com.symplified.easydukanpos.ui.main.menu_and_cart.cart

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.symplified.easydukanpos.R
import com.symplified.easydukanpos.models.paymentchannel.PaymentOption

class PaymentChannelAdapter(
    private val onPaymentTypeClickListener: OnPaymentTypeClickListener
) : RecyclerView.Adapter<PaymentChannelAdapter.ViewHolder>() {

    private val paymentOptions: Array<PaymentOption> = enumValues()
    private var selectedPosition = RecyclerView.NO_POSITION

    interface OnPaymentTypeClickListener {
        fun onPaymentTypeClicked(paymentOption: PaymentOption)
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

        viewHolder.textView.setTextColor(
            Color.parseColor(
                if (isSelected) "#FFFFFFFF"
                else "#FF000000"
            )
        )

        viewHolder.textView.text = paymentOptions[position].displayName
        viewHolder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = viewHolder.adapterPosition
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            onPaymentTypeClickListener.onPaymentTypeClicked(paymentOptions[selectedPosition])
        }

        viewHolder.itemView.isEnabled = paymentOptions[position].isEnabled
        viewHolder.textView.isEnabled = paymentOptions[position].isEnabled
    }

    override fun getItemCount() = paymentOptions.size

    fun selectPaymentOption(selectedPaymentOption: PaymentOption) {
        clearSelectedPaymentOption()
        val indexOfSelected = paymentOptions.indexOf(selectedPaymentOption)
        if (indexOfSelected != -1) {
            selectedPosition = indexOfSelected
            notifyItemChanged(indexOfSelected)
        }
    }

    private fun clearSelectedPaymentOption() {
        val previouslySelected = selectedPosition
        selectedPosition = RecyclerView.NO_POSITION
        if (previouslySelected != RecyclerView.NO_POSITION) {
            notifyItemChanged(previouslySelected)
        }
    }

    fun setPaymentOptionEnabled(paymentOption: PaymentOption, isEnabled: Boolean) {
        val index = paymentOptions.indexOfFirst { it.name == paymentOption.name }
        paymentOptions[index].isEnabled = isEnabled
        if (selectedPosition == index) {
            clearSelectedPaymentOption()
        }
        notifyItemChanged(index)
    }
}