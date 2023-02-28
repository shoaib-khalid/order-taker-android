package com.symplified.ordertaker.ui.main.menuandcart.menu

import android.content.Context
import android.graphics.Insets
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doBeforeTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.textfield.TextInputLayout
import com.symplified.ordertaker.R
import com.symplified.ordertaker.models.cartitems.CartItem
import com.symplified.ordertaker.models.products.ProductWithDetails
import com.symplified.ordertaker.viewmodels.CartViewModel
import java.text.DecimalFormat
import java.util.regex.Pattern


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

    private var currencySymbol = "RM"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
//            if (productWithDetails.productVariantsWithVariantsAvailable.isNotEmpty()
//                || productWithDetails.product.hasAddOn
//                || productWithDetails.product.isPackage
//            )
            R.layout.dialog_product_selection_with_variants
//            else
//                R.layout.dialog_product_selection
            ,
            container,
            false
        )

        val productNameView: TextView = view.findViewById(R.id.product_name)
        val productQuantityTextView: TextView = view.findViewById(R.id.quantity_count)
        val decrementButton: TextView = view.findViewById(R.id.btn_decrement)
        val incrementButton: TextView = view.findViewById(R.id.btn_add)
        val variantsLayout: LinearLayout = view.findViewById(R.id.product_variant_layout)
        val addToCartButton: Button = view.findViewById(R.id.btn_add_to_cart)
        val productPriceInputLayout: TextInputLayout = view.findViewById(R.id.price_input_layout)
        val productPriceEditText: EditText = productPriceInputLayout.editText!!

        decrementButton.setOnClickListener { dialogViewModel.decrementProductQuantity() }
        incrementButton.setOnClickListener { dialogViewModel.incrementProductQuantity() }

        productPriceEditText.addTextChangedListener(object: TextWatcher {
            private var ignore = false
            private var currentAmount = StringBuilder()
            private val formatter: DecimalFormat = DecimalFormat("#,##0.00")

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isEmpty()) {
                    currentAmount.clear()
                }

                if (before == 0 && currentAmount.length < 8) {
                    currentAmount.append(s[start])
                } else if (count == 0 && currentAmount.isNotEmpty()) {
                    currentAmount.deleteCharAt(currentAmount.length - 1)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (ignore)
                    return

                ignore = true

                val newAmount = (currentAmount.toString().toDoubleOrNull() ?: 0.00) / 100
                dialogViewModel.setCustomPrice(newAmount)
                productPriceEditText.setText(formatter.format(newAmount), TextView.BufferType.EDITABLE)

                if (productPriceEditText.length() > 0) {
                    productPriceEditText.setSelection(productPriceEditText.length())
                }
                ignore = false
            }

        })

        dialogViewModel.productQuantity.observe(viewLifecycleOwner) { quantity ->
            productQuantityTextView.text = quantity.toString()
            decrementButton.isEnabled = quantity > 1
        }

        dialogViewModel.currencySymbol.observe(viewLifecycleOwner) { currencySymbol ->
            this.currencySymbol = currencySymbol ?: "RM"
        }

        dialogViewModel.productWithDetails.observe(viewLifecycleOwner) { productWithDetails ->
            productNameView.text = productWithDetails.product.name

            if (productWithDetails.product.isCustomPrice) {
                productPriceInputLayout.visibility = View.VISIBLE
                productPriceEditText.requestFocus()
                val imm =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(productPriceEditText, InputMethodManager.SHOW_IMPLICIT)
            }

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
                        val radioButton = RadioButton(view.context)
                        radioButton.text =
                            "${item.productVariantAvailable.value}...$currencySymbol${
                                String.format("%.2f", inventory.productInventory.dineInPrice)
                            }"
                        radioButton.id = inventoryIndex
                        radioGroup.addView(radioButton)
                        radioGroup.setOnCheckedChangeListener { group, checkedId ->
                            dialogViewModel.setCartItemWithVariant(checkedId)
                        }
                        if (inventoryIndex == 0) {
                            radioGroup.check(inventoryIndex)
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
                            else "Select at least ${addOnGroup.minAllowed}"
                        }) Max. ${addOnGroup.maxAllowed}"
                (addOnLayout.findViewById(R.id.checkbox_group_title) as TextView).text =
                    titleText
                addOnGroupWithDetails.addOnDetails.forEachIndexed { detailsIndex, addOnDetails ->
                    val addOnView: View =
                        inflater.inflate(R.layout.row_addon_option, container, false)

                    val checkbox = addOnView.findViewById(R.id.addon_checkbox) as CheckBox
                    checkbox.id = detailsIndex
                    checkbox.text = addOnDetails.name

                    checkbox.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            dialogViewModel.selectAddOn(addOnDetails, addOnGroup.id)
                        } else {
                            dialogViewModel.removeAddOn(addOnDetails, addOnGroup.id)
                        }
                    }

                    (addOnView.findViewById(R.id.price_text_view) as TextView).text =
                        "+$currencySymbol${String.format("%.2f", addOnDetails.dineInPrice)}"

                    checkboxes.add(checkbox)
                    (addOnLayout.rootView as LinearLayout).addView(addOnView)
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
                    "Select at least $minAmount of $maxAmount"

                productPackageWithOptionDetails.productPackageOptionDetails.forEach { optionDetails ->
                    val optionView: View =
                        inflater.inflate(R.layout.row_package_option, container, false)
                    (optionView.findViewById<TextView>(R.id.option_text_view)).text =
                        optionDetails.product!!.name
                    (optionView.findViewById<ImageButton>(R.id.btn_add)).setOnClickListener {
                        dialogViewModel.addCartSubItem(packageGroup, optionDetails)
                    }
                    (optionView.findViewById<ImageButton>(R.id.btn_decrement)).setOnClickListener {
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
//            var isAddToCartButtonEnabled = true
            addOnGroupsCountMap.forEach { (groupId, selectionStats) ->

//                if (selectionStats.selected < selectionStats.minAllowed) {
//                    isAddToCartButtonEnabled = false
//                }

                addOnGroupsWithCheckboxes[groupId]?.forEach { checkbox ->
                    checkbox.isEnabled =
                        selectionStats.selected != selectionStats.maxAllowed || checkbox.isChecked
                }
            }
//            addToCartButton.isEnabled = isAddToCartButtonEnabled
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

    override fun onResume() {
        dialogViewModel.productWithDetails.observe(viewLifecycleOwner) { productWithDetails ->
            val window = dialog!!.window!!
            val heightMultiplier =
                if (productWithDetails.product.hasAddOn ||
                    productWithDetails.product.isPackage ||
                    productWithDetails.productVariantsWithVariantsAvailable.isNotEmpty()
                )
                    0.99
                else if (productWithDetails.product.isCustomPrice)
                    0.45
                else
                    0.25
            val width: Int
            val height: Int
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val windowMetrics =
                    requireActivity().windowManager.currentWindowMetrics
                val insets: Insets = windowMetrics.windowInsets
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
                width = windowMetrics.bounds.width() - insets.left -
                        insets.right
                height = windowMetrics.bounds.height() - insets.top -
                        insets.bottom
            } else {
                val size = Point()

                val display = window.windowManager.defaultDisplay
                display.getSize(size)
                width = size.x
                height = size.y
            }

            window.setLayout((width * 0.5).toInt(), (height * heightMultiplier).toInt())
            window.setGravity(Gravity.CENTER)
            super.onResume()
        }
    }
}