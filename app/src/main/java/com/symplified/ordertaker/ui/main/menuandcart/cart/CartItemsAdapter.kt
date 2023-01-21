package com.symplified.ordertaker.ui.main.menuandcart.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.symplified.ordertaker.R
import com.symplified.ordertaker.models.cartitems.CartItem
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
        val itemQuantity: TextView
        val itemPrice: TextView
        val deleteBtn: AppCompatImageButton

        init {
            itemNumber = view.findViewById(R.id.cart_item_no)
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

        val quantity = cartItemsWithAddOnsAndSubItems[position].cartItem.quantity
        val price = cartItemsWithAddOnsAndSubItems[position].cartItem.itemPrice
        viewHolder.itemQuantity.text = quantity.toString()
        viewHolder.itemPrice.text = "RM ${String.format("%.2f", price * quantity)}"

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