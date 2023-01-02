package com.symplified.ordertaker.ui.main.menuandcart.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.symplified.ordertaker.OrderTakerApplication
import com.symplified.ordertaker.R
import com.symplified.ordertaker.models.CartItem
import com.symplified.ordertaker.models.Item
import com.symplified.ordertaker.viewmodels.CartViewModel
import com.symplified.ordertaker.viewmodels.CartViewModelFactory
import kotlinx.coroutines.launch

class MenuItemSelectionBottomSheet(
    private val menuItem: Item,
    private val onAddToCartListener: OnAddToCartListener
) : BottomSheetDialogFragment() {

    interface OnAddToCartListener {
        fun onItemAdded(cartItem: CartItem)
    }

    private var quantity = 1
    private val cartViewModel: CartViewModel by viewModels {
        CartViewModelFactory(OrderTakerApplication.repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.dialog_menu_item_selection,
            container,
            false
        )
        val productNameView: TextView = view.findViewById(R.id.product_name)
        productNameView.text = menuItem.name

        val quantityCount: TextView = view.findViewById(R.id.quantity_count)
        quantityCount.text = quantity.toString()

        val subtractButton: TextView = view.findViewById(R.id.btn_subtract)
        subtractButton.setOnClickListener {
            quantityCount.text = (--quantity).toString()
        }

        val addButton: TextView = view.findViewById(R.id.btn_add)
        addButton.setOnClickListener {
            quantityCount.text = (++quantity).toString()
        }

        val addToCartButton: Button = view.findViewById(R.id.btn_add_to_cart)
        addToCartButton.setOnClickListener {
//            cartViewModel.insert(
//                CartItem(0, menuItem.name, menuItem.price, quantity)
//            )
            onAddToCartListener.onItemAdded(
                CartItem(0, menuItem.name, menuItem.price, quantity)
            )
            dismiss()
        }

        return view
    }
}