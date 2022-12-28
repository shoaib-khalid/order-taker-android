package com.symplified.ordertaker.ui.menuandcart.categories

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.symplified.ordertaker.SampleData
import com.symplified.ordertaker.databinding.FragmentCategoryBinding

class CategoryFragment : Fragment(), CategoriesAdapter.OnCategoryClickListener {

    private var _binding: FragmentCategoryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        arguments?.takeIf { it.containsKey("ZONE_NAME") }?.apply {
//            binding.textView.text = getString("ZONE_NAME")
//        }
        binding.categoryList.layoutManager = LinearLayoutManager(context);
        binding.categoryList.adapter = CategoriesAdapter(SampleData.categories(), this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCategoryClicked(category: String) {
    }
}