package com.propdf.editor.ui.scanner

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.propdf.editor.R

class ScannedPageAdapter(
    private val onDelete: (Int) -> Unit,
    private val onRotate: (Int) -> Unit
) : ListAdapter<Bitmap, ScannedPageAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_scanned_page, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivPage: ImageView = itemView.findViewById(R.id.ivPage)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
        private val btnRotate: ImageButton = itemView.findViewById(R.id.btnRotate)

        fun bind(bitmap: Bitmap, position: Int) {
            ivPage.setImageBitmap(bitmap)
            btnDelete.setOnClickListener { onDelete(position) }
            btnRotate.setOnClickListener { onRotate(position) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Bitmap>() {
        override fun areItemsTheSame(oldItem: Bitmap, newItem: Bitmap): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Bitmap, newItem: Bitmap): Boolean {
            return oldItem.sameAs(newItem)
        }
    }
}
