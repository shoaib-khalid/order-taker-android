package com.symplified.ordertaker.ui.main.menuandcart.menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.symplified.ordertaker.OrderTakerApplication
import com.symplified.ordertaker.SampleData
import com.symplified.ordertaker.databinding.FragmentMenuBinding
import com.symplified.ordertaker.models.CartItem
import com.symplified.ordertaker.models.Item
import com.symplified.ordertaker.viewmodels.CartViewModel
import com.symplified.ordertaker.viewmodels.CartViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MenuFragment : Fragment(),
    MenuAdapter.OnMenuItemClickedListener,
    MenuItemSelectionBottomSheet.OnAddToCartListener {

    private var _binding: FragmentMenuBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val cartViewModel: CartViewModel by viewModels {
        CartViewModelFactory(OrderTakerApplication.cartItemRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        arguments?.takeIf { it.containsKey("ZONE_NAME") }?.apply {
//            binding.textView.text = getString("ZONE_NAME")
//        }
        binding.itemList.layoutManager = GridLayoutManager(context, 3);
        binding.itemList.adapter = MenuAdapter(SampleData.items(), this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClicked(item: Item) {
        MenuItemSelectionBottomSheet(item, this).show(
            childFragmentManager, "MenuItemSelectionBottomSheet"
        )
    }

    override fun onItemAdded(cartItem: CartItem) {
        CoroutineScope(Dispatchers.IO).launch { cartViewModel.insert(cartItem) }
    }
}