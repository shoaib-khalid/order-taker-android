package com.symplified.ordertaker.ui.main.menuandcart.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.symplified.ordertaker.R
import com.symplified.ordertaker.models.CartItem
import com.symplified.ordertaker.models.products.Product

class MenuItemSelectionBottomSheet(
    private val product: Product,
    private val onAddToCartListener: OnAddToCartListener
) : BottomSheetDialogFragment() {

    interface OnAddToCartListener {
        fun onItemAdded(cartItem: CartItem)
    }

    private var quantity = 1
//    private val cartViewModel: CartViewModel by viewModels {
//        CartViewModelFactory(OrderTakerApplication.cartItemRepository)
//    }

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
        productNameView.text = product.name

        val quantityCount: TextView = view.findViewById(R.id.quantity_count)
        quantityCount.text = quantity.toString()

        val subtractButton: TextView = view.findViewById(R.id.btn_subtract)
        subtractButton.setOnClickListener {
            quantityCount.text = (--quantity).toString()
            if (quantity < 2)
                subtractButton.isEnabled = false
        }

        val addButton: TextView = view.findViewById(R.id.btn_add)
        addButton.setOnClickListener {
            quantityCount.text = (++quantity).toString()
            if (quantity > 1)
                subtractButton.isEnabled = true
        }

        val addToCartButton: Button = view.findViewById(R.id.btn_add_to_cart)
        addToCartButton.setOnClickListener {
//            onAddToCartListener.onItemAdded(
//                CartItem(0, product.name, product.price, quantity)
//            )
            dismiss()
        }

        return view
    }
}