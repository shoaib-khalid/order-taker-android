package com.symplified.easydukanpos.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.symplified.easydukanpos.databinding.FragmentHomeBinding
import com.symplified.easydukanpos.viewmodels.MenuViewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val menuViewModel: MenuViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
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
}