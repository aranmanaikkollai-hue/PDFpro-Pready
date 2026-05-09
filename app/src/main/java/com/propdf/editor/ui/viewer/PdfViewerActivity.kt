package com.propdf.editor.ui.viewer

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.propdf.editor.R
import com.propdf.editor.ui.viewer.annotation.AnnotationOverlayView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PdfViewerActivity : AppCompatActivity(), OnPageChangeListener, OnLoadCompleteListener {

    private val viewModel: PdfViewerViewModel by viewModels()

    private lateinit var pdfView: PDFView
    private lateinit var tvPageCount: TextView
    private lateinit var tvTitle: TextView
    private lateinit var fabAnnotate: FloatingActionButton
    private lateinit var annotationBar: View
    private lateinit var annotationOverlay: AnnotationOverlayView

    private var isAnnotationMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_viewer)

        pdfView = findViewById(R.id.pdfView)
        tvPageCount = findViewById(R.id.tvPageCount)
        tvTitle = findViewById(R.id.tvTitle)
        fabAnnotate = findViewById(R.id.fabAnnotate)
        annotationBar = findViewById(R.id.annotationBar)
        annotationOverlay = findViewById(R.id.annotationOverlay)

        val uriString = intent.getStringExtra("pdf_uri") ?: return finish()
        val name = intent.getStringExtra("pdf_name") ?: "PDF"
        tvTitle.text = name

        loadPdf(Uri.parse(uriString))

        fabAnnotate.setOnClickListener { toggleAnnotationMode() }
        setupAnnotationBar()
        observeViewModel()
    }

    private fun loadPdf(uri: Uri) {
        pdfView.fromUri(uri)
            .defaultPage(0)
            .enableSwipe(true)
            .swipeHorizontal(false)
            .enableDoubletap(true)
            .scrollHandle(DefaultScrollHandle(this))
            .onPageChange(this)
            .onLoad(this)
            .spacing(10)
            .load()
    }

    override fun onPageChanged(page: Int, pageCount: Int) {
        tvPageCount.text = getString(R.string.page_count, page + 1, pageCount)
        viewModel.goToPage(page)
    }

    override fun loadComplete(nbPages: Int) {
        viewModel.setPageCount(nbPages)
    }

    private fun toggleAnnotationMode() {
        isAnnotationMode = !isAnnotationMode
        annotationBar.isVisible = isAnnotationMode
        annotationOverlay.isVisible = isAnnotationMode
        fabAnnotate.setImageResource(if (isAnnotationMode) android.R.drawable.ic_menu_close_clear_cancel else android.R.drawable.ic_menu_edit)
    }

    private fun setupAnnotationBar() {
        findViewById<View>(R.id.btnHighlight).setOnClickListener {
            annotationOverlay.setTool(AnnotationOverlayView.Tool.HIGHLIGHT)
        }
        findViewById<View>(R.id.btnPen).setOnClickListener {
            annotationOverlay.setTool(AnnotationOverlayView.Tool.PEN)
        }
        findViewById<View>(R.id.btnText).setOnClickListener {
            annotationOverlay.setTool(AnnotationOverlayView.Tool.TEXT)
        }
        findViewById<View>(R.id.btnErase).setOnClickListener {
            annotationOverlay.clear()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.error.collect { it?.let { Toast.makeText(this@PdfViewerActivity, it, Toast.LENGTH_SHORT).show() } }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        pdfView.recycle()
    }
}
