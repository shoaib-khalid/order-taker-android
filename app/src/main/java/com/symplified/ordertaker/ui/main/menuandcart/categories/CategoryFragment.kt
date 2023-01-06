package com.symplified.ordertaker.ui.main.menuandcart.categories

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.symplified.ordertaker.OrderTakerApplication
import com.symplified.ordertaker.SampleData
import com.symplified.ordertaker.databinding.FragmentCategoryBinding
import com.symplified.ordertaker.models.categories.Category
import com.symplified.ordertaker.networking.ServiceGenerator
import com.symplified.ordertaker.viewmodels.MenuViewModel
import com.symplified.ordertaker.viewmodels.MenuViewModelFactory

class CategoryFragment : Fragment(), CategoriesAdapter.OnCategoryClickListener {

    private var _binding: FragmentCategoryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val menuViewModel: MenuViewModel by viewModels {
        MenuViewModelFactory(
            OrderTakerApplication.tableRepository,
            OrderTakerApplication.zoneRepository,
            OrderTakerApplication.categoryRepository,
            OrderTakerApplication.menuItemRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.let { activity ->
            menuViewModel.getCategories(ServiceGenerator.createProductService(activity.applicationContext))
        }

        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var isCategoriesEmpty = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val categoriesAdapter = CategoriesAdapter(onCategoryClickListener = this)
        val categoryList = binding.categoryList
        categoryList.layoutManager = LinearLayoutManager(view.context)
        categoryList.adapter = categoriesAdapter

        menuViewModel.categories.observe(viewLifecycleOwner) { categories ->
            isCategoriesEmpty = categories.isEmpty()
            categoriesAdapter.updateItems(categories)
        }

        menuViewModel.currentCategory.observe(viewLifecycleOwner) { newCategory ->
            Log.d("categories", "CategoryFragment: New category set: ${newCategory.name}")
            Toast.makeText(
                context,
                "CategoryFragment: New category set: ${newCategory.name}",
                Toast.LENGTH_SHORT
            )
                .show()
        }

        menuViewModel.isLoadingCategories.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility =
                if (isLoading && isCategoriesEmpty) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCategoryClicked(category: Category) {
        menuViewModel.setCurrentCategory(category)
    }
}