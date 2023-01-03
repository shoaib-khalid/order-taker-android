package com.symplified.ordertaker.ui.main.menuandcart.categories

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.symplified.ordertaker.R

class CategoriesAdapter(private val categories: List<String>,
                        private val onCategoryClickListener: OnCategoryClickListener
)
    : RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

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
        val isSelected = (selectedPosition == position)
        viewHolder.itemView.isSelected = isSelected
//        viewHolder.textView.isSelected = isSelected
        viewHolder.textView.setTextColor(Color.parseColor(if (isSelected) "#FFFFFFFF" else "#FF000000"))

        viewHolder.textView.text = categories[position]
        viewHolder.itemView.setOnClickListener {
            Log.d("category", "onClicked ${categories[position]}")
            notifyItemChanged(selectedPosition)
            notifyItemChanged(viewHolder.adapterPosition)
            selectedPosition = viewHolder.adapterPosition
            onCategoryClickListener.onCategoryClicked(categories[selectedPosition])
        }
    }

    override fun getItemCount() = categories.size
}