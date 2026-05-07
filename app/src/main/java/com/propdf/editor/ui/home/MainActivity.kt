package com.propdf.editor.ui.home

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.propdf.editor.R
import com.propdf.editor.databinding.ActivityMainLegacyBinding
import com.propdf.editor.domain.model.PdfFile
import com.propdf.editor.ui.scanner.DocumentScannerActivity
import com.propdf.editor.ui.settings.SettingsActivity
import com.propdf.editor.ui.tools.ToolsActivity
import com.propdf.editor.ui.viewer.PdfViewerActivity
import com.propdf.editor.utils.DeviceCapabilities
import com.propdf.editor.utils.FileUtils
import com.propdf.editor.utils.PermissionUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainLegacyBinding
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var pdfAdapter: PdfFileAdapter

    private val pdfPicker = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { openPdf(it) }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            loadFiles()
        } else {
            Toast.makeText(this, R.string.error_permission_denied, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainLegacyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupClickListeners()
        hideAdContainer()
        observeViewModel()
        checkPermissions()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
    }

    private fun setupRecyclerView() {
        pdfAdapter = PdfFileAdapter(
            onItemClick = { pdfFile ->
                openPdf(Uri.parse(pdfFile.uri))
            },
            onFavoriteClick = { pdfFile ->
                viewModel.toggleFavorite(pdfFile)
            },
            onMoreClick = { pdfFile, view ->
                showFileOptions(pdfFile, view)
            }
        )

        binding.rvRecentFiles.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = pdfAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupClickListeners() {
        binding.btnOpenPdf.setOnClickListener {
            pdfPicker.launch(arrayOf("application/pdf"))
        }

        binding.btnScanDocument.setOnClickListener {
            if (PermissionUtils.hasCameraPermission(this)) {
                startActivity(Intent(this, DocumentScannerActivity::class.java))
            } else {
                PermissionUtils.requestCameraPermission(this, REQUEST_CAMERA)
            }
        }

        binding.btnPdfTools.setOnClickListener {
            startActivity(Intent(this, ToolsActivity::class.java))
        }

        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun hideAdContainer() {
        // Ads disabled - hide banner container
        binding.adContainer.visibility = View.GONE
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.recentFiles.collectLatest { files ->
                    pdfAdapter.submitList(files)
                    binding.tvNoRecentFiles.visibility =
                        if (files.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.error.collectLatest { error ->
                    error?.let {
                        Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
                        viewModel.clearError()
                    }
                }
            }
        }
    }

    private fun checkPermissions() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun loadFiles() {
        // Files are loaded automatically by ViewModel
    }

    private fun openPdf(uri: Uri) {
        viewModel.addRecentFile(uri)
        val intent = Intent(this, PdfViewerActivity::class.java).apply {
            data = uri
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        startActivity(intent)
    }

    private fun showFileOptions(pdfFile: PdfFile, anchorView: View) {
        val options = arrayOf(
            getString(R.string.action_share),
            getString(R.string.action_rename),
            getString(R.string.action_delete),
            getString(R.string.action_properties)
        )

        MaterialAlertDialogBuilder(this)
            .setTitle(pdfFile.name)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> sharePdf(pdfFile)
                    1 -> renamePdf(pdfFile)
                    2 -> deletePdf(pdfFile)
                    3 -> showProperties(pdfFile)
                }
            }
            .show()
    }

    private fun sharePdf(pdfFile: PdfFile) {
        val uri = Uri.parse(pdfFile.uri)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_pdf)))
    }

    private fun renamePdf(pdfFile: PdfFile) {
        // Implementation for rename
    }

    private fun deletePdf(pdfFile: PdfFile) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.action_delete)
            .setMessage("Are you sure you want to delete ${pdfFile.name}?")
            .setPositiveButton(R.string.action_delete) { _, _ ->
                // Delete implementation
            }
            .setNegativeButton(R.string.action_cancel, null)
            .show()
    }

    private fun showProperties(pdfFile: PdfFile) {
        val info = buildString {
            appendLine("Name: ${pdfFile.name}")
            appendLine("Size: ${FileUtils.formatFileSize(pdfFile.size)}")
            appendLine("Pages: ${pdfFile.pageCount}")
            append("Modified: ${FileUtils.formatDate(pdfFile.lastModified)}")
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.action_properties)
            .setMessage(info)
            .setPositiveButton(R.string.action_ok, null)
            .show()
    }

    companion object {
        private const val REQUEST_CAMERA = 1001
    }
}
