package com.symplified.ordertaker.ui.main.home

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.symplified.ordertaker.SampleData
import com.symplified.ordertaker.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override  fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val zones = SampleData.zones()
        binding.pager.adapter = ZoneCollectionAdapter(this, zones)

        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            tab.text = zones[position].name
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}