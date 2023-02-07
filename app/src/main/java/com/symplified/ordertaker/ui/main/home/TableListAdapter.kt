package com.symplified.ordertaker.ui.main.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.symplified.ordertaker.R
import com.symplified.ordertaker.models.zones.Table

class TableListAdapter(private var tables: MutableList<Table> = mutableListOf(),
                       private val onTableClickListener: OnTableClickListener
) :
    RecyclerView.Adapter<TableListAdapter.ViewHolder>() {

    interface OnTableClickListener {
        fun onTableClicked(table: Table)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.text_view)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.grid_table, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView.text = tables[position].combinationTableNumber
        viewHolder.textView.setOnClickListener {
            onTableClickListener.onTableClicked(tables[position])
        }
    }

    override fun getItemCount() = tables.size

    fun setTables(newTables: List<Table>) {
        newTables.forEach { newTable ->
            if (tables.firstOrNull { it.id == newTable.id } == null) {
                if (tables.add(newTable)) {
                    notifyItemInserted(tables.size - 1)
                }
            }
        }

        val tablesToRemove: MutableList<Int> = mutableListOf()
        tables.forEachIndexed { index, table ->
            if (newTables.firstOrNull { it.id == table.id  } == null) {
                tablesToRemove.add(index)
            }
        }

        tablesToRemove.forEach { indexToRemove ->
            tables.elementAtOrNull(indexToRemove)?.let {
                tables.removeAt(indexToRemove)
                notifyItemRemoved(indexToRemove)
            }
        }
    }
}