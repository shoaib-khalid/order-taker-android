package com.symplified.ordertaker.ui.main.home

import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
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

        val width: Int = getScreenWidth()
        val spanCount = if (width >= 2000) 6 else if (width >= 1200) 4 else 3

        tablesRecyclerView.layoutManager = GridLayoutManager(view.context, spanCount)
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

    private fun getScreenWidth(): Int {
        return if (Build.VERSION.SDK_INT >= 30) {
            val windowMetrics = requireActivity().windowManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())

            windowMetrics.bounds.width() - insets.left - insets.right
        } else {
            val displayMetrics = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels
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