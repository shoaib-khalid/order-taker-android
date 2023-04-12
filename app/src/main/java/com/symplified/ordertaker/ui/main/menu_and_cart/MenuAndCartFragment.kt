package com.symplified.ordertaker.ui.main.menu_and_cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.symplified.ordertaker.databinding.FragmentMenuAndCartBinding
import com.symplified.ordertaker.viewmodels.MenuViewModel

class MenuAndCartFragment : Fragment() {

    private var _binding: FragmentMenuAndCartBinding? = null
    private val binding get() = _binding!!

    private val menuViewModel: MenuViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuAndCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        menuViewModel.selectedTable.observe(viewLifecycleOwner) { table ->
            if (table != null) {
                (requireActivity() as AppCompatActivity)
                    .supportActionBar?.title = "Order for ${table.combinationTableNumber}"
            }
        }
    }
}