package com.symplified.ordertaker.ui.main.menuandcart.cart

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.symplified.ordertaker.R
import com.symplified.ordertaker.models.CartItem

class CartItemsAdapter(private val cartItems : MutableList<CartItem> = mutableListOf())
    : RecyclerView.Adapter<CartItemsAdapter.ViewHolder>() {

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
        viewHolder.itemName.text = cartItems[position].itemName

        val quantity = cartItems[position].quantity
        val price = cartItems[position].itemPrice
        viewHolder.itemQuantity.text = quantity.toString()
        viewHolder.itemPrice.text = "RM " + (price * quantity)
    }

    override fun getItemCount() = cartItems.size

    fun addItem(cartItem: CartItem) = cartItems.add(cartItem)

    fun clearCart() = cartItems.clear()

    fun updateItems(cartItemsToAdd: List<CartItem>) {
        if (cartItemsToAdd.isEmpty()) {
            cartItems.clear()
            notifyDataSetChanged()
        } else {
            cartItemsToAdd.forEach { cartItem ->
                if (!cartItems.contains(cartItem)) {
                    cartItems.add(cartItem)
                    Log.d("add-to-cart", "${cartItem.itemName} added.")
                } else {
                    Log.d("add-to-cart", "${cartItem.itemName} not added.")
                }
                notifyDataSetChanged()
            }
        }
    }
}