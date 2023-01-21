package com.symplified.ordertaker.ui.main.menuandcart.menu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.symplified.ordertaker.R
import com.symplified.ordertaker.models.cartitems.CartItem
import com.symplified.ordertaker.models.products.ProductWithDetails
import com.symplified.ordertaker.viewmodels.CartViewModel

class ProductSelectionDialog(
    private val productWithDetails: ProductWithDetails,
    private val onAddToCartListener: OnAddToCartListener
) : DialogFragment() {

    interface OnAddToCartListener {
        fun onItemAdded(cartItem: CartItem)
    }

    private val cartViewModel: CartViewModel by activityViewModels()
    private val dialogViewModel: ProductSelectionViewModel by activityViewModels()

    private val addOnGroupsWithCheckboxes: MutableMap<String, List<CheckBox>> = mutableMapOf()

    private val productPackagesWithOptionViews: MutableMap<String, Map<String, View>> =
        mutableMapOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            if (productWithDetails.productVariantsWithVariantsAvailable.isNotEmpty()
                || productWithDetails.product.hasAddOn
                || productWithDetails.product.isPackage
            )
                R.layout.dialog_product_selection_with_variants
            else
                R.layout.dialog_product_selection,
            container,
            false
        )

        val productNameView: TextView = view.findViewById(R.id.product_name)
        val productQuantityTextView: TextView = view.findViewById(R.id.quantity_count)
        val decrementButton: TextView = view.findViewById(R.id.btn_decrement)
        val incrementButton: TextView = view.findViewById(R.id.btn_add)
        val variantsLayout: LinearLayout = view.findViewById(R.id.product_variant_layout)
        val addToCartButton: Button = view.findViewById(R.id.btn_add_to_cart)

        decrementButton.setOnClickListener { dialogViewModel.decrementProductQuantity() }
        incrementButton.setOnClickListener { dialogViewModel.incrementProductQuantity() }

        dialogViewModel.productQuantity.observe(viewLifecycleOwner) { quantity ->
            productQuantityTextView.text = quantity.toString()
            decrementButton.isEnabled = quantity > 1
        }

        dialogViewModel.productWithDetails.observe(viewLifecycleOwner) { productWithDetails ->
            productNameView.text = productWithDetails.product.name

            if (productWithDetails.productVariantsWithVariantsAvailable.isNotEmpty()) {
                val radioGroupLayout: View =
                    inflater.inflate(R.layout.group_radio_product_variants, container, false)
                val radioGroup: RadioGroup = radioGroupLayout.findViewById(R.id.radio_group)
                val groupTitle: TextView =
                    radioGroupLayout.findViewById(R.id.product_variant_group_title)
                productWithDetails.productInventoriesWithItems.forEachIndexed { inventoryIndex, inventory ->

                    if (inventoryIndex in 0..productWithDetails.productVariantsWithVariantsAvailable.lastIndex) {
                        groupTitle.text =
                            "${productWithDetails.productVariantsWithVariantsAvailable[inventoryIndex].productVariant.name}:"
                    }

                    inventory.inventoryItems.firstOrNull()?.let { item ->
                        Log.d("dialogfragment", "inventoryIndex: $inventoryIndex, item: ${item.productVariantAvailable.value}")
                        val radioButton = RadioButton(view.context)
                        radioButton.text = "${item.productVariantAvailable.value}...RM${
                            String.format("%.2f", inventory.productInventory.dineInPrice)
                        }"
                        radioButton.id = inventoryIndex
                        radioGroup.addView(radioButton)
                        radioGroup.setOnCheckedChangeListener { group, checkedId ->
                            dialogViewModel.setCartItemWithVariant(checkedId)
//                        itemCode = productWithDetails.productInventories[checkedId].itemCode
//                        productId = productWithDetails.productInventories[checkedId].productId
//                        fullProductName = "${productWithDetails.name} - ${item.productVariantAvailable?.value}"
//                        itemPrice = productWithDetails.productInventories[checkedId].dineInPrice
                        }
                        if (inventoryIndex == 0) {
                            radioGroup.check(inventoryIndex)
//                        itemCode = productWithDetails.productInventories[inventoryIndex].itemCode
//                        productId = productWithDetails.productInventories[inventoryIndex].productId
//                        fullProductName = "${productWithDetails.name} - ${item.productVariantAvailable?.value}"
//                        itemPrice = productWithDetails.productInventories[inventoryIndex].dineInPrice
                        }
                    }
                }
                variantsLayout.addView(radioGroupLayout)
            }

            productWithDetails.productAddOnGroupsWithDetails.forEach { addOnGroupWithDetails ->
                val addOnLayout: View =
                    inflater.inflate(R.layout.group_choices, container, false)
                val checkboxes: MutableList<CheckBox> = mutableListOf()

                val addOnGroup = addOnGroupWithDetails.productAddOnGroup
                val titleText = "${addOnGroup.title} " +
                        "(${
                            if (addOnGroup.minAllowed == 0) "Optional"
                            else "Select at least ${addOnGroup.maxAllowed}"
                        })"
                (addOnLayout.findViewById(R.id.checkbox_group_title) as TextView).text =
                    titleText
                addOnGroupWithDetails.addOnDetails.forEachIndexed { detailsIndex, addOnDetails ->
                    val checkbox = CheckBox(view.context)
                    checkbox.id = detailsIndex
                    checkbox.text = "${addOnDetails.name}   +RM${
                        String.format(
                            "%.2f",
                            addOnDetails.dineInPrice
                        )
                    }"
                    checkbox.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            dialogViewModel.selectAddOn(addOnDetails, addOnGroup.id)
                        } else {
                            dialogViewModel.removeAddOn(addOnDetails, addOnGroup.id)
                        }
                    }
                    checkboxes.add(checkbox)
                    (addOnLayout.rootView as LinearLayout).addView(checkbox)
                }
                addOnGroupsWithCheckboxes[addOnGroup.id] = checkboxes.toList()
                variantsLayout.addView(addOnLayout)
            }

            productWithDetails.productPackages.forEach { productPackageWithOptionDetails ->
                val packageLayout: View =
                    inflater.inflate(R.layout.group_choices, container, false)
                val packageOptionsWithViews: MutableMap<String, View> = mutableMapOf()

                val packageGroup = productPackageWithOptionDetails.productPackage
                val minAmount = packageGroup.minAllow
                val maxAmount = packageGroup.totalAllow
                (packageLayout.findViewById(R.id.checkbox_group_title) as TextView).text =
                    "Select $minAmount of $maxAmount"

                productPackageWithOptionDetails.productPackageOptionDetails.forEach { optionDetails ->
                    val optionView: View =
                        inflater.inflate(R.layout.row_package_option, container, false)
                    (optionView.findViewById(R.id.option_text_view) as TextView).text =
                        optionDetails.product!!.name
                    (optionView.findViewById(R.id.btn_add) as ImageButton).setOnClickListener {
                        dialogViewModel.addCartSubItem(packageGroup, optionDetails)
                    }
                    (optionView.findViewById(R.id.btn_decrement) as ImageButton).setOnClickListener {
                        dialogViewModel.decrementCartSubItem(packageGroup, optionDetails)
                    }

                    packageOptionsWithViews[optionDetails.optionDetails.id] = optionView
                    (packageLayout.rootView as LinearLayout).addView(optionView)
                }

                productPackagesWithOptionViews[packageGroup.id] =
                    packageOptionsWithViews
                variantsLayout.addView(packageLayout)
            }
        }

        dialogViewModel.addOnGroupsCountMap.observe(viewLifecycleOwner) { addOnGroupsCountMap ->
            var isAddToCartButtonEnabled = true
            addOnGroupsCountMap.forEach { (groupId, selectionStats) ->

                if (selectionStats.selected < selectionStats.minAllowed) {
                    isAddToCartButtonEnabled = false
                }

                addOnGroupsWithCheckboxes[groupId]?.forEach { checkbox ->
                    checkbox.isEnabled =
                        selectionStats.selected != selectionStats.maxAllowed || checkbox.isChecked
                }
            }
            addToCartButton.isEnabled = isAddToCartButtonEnabled
        }

        dialogViewModel.cartSubItems.observe(viewLifecycleOwner) { cartSubItems ->

            productPackagesWithOptionViews.forEach { (_, packageOptionsWithViews) ->
                packageOptionsWithViews.forEach { (optionId, optionView) ->
                    (optionView.findViewById(R.id.quantity_count) as TextView).text =
                        (cartSubItems.firstOrNull { it.optionId == optionId }?.quantity
                            ?: 0).toString()
                }
            }

            // TODO: Toggle visibility of minus and plus buttons
        }

        dialogViewModel.isCartItemValid.observe(viewLifecycleOwner) { isCartItemValid ->
            addToCartButton.isEnabled = isCartItemValid
        }

        addToCartButton.setOnClickListener {
            dialogViewModel.addToCart()
            dismiss()
        }

        return view
    }
}