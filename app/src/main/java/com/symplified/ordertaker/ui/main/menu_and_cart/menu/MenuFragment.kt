package com.symplified.ordertaker.ui.main.menu_and_cart.menu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.flexbox.*
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.symplified.ordertaker.R
import com.symplified.ordertaker.databinding.FragmentMenuBinding
import com.symplified.ordertaker.models.products.ProductWithDetails
import com.symplified.ordertaker.ui.main.menu_and_cart.MenuAndCartFragmentDirections
import com.symplified.ordertaker.viewmodels.CartViewModel
import com.symplified.ordertaker.viewmodels.MenuViewModel
import kotlinx.coroutines.launch

@ExperimentalBadgeUtils
class MenuFragment : Fragment(),
    ProductsAdapter.OnMenuItemClickedListener {

    private lateinit var binding: FragmentMenuBinding

    private val menuViewModel: MenuViewModel by activityViewModels()
    private val dialogViewModel: ProductSelectionViewModel by activityViewModels()
    private val cartViewModel: CartViewModel by activityViewModels()

    private var products: List<ProductWithDetails> = listOf()
    private var searchTerm = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        searchTerm = binding.textBoxSearch.editText?.text?.toString() ?: ""
        binding.itemList.apply {
            layoutManager = FlexboxLayoutManager(view.context).apply {
                justifyContent = JustifyContent.CENTER
                alignItems = AlignItems.CENTER
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }
        }

        lifecycleScope.launch {
            menuViewModel.currencySymbol.collect { currencySymbol ->
                val productsAdapter2 = ProductsAdapter2({
                    dialogViewModel.setSelectedProduct(it)
                    ProductSelectionDialog()
                        .show(childFragmentManager, "MenuItemSelectionBottomSheet")
                }, currencySymbol)
                binding.itemList.adapter = productsAdapter2

                binding.textBoxSearch.editText!!.doAfterTextChanged {
                    if (it!!.isNotBlank()) {
                        menuViewModel.clearSelectedCategory()
                    }
                    searchTerm = it.toString().trim().lowercase() ?: ""

                    productsAdapter2.submitList(getFilteredProducts())
                }

                menuViewModel.productsWithDetails2.collect {
                    products = it
                    productsAdapter2.submitList(getFilteredProducts())
                }
            }
        }

        if (resources.getBoolean(R.bool.isPhone)) {
            binding.checkoutButton.apply {
                setOnClickListener {
                    findNavController().navigate(
                        MenuAndCartFragmentDirections.actionMenuAndCartFragmentToCartFragment()
                    )
                }
                lifecycleScope.launch {
                    cartViewModel.cartItemsWithAddOnsAndSubItems.collect { cartItems ->
                        visibility = if (cartItems.isEmpty()) View.GONE else View.VISIBLE
                        text = cartItems.size.toString()
                    }
                }
            }
        }
    }

    override fun onItemClicked(item: ProductWithDetails) {
        dialogViewModel.setSelectedProduct(item)
        ProductSelectionDialog()
            .show(childFragmentManager, "MenuItemSelectionBottomSheet")
    }

    private fun getFilteredProducts(): List<ProductWithDetails> = products.filter {
            val skuMatches = it.productInventoriesWithItems.any { invWithItems ->
                invWithItems.productInventory.sku.replace("-", " ")
                    .lowercase().contains(searchTerm)
            }

            it.product.name.lowercase().contains(searchTerm)
                    || it.product.description.lowercase().contains(searchTerm)
                    || skuMatches
    }
}