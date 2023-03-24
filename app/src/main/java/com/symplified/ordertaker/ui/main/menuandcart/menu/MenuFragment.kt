package com.symplified.ordertaker.ui.main.menuandcart.menu

import android.Manifest.permission.CAMERA
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.symplified.ordertaker.databinding.FragmentMenuBinding
import com.symplified.ordertaker.models.cartitems.CartItem
import com.symplified.ordertaker.models.categories.Category
import com.symplified.ordertaker.models.products.ProductWithDetails
import com.symplified.ordertaker.ui.main.home.HomeFragmentDirections
import com.symplified.ordertaker.ui.main.menuandcart.MenuAndCartFragmentDirections
import com.symplified.ordertaker.ui.main.menuandcart.categories.CategoriesAdapter
import com.symplified.ordertaker.viewmodels.CartViewModel
import com.symplified.ordertaker.viewmodels.MenuViewModel

class MenuFragment : Fragment(),
    MenuAdapter.OnMenuItemClickedListener,
    ProductSelectionDialog.OnAddToCartListener,
    CategoriesAdapter.OnCategoryClickListener {

    private var _binding: FragmentMenuBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val cartViewModel: CartViewModel by activityViewModels()
    private val menuViewModel: MenuViewModel by activityViewModels()
    private val dialogViewModel: ProductSelectionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.itemList.layoutManager = GridLayoutManager(view.context, 3)

        val permissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted) {
                    findNavController().navigate(
                        MenuAndCartFragmentDirections
                            .actionMenuAndCartFragmentToBarcodeScannerFragment()
                    )
                }
            }

        binding.scanBarcodeButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext().applicationContext,
                    CAMERA
                ) == PackageManager.PERMISSION_DENIED
            ) {
                permissionLauncher.launch(CAMERA)
                return@setOnClickListener
            }

            findNavController().navigate(
                MenuAndCartFragmentDirections
                    .actionMenuAndCartFragmentToBarcodeScannerFragment()
            )
        }

        val menuAdapter = MenuAdapter(listOf(), this)
        binding.itemList.adapter = menuAdapter
        var searchTerm = binding.textBoxSearch.editText?.text?.toString() ?: ""
        binding.textBoxSearch.editText!!.doAfterTextChanged {
            searchTerm = it.toString().trim().lowercase()
            menuAdapter.filter(searchTerm)
        }
        menuViewModel.productsWithDetails.observe(viewLifecycleOwner) { products ->
            menuAdapter.setProducts(products)
            menuAdapter.filter(searchTerm)
        }

        menuViewModel.isLoadingProducts.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClicked(item: ProductWithDetails) {
        dialogViewModel.setSelectedProduct(item)
        ProductSelectionDialog(item, this)
            .show(
                childFragmentManager, "MenuItemSelectionBottomSheet"
            )
    }

    override fun onItemAdded(cartItem: CartItem) {
//        cartViewModel.insert(cartItem)
    }

    override fun onCategoryClicked(category: Category) {
    }
}