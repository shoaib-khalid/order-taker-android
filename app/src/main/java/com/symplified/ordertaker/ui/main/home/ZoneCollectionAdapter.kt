package com.symplified.ordertaker.ui.main.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.symplified.ordertaker.models.zones.ZoneWithTables

class ZoneCollectionAdapter(
    fragment: Fragment,
    private var zones: List<ZoneWithTables>
) : FragmentStateAdapter(fragment) {


    override fun createFragment(position: Int): Fragment {
        val fragment = ZoneFragment()
        fragment.arguments = Bundle().apply {
            putInt(ZoneFragment.ZONE_ID, zones[position].zone.id)
        }
        return fragment
    }

    override fun getItemCount() = zones.size

    fun setZones(newZones: List<ZoneWithTables>) {
        zones = newZones
    }
}