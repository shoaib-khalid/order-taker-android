package com.symplified.ordertaker.ui.main.menuandcart.menu

import android.util.Log
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MenuAdapter(
    private var items: List<ProductWithDetails>,
    private val onMenuItemClickListener: OnMenuItemClickedListener
) : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    private var itemsToShow: List<ProductWithDetails> = items

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
        val item = itemsToShow[position]

        viewHolder.itemName.text = item.product.name

        if (item.product.thumbnailUrl.isNotBlank()) {
            val fullThumbnailUrl = "${assetUrl}/${item.product.thumbnailUrl}"
            Glide.with(viewHolder.itemView.context)
                .load(fullThumbnailUrl)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(viewHolder.itemImage)
        }

        CoroutineScope(Dispatchers.IO).launch {
            App.userRepository.user.collect { user ->
                val minimumDineInPrice = String.format(
                    "%.2f",
                    item.productInventoriesWithItems.minOfOrNull {
                        it.productInventory.dineInPrice
                    } ?: 0.0
                )

                withContext(Dispatchers.Main) {
                    viewHolder.itemPrice.text = viewHolder.itemView.context.getString(
                        R.string.monetary_amount,
                        user?.currencySymbol ?: "RM",
                        minimumDineInPrice
                    )
                }
            }
        }

        viewHolder.itemView.setOnClickListener {
            onMenuItemClickListener.onItemClicked(item)
        }

//        if (
//            !item.product.allowOutOfStockPurchases
//            && item.productInventoriesWithItems[0].productInventory.quantity <= 0
//            && position == viewHolder.adapterPosition
//        ) {
//            Log.d(
//                "out-of-stock",
//                "${item.product.name} ${item.product.id} at position ${viewHolder.adapterPosition} is out of stock. " +
//                        "allowOutOfStockPurchases: ${item.product.allowOutOfStockPurchases}. " +
//                        "quantity: ${item.productInventoriesWithItems[0].productInventory.quantity}."
//            )
//            viewHolder.itemView.isEnabled = false
//            viewHolder.outOfStockOverlay.visibility = View.VISIBLE
//        }
    }

    override fun getItemCount() = itemsToShow.size

    fun setProducts(products: List<ProductWithDetails>) {
        items = products
        itemsToShow = products
        notifyDataSetChanged()
    }

    fun filter(searchTerm: String) {
        itemsToShow =
            items.filter {
                val skuMatches = it.productInventoriesWithItems.any { invWithItems ->
                    invWithItems.productInventory.sku.replace("-", " ")
                        .lowercase().contains(searchTerm)
                }

                it.product.name.lowercase().contains(searchTerm)
                        || it.product.description.lowercase().contains(searchTerm)
                        || skuMatches
            }
        notifyDataSetChanged()
    }
}