package com.symplified.ordertaker.ui.main.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.symplified.ordertaker.models.zones.Zone
import com.symplified.ordertaker.models.zones.ZoneWithTables

class ZoneCollectionAdapter(
    fragment: Fragment,
    val zonesWithTables: MutableList<ZoneWithTables>
) : FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        val fragment = ZoneFragment()
        fragment.arguments = Bundle().apply {
            putInt(ZoneFragment.ZONE_ID, zonesWithTables[position].zone.id)
        }
        return fragment
    }

    override fun getItemCount() = zonesWithTables.size

    fun updateZones(updatedZones: List<ZoneWithTables>) {
        updatedZones.forEach { updatedZoneWithTables ->
            if (!hasZone(updatedZoneWithTables.zone)) {
                if (zonesWithTables.add(updatedZoneWithTables)) {
                    notifyItemInserted(zonesWithTables.size - 1)
                }
            }
        }

        val zonesToRemove: MutableList<Int> = mutableListOf()
        zonesWithTables.forEachIndexed { index, zoneWithTables ->
            if (updatedZones.firstOrNull { it.zone.id == zoneWithTables.zone.id  } == null) {
                zonesToRemove.add(index)
            }
        }

        zonesToRemove.forEach { indexToRemove ->
            zonesWithTables.elementAtOrNull(indexToRemove)?.let {
                zonesWithTables.removeAt(indexToRemove)
                notifyItemRemoved(indexToRemove)
            }
        }
    }

    private fun hasZone(updatedZone: Zone): Boolean =
        zonesWithTables.firstOrNull{ it.zone.id == updatedZone.id} != null
}