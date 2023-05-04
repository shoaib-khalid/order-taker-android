package com.symplified.easydukanpos.ui.main.menu_and_cart.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.symplified.easydukanpos.R
import com.symplified.easydukanpos.databinding.FragmentCartBinding
import com.symplified.easydukanpos.models.paymentchannel.PaymentOption
import com.symplified.easydukanpos.models.zones.ZoneWithTables
import com.symplified.easydukanpos.utils.Utils
import com.symplified.easydukanpos.viewmodels.CartViewModel
import com.symplified.easydukanpos.viewmodels.MenuViewModel
import com.symplified.easydukanpos.viewmodels.OrderResult
import kotlinx.coroutines.launch

class CartFragment : Fragment(),
    PaymentChannelAdapter.OnPaymentTypeClickListener {

    private lateinit var binding: FragmentCartBinding

    private val menuViewModel: MenuViewModel by activityViewModels()
    private val cartViewModel: CartViewModel by activityViewModels()

    private var totalPrice = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val paymentChannelAdapter = PaymentChannelAdapter(this)
        binding.paymentTypeList.apply {
            layoutManager = LinearLayoutManager(
                view.context, LinearLayoutManager.HORIZONTAL, false
            )
            adapter = paymentChannelAdapter
        }

        cartViewModel.user.observe(viewLifecycleOwner) { user ->
            if (user == null) {
                return@observe
            }

            binding.productPriceHeader.text = getString(R.string.price_header, user.currencySymbol)

            val cartItemsAdapter = CartItemsAdapter(user.currencySymbol)
            binding.cartItemsList.apply {
                layoutManager = LinearLayoutManager(view.context)
                adapter = cartItemsAdapter
            }

            binding.serverName.text = getString(R.string.server_label, user.name)

            lifecycleScope.launch {
                cartViewModel.cartItemsWithAddOnsAndSubItems.collect { items ->
                    binding.placeOrderButton.isEnabled = items.isNotEmpty()
                    cartItemsAdapter.submitList(items)

                    totalPrice = 0.0
                    items.forEach { item ->
                        var itemPrice = item.cartItem.itemPrice
                        item.cartItemAddons.forEach { addOn -> itemPrice += addOn.price }

                        totalPrice += (itemPrice * item.cartItem.quantity)
                    }
                    binding.totalPriceText.text =
                        getString(
                            R.string.total_price,
                            user.currencySymbol,
                            Utils.formatPrice(totalPrice)
                        )

                    paymentChannelAdapter.setPaymentOptionEnabled(
                        PaymentOption.PAYLATER,
                        totalPrice >= 200
                    )
                    if (totalPrice < 200 && cartViewModel.selectedPaymentOption.value == PaymentOption.PAYLATER) {
                        cartViewModel.setSelectedPaymentOption(PaymentOption.CASH)
                    }
                }

                cartViewModel.orderResult.collect { orderResult ->
                    if (orderResult is OrderResult.Success) {
                        findNavController().popBackStack(R.id.nav_home, false)
                    }
                }
            }

            menuViewModel.selectedTable.observe(viewLifecycleOwner) { selectedTable ->
                var selectedZoneWithTables: ZoneWithTables? = null

                if (selectedTable != null) {
                    binding.tableNo.text =
                        getString(R.string.table_no_label, selectedTable.combinationTableNumber)
                    binding.tableNo.visibility = View.VISIBLE

                    menuViewModel.zonesWithTables.value?.let { zonesWithTables ->
                        selectedZoneWithTables = zonesWithTables.firstOrNull { zoneWithTables ->
                            zoneWithTables.zone.id == selectedTable.zoneId
                        }
                        if (selectedZoneWithTables != null) {
                            binding.zoneNo.text = getString(
                                R.string.zone_label,
                                selectedZoneWithTables!!.zone.zoneName
                            )
                            binding.zoneNo.visibility = View.VISIBLE
                        }
                    }
                }
                binding.placeOrderButton.setOnClickListener {
                    if (cartViewModel.selectedPaymentOption.value!! == PaymentOption.CASH) {
                        CashPaymentDialog(user.currencySymbol, totalPrice)
                            .show(childFragmentManager, CashPaymentDialog.TAG)
                    } else {
                        cartViewModel.placeOrder(selectedZoneWithTables?.zone, selectedTable)
                    }
                }
            }
        }

        cartViewModel.isPlacingOrder.observe(viewLifecycleOwner) { isPlacingOrder ->
            if (isPlacingOrder) {
                binding.mainLayout.visibility = View.GONE
                binding.progressBarLayout.visibility = View.VISIBLE
            } else {
                binding.mainLayout.visibility = View.VISIBLE
                binding.progressBarLayout.visibility = View.GONE
            }
        }

//        cartViewModel.isOrderSuccessful.observe(viewLifecycleOwner) { isOrderSuccessful ->
//            if (isOrderSuccessful) {
//                findNavController().popBackStack(R.id.nav_home, false)
//            }
//        }

        cartViewModel.selectedPaymentOption.observe(viewLifecycleOwner) { selectedPaymentChannel ->
            paymentChannelAdapter.selectPaymentOption(selectedPaymentChannel)
        }

        binding.clearCartButton.setOnClickListener { cartViewModel.clearAll() }
    }

    override fun onPaymentTypeClicked(paymentOption: PaymentOption) {
        cartViewModel.setSelectedPaymentOption(paymentOption)
    }
}