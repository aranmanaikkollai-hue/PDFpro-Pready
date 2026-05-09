package com.propdf.editor.ui.viewer

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.propdf.editor.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PdfViewerActivity : AppCompatActivity() {

    private val viewModel: PdfViewerViewModel by viewModels()

    private var btnBookmark: View? = null
    private var btnAnnotate: View? = null
    private var btnShare: View? = null
    private var btnMore: View? = null
    private var btnDraw: View? = null
    private var btnTextNote: View? = null
    private var btnEraser: View? = null
    private var btnUndo: View? = null
    private var btnSaveAnnotation: View? = null
    private var btnCloseAnnotation: View? = null
    private var adContainer: View? = null
    private var tvPageCount: TextView? = null
    private var bottomToolbar: View? = null
    private var annotationToolbar: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_viewer)
        initViews()
        intent.getStringExtra("pdf_uri")?.let { viewModel.openDocument(Uri.parse(it)) }
        setupObservers()
        setupListeners()
    }

    private fun initViews() {
        btnBookmark = findViewById(R.id.btnBookmark)
        btnAnnotate = findViewById(R.id.btnAnnotate)
        btnShare = findViewById(R.id.btnShare)
        btnMore = findViewById(R.id.btnMore)
        btnDraw = findViewById(R.id.btnDraw)
        btnTextNote = findViewById(R.id.btnTextNote)
        btnEraser = findViewById(R.id.btnEraser)
        btnUndo = findViewById(R.id.btnUndo)
        btnSaveAnnotation = findViewById(R.id.btnSaveAnnotation)
        btnCloseAnnotation = findViewById(R.id.btnCloseAnnotation)
        adContainer = findViewById(R.id.adContainer)
        tvPageCount = findViewById(R.id.tvPageCount)
        bottomToolbar = findViewById(R.id.bottomToolbar)
        annotationToolbar = findViewById(R.id.annotationToolbar)
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.pageCount.collect { updatePageCount(viewModel.currentPage.value, it) }
                }
                launch {
                    viewModel.currentPage.collect { updatePageCount(it, viewModel.pageCount.value) }
                }
            }
        }
    }

    private fun setupListeners() {
        btnBookmark?.setOnClickListener { }
        btnAnnotate?.setOnClickListener { showAnnotationToolbar() }
        btnShare?.setOnClickListener { }
        btnMore?.setOnClickListener { }
        btnDraw?.setOnClickListener { }
        btnTextNote?.setOnClickListener { }
        btnEraser?.setOnClickListener { }
        btnUndo?.setOnClickListener { }
        btnSaveAnnotation?.setOnClickListener { saveAnnotation() }
        btnCloseAnnotation?.setOnClickListener { hideAnnotationToolbar() }
    }

    private fun updatePageCount(current: Int, total: Int) {
        tvPageCount?.text = "${current + 1} / $total"
    }

    private fun showAnnotationToolbar() {
        bottomToolbar?.visibility = View.GONE
        annotationToolbar?.visibility = View.VISIBLE
    }

    private fun hideAnnotationToolbar() {
        bottomToolbar?.visibility = View.VISIBLE
        annotationToolbar?.visibility = View.GONE
    }

    private fun saveAnnotation() {
        Toast.makeText(this, "Annotation saved", Toast.LENGTH_SHORT).show()
        hideAnnotationToolbar()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.closeDocument()
    }
}
