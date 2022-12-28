package com.symplified.ordertaker.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.recyclerview.widget.GridLayoutManager
import com.symplified.ordertaker.R
import com.symplified.ordertaker.databinding.FragmentZoneBinding
import com.symplified.ordertaker.ui.menuandcart.MenuAndCartFragment

class ZoneFragment : Fragment(), TablesAdapter.OnTableClickListener {

    private var _binding: FragmentZoneBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentZoneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        arguments?.takeIf { it.containsKey("ZONE_NAME") }?.apply {
//            binding.textView.text = getString("ZONE_NAME")
//        }
        binding.tablesList.layoutManager = GridLayoutManager(context, 8);
        val tables = IntArray(50) { i -> i + 1 }
        binding.tablesList.adapter = TablesAdapter(tables, this)
    }

    override fun onTableClicked(tableNo: Int) {
        activity?.supportFragmentManager?.commit {
            setReorderingAllowed(true)
            addToBackStack(null)
            replace<MenuAndCartFragment>(R.id.nav_host_fragment_content_main)
        }
    }
}