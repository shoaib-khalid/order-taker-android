package com.symplified.ordertaker.ui.main.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.symplified.ordertaker.models.zones.Zone

class ZoneCollectionAdapter(
    fragment: Fragment,
    private val zones: MutableList<Zone> = mutableListOf()
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return zones.size
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = ZoneFragment()
//        fragment.arguments = Bundle().apply {
//            putString("ZONE_NAME", zones[position].name)
//        }
        return fragment
    }
}