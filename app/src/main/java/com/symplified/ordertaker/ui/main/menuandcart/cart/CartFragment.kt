package com.symplified.ordertaker.ui.main.menuandcart.cart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.symplified.ordertaker.App
import com.symplified.ordertaker.R
import com.symplified.ordertaker.databinding.FragmentCartBinding
import com.symplified.ordertaker.models.CartItem
import com.symplified.ordertaker.viewmodels.CartViewModel
import com.symplified.ordertaker.viewmodels.CartViewModelFactory

class CartFragment : Fragment(), CartItemsAdapter.OnRemoveFromCartListener {
    private var _binding: FragmentCartBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val cartViewModel: CartViewModel by viewModels {
        CartViewModelFactory(App.cartItemRepository)
    }

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
            cartItemsAdapter.updateItems(cartItems)
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemRemoved(cartItem: CartItem) {
        cartViewModel.delete(cartItem)
    }
}