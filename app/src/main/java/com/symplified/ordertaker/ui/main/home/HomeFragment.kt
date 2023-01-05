package com.symplified.ordertaker.ui.main.home

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.symplified.ordertaker.OrderTakerApplication
import com.symplified.ordertaker.databinding.FragmentHomeBinding
import com.symplified.ordertaker.networking.ServiceGenerator
import com.symplified.ordertaker.viewmodels.MenuViewModel
import com.symplified.ordertaker.viewmodels.MenuViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val menuViewModel: MenuViewModel by viewModels {
        MenuViewModelFactory(
            OrderTakerApplication.tableRepository,
            OrderTakerApplication.zoneRepository,
            OrderTakerApplication.categoryRepository,
            OrderTakerApplication.menuItemRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR

        menuViewModel.zones.observe(viewLifecycleOwner) { zones ->
            Log.d("zones", "HomeFragment: Zones updated. Size: ${zones.size}.")

        }

        activity?.let { activity ->
            menuViewModel.getZonesAndTables(
                ServiceGenerator
                    .createLocationService(activity.applicationContext)
            )
        }

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        val zones = SampleData.zones()
        binding.pager.adapter = ZoneCollectionAdapter(this)

//        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
//            tab.text = zones[position].name
//        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}