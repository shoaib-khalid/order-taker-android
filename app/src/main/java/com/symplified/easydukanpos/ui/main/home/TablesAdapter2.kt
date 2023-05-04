package com.symplified.easydukanpos.ui.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.symplified.easydukanpos.databinding.GridTableBinding
import com.symplified.easydukanpos.models.zones.Table

class TablesAdapter2(
    private val onItemClicked: (Table) -> Unit
) : ListAdapter<Table, TablesAdapter2.TableViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Table>() {
            override fun areItemsTheSame(oldItem: Table, newItem: Table) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Table, newItem: Table) =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TablesAdapter2.TableViewHolder {
        val viewHolder = TableViewHolder(
            GridTableBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
        viewHolder.itemView.setOnClickListener {
            onItemClicked(getItem(viewHolder.adapterPosition))
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: TableViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TableViewHolder(private val binding: GridTableBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(table: Table) {
            binding.textView.text = table.combinationTableNumber
        }
    }
}