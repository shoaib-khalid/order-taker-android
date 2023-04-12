package com.symplified.ordertaker.ui.main.home

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var isZonesEmpty = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val zoneCollectionAdapter = ZoneCollectionAdapter(this, mutableListOf())
        binding.pager.adapter = zoneCollectionAdapter
        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            tab.text = zoneCollectionAdapter.zonesWithTables[position].zone.zoneName
        }.attach()

        menuViewModel.zonesWithTables.observe(viewLifecycleOwner) { zonesWithTables ->
            isZonesEmpty = zonesWithTables.isEmpty()
            zoneCollectionAdapter.updateZones(zonesWithTables)
        }

        menuViewModel.isLoadingZonesAndTables.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility =
                if (isLoading && isZonesEmpty) View.VISIBLE else View.GONE
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}