package com.symplified.ordertaker.ui.main.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.symplified.ordertaker.R
import com.symplified.ordertaker.models.zones.Table

class TablesAdapter(private var tables: List<Table> = listOf(),
                    private val onTableClickListener: OnTableClickListener
) :
    RecyclerView.Adapter<TablesAdapter.ViewHolder>() {

    interface OnTableClickListener {
        fun onTableClicked(tableNo: String)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView

        init {
            textView = view.findViewById(R.id.text_view)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.grid_zone, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView.text = tables[position].combinationTableNumber
        viewHolder.textView.setOnClickListener {
            onTableClickListener.onTableClicked(tables[position].combinationTableNumber)
        }
    }

    override fun getItemCount() = tables.size

    fun setTables(newTables: List<Table>) {
        tables = newTables
        notifyDataSetChanged()
    }
}