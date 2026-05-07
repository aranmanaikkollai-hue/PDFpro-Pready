package com.propdf.editor.ui.viewer

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.listener.OnTapListener
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.propdf.editor.R
import com.propdf.editor.databinding.ActivityPdfViewerBinding
import com.propdf.editor.utils.DeviceCapabilities
import com.propdf.editor.utils.FileUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PdfViewerActivity : AppCompatActivity(), OnPageChangeListener, OnLoadCompleteListener, OnTapListener {

    private lateinit var binding: ActivityPdfViewerBinding
    private val viewModel: PdfViewerViewModel by viewModels()

    private var isToolbarVisible = true
    private var totalPages = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPdfView()
        setupToolbar()
        setupClickListeners()
        hideAdContainer()
        observeViewModel()

        intent.data?.let { uri ->
            loadPdf(uri)
        } ?: run {
            Toast.makeText(this, R.string.error_no_pdf_selected, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupPdfView() {
        binding.pdfView.setBackgroundColor(getColor(R.color.background))
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.toolbar.inflateMenu(R.menu.menu_pdf_viewer)
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_search -> showSearchDialog()
                R.id.action_bookmark -> showBookmarkDialog()
                R.id.action_share -> sharePdf()
                R.id.action_properties -> showProperties()
            }
            true
        }
    }

    private fun setupClickListeners() {
        binding.btnSearch.setOnClickListener { showSearchDialog() }
        binding.btnBookmark.setOnClickListener { showBookmarkDialog() }
        binding.btnAnnotate.setOnClickListener { toggleAnnotationMode() }
        binding.btnShare.setOnClickListener { sharePdf() }
        binding.btnMore.setOnClickListener { showMoreOptions() }

        // Annotation toolbar
        binding.btnHighlight.setOnClickListener { setAnnotationTool(AnnotationTool.HIGHLIGHT) }
        binding.btnDraw.setOnClickListener { setAnnotationTool(AnnotationTool.DRAW) }
        binding.btnTextNote.setOnClickListener { setAnnotationTool(AnnotationTool.TEXT) }
        binding.btnEraser.setOnClickListener { setAnnotationTool(AnnotationTool.ERASER) }
        binding.btnUndo.setOnClickListener { undoAnnotation() }
        binding.btnSaveAnnotation.setOnClickListener { saveAnnotations() }
        binding.btnCloseAnnotation.setOnClickListener { toggleAnnotationMode() }
    }

    private fun hideAdContainer() {
        // Ads disabled - hide banner container
        binding.adContainer.visibility = View.GONE
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoading.collectLatest { isLoading ->
                    // Show/hide loading indicator
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.error.collectLatest { error ->
                    error?.let {
                        Toast.makeText(this@PdfViewerActivity, it, Toast.LENGTH_SHORT).show()
                        viewModel.clearError()
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.ocrText.collectLatest { text ->
                    text?.let {
                        MaterialAlertDialogBuilder(this@PdfViewerActivity)
                            .setTitle(R.string.extract_text)
                            .setMessage(it)
                            .setPositiveButton(R.string.action_share) { _, _ ->
                                shareText(it)
                            }
                            .setNegativeButton(R.string.action_ok, null)
                            .show()
                        viewModel.clearOcrResult()
                    }
                }
            }
        }
    }

    private fun loadPdf(uri: Uri) {
        binding.pdfView.fromUri(uri)
            .pages(0)
            .enableSwipe(true)
            .swipeHorizontal(false)
            .enableDoubletap(true)
            .defaultPage(0)
            .enableAnnotationRendering(true)
            .password(null)
            .scrollHandle(null)
            .enableAntialiasing(!DeviceCapabilities.isLowRamDevice)
            .spacing(10)
            .autoSpacing(false)
            .pageFitPolicy(FitPolicy.WIDTH)
            .fitEachPage(true)
            .pageSnap(false)
            .pageFling(false)
            .nightMode(true)
            .onPageChange(this)
            .onLoad(this)
            .onTap(this)
            .load()
    }

    override fun onPageChanged(page: Int, pageCount: Int) {
        totalPages = pageCount
        binding.tvPageCount.text = getString(R.string.page_count_format, page + 1, pageCount)
        viewModel.loadPage(page)
    }

    override fun loadComplete(nbPages: Int) {
        totalPages = nbPages
        binding.tvPageCount.text = getString(R.string.page_count_format, 1, nbPages)
        binding.tvPageCount.visibility = View.VISIBLE
    }

    override fun onTap(e: android.view.MotionEvent?): Boolean {
        toggleToolbarVisibility()
        return true
    }

    private fun toggleToolbarVisibility() {
        isToolbarVisible = !isToolbarVisible
        val visibility = if (isToolbarVisible) View.VISIBLE else View.GONE
        binding.toolbar.visibility = visibility
        binding.tvPageCount.visibility = if (isToolbarVisible) View.VISIBLE else View.GONE
        binding.bottomToolbar.visibility = if (isToolbarVisible && !viewModel.isAnnotationMode.value) View.VISIBLE else View.GONE
    }

    private fun toggleAnnotationMode() {
        viewModel.toggleAnnotationMode()
        val isAnnotationMode = viewModel.isAnnotationMode.value
        binding.bottomToolbar.visibility = if (isAnnotationMode) View.GONE else View.VISIBLE
        binding.annotationToolbar.visibility = if (isAnnotationMode) View.VISIBLE else View.GONE
        Toast.makeText(this, if (isAnnotationMode) R.string.annotation_mode else "Annotation mode off", Toast.LENGTH_SHORT).show()
    }

    private fun setAnnotationTool(tool: AnnotationTool) {
        // Implementation for annotation tools
    }

    private fun undoAnnotation() {
        // Implementation
    }

    private fun saveAnnotations() {
        // Implementation
    }

    private fun showSearchDialog() {
        val editText = android.widget.EditText(this).apply {
            hint = getString(R.string.search_pdf)
        }
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.search_pdf)
            .setView(editText)
            .setPositiveButton(R.string.action_ok) { _, _ ->
                val query = editText.text.toString()
                if (query.isNotBlank()) {
                    viewModel.searchInDocument(query)
                }
            }
            .setNegativeButton(R.string.action_cancel, null)
            .show()
    }

    private fun showBookmarkDialog() {
        val editText = android.widget.EditText(this).apply {
            hint = "Bookmark name"
            setText("Page ${viewModel.currentPage.value + 1}")
        }
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.bookmark_added)
            .setView(editText)
            .setPositiveButton(R.string.action_save) { _, _ ->
                viewModel.addBookmark(editText.text.toString())
                Toast.makeText(this, R.string.bookmark_added, Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.action_cancel, null)
            .show()
    }

    private fun sharePdf() {
        intent.data?.let { uri ->
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_pdf)))
        }
    }

    private fun shareText(text: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_pdf)))
    }

    private fun showProperties() {
        intent.data?.let { uri ->
            val name = FileUtils.getFileName(this, uri) ?: "Unknown"
            val size = FileUtils.getFileSize(this, uri)
            val info = buildString {
                appendLine("Name: $name")
                appendLine("Size: ${FileUtils.formatFileSize(size)}")
                append("Pages: $totalPages")
            }
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.action_properties)
                .setMessage(info)
                .setPositiveButton(R.string.action_ok, null)
                .show()
        }
    }

    private fun showMoreOptions() {
        val bottomSheet = BottomSheetDialog(this)
        val sheetView = layoutInflater.inflate(R.layout.bottom_sheet_viewer_options, null)
        bottomSheet.setContentView(sheetView)
        bottomSheet.show()
    }

    override fun onPause() {
        super.onPause()
        binding.pdfView.pauseRendering()
    }

    override fun onResume() {
        super.onResume()
        binding.pdfView.resumeRendering()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.pdfView.recycle()
    }

    enum class AnnotationTool {
        HIGHLIGHT, DRAW, TEXT, ERASER
    }
}
