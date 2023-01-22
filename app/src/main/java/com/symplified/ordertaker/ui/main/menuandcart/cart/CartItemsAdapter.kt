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
        val itemNumber: TextView
        val itemName: TextView
        val subItems: TextView
        val itemQuantity: TextView
        val itemPrice: TextView
        val deleteBtn: AppCompatImageButton

        init {
            itemNumber = view.findViewById(R.id.cart_item_no)
            subItems = view.findViewById(R.id.cart_sub_items_text)
            itemName = view.findViewById(R.id.cart_item_name)
            itemQuantity = view.findViewById(R.id.cart_item_quantity)
            itemPrice = view.findViewById(R.id.cart_item_price)
            deleteBtn = view.findViewById(R.id.delete_btn)
        }
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
                && cartItemsWithAddOnsAndSubItems[position].cartItemAddons.isEmpty())
                View.GONE
            else
                View.VISIBLE

        var itemPrice = cartItemsWithAddOnsAndSubItems[position].cartItem.itemPrice
        val subItemsText = StringBuilder()
        cartItemsWithAddOnsAndSubItems[position].cartSubItems.forEach { subItem ->
            if (subItemsText.isNotEmpty()) {
                subItemsText.append(", ")
            }
            subItemsText.append(subItem.productName)
        }
        cartItemsWithAddOnsAndSubItems[position].cartItemAddons.forEach { addOn ->
            itemPrice += addOn.price

            if (subItemsText.isNotEmpty()) {
                subItemsText.append(", ")
            }
            subItemsText.append(addOn.name)
        }
        viewHolder.subItems.text = subItemsText.toString()

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