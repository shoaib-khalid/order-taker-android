package com.symplified.ordertaker.ui.main.menuandcart

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.symplified.ordertaker.databinding.FragmentMenuAndCartBinding
import com.symplified.ordertaker.viewmodels.CartViewModel
import com.symplified.ordertaker.viewmodels.MenuViewModel

class MenuAndCartFragment : Fragment() {

    private var _binding: FragmentMenuAndCartBinding? = null
    private val binding get() = _binding!!

    private val menuViewModel: MenuViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val orientation = activity?.resources?.configuration?.orientation
        if (orientation != Configuration.ORIENTATION_LANDSCAPE) {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuAndCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("menuandcart", "onDestroyView")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("menuandcart", "onDetach")
    }
}