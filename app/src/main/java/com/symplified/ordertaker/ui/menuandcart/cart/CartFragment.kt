package com.symplified.ordertaker.ui.menuandcart.cart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.symplified.ordertaker.R
import com.symplified.ordertaker.SampleData
import com.symplified.ordertaker.databinding.FragmentCartBinding
import com.symplified.ordertaker.databinding.FragmentCategoryBinding
import com.symplified.ordertaker.ui.menuandcart.categories.CategoriesAdapter

class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        arguments?.takeIf { it.containsKey("ZONE_NAME") }?.apply {
//            binding.textView.text = getString("ZONE_NAME")
//        }
        binding.cartItemsList.layoutManager = LinearLayoutManager(context);
        binding.cartItemsList.adapter = CartItemsAdapter(SampleData.cartItems())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}