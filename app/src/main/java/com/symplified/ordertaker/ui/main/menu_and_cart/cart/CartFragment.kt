package com.symplified.ordertaker.ui.main.menu_and_cart.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.symplified.ordertaker.R
import com.symplified.ordertaker.databinding.FragmentCartBinding
import com.symplified.ordertaker.models.cartitems.CartItemWithAddOnsAndSubItems
import com.symplified.ordertaker.models.paymentchannel.PaymentChannel
import com.symplified.ordertaker.models.stores.BusinessType
import com.symplified.ordertaker.viewmodels.CartViewModel
import com.symplified.ordertaker.viewmodels.MenuViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat

class CartFragment : Fragment(), CartItemsAdapter.OnRemoveFromCartListener,
    PaymentChannelAdapter.OnPaymentTypeClickListener {
    private var _binding: FragmentCartBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val menuViewModel: MenuViewModel by activityViewModels()
    private val cartViewModel: CartViewModel by activityViewModels()

    private val formatter: DecimalFormat = DecimalFormat("#,##0.00")

    private var totalPrice = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val cartItemsAdapter = CartItemsAdapter(onRemoveFromCartListener = this)
        val cartItemsList = binding.cartItemsList
        var currencySymbol = "RM"
        cartItemsList.layoutManager = LinearLayoutManager(view.context);
        cartItemsList.adapter = cartItemsAdapter

        binding.paymentTypeList.layoutManager =
            LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
        val paymentChannelAdapter = PaymentChannelAdapter(this)
        binding.paymentTypeList.adapter = paymentChannelAdapter

        cartViewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                currencySymbol = user.currencySymbol
                binding.serverName.text = getString(R.string.server_label, user.name)

                cartViewModel.cartItemsWithAddOnsAndSubItems
                    .observe(viewLifecycleOwner) { cartItemsWithAddOnsAndSubItems ->

                        binding.placeOrderButton.isEnabled =
                            cartItemsWithAddOnsAndSubItems.isNotEmpty()

                        cartItemsAdapter.updateItems(cartItemsWithAddOnsAndSubItems)

                        lifecycleScope.launch(Dispatchers.Default) {
                            totalPrice = 0.0
                            cartItemsWithAddOnsAndSubItems.forEach { cartItemWithAddOnsAndSubItems ->
                                var itemPrice = cartItemWithAddOnsAndSubItems.cartItem.itemPrice
                                cartItemWithAddOnsAndSubItems.cartItemAddons.forEach { addOn ->
                                    itemPrice += addOn.price
                                }

                                totalPrice += (itemPrice * cartItemWithAddOnsAndSubItems.cartItem.quantity)
                            }
                            withContext(Dispatchers.Main) {
                                binding.totalPriceCount.text =
                                    getString(
                                    R.string.monetary_amount,
                                    user.currencySymbol,
                                    formatter.format(totalPrice)
                                )
                            }
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

        cartViewModel.isOrderSuccessful.observe(viewLifecycleOwner) { isOrderSuccessful ->
            if (isOrderSuccessful) {
                cartViewModel.user.value.let { user ->
                    if (user?.businessType == BusinessType.FNB) {
                        findNavController().popBackStack()
                    }
                }
            }
        }

        cartViewModel.orderResultMessage.observe(viewLifecycleOwner) { message ->
            if (message.isNotBlank()) {
                Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.paymentTypeRetryButton.setOnClickListener {
            cartViewModel.fetchPaymentChannels()
        }

        var isPaymentChannelsEmpty = false
        cartViewModel.paymentChannels.observe(viewLifecycleOwner) { paymentChannels ->
            isPaymentChannelsEmpty = paymentChannels.isEmpty()

            paymentChannelAdapter.updatePaymentChannels(paymentChannels)
            paymentChannelAdapter.selectPaymentChannel(cartViewModel.selectedPaymentChannel.value!!)
            if (paymentChannels.isEmpty()) {
                cartViewModel.fetchPaymentChannels()
            }
        }

        cartViewModel.selectedPaymentChannel.observe(viewLifecycleOwner) { selectedPaymentChannel ->
            paymentChannelAdapter.selectPaymentChannel(selectedPaymentChannel)
        }

        cartViewModel.isPaymentChannelsReceived.observe(viewLifecycleOwner) { isReceived ->
            binding.paymentTypeErrorText.visibility = if (isReceived) View.GONE else View.VISIBLE
            binding.paymentTypeRetryButton.visibility = if (isReceived) View.GONE else View.VISIBLE
        }

        cartViewModel.isLoadingPaymentChannels.observe(viewLifecycleOwner) { isLoading ->
            binding.paymentTypeProgressBar.visibility =
                if (isLoading && isPaymentChannelsEmpty) View.VISIBLE else View.GONE
            if (isLoading) {
                binding.paymentTypeErrorText.visibility = View.GONE
                binding.paymentTypeRetryButton.visibility = View.GONE
            }
        }

        menuViewModel.selectedTable.value.let { selectedTable ->
            if (selectedTable != null) {
                binding.tableNo.text =
                    getString(R.string.table_no_label, selectedTable.combinationTableNumber)
                binding.tableNo.visibility = View.VISIBLE

                menuViewModel.zonesWithTables.value?.let { zonesWithTables ->
                    zonesWithTables.firstOrNull { zoneWithTables ->
                        zoneWithTables.zone.id == selectedTable.zoneId
                    }?.let { zoneWithTables ->

                        binding.zoneNo.text =
                            getString(R.string.zone_label, zoneWithTables.zone.zoneName)
                        binding.zoneNo.visibility = View.VISIBLE

                        binding.placeOrderButton.setOnClickListener {
                            if (cartViewModel.selectedPaymentChannel.value!!.channelCode == "CASH") {
                                CashPaymentDialog(currencySymbol, totalPrice)
                                    .show(childFragmentManager, CashPaymentDialog.TAG)
                            } else {
                                cartViewModel.placeOrder(zoneWithTables.zone, selectedTable)
                            }
                        }
                    }
                }
            } else {
                binding.placeOrderButton.setOnClickListener {
                    if (cartViewModel.selectedPaymentChannel.value!!.channelCode == "CASH") {
                        CashPaymentDialog(currencySymbol, totalPrice)
                            .show(childFragmentManager, CashPaymentDialog.TAG)
                    } else {
                        cartViewModel.placeOrder()
                    }
                }
            }
        }

        binding.clearCartButton.setOnClickListener { cartViewModel.clearAll() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemRemoved(cartItem: CartItemWithAddOnsAndSubItems) {
        cartViewModel.delete(cartItem)
    }

    override fun onPaymentTypeClicked(paymentChannel: PaymentChannel) {
        cartViewModel.setSelectedPaymentChannel(paymentChannel)
    }
}