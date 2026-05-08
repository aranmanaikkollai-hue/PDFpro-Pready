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

    private lateinit var btnBookmark: View
    private lateinit var btnAnnotate: View
    private lateinit var btnShare: View
    private lateinit var btnMore: View
    private lateinit var btnDraw: View
    private lateinit var btnTextNote: View
    private lateinit var btnEraser: View
    private lateinit var btnUndo: View
    private lateinit var btnSaveAnnotation: View
    private lateinit var btnCloseAnnotation: View
    private lateinit var adContainer: View
    private lateinit var tvPageCount: TextView
    private lateinit var bottomToolbar: View
    private lateinit var annotationToolbar: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_viewer)

        initViews()
        
        val uriString = intent.getStringExtra("pdf_uri")
        if (uriString != null) {
            viewModel.openDocument(Uri.parse(uriString))
        }

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
                    viewModel.pageCount.collect { count ->
                        updatePageCount(viewModel.currentPage.value, count)
                    }
                }
                launch {
                    viewModel.currentPage.collect { page ->
                        updatePageCount(page, viewModel.pageCount.value)
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        btnBookmark.setOnClickListener { }
        btnAnnotate.setOnClickListener { showAnnotationToolbar() }
        btnShare.setOnClickListener { }
        btnMore.setOnClickListener { }
        btnDraw.setOnClickListener { }
        btnTextNote.setOnClickListener { }
        btnEraser.setOnClickListener { }
        btnUndo.setOnClickListener { }
        btnSaveAnnotation.setOnClickListener { saveAnnotation() }
        btnCloseAnnotation.setOnClickListener { hideAnnotationToolbar() }
    }

    private fun updatePageCount(current: Int, total: Int) {
        tvPageCount.text = "${current + 1} / $total"
    }

    private fun showAnnotationToolbar() {
        bottomToolbar.visibility = View.GONE
        annotationToolbar.visibility = View.VISIBLE
    }

    private fun hideAnnotationToolbar() {
        bottomToolbar.visibility = View.VISIBLE
        annotationToolbar.visibility = View.GONE
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
