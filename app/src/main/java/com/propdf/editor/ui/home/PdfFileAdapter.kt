package com.propdf.editor.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.propdf.editor.R
import com.propdf.editor.domain.model.PdfFile
import com.propdf.editor.utils.FileUtils

class PdfFileAdapter(
    private val onItemClick: (PdfFile) -> Unit,
    private val onFavoriteClick: (PdfFile) -> Unit,
    private val onMoreClick: (PdfFile, View) -> Unit
) : ListAdapter<PdfFile, PdfFileAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pdf_file, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvFileName: TextView = itemView.findViewById(R.id.tvFileName)
        private val tvFileInfo: TextView = itemView.findViewById(R.id.tvFileInfo)
        private val btnFavorite: ImageButton = itemView.findViewById(R.id.btnFavorite)
        private val btnMore: ImageButton = itemView.findViewById(R.id.btnMore)

        fun bind(pdfFile: PdfFile) {
            tvFileName.text = pdfFile.name
            tvFileInfo.text = buildString {
                append(FileUtils.formatFileSize(pdfFile.size))
                append("  ")
                append(FileUtils.formatDate(pdfFile.lastModified))
                if (pdfFile.pageCount > 0) {
                    append("  ")
                    append("${pdfFile.pageCount} pages")
                }
            }

            btnFavorite.setImageResource(
                if (pdfFile.isFavorite) R.drawable.ic_favorite_filled
                else R.drawable.ic_favorite_outline
            )

            itemView.setOnClickListener { onItemClick(pdfFile) }
            btnFavorite.setOnClickListener { onFavoriteClick(pdfFile) }
            btnMore.setOnClickListener { onMoreClick(pdfFile, it) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<PdfFile>() {
        override fun areItemsTheSame(oldItem: PdfFile, newItem: PdfFile): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PdfFile, newItem: PdfFile): Boolean {
            return oldItem == newItem
        }
    }
}
