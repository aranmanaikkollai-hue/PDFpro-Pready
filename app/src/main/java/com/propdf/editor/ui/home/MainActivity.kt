package com.propdf.editor.ui.home

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.propdf.editor.R
import com.propdf.editor.domain.model.PdfFile
import com.propdf.editor.ui.scanner.DocumentScannerActivity
import com.propdf.editor.ui.settings.SettingsActivity
import com.propdf.editor.ui.tools.ToolsActivity
import com.propdf.editor.ui.viewer.PdfViewerActivity
import com.propdf.editor.util.FileUtils
import com.propdf.editor.util.PermissionUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var progressBar: CircularProgressIndicator
    private lateinit var btnOpen: MaterialButton
    private lateinit var btnScan: MaterialButton
    private lateinit var btnTools: MaterialButton
    private lateinit var btnSettings: MaterialButton
    private lateinit var adapter: PdfFileAdapter

    private val requestPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
        if (results.values.any { it }) loadRecent()
    }

    private val openPdfLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { openPdfViewer(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupRecyclerView()
        setupListeners()
        observeViewModel()
        checkPermissions()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView)
        tvEmpty = findViewById(R.id.tvEmpty)
        progressBar = findViewById(R.id.progressBar)
        btnOpen = findViewById(R.id.btnOpenPdf)
        btnScan = findViewById(R.id.btnScan)
        btnTools = findViewById(R.id.btnTools)
        btnSettings = findViewById(R.id.btnSettings)
    }

    private fun setupRecyclerView() {
        adapter = PdfFileAdapter(
            onClick = { openPdfViewer(it.uri) },
            onFavorite = { viewModel.toggleFavorite(it.uri.toString()) }
        )
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
    }

    private fun setupListeners() {
        btnOpen.setOnClickListener { openPdfLauncher.launch(arrayOf("application/pdf")) }
        btnScan.setOnClickListener { startActivity(Intent(this, DocumentScannerActivity::class.java)) }
        btnTools.setOnClickListener { startActivity(Intent(this, ToolsActivity::class.java)) }
        btnSettings.setOnClickListener { startActivity(Intent(this, SettingsActivity::class.java)) }
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
                launch {
                    viewModel.isLoading.collect { progressBar.visibility = if (it) View.VISIBLE else View.GONE }
                }
            }
        }
    }

    private fun checkPermissions() {
        if (!PermissionUtils.hasStoragePermission(this)) {
            requestPermissions.launch(PermissionUtils.storagePermissions())
        }
    }

    private fun openPdfViewer(uri: Uri) {
        val name = FileUtils.getFileName(this, uri)
        val size = FileUtils.getFileSize(this, uri)
        viewModel.addRecentFile(PdfFile(uri, name, size))
        val intent = Intent(this, PdfViewerActivity::class.java).apply {
            putExtra("pdf_uri", uri.toString())
            putExtra("pdf_name", name)
        }
        startActivity(intent)
    }
}
