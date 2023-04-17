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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        var searchTerm = binding.textBoxSearch.editText?.text?.toString() ?: ""
        val productsAdapter = ProductsAdapter(this@MenuFragment)
        binding.itemList.apply {
            adapter = productsAdapter
            layoutManager = FlexboxLayoutManager(view.context).apply {
                justifyContent = JustifyContent.CENTER
                alignItems = AlignItems.CENTER
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }
        }
        binding.textBoxSearch.editText!!.doAfterTextChanged {
            if (it?.isNotBlank() == true) {
                menuViewModel.clearSelectedCategory()
            }
            searchTerm = it?.toString() ?: ""
            productsAdapter.filter(searchTerm)
        }

        lifecycleScope.launch {
            menuViewModel.currencySymbol.collect { currencySymbol ->
                currencySymbol?.let {
                    productsAdapter.setCurrencySymbol(it)
                }

                menuViewModel.productsWithDetails2.collect {
//            productsAdapter2.submitList(it)
                    productsAdapter.setProducts(it)
                    productsAdapter.filter(searchTerm)
                }

                val productsAdapter2 = ProductsAdapter2({
                    dialogViewModel.setSelectedProduct(it)
                    ProductSelectionDialog()
                        .show(childFragmentManager, "MenuItemSelectionBottomSheet")
                }, currencySymbol)
            }
        }

//        menuViewModel.productsWithDetails.observe(viewLifecycleOwner) { products ->
//            productsAdapter.setProducts(products)
//            productsAdapter.filter(searchTerm)
//        }

        if (resources.getBoolean(R.bool.isPhone)) {
            binding.checkoutButton.apply {
                setOnClickListener {
                    findNavController().navigate(
                        MenuAndCartFragmentDirections.actionMenuAndCartFragmentToCartFragment()
                    )
                }
                cartViewModel.cartItemsWithAddOnsAndSubItems.observe(viewLifecycleOwner) { cartItems ->
                        visibility = if (cartItems.isEmpty()) View.GONE else View.VISIBLE
                        text = cartItems.size.toString()
                    }
            }
        }
    }

    override fun onItemClicked(item: ProductWithDetails) {
        dialogViewModel.setSelectedProduct(item)
        ProductSelectionDialog()
            .show(childFragmentManager, "MenuItemSelectionBottomSheet")
    }
}