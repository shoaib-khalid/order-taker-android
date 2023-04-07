package com.symplified.ordertaker.ui.main.menu_and_cart.menu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.flexbox.*
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeDrawable.BadgeGravity
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.symplified.ordertaker.R
import com.symplified.ordertaker.databinding.FragmentMenuBinding
import com.symplified.ordertaker.models.products.ProductWithDetails
import com.symplified.ordertaker.ui.main.menu_and_cart.MenuAndCartFragmentDirections
import com.symplified.ordertaker.viewmodels.CartViewModel
import com.symplified.ordertaker.viewmodels.MenuViewModel
import kotlinx.coroutines.launch

@ExperimentalBadgeUtils
class MenuFragment : Fragment(),
    MenuAdapter.OnMenuItemClickedListener {

    private var _binding: FragmentMenuBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val menuViewModel: MenuViewModel by activityViewModels()
    private val dialogViewModel: ProductSelectionViewModel by activityViewModels()
    private val cartViewModel: CartViewModel by activityViewModels()

    private val barCodeScanLauncher: ActivityResultLauncher<ScanOptions> =
        registerForActivityResult(ScanContract()) { result ->
            result.contents?.let { contents ->
                Toast.makeText(
                    requireActivity(),
                    "Barcode scanned: $contents",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

//        binding.itemList.layoutManager = GridLayoutManager(view.context, 3)
        binding.itemList.layoutManager = FlexboxLayoutManager(view.context).apply {
            justifyContent = JustifyContent.CENTER
            alignItems = AlignItems.CENTER
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
        }

        val menuAdapter = MenuAdapter(this)
        binding.itemList.adapter = menuAdapter

        var searchTerm = binding.textBoxSearch.editText?.text?.toString() ?: ""

        lifecycleScope.launch {
            menuViewModel.currencySymbol.observe(viewLifecycleOwner) { currencySymbol ->
                if (currencySymbol != null) {
                    menuAdapter.setCurrencySymbol(currencySymbol)
                    menuAdapter.filter(searchTerm)
                }
            }
        }

        binding.textBoxSearch.editText!!.doAfterTextChanged {
            if (it?.isNotBlank() == true) {
                menuViewModel.clearSelectedCategory()
            }
            searchTerm = it.toString()
            menuAdapter.filter(searchTerm)
        }

        menuViewModel.productsWithDetails.observe(viewLifecycleOwner) { products ->
            menuAdapter.setProducts(products)
            menuAdapter.filter(searchTerm)
        }

        if (resources.getBoolean(R.bool.isPhone)) {
            binding.checkoutButton.apply {
                setOnClickListener {
                    findNavController().navigate(
                        MenuAndCartFragmentDirections.actionMenuAndCartFragmentToCartFragment()
                    )
                }
                cartViewModel.cartItemsWithAddOnsAndSubItems
                    .observe(viewLifecycleOwner) { cartItems ->
                        visibility = if (cartItems.isEmpty()) View.GONE else View.VISIBLE
                        text = cartItems.size.toString()
                    }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClicked(item: ProductWithDetails) {
        dialogViewModel.setSelectedProduct(item)
        ProductSelectionDialog()
            .show(childFragmentManager, "MenuItemSelectionBottomSheet")
    }
}