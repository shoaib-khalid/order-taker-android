package com.symplified.ordertaker.ui.menuandcart.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.symplified.ordertaker.R
import com.symplified.ordertaker.models.CartItem

class CartItemsAdapter(private val cartItems: List<CartItem>)
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
        viewHolder.itemName.text = cartItems[position].item.name

        val quantity = cartItems[position].quantity
        val price = cartItems[position].item.price
        viewHolder.itemQuantity.text = quantity.toString()
        viewHolder.itemPrice.text = "RM " + (price * quantity)
    }

    override fun getItemCount() = cartItems.size
}