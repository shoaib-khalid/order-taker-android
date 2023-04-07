package com.symplified.ordertaker.ui.main.menu_and_cart.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.symplified.ordertaker.R
import com.symplified.ordertaker.models.cartitems.CartItemWithAddOnsAndSubItems
import com.symplified.ordertaker.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartItemsAdapter(
    private val currencySymbol: String,
    private val onRemoveFromCartListener: OnRemoveFromCartListener,
    private val items: MutableList<CartItemWithAddOnsAndSubItems> = mutableListOf()
) : RecyclerView.Adapter<CartItemsAdapter.ViewHolder>() {

    interface OnRemoveFromCartListener {
        fun onItemRemoved(cartItem: CartItemWithAddOnsAndSubItems)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.cart_item_name)
        val subItems: TextView = view.findViewById(R.id.cart_sub_items_text)
        val itemQuantity: TextView = view.findViewById(R.id.cart_item_quantity)
        val itemPrice: TextView = view.findViewById(R.id.cart_item_price)
        val deleteBtn: AppCompatImageButton = view.findViewById(R.id.btn_close)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(
            R.layout.row_cart_item,
            viewGroup,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemName.text = items[position].cartItem.itemName

        viewHolder.subItems.visibility =
            if (items[position].cartSubItems.isEmpty()
                && items[position].cartItemAddons.isEmpty())
                View.GONE
            else
                View.VISIBLE

        val quantity = items[position].cartItem.quantity
        viewHolder.itemQuantity.text = quantity.toString()

        CoroutineScope(Dispatchers.Default).launch {
            var itemPrice = items[position].cartItem.itemPrice
            val subItemsText = StringBuilder()
            items[position].cartSubItems.forEach { subItem ->
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
            items[position].cartItemAddons.forEach { addOn ->
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

                viewHolder.itemPrice.text = viewHolder.itemView.context.getString(
                    R.string.monetary_amount,
                    currencySymbol,
                    Utils.formatPrice(totalItemPrice)
                )
            }
        }

        viewHolder.deleteBtn.setOnClickListener {
            val removedCartItem = items.removeAt(viewHolder.adapterPosition)
            notifyItemRemoved(viewHolder.adapterPosition)
            onRemoveFromCartListener.onItemRemoved(removedCartItem)
        }
    }

    override fun getItemCount() = items.size

    fun updateItems(updatedCartItems: List<CartItemWithAddOnsAndSubItems>) {
        if (updatedCartItems.isEmpty()) {
            clear()
        } else {
            updatedCartItems.forEach { cartItemToAdd ->
                if (!items.contains(cartItemToAdd)) {
                    items.add(cartItemToAdd)
                    notifyItemInserted(items.indexOf(cartItemToAdd))
                }
            }

            val cartItemsToRemove: MutableList<Int> = mutableListOf()
            items.forEachIndexed { index, cartItem ->
                if (!updatedCartItems.contains(cartItem)) {
                    cartItemsToRemove.add(index)
                }
            }

            cartItemsToRemove.forEach { indexToRemove ->
                items.elementAtOrNull(indexToRemove)?.let {
                    items.removeAt(indexToRemove)
                    notifyItemRemoved(indexToRemove)
                }
            }
        }
    }

    private fun clear() {
        val originalSize = items.size
        items.clear()
        notifyItemRangeRemoved(0, originalSize)
    }

}