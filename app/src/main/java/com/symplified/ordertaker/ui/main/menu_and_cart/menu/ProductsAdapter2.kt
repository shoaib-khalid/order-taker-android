package com.symplified.ordertaker.ui.main.menu_and_cart.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.symplified.ordertaker.App
import com.symplified.ordertaker.R
import com.symplified.ordertaker.constants.SharedPrefsKey
import com.symplified.ordertaker.databinding.GridProductBinding
import com.symplified.ordertaker.models.products.ProductWithDetails
import com.symplified.ordertaker.utils.Utils

class ProductsAdapter2(
    private val onItemClicked: (ProductWithDetails) -> Unit,
    private var currencySymbol: String? = "RM"
) : ListAdapter<ProductWithDetails, ProductsAdapter2.ProductViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ProductWithDetails>() {
            override fun areItemsTheSame(
                oldItem: ProductWithDetails,
                newItem: ProductWithDetails
            ): Boolean = oldItem.product.id == newItem.product.id

            override fun areContentsTheSame(
                oldItem: ProductWithDetails,
                newItem: ProductWithDetails
            ): Boolean = oldItem == newItem
        }

        val assetUrl =
            if (App.sharedPreferences().getBoolean(SharedPrefsKey.IS_STAGING, false))
                App.ASSET_URL_STAGING
            else App.ASSET_URL_PRODUCTION
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val viewHolder = ProductViewHolder(
            GridProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            onItemClicked(getItem(position))
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ProductViewHolder(private var binding: GridProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(productWithDetails: ProductWithDetails) {

            val product = productWithDetails.product
            binding.itemName.text = product.name

            binding.itemPrice.text =
                if (product.isCustomPrice) binding.root.context.getString(R.string.custom_price)
                else {
                    val minimumDineInPrice = Utils.formatPrice(
                        productWithDetails.productInventoriesWithItems.minOfOrNull {
                            it.productInventory.dineInPrice
                        } ?: 0.0
                    )

                    binding.root.context.getString(
                        R.string.monetary_amount,
                        "RM",
                        minimumDineInPrice
                    )
                }

            if (productWithDetails.product.thumbnailUrl.isBlank()) {
                binding.itemImage.setImageResource(R.drawable.ic_fastfood)
            } else {
                val fullThumbnailUrl = "${assetUrl}/${productWithDetails.product.thumbnailUrl}"
                Glide.with(binding.root.context)
                    .load(fullThumbnailUrl)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(binding.itemImage)
            }

            val isOutOfStock =
                !product.allowOutOfStockPurchases && productWithDetails.productInventoriesWithItems[0].productInventory.quantity <= 0
            binding.root.isEnabled = !isOutOfStock
            binding.outOfStockOverlay.visibility = if (isOutOfStock) View.VISIBLE else View.GONE
        }
    }
}