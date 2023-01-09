package com.symplified.ordertaker.ui.main.menuandcart.menu

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.symplified.ordertaker.App
import com.symplified.ordertaker.databinding.FragmentMenuBinding
import com.symplified.ordertaker.models.CartItem
import com.symplified.ordertaker.models.categories.Category
import com.symplified.ordertaker.models.products.Product
import com.symplified.ordertaker.ui.main.menuandcart.categories.CategoriesAdapter
import com.symplified.ordertaker.viewmodels.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MenuFragment : Fragment(),
    MenuAdapter.OnMenuItemClickedListener,
    MenuItemSelectionBottomSheet.OnAddToCartListener,
    CategoriesAdapter.OnCategoryClickListener {

    private var _binding: FragmentMenuBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val cartViewModel: CartViewModel by activityViewModels()
    private val productViewModel: ProductViewModel by activityViewModels()

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

        productViewModel.products.observe(viewLifecycleOwner) { products ->
            binding.itemList.layoutManager = GridLayoutManager(view.context, 3)
            binding.itemList.adapter = MenuAdapter(products, this)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClicked(item: Product) {
        MenuItemSelectionBottomSheet(item, this).show(
            childFragmentManager, "MenuItemSelectionBottomSheet"
        )
    }

    override fun onItemAdded(cartItem: CartItem) {
        cartViewModel.insert(cartItem)
    }

    override fun onCategoryClicked(category: Category) {
        Log.d("categories", "MenuFragment onCurrentCategoryChanged to ${category.name}")
    }
}