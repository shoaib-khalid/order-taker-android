package com.symplified.ordertaker.ui.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.symplified.ordertaker.databinding.FragmentZoneBinding
import com.symplified.ordertaker.models.zones.Table
import com.symplified.ordertaker.viewmodels.MenuViewModel

class ZoneFragment : Fragment(), TablesAdapter.OnTableClickListener {

    companion object {
        const val ZONE_ID = "ZONE_ID"
    }

    private var _binding: FragmentZoneBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var tablesRecyclerView: RecyclerView
    private lateinit var zoneName: String

    private val menuViewModel: MenuViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentZoneBinding.inflate(inflater, container, false)
        tablesRecyclerView = binding.tablesList
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        tablesRecyclerView.layoutManager = GridLayoutManager(view.context, 6)
        val adapter = TablesAdapter(onTableClickListener = this)
        tablesRecyclerView.adapter = adapter

        arguments?.takeIf { it.containsKey(ZONE_ID) }?.apply {
            val zoneId = getInt(ZONE_ID)
            menuViewModel.zonesWithTables.observe(viewLifecycleOwner) { zone ->
                zone.firstOrNull { zoneWithTables ->
                    zoneWithTables.zone.id == zoneId
                }?.let { currentZone ->
                    zoneName = currentZone.zone.zoneName
                    val newTables = currentZone.tables
                    adapter.setTables(newTables)
                }
            }
        }


    }

    override fun onTableClicked(table: Table) {
        menuViewModel.selectedTable = table
        findNavController().navigate(
            HomeFragmentDirections
                .actionNavHomeToMenuAndCartFragment()
        )
    }
}