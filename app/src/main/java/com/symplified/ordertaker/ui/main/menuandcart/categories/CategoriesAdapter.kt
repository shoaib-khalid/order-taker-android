package com.symplified.ordertaker.ui.main.menuandcart.categories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.symplified.ordertaker.R

class CategoriesAdapter(private val categories: List<String>,
                        private val onCategoryClickListener: OnCategoryClickListener
)
    : RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {

    interface OnCategoryClickListener {
        fun onCategoryClicked(category: String)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView

        init {
            textView = view.findViewById(R.id.category_text_view)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.row_category, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView.text = categories[position]
        viewHolder.itemView.setOnClickListener {
            onCategoryClickListener.onCategoryClicked(categories[position])
        }
    }

    override fun getItemCount() = categories.size
}