package com.symplified.ordertaker.ui.main.home

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.symplified.ordertaker.databinding.FragmentHomeBinding
import com.symplified.ordertaker.viewmodels.MenuViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val menuViewModel: MenuViewModel by activityViewModels()
//    {
//        MenuViewModelFactory(
//            OrderTakerApplication.tableRepository,
//            OrderTakerApplication.zoneRepository,
//            OrderTakerApplication.categoryRepository,
//            OrderTakerApplication.productRepository
//        )
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var isZonesEmpty = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        menuViewModel.zonesWithTables.observe(viewLifecycleOwner) { zonesWithTables ->
            isZonesEmpty = zonesWithTables.isEmpty()
            if (isZonesEmpty) {
                menuViewModel.fetchZonesAndTables()
            }

            binding.pager.adapter = ZoneCollectionAdapter(this, zonesWithTables)

            TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
                tab.text = zonesWithTables[position].zone.zoneName
            }.attach()
        }

        menuViewModel.isLoadingZonesAndTables.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility =
                if (isLoading && isZonesEmpty) View.VISIBLE else View.GONE
            if (!isLoading) {
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            menuViewModel.fetchZonesAndTables()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}