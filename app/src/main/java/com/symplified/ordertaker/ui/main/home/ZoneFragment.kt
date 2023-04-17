package com.symplified.ordertaker.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.flexbox.*
import com.symplified.ordertaker.databinding.FragmentZoneBinding
import com.symplified.ordertaker.models.zones.Table
import com.symplified.ordertaker.viewmodels.MenuViewModel

class ZoneFragment : Fragment() {

    companion object {
        const val ZONE_ID = "ZONE_ID"
    }

    private lateinit var binding: FragmentZoneBinding

    private val menuViewModel: MenuViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentZoneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val tablesAdapter2 = TablesAdapter2 { table ->
            menuViewModel.setSelectedTable(table)
            findNavController().navigate(
                HomeFragmentDirections
                    .actionNavHomeToMenuAndCartFragment()
            )
        }
        binding.tablesList.apply {
            adapter = tablesAdapter2

            layoutManager = FlexboxLayoutManager(view.context).apply {
                justifyContent = JustifyContent.SPACE_EVENLY
                alignItems = AlignItems.CENTER
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }
        }

        arguments?.takeIf { it.containsKey(ZONE_ID) }?.apply {
            val zoneId = getInt(ZONE_ID)
            menuViewModel.zonesWithTables.observe(viewLifecycleOwner) { zonesWithTables ->
                zonesWithTables.firstOrNull { zoneWithTables ->
                    zoneWithTables.zone.id == zoneId
                }?.let { currentZone ->
                    tablesAdapter2.submitList(currentZone.tables)
                }
            }
        }
    }

}