package com.symplified.easydukanpos.ui.main.menu_and_cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.symplified.easydukanpos.databinding.FragmentPaymentBinding

class PaymentFragment : Fragment() {

    private lateinit var binding: FragmentPaymentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }
}