package com.symplified.ordertaker.ui.main.menuandcart.cart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.symplified.ordertaker.App
import com.symplified.ordertaker.constants.SharedPrefsKey
import com.symplified.ordertaker.databinding.FragmentCartBinding
import com.symplified.ordertaker.models.cartitems.CartItemWithAddOnsAndSubItems
import com.symplified.ordertaker.models.paymentchannel.PaymentChannel
import com.symplified.ordertaker.viewmodels.CartViewModel
import com.symplified.ordertaker.viewmodels.MenuViewModel

class CartFragment : Fragment(), CartItemsAdapter.OnRemoveFromCartListener,
    PaymentChannelAdapter.OnPaymentTypeClickListener {
    private var _binding: FragmentCartBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val menuViewModel: MenuViewModel by activityViewModels()
    private val cartViewModel: CartViewModel by activityViewModels()

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
        cartItemsList.layoutManager = LinearLayoutManager(view.context);
        cartItemsList.adapter = cartItemsAdapter

        binding.paymentTypeList.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
        val paymentChannelAdapter = PaymentChannelAdapter(this)
        binding.paymentTypeList.adapter = paymentChannelAdapter

        cartViewModel.cartItemsWithAddOnsAndSubItems.observe(viewLifecycleOwner) { cartItemsWithAddOnsAndSubItems ->

            binding.placeOrderButton.isEnabled = cartItemsWithAddOnsAndSubItems.isNotEmpty()

            cartItemsAdapter.updateItems(cartItemsWithAddOnsAndSubItems)

            var totalPrice = 0.0
            cartItemsWithAddOnsAndSubItems.forEach { cartItemWithAddOnsAndSubItems ->
                var itemPrice = cartItemWithAddOnsAndSubItems.cartItem.itemPrice
                cartItemWithAddOnsAndSubItems.cartItemAddons.forEach { addOn ->
                    itemPrice += addOn.price
                }

                totalPrice += (itemPrice * cartItemWithAddOnsAndSubItems.cartItem.quantity)
            }
            binding.totalPriceCount.text = "RM ${String.format("%.2f", totalPrice)}"
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
                findNavController().popBackStack()
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
            binding.paymentTypeProgressBar.visibility = if (isLoading && isPaymentChannelsEmpty) View.VISIBLE else View.GONE
            if (isLoading) {
                binding.paymentTypeErrorText.visibility = View.GONE
                binding.paymentTypeRetryButton.visibility = View.GONE
            }
        }

        menuViewModel.selectedTable?.let { selectedTable ->
            binding.tableNo.text = "Table No.: ${selectedTable.combinationTableNumber}"

            menuViewModel.zonesWithTables.observe(viewLifecycleOwner) { zonesWithTables ->
                zonesWithTables.firstOrNull { zoneWithTables ->
                    zoneWithTables.zone.id == selectedTable.zoneId
                }?.let { zoneWithTables ->
                    binding.zoneNo.text = "Zone: ${zoneWithTables.zone.zoneName}"

                    binding.placeOrderButton.setOnClickListener {
                        cartViewModel.placeOrder(zoneWithTables.zone, selectedTable)
                    }
                }
            }
        }

        cartViewModel.user.observe(viewLifecycleOwner) { user ->
            binding.serverName.text =
                "Served by: ${user?.name ?: ""}"
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