package com.propdf.editor.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.propdf.editor.R
import com.propdf.editor.domain.model.PdfFile

class PdfFileAdapter(
    private val onClick: (PdfFile) -> Unit,
    private val onFavorite: (PdfFile) -> Unit
) : ListAdapter<PdfFile, PdfFileAdapter.VH>(Diff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_pdf_file, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivThumb: ImageView = itemView.findViewById(R.id.ivThumb)
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvMeta: TextView = itemView.findViewById(R.id.tvMeta)
        private val btnFav: ImageButton = itemView.findViewById(R.id.btnFavorite)

        fun bind(file: PdfFile) {
            tvName.text = file.name
            tvMeta.text = formatSize(file.size)
            btnFav.setImageResource(if (file.isFavorite) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off)
            itemView.setOnClickListener { onClick(file) }
            btnFav.setOnClickListener { onFavorite(file) }
            ivThumb.setImageResource(R.drawable.bg_pdf_icon)
        }

        private fun formatSize(size: Long): String = when {
            size < 1024 -> "$size B"
            size < 1024 * 1024 -> "${size / 1024} KB"
            else -> String.format("%.1f MB", size / (1024.0 * 1024.0))
        }
    }

    class Diff : DiffUtil.ItemCallback<PdfFile>() {
        override fun areItemsTheSame(a: PdfFile, b: PdfFile) = a.uri == b.uri
        override fun areContentsTheSame(a: PdfFile, b: PdfFile) = a == b
    }
}
