package com.symplified.easydukanpos.ui.main.menu_and_cart.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.symplified.easydukanpos.R
import com.symplified.easydukanpos.databinding.FragmentCategoryBinding
import com.symplified.easydukanpos.models.categories.Category
import com.symplified.easydukanpos.viewmodels.MenuViewModel
import kotlinx.coroutines.launch

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

        lifecycleScope.launch {
            menuViewModel.selectedCategory.collect { category ->
                (binding.categoryList.adapter as CategoriesAdapter).setSelectedCategory(category)
            }
        }
    }

    override fun onCategoryClicked(category: Category) {
        menuViewModel.selectCategory(category)
    }
}