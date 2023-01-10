package com.symplified.ordertaker.ui.main.menuandcart.menu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.symplified.ordertaker.R
import com.symplified.ordertaker.models.cartitems.CartItem
import com.symplified.ordertaker.models.products.Product
import com.symplified.ordertaker.viewmodels.CartViewModel

class MenuItemSelectionBottomSheet(
    private val product: Product,
    private val onAddToCartListener: OnAddToCartListener
) : BottomSheetDialogFragment() {

    interface OnAddToCartListener {
        fun onItemAdded(cartItem: CartItem)
    }

    private var quantity = 1
    private var productId = product.id
    private var fullProductName = product.name
    private var itemCode = product.productInventories.firstOrNull()?.itemCode ?: ""
    private var itemPrice: Double = product.productInventories.minOf { it.dineInPrice }
    private val cartViewModel: CartViewModel by activityViewModels()

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
            cartViewModel.insert(
                CartItem(
                    itemName = fullProductName,
                    itemPrice = itemPrice,
                    itemCode = itemCode,
                    productId = productId,
                    quantity = quantity
                )
            )
            dismiss()
        }

        if (product.productVariants.isNotEmpty()) {
            val variantsLayout: LinearLayout = view.findViewById(R.id.product_variant_layout)
            variantsLayout.visibility = View.VISIBLE
            val radioGroupLayout: View =
                inflater.inflate(R.layout.group_radio_product_variants, container, false)
            val radioGroup: RadioGroup = radioGroupLayout.findViewById(R.id.radio_group)
            val groupTitle: TextView =
                radioGroupLayout.findViewById(R.id.product_variant_group_title)
            product.productInventories.forEachIndexed { inventoryIndex, inventory ->

                if (inventoryIndex in 0..product.productVariants.lastIndex) {
                    groupTitle.text = "${product.productVariants[inventoryIndex].name}:"
                }

                inventory.productInventoryItems.firstOrNull()?.let { item ->
                    val radioButton = RadioButton(view.context)
                    radioButton.text = "${item.productVariantAvailable.value}...RM${
                        String.format("%.2f", inventory.dineInPrice)
                    }"
                    radioButton.id = inventoryIndex
                    radioGroup.addView(radioButton)
                    if (inventoryIndex == 0) {
                        radioGroup.check(inventoryIndex)
                        itemCode = product.productInventories[inventoryIndex].itemCode
                        productId = product.productInventories[inventoryIndex].productId
                        fullProductName = "${product.name} - ${item.productVariantAvailable.value}"
                        itemPrice = product.productInventories[inventoryIndex].price
                    }
                    radioGroup.setOnCheckedChangeListener { group, checkedId ->
                        itemCode = product.productInventories[checkedId].itemCode
                        productId = product.productInventories[checkedId].productId
                        fullProductName = "${product.name} - ${item.productVariantAvailable.value}"
                        itemPrice = product.productInventories[checkedId].price
                    }
                }

            }
            variantsLayout.addView(radioGroupLayout)
        }

        return view
    }
}