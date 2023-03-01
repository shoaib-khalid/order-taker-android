package com.symplified.ordertaker.ui.main.menuandcart.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.symplified.ordertaker.R
import com.symplified.ordertaker.models.cartitems.CartItemWithAddOnsAndSubItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat

class CartItemsAdapter(
    private val cartItemsWithAddOnsAndSubItems: MutableList<CartItemWithAddOnsAndSubItems>
    = mutableListOf(),
    private val onRemoveFromCartListener: OnRemoveFromCartListener
) : RecyclerView.Adapter<CartItemsAdapter.ViewHolder>() {

    private val formatter: DecimalFormat = DecimalFormat("#,##0.00")

    interface OnRemoveFromCartListener {
        fun onItemRemoved(cartItem: CartItemWithAddOnsAndSubItems)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemNumber: TextView = view.findViewById(R.id.cart_item_no)
        val itemName: TextView = view.findViewById(R.id.cart_item_name)
        val subItems: TextView = view.findViewById(R.id.cart_sub_items_text)
        val itemQuantity: TextView = view.findViewById(R.id.cart_item_quantity)
        val itemPrice: TextView = view.findViewById(R.id.cart_item_price)
        val deleteBtn: AppCompatImageButton = view.findViewById(R.id.delete_btn)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.row_cart_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemNumber.text = position.toString()
        viewHolder.itemName.text = cartItemsWithAddOnsAndSubItems[position].cartItem.itemName

        viewHolder.subItems.visibility =
            if (cartItemsWithAddOnsAndSubItems[position].cartSubItems.isEmpty() &&
                cartItemsWithAddOnsAndSubItems[position].cartItemAddons.isEmpty()
            )
                View.GONE
            else
                View.VISIBLE

        val quantity = cartItemsWithAddOnsAndSubItems[position].cartItem.quantity
        viewHolder.itemQuantity.text = quantity.toString()

        CoroutineScope(Dispatchers.Default).launch {
            var itemPrice = cartItemsWithAddOnsAndSubItems[position].cartItem.itemPrice
            val subItemsText = StringBuilder()
            cartItemsWithAddOnsAndSubItems[position].cartSubItems.forEach { subItem ->
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
            cartItemsWithAddOnsAndSubItems[position].cartItemAddons.forEach { addOn ->
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

            val totalItemPrice = itemPrice * quantity
            withContext(Dispatchers.Main) {
                if (subItemsText.isNotEmpty()) {
                    viewHolder.subItems.visibility = View.VISIBLE
                    viewHolder.subItems.text = subItemsText.toString()
                }

                viewHolder.itemPrice.text = "RM ${formatter.format(totalItemPrice)}"
            }
        }

        viewHolder.deleteBtn.setOnClickListener {
            val removedCartItem = cartItemsWithAddOnsAndSubItems.removeAt(viewHolder.adapterPosition)
            notifyItemRemoved(viewHolder.adapterPosition)
            onRemoveFromCartListener.onItemRemoved(removedCartItem)
        }
    }

    override fun getItemCount() = cartItemsWithAddOnsAndSubItems.size

    fun updateItems(updatedCartItems: List<CartItemWithAddOnsAndSubItems>) {
        if (updatedCartItems.isEmpty()) {
            clear()
        } else {
            updatedCartItems.forEach { cartItemToAdd ->
                if (!cartItemsWithAddOnsAndSubItems.contains(cartItemToAdd)) {
                    cartItemsWithAddOnsAndSubItems.add(cartItemToAdd)
                    notifyItemInserted(cartItemsWithAddOnsAndSubItems.indexOf(cartItemToAdd))
                }
            }

            val cartItemsToRemove: MutableList<Int> = mutableListOf()
            cartItemsWithAddOnsAndSubItems.forEachIndexed { index, cartItem ->
                if (!updatedCartItems.contains(cartItem)) {
                    cartItemsToRemove.add(index)
                }
            }

            cartItemsToRemove.forEach { indexToRemove ->
                cartItemsWithAddOnsAndSubItems.elementAtOrNull(indexToRemove)?.let {
                    cartItemsWithAddOnsAndSubItems.removeAt(indexToRemove)
                    notifyItemRemoved(indexToRemove)
                }
            }
        }
    }

    private fun clear() {
        val originalSize = cartItemsWithAddOnsAndSubItems.size
        cartItemsWithAddOnsAndSubItems.clear()
        notifyItemRangeRemoved(0, originalSize)
    }

}