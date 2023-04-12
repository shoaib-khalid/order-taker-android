package com.symplified.ordertaker.ui.main.menu_and_cart.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.symplified.ordertaker.R
import com.symplified.ordertaker.databinding.FragmentCategoryBinding
import com.symplified.ordertaker.models.categories.Category
import com.symplified.ordertaker.viewmodels.MenuViewModel

class CategoryFragment : Fragment(), CategoriesAdapter.OnCategoryClickListener {

    private lateinit var binding: FragmentCategoryBinding

    private val menuViewModel: MenuViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var isCategoriesEmpty = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.categoryList.apply {
            layoutManager = LinearLayoutManager(
                view.context,
                if (resources.getBoolean(R.bool.isTablet) && resources.getBoolean(R.bool.isLandscapeOriented))
                    LinearLayoutManager.VERTICAL
                else LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = CategoriesAdapter(this@CategoryFragment)
        }

        menuViewModel.categories.observe(viewLifecycleOwner) { categories ->
            isCategoriesEmpty = categories.size < 3
            if (isCategoriesEmpty) {
                menuViewModel.fetchCategories()
            } else {
                binding.categoryList.adapter = CategoriesAdapter(this, categories)
            }
        }
        menuViewModel.isLoadingCategories.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility =
                if (isLoading && isCategoriesEmpty) View.VISIBLE else View.GONE
        }
        menuViewModel.selectedCategory.observe(viewLifecycleOwner) { category ->
            (binding.categoryList.adapter as CategoriesAdapter)
                .setSelectedCategory(category)
        }
    }

    override fun onCategoryClicked(category: Category) {
        menuViewModel.selectCategory(category)
    }
}