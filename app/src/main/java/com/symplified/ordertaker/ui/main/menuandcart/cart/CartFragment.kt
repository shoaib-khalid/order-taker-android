package com.symplified.ordertaker.ui.main.menuandcart.cart

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.symplified.ordertaker.App
import com.symplified.ordertaker.R
import com.symplified.ordertaker.constants.SharedPrefsKey
import com.symplified.ordertaker.databinding.FragmentCartBinding
import com.symplified.ordertaker.models.cartitems.CartItem
import com.symplified.ordertaker.models.cartitems.CartItemWithSubItems
import com.symplified.ordertaker.viewmodels.CartViewModel
import com.symplified.ordertaker.viewmodels.MenuViewModel

class CartFragment : Fragment(), CartItemsAdapter.OnRemoveFromCartListener {
    private var _binding: FragmentCartBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val menuViewModel: MenuViewModel by activityViewModels()
    private val cartViewModel: CartViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val cartItemsAdapter = CartItemsAdapter(onRemoveFromCartListener = this)
        val cartItemsList = binding.cartItemsList
        cartItemsList.layoutManager = LinearLayoutManager(view.context);
        cartItemsList.adapter = cartItemsAdapter

        cartViewModel.cartItems.observe(viewLifecycleOwner) { cartItems ->
            binding.placeOrderButton.isEnabled = cartItems.isNotEmpty()

            cartItemsAdapter.updateItems(cartItems)

            var totalPrice = 0.0
            cartItems.forEach { cartItem ->
                totalPrice += (cartItem.itemPrice * cartItem.quantity)
            }
            binding.totalPriceCount.text = "RM ${String.format("%.2f", totalPrice)}"
        }

        cartViewModel.isPlacingOrder.observe(viewLifecycleOwner) { isPlacingOrder ->
            if (isPlacingOrder) {
                binding.mainLayout.visibility = View.GONE
                binding.progressBarLayout.visibility = View.VISIBLE
            } else {
                binding.mainLayout.visibility = View.VISIBLE
                binding.progressBarLayout.visibility = View.GONE
            }
        }

        cartViewModel.orderResultMessage.observe(viewLifecycleOwner) { message ->
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
        }

        val paymentButtonsMap: Map<String, Button> = mapOf(
            "CASH" to binding.buttonPaymentTypeCash,
            "TNG" to binding.buttonPaymentTypeTouchNGo,
            "GRABPAY" to binding.buttonPaymentTypeGrabpay,
            "OTHERS" to binding.buttonPaymentTypeOthers
        )

        val typedValue = TypedValue()
        view.context.theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true)
        val primaryColor = ContextCompat.getColor(view.context, typedValue.resourceId)
        cartViewModel.selectedPaymentType.observe(viewLifecycleOwner) { selectedPaymentType ->
            for ((paymentType, button) in paymentButtonsMap) {
                val isSelectedPaymentType = (paymentType == selectedPaymentType)
                button.isSelected = isSelectedPaymentType
                button.setBackgroundColor(if (isSelectedPaymentType) primaryColor else Color.WHITE)
//                button.setBackgroundColor(Color.parseColor(if (isSelectedPaymentType) "#FF03DAC5" else "#FFFFFFFF"))
                button.setTextColor(if (isSelectedPaymentType) Color.WHITE else Color.BLACK)
            }
        }

        binding.buttonPaymentTypeCash.setOnClickListener { cartViewModel.setCurrentPaymentType("CASH") }
        binding.buttonPaymentTypeTouchNGo.setOnClickListener { cartViewModel.setCurrentPaymentType("TNG") }
        binding.buttonPaymentTypeGrabpay.setOnClickListener { cartViewModel.setCurrentPaymentType("GRABPAY") }
        binding.buttonPaymentTypeOthers.setOnClickListener { cartViewModel.setCurrentPaymentType("OTHERS") }

        binding.clearCartButton.setOnClickListener { cartViewModel.clearAll() }
        binding.placeOrderButton.setOnClickListener {
            cartViewModel.placeOrder(
                menuViewModel.selectedZone!!,
                menuViewModel.selectedTable!!
            )
        }

        binding.zoneNo.text = "Zone: ${menuViewModel.selectedZone}"
        binding.tableNo.text = "Table No.: ${menuViewModel.selectedTable}"
        binding.serverName.text =
            "Served by: ${App.sharedPreferences().getString(SharedPrefsKey.USERNAME, "")}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemRemoved(cartItem: CartItem) {
        cartViewModel.delete(cartItem)
    }
}