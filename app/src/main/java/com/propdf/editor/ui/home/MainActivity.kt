package com.propdf.editor.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.propdf.editor.R
import com.propdf.editor.ui.scanner.DocumentScannerActivity
import com.propdf.editor.ui.tools.ToolsActivity
import com.propdf.editor.ui.viewer.PdfViewerActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var btnOpenPdf: MaterialButton
    private lateinit var btnScan: MaterialButton
    private lateinit var btnTools: MaterialButton
    private lateinit var adapter: PdfFileAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView)
        tvEmpty = findViewById(R.id.tvEmpty)
        btnOpenPdf = findViewById(R.id.btnOpenPdf)
        btnScan = findViewById(R.id.btnScan)
        btnTools = findViewById(R.id.btnTools)
    }

    private fun setupRecyclerView() {
        adapter = PdfFileAdapter { pdfFile ->
            openPdfViewer(pdfFile.uri.toString())
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        btnOpenPdf.setOnClickListener {
            // TODO: Launch file picker to select PDF
        }
        btnScan.setOnClickListener {
            startActivity(Intent(this, DocumentScannerActivity::class.java))
        }
        btnTools.setOnClickListener {
            startActivity(Intent(this, ToolsActivity::class.java))
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.recentFiles.collect { files ->
                        adapter.submitList(files)
                        tvEmpty.visibility = if (files.isEmpty()) View.VISIBLE else View.GONE
                    }
                }
            }
        }
    }

    private fun openPdfViewer(uri: String) {
        val intent = Intent(this, PdfViewerActivity::class.java).apply {
            putExtra("pdf_uri", uri)
        }
        startActivity(intent)
    }
}
