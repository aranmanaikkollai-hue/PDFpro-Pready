package com.propdf.editor.ui.home

import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.propdf.editor.domain.model.PdfFile

class PdfFileAdapter : ListAdapter<PdfFile, PdfFileAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val tv = TextView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setPadding(48, 32, 48, 32)
        }
        return ViewHolder(tv)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val tv: TextView) : RecyclerView.ViewHolder(tv) {
        fun bind(file: PdfFile) { tv.text = file.name }
    }

    class DiffCallback : DiffUtil.ItemCallback<PdfFile>() {
        override fun areItemsTheSame(old: PdfFile, new: PdfFile) = old.uri == new.uri
        override fun areContentsTheSame(old: PdfFile, new: PdfFile) = old == new
    }
}
