package com.symplified.ordertaker.ui.menuandcart.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.symplified.ordertaker.R
import com.symplified.ordertaker.models.Item

class MenuAdapter(private val items: List<Item>,
                  private val onMenuItemClickListener: OnMenuItemClickedListener) :
RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    interface OnMenuItemClickedListener {
        fun onItemClicked(item: Item)
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val itemName: TextView
        val itemPrice: TextView

        init {
            itemName = view.findViewById(R.id.item_name)
            itemPrice = view.findViewById(R.id.cart_item_price)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.grid_menu_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemName.text = items[position].name
        viewHolder.itemPrice.text = "RM " + items[position].price
        viewHolder.itemView.setOnClickListener {
            onMenuItemClickListener.onItemClicked(items[position])
        }
    }

    override fun getItemCount() = items.size
}