package com.propdf.editor.ui.tools

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.propdf.editor.R

class ToolAdapter(
    private val items: List<ToolsActivity.ToolItem>,
    private val onClick: (ToolsActivity.ToolItem) -> Unit
) : RecyclerView.Adapter<ToolAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_tool, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])
    override fun getItemCount() = items.size

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val card: MaterialCardView = itemView.findViewById(R.id.card)
        private val icon: ImageView = itemView.findViewById(R.id.icon)
        private val title: TextView = itemView.findViewById(R.id.title)

        fun bind(item: ToolsActivity.ToolItem) {
            icon.setImageResource(item.icon)
            title.text = item.title
            card.setOnClickListener { onClick(item) }
        }
    }
}
