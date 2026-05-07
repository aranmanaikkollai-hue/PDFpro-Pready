package com.propdf.editor.ui.tools

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.propdf.editor.R
import com.propdf.editor.databinding.ActivityToolsBinding
import com.propdf.editor.utils.FileUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class ToolsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityToolsBinding
    private val viewModel: ToolsViewModel by viewModels()

    private var selectedFiles = mutableListOf<Uri>()
    private var currentOperation: PdfOperation? = null

    private val pdfPicker = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { selectedFiles.add(it) }
    }

    private val multiPdfPicker = registerForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        uris?.let { selectedFiles.addAll(it) }
    }

    private val imagePicker = registerForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        uris?.let { selectedFiles.addAll(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityToolsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.pdf_tools)
    }

    private fun setupClickListeners() {
        // Merge PDFs
        binding.cardMerge.setOnClickListener {
            currentOperation = PdfOperation.MERGE
            selectedFiles.clear()
            multiPdfPicker.launch(arrayOf("application/pdf"))
            showOperationDialog("Merge PDFs", "Select 2 or more PDFs to merge") {
                if (selectedFiles.size < 2) {
                    Toast.makeText(this, "Select at least 2 PDFs", Toast.LENGTH_SHORT).show()
                    return@showOperationDialog
                }
                val outputFile = File(cacheDir, FileUtils.generateUniqueFileName("merged"))
                viewModel.mergePdfs(selectedFiles, outputFile)
            }
        }

        // Split PDF
        binding.cardSplit.setOnClickListener {
            currentOperation = PdfOperation.SPLIT
            selectedFiles.clear()
            pdfPicker.launch(arrayOf("application/pdf"))
            showInputDialog("Split PDF", "Enter page ranges (e.g., 1-3,5,7-10)") { input ->
                val ranges = parsePageRanges(input)
                val outputDir = File(cacheDir, "split_output").apply { mkdirs() }
                viewModel.splitPdf(selectedFiles.first(), ranges, outputDir)
            }
        }

        // Compress PDF
        binding.cardCompress.setOnClickListener {
            currentOperation = PdfOperation.COMPRESS
            selectedFiles.clear()
            pdfPicker.launch(arrayOf("application/pdf"))
            showOperationDialog("Compress PDF", "Select a PDF to compress") {
                val outputFile = File(cacheDir, FileUtils.generateUniqueFileName("compressed"))
                viewModel.compressPdf(selectedFiles.first(), outputFile, 5)
            }
        }

        // Rotate Pages
        binding.cardRotate.setOnClickListener {
            currentOperation = PdfOperation.ROTATE
            selectedFiles.clear()
            pdfPicker.launch(arrayOf("application/pdf"))
            showRotateDialog()
        }

        // Delete Pages
        binding.cardDeletePages.setOnClickListener {
            currentOperation = PdfOperation.DELETE_PAGES
            selectedFiles.clear()
            pdfPicker.launch(arrayOf("application/pdf"))
            showInputDialog("Delete Pages", "Enter pages to delete (e.g., 2,4,6-8)") { input ->
                val pages = parsePageList(input)
                val outputFile = File(cacheDir, FileUtils.generateUniqueFileName("deleted"))
                viewModel.deletePages(selectedFiles.first(), outputFile, pages)
            }
        }

        // Add Watermark
        binding.cardWatermark.setOnClickListener {
            currentOperation = PdfOperation.WATERMARK
            selectedFiles.clear()
            pdfPicker.launch(arrayOf("application/pdf"))
            showInputDialog("Add Watermark", "Enter watermark text") { text ->
                val outputFile = File(cacheDir, FileUtils.generateUniqueFileName("watermarked"))
                viewModel.addWatermark(selectedFiles.first(), outputFile, text)
            }
        }

        // Encrypt PDF
        binding.cardEncrypt.setOnClickListener {
            currentOperation = PdfOperation.ENCRYPT
            selectedFiles.clear()
            pdfPicker.launch(arrayOf("application/pdf"))
            showPasswordDialog { password ->
                val outputFile = File(cacheDir, FileUtils.generateUniqueFileName("encrypted"))
                viewModel.encryptPdf(selectedFiles.first(), outputFile, password)
            }
        }

        // Images to PDF
        binding.cardImagesToPdf.setOnClickListener {
            currentOperation = PdfOperation.IMAGES_TO_PDF
            selectedFiles.clear()
            imagePicker.launch(arrayOf("image/*"))
            showOperationDialog("Images to PDF", "Select images to convert") {
                val outputFile = File(cacheDir, FileUtils.generateUniqueFileName("images"))
                viewModel.imagesToPdf(selectedFiles, outputFile)
            }
        }

        // PDF to Images
        binding.cardPdfToImages.setOnClickListener {
            currentOperation = PdfOperation.PDF_TO_IMAGES
            selectedFiles.clear()
            pdfPicker.launch(arrayOf("application/pdf"))
            showOperationDialog("PDF to Images", "Select a PDF to convert") {
                val outputDir = File(cacheDir, "pdf_images").apply { mkdirs() }
                viewModel.pdfToImages(selectedFiles.first(), outputDir)
            }
        }

        // Extract Text
        binding.cardExtractText.setOnClickListener {
            currentOperation = PdfOperation.EXTRACT_TEXT
            selectedFiles.clear()
            pdfPicker.launch(arrayOf("application/pdf"))
            showOperationDialog("Extract Text", "Select a PDF to extract text from") {
                viewModel.extractText(selectedFiles.first())
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isProcessing.collectLatest { isProcessing ->
                    binding.progressBar.visibility = if (isProcessing) View.VISIBLE else View.GONE
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.result.collectLatest { result ->
                    result?.let {
                        it.onSuccess { file ->
                            Toast.makeText(this@ToolsActivity, R.string.operation_success, Toast.LENGTH_SHORT).show()
                            shareFile(file)
                        }.onFailure { e ->
                            Toast.makeText(this@ToolsActivity, "${getString(R.string.operation_failed)}: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                        viewModel.clearResult()
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.extractedText.collectLatest { text ->
                    text?.let {
                        MaterialAlertDialogBuilder(this@ToolsActivity)
                            .setTitle(R.string.extract_text)
                            .setMessage(it.take(2000))
                            .setPositiveButton(R.string.action_share) { _, _ ->
                                shareText(it)
                            }
                            .setNegativeButton(R.string.action_ok, null)
                            .show()
                        viewModel.clearResult()
                    }
                }
            }
        }
    }

    private fun showOperationDialog(title: String, message: String, onConfirm: () -> Unit) {
        MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Process") { _, _ -> onConfirm() }
            .setNegativeButton(R.string.action_cancel, null)
            .show()
    }

    private fun showInputDialog(title: String, hint: String, onConfirm: (String) -> Unit) {
        val editText = android.widget.EditText(this).apply {
            this.hint = hint
        }
        MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setView(editText)
            .setPositiveButton(R.string.action_ok) { _, _ ->
                onConfirm(editText.text.toString())
            }
            .setNegativeButton(R.string.action_cancel, null)
            .show()
    }

    private fun showPasswordDialog(onConfirm: (String) -> Unit) {
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(48, 24, 48, 24)
        }
        val passwordInput = android.widget.EditText(this).apply {
            hint = "Enter password"
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        layout.addView(passwordInput)

        MaterialAlertDialogBuilder(this)
            .setTitle("Encrypt PDF")
            .setView(layout)
            .setPositiveButton(R.string.action_ok) { _, _ ->
                onConfirm(passwordInput.text.toString())
            }
            .setNegativeButton(R.string.action_cancel, null)
            .show()
    }

    private fun showRotateDialog() {
        val options = arrayOf("Rotate 90°", "Rotate 180°", "Rotate 270°")
        MaterialAlertDialogBuilder(this)
            .setTitle("Rotate Pages")
            .setItems(options) { _, which ->
                val degrees = when (which) {
                    0 -> 90
                    1 -> 180
                    else -> 270
                }
                showInputDialog("Pages", "Enter page numbers (e.g., 1,3,5) or leave blank for all") { input ->
                    val pages = if (input.isBlank()) {
                        (1..1000).toList()
                    } else {
                        parsePageList(input)
                    }
                    val outputFile = File(cacheDir, FileUtils.generateUniqueFileName("rotated"))
                    viewModel.rotatePages(selectedFiles.first(), outputFile, pages.associateWith { degrees })
                }
            }
            .show()
    }

    private fun parsePageRanges(input: String): List<IntRange> {
        val result = mutableListOf<IntRange>()
        input.split(",").forEach { part ->
            val trimmed = part.trim()
            if (trimmed.contains("-")) {
                val parts = trimmed.split("-").mapNotNull { it.trim().toIntOrNull() }
                if (parts.size == 2) {
                    result.add(parts[0]..parts[1])
                }
            } else {
                trimmed.toIntOrNull()?.let { result.add(it..it) }
            }
        }
        return result
    }

    private fun parsePageList(input: String): List<Int> {
        val result = mutableListOf<Int>()
        input.split(",").forEach { part ->
            val trimmed = part.trim()
            if (trimmed.contains("-")) {
                val parts = trimmed.split("-").mapNotNull { it.trim().toIntOrNull() }
                if (parts.size == 2) {
                    result.addAll(parts[0]..parts[1])
                }
            } else {
                trimmed.toIntOrNull()?.let { result.add(it) }
            }
        }
        return result
    }

    private fun shareFile(file: File) {
        val uri = FileUtils.getUriForFile(this, file)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_pdf)))
    }

    private fun shareText(text: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_pdf)))
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    enum class PdfOperation {
        MERGE, SPLIT, COMPRESS, ROTATE, DELETE_PAGES,
        WATERMARK, ENCRYPT, IMAGES_TO_PDF, PDF_TO_IMAGES, EXTRACT_TEXT
    }
}
