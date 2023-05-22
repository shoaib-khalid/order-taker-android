package com.symplified.easydukanpos.ui.main.menu_and_cart.menu

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.symplified.easydukanpos.App
import com.symplified.easydukanpos.R
import com.symplified.easydukanpos.constants.SharedPrefsKey
import com.symplified.easydukanpos.models.products.ProductWithDetails

class ProductsAdapter(
    private val onMenuItemClickListener: OnMenuItemClickedListener,
    private var items: List<ProductWithDetails> = listOf(),
    private var currencySymbol: String? = "RM",
) : RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

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
        val itemPrice: TextView = view.findViewById(R.id.item_price)
        val outOfStockOverlay: TextView = view.findViewById(R.id.out_of_stock_overlay)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.grid_product, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = itemsToShow[position]

        viewHolder.itemName.text = item.product.name

        if (item.product.isCustomPrice) {
            viewHolder.itemPrice.text =
                viewHolder.itemView.context.getString(R.string.custom_price)
        } else {
            val minimumDineInPrice = String.format(
                "%.2f",
                item.productInventoriesWithItems.minOfOrNull {
                    it.productInventory.dineInPrice
                } ?: 0.0
            )

            viewHolder.itemPrice.text = viewHolder.itemView.context.getString(
                R.string.monetary_amount,
                currencySymbol,
                minimumDineInPrice
            )
        }

        viewHolder.itemView.setOnClickListener {
            onMenuItemClickListener.onItemClicked(item)
        }

        if (item.product.thumbnailUrl.isBlank()) {
            viewHolder.itemImage.setImageResource(R.drawable.ic_fastfood)
        } else {
//            val fullThumbnailUrl = "${assetUrl}/${item.product.thumbnailUrl}"
            val fullThumbnailUrl = item.product.thumbnailUrl
            Log.d("thumbnail-url", "Loading $fullThumbnailUrl")
            Glide.with(viewHolder.itemView.context)
                .load(fullThumbnailUrl)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(viewHolder.itemImage)
        }

        val isOutOfStock = !item.product.allowOutOfStockPurchases
                && item.productInventoriesWithItems[0].productInventory.quantity <= 0
        viewHolder.itemView.isEnabled = !isOutOfStock
        viewHolder.outOfStockOverlay.visibility = if (isOutOfStock) View.VISIBLE else View.GONE
    }

    override fun getItemCount() = itemsToShow.size

    fun setProducts(products: List<ProductWithDetails>) {
        items = products
        itemsToShow = products
        notifyDataSetChanged()
    }

    fun filter(searchTerm: String) {
        val normalizedSearchTerm = searchTerm.trim().lowercase()

        itemsToShow =
            items.filter {
                val skuMatches = it.productInventoriesWithItems.any { invWithItems ->
                    invWithItems.productInventory.sku.replace("-", " ")
                        .lowercase().contains(normalizedSearchTerm)
                }

                it.product.name.lowercase().contains(normalizedSearchTerm)
                        || it.product.description.lowercase().contains(normalizedSearchTerm)
                        || skuMatches
            }
        notifyDataSetChanged()
    }

    fun setCurrencySymbol(currencySymbol: String) {
        this.currencySymbol = currencySymbol
        notifyItemRangeChanged(0, itemsToShow.size)
    }
}