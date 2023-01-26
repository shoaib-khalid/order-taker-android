package com.symplified.ordertaker.ui.main.menuandcart.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.symplified.ordertaker.R
import com.symplified.ordertaker.models.cartitems.CartItemWithAddOnsAndSubItems

class CartItemsAdapter(
    private val cartItemsWithAddOnsAndSubItems: MutableList<CartItemWithAddOnsAndSubItems>
    = mutableListOf(),
    private val onRemoveFromCartListener: OnRemoveFromCartListener
) : RecyclerView.Adapter<CartItemsAdapter.ViewHolder>() {

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
            if (cartItemsWithAddOnsAndSubItems[position].cartSubItems.isEmpty()
                && cartItemsWithAddOnsAndSubItems[position].cartItemAddons.isEmpty()
            )
                View.GONE
            else
                View.VISIBLE

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
                subItemsText.append(subItem.productName.substring(0..6).trim())
                    .append("...")
            }
        }
        cartItemsWithAddOnsAndSubItems[position].cartItemAddons.forEach { addOn ->
            itemPrice += addOn.price

            if (subItemsText.isNotEmpty()) {
                subItemsText.append(", ")
            }
            subItemsText.append(addOn.name)
        }
        if (subItemsText.isNotEmpty()) {
            viewHolder.subItems.visibility = View.VISIBLE
            viewHolder.subItems.text = subItemsText.toString()
        }

        val quantity = cartItemsWithAddOnsAndSubItems[position].cartItem.quantity

        viewHolder.itemQuantity.text = quantity.toString()
        viewHolder.itemPrice.text = "RM ${String.format("%.2f", itemPrice * quantity)}"

        viewHolder.deleteBtn.setOnClickListener {
            onRemoveFromCartListener.onItemRemoved(cartItemsWithAddOnsAndSubItems[position])
        }
    }

    override fun getItemCount() = cartItemsWithAddOnsAndSubItems.size

//    fun addItem(cartItem: CartItem) = cartItems.add(cartItem)

    fun clearCart() = cartItemsWithAddOnsAndSubItems.clear()

    fun updateItems(cartItemsToAdd: List<CartItemWithAddOnsAndSubItems>) {
        if (cartItemsToAdd.isEmpty()) {
            cartItemsWithAddOnsAndSubItems.clear()
        } else {
            cartItemsWithAddOnsAndSubItems.clear()
            cartItemsWithAddOnsAndSubItems.addAll(cartItemsToAdd)
        }
        notifyDataSetChanged()
    }
}