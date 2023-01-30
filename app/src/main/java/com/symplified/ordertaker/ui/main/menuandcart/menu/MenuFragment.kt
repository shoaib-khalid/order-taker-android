package com.symplified.ordertaker.ui.main.menuandcart.menu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.symplified.ordertaker.databinding.FragmentMenuBinding
import com.symplified.ordertaker.models.cartitems.CartItem
import com.symplified.ordertaker.models.categories.Category
import com.symplified.ordertaker.models.products.ProductWithDetails
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
//        val menuAdapter = MenuAdapter(SampleData.items(), this)
//        val itemList = binding.itemList
//        itemList.layoutManager = GridLayoutManager(context, 3);
//        itemList.adapter = menuAdapter
        binding.itemList.layoutManager = GridLayoutManager(view.context, 3)

        menuViewModel.productsWithDetails.observe(viewLifecycleOwner) { products ->
//            if (products.isEmpty()) { menuViewModel.fetchProducts() }

            binding.itemList.adapter = MenuAdapter(products, this)
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