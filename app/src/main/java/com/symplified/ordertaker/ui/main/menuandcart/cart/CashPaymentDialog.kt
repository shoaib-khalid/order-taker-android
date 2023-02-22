package com.symplified.ordertaker.ui.main.menuandcart.cart

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.textfield.TextInputLayout
import com.symplified.ordertaker.R
import com.symplified.ordertaker.models.zones.Zone
import com.symplified.ordertaker.viewmodels.CartViewModel
import com.symplified.ordertaker.viewmodels.MenuViewModel
import java.text.DecimalFormat

class CashPaymentDialog(
    private val currency: String,
    private val totalAmount: Double,
): DialogFragment() {

    companion object {
        const val TAG = "CashPaymentDialog"
    }

    private val menuViewModel: MenuViewModel by activityViewModels()
    private val cartViewModel: CartViewModel by activityViewModels()

    private var formatter: DecimalFormat = DecimalFormat("#,###0.00")
    private var changeDueText: TextView? = null
    private var confirmButton: Button? = null
    private var changeDue = 0.00
    private var amountPaid = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View =
            inflater.inflate(R.layout.dialog_cash_payment, container, false)
        isCancelable = false
        (view.findViewById(R.id.cancel_button) as Button).setOnClickListener {
            dismiss()
        }

        val changeDueTextView = view.findViewById<TextView>(R.id.change_due_text)
        val totalSalesAmountText = view.findViewById<TextView>(R.id.total_sales_amount)
        val layoutAmountPaid = view.findViewById<TextInputLayout>(R.id.amount_text_input)
        val editTextAmountPaid = view.findViewById<EditText>(R.id.amount_edit_text)
        val confirmButton = view.findViewById<Button>(R.id.confirm_button)

        totalSalesAmountText.text =
            getString(R.string.monetary_amount, currency, formatter.format(totalAmount))
        changeDueTextView.text =
            getString(R.string.monetary_amount, currency, formatter.format(0.0))

        editTextAmountPaid.doAfterTextChanged { editable ->
            amountPaid = editable.toString().toDoubleOrNull() ?: 0.00
            changeDue = amountPaid - totalAmount
            changeDueTextView.text = getString(
                R.string.monetary_amount,
                currency,
                if (changeDue >= 0) formatter.format(changeDue) else "0.00"
            )
            confirmButton.isEnabled = changeDue >= 0
        }

        val selectedTable = menuViewModel.selectedTable!!
        val selectedZone = menuViewModel.zonesWithTables.value!!.first {
            it.zone.id == selectedTable.zoneId
        }.zone
        confirmButton.setOnClickListener {
            cartViewModel.placeOrder(selectedZone, selectedTable)
            dismiss()
        }

        return view
    }
}