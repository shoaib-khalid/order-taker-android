package com.symplified.ordertaker.ui.menuandcart.menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.MenuItem.OnMenuItemClickListener
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.symplified.ordertaker.R
import com.symplified.ordertaker.SampleData
import com.symplified.ordertaker.databinding.FragmentCategoryBinding
import com.symplified.ordertaker.databinding.FragmentMenuBinding
import com.symplified.ordertaker.models.Item
import com.symplified.ordertaker.ui.menuandcart.categories.CategoriesAdapter

class MenuFragment : Fragment(), MenuAdapter.OnMenuItemClickedListener {

    private var _binding: FragmentMenuBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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

    }
}