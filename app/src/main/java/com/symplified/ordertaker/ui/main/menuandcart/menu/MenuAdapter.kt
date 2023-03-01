package com.symplified.ordertaker.ui.main.menuandcart.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.symplified.ordertaker.App
import com.symplified.ordertaker.R
import com.symplified.ordertaker.constants.SharedPrefsKey
import com.symplified.ordertaker.models.products.ProductWithDetails

class MenuAdapter(
    private val items: List<ProductWithDetails>,
    private val onMenuItemClickListener: OnMenuItemClickedListener
) :
    RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    private val assetUrl =
        if (App.sharedPreferences().getBoolean(SharedPrefsKey.IS_STAGING, false))
            App.ASSET_URL_STAGING
        else App.ASSET_URL_PRODUCTION

    interface OnMenuItemClickedListener {
        fun onItemClicked(item: ProductWithDetails)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.item_name)
        val itemImage: ImageView = view.findViewById(R.id.item_image)
        val itemPrice: TextView = view.findViewById(R.id.cart_item_price)
        val outOfStockOverlay: TextView = view.findViewById(R.id.out_of_stock_overlay)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.grid_menu_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemName.text = items[position].product.name

        if (items[position].product.thumbnailUrl.isNotBlank()) {
                val fullThumbnailUrl = "${assetUrl}/${items[position].product.thumbnailUrl}"
                Glide.with(viewHolder.itemView.context)
                    .load(fullThumbnailUrl)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(viewHolder.itemImage)
        }

        val minimumDineInPrice = String.format(
            "%.2f",
            items[position].productInventoriesWithItems.minOfOrNull { it.productInventory.dineInPrice }
                ?: 0.0
        )
        viewHolder.itemPrice.text = "RM $minimumDineInPrice"

        viewHolder.itemView.setOnClickListener {
            onMenuItemClickListener.onItemClicked(items[viewHolder.adapterPosition])
        }

        items[position].productInventoriesWithItems.getOrNull(0)?.let { inventoryWithItems ->
            if (inventoryWithItems.productInventory.quantity <= 0 &&
                !items[position].product.allowOutOfStockPurchases) {
                viewHolder.itemView.isEnabled = false
                viewHolder.outOfStockOverlay.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount() = items.size
}