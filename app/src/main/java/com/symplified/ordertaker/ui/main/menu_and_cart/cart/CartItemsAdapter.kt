package com.symplified.ordertaker.ui.main.menu_and_cart.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.symplified.ordertaker.App
import com.symplified.ordertaker.R
import com.symplified.ordertaker.databinding.RowCartItemBinding
import com.symplified.ordertaker.models.cartitems.CartItemWithAddOnsAndSubItems
import com.symplified.ordertaker.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartItemsAdapter(
    private val currencySymbol: String
) : ListAdapter<CartItemWithAddOnsAndSubItems, CartItemsAdapter.CartItemViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<CartItemWithAddOnsAndSubItems>() {
            override fun areItemsTheSame(
                oldItem: CartItemWithAddOnsAndSubItems,
                newItem: CartItemWithAddOnsAndSubItems
            ) = oldItem.cartItem.id == newItem.cartItem.id

            override fun areContentsTheSame(
                oldItem: CartItemWithAddOnsAndSubItems,
                newItem: CartItemWithAddOnsAndSubItems
            ) = oldItem.cartItem == newItem.cartItem
                    && oldItem.cartItemAddons == newItem.cartItemAddons
                    && oldItem.cartSubItems == newItem.cartSubItems
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        return CartItemViewHolder(
            RowCartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            currencySymbol
        )
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) =
        holder.bind(getItem(position))

    class CartItemViewHolder(
        private val binding: RowCartItemBinding,
        private val currencySymbol: String
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cartItemWithDetails: CartItemWithAddOnsAndSubItems) {
            binding.cartItemName.text = cartItemWithDetails.cartItem.itemName

            binding.cartSubItemsText.visibility =
                if (cartItemWithDetails.cartSubItems.isEmpty()
                    && cartItemWithDetails.cartItemAddons.isEmpty()
                )
                    View.GONE
                else
                    View.VISIBLE

            binding.cartItemQuantity.text = cartItemWithDetails.cartItem.quantity.toString()

            binding.btnClose.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    App.cartItemRepository.delete(cartItemWithDetails)
                }
            }

            CoroutineScope(Dispatchers.Default).launch {
                var itemPrice = cartItemWithDetails.cartItem.itemPrice
                val subItemsText = StringBuilder()

                cartItemWithDetails.cartSubItems.forEach { subItem ->
                    if (subItemsText.isNotEmpty()) {
                        subItemsText.append(", ")
                    }
                    subItemsText.append(subItem.quantity).append(" x ")
                    if (subItem.productName.length <= 10) {
                        subItemsText.append(subItem.productName)
                    } else {
                        subItemsText.append(subItem.productName.substring(0..7).trim())
                            .append("...")
                    }
                }
                cartItemWithDetails.cartItemAddons.forEach { addOn ->
                    itemPrice += addOn.price

                    if (subItemsText.isNotEmpty()) {
                        subItemsText.append(", ")
                    }

                    if (addOn.name.length <= 10) {
                        subItemsText.append(addOn.name)
                    } else {
                        subItemsText.append(addOn.name.substring(0..7).trim())
                            .append("...")
                    }
                }

                val totalItemPrice = itemPrice * cartItemWithDetails.cartItem.quantity
                withContext(Dispatchers.Main) {
                    if (subItemsText.isNotEmpty()) {
                        binding.cartSubItemsText.apply {
                            visibility = View.VISIBLE
                            text = subItemsText.toString()
                        }
                    }

                    binding.itemPrice.text = binding.root.context.getString(
                        R.string.monetary_amount,
                        currencySymbol,
                        Utils.formatPrice(totalItemPrice)
                    )
                }
            }
        }
    }
}