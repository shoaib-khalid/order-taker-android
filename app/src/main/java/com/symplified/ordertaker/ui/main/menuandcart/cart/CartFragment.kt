package com.symplified.ordertaker.ui.main.menuandcart.cart

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
                totalPrice += (cartItem.cartItem.itemPrice * cartItem.cartItem.quantity)
            }
            binding.totalPriceCount.text = "RM ${String.format("%.2f", totalPrice)}"
        }

        val spinner = binding.paymentTypeSpinner
        ArrayAdapter.createFromResource(
            view.context,
            R.array.payment_options_array,
            android.R.layout.simple_spinner_item
        ).also { spinnerAdapter ->
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = spinnerAdapter
        }

        binding.clearCartButton.setOnClickListener { cartViewModel.clearAll() }
        binding.placeOrderButton.setOnClickListener {
            cartViewModel.clearAll()
            Snackbar.make(view, "Order placed", Snackbar.LENGTH_SHORT).show()
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

    override fun onItemRemoved(cartItem: CartItemWithSubItems) {
        cartViewModel.delete(cartItem)
    }
}