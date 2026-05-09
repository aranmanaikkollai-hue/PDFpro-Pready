package com.propdf.editor.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.propdf.editor.R
import com.propdf.editor.domain.model.PdfFile

class PdfFileAdapter(
    private val onClick: (PdfFile) -> Unit
) : ListAdapter<PdfFile, PdfFileAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pdf_file, parent, false)
        return ViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        itemView: View,
        private val onClick: (PdfFile) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvSize: TextView = itemView.findViewById(R.id.tvSize)

        fun bind(file: PdfFile) {
            tvName.text = file.name
            tvSize.text = formatFileSize(file.size)
            itemView.setOnClickListener { onClick(file) }
        }

        private fun formatFileSize(size: Long): String {
            return when {
                size < 1024 -> "$size B"
                size < 1024 * 1024 -> "${size / 1024} KB"
                else -> "${size / (1024 * 1024)} MB"
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<PdfFile>() {
        override fun areItemsTheSame(old: PdfFile, new: PdfFile) = old.uri == new.uri
        override fun areContentsTheSame(old: PdfFile, new: PdfFile) = old == new
    }
}
