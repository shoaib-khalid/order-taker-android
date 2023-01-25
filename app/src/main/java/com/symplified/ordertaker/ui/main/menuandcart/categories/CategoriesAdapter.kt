package com.symplified.ordertaker.ui.main.menuandcart.categories

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.symplified.ordertaker.R
import com.symplified.ordertaker.models.categories.Category

class CategoriesAdapter(
    private var categories: List<Category> = mutableListOf(),
    private val onCategoryClickListener: OnCategoryClickListener
) : RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    interface OnCategoryClickListener {
        fun onCategoryClicked(category: Category)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.category_text_view)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.row_category, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val isSelected = (selectedPosition == position)
        viewHolder.itemView.isSelected = isSelected
//        viewHolder.textView.isSelected = isSelected
        viewHolder.textView.setTextColor(Color.parseColor(if (isSelected) "#FFFFFFFF" else "#FF000000"))

        viewHolder.textView.text = categories[position].name
        viewHolder.itemView.setOnClickListener {
            notifyItemChanged(selectedPosition)
            notifyItemChanged(viewHolder.adapterPosition)
            selectedPosition = viewHolder.adapterPosition
            onCategoryClickListener.onCategoryClicked(categories[selectedPosition])
        }
    }

    override fun getItemCount() = categories.size

    fun updateItems(updatedCategories: List<Category>) {
        categories = updatedCategories
        notifyDataSetChanged()
    }

    fun setSelectedCategory(category: Category) {
        selectedPosition = categories.indexOf(category)
        notifyItemChanged(selectedPosition)
    }
}