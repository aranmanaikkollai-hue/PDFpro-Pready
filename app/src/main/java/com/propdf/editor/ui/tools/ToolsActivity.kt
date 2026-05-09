package com.propdf.editor.ui.tools

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.propdf.editor.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class ToolsActivity : AppCompatActivity() {

    private val viewModel: ToolsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tools)

        observeViewModel()
        // TODO: Wire up your UI buttons to call the methods below
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isProcessing.collect { /* show/hide progress */ }
                }
                launch {
                    viewModel.result.collect { msg ->
                        msg?.let { Toast.makeText(this@ToolsActivity, it, Toast.LENGTH_SHORT).show() }
                    }
                }
                launch {
                    viewModel.error.collect { err ->
                        err?.let { Toast.makeText(this@ToolsActivity, "Error: $it", Toast.LENGTH_LONG).show() }
                    }
                }
                launch {
                    viewModel.extractedText.collect { text ->
                        text?.let { /* display extracted text */ }
                    }
                }
            }
        }
    }

    // Helper to convert File -> Uri
    private fun fileToUri(file: File): Uri = FileProvider.getUriForFile(
        this, "${packageName}.fileprovider", file
    )

    // Example wiring methods — call these from your buttons
    fun doMerge(file1: File, file2: File, out: File) {
        viewModel.mergePdfs(
            listOf(fileToUri(file1), fileToUri(file2)),
            fileToUri(out)
        )
    }

    fun doSplit(source: File, pageList: List<Int>, out: File) {
        viewModel.splitPdf(fileToUri(source), pageList, fileToUri(out))
    }

    fun doCompress(source: File, out: File) {
        viewModel.compressPdf(fileToUri(source), fileToUri(out))
    }

    fun doRotate(source: File, pages: List<Int>, degrees: Int, out: File) {
        viewModel.rotatePages(fileToUri(source), pages, degrees, fileToUri(out))
    }

    fun doDeletePages(source: File, pages: List<Int>, out: File) {
        viewModel.deletePages(fileToUri(source), pages, fileToUri(out))
    }

    fun doWatermark(source: File, text: String, out: File) {
        viewModel.addWatermark(fileToUri(source), text, fileToUri(out))
    }

    fun doEncrypt(source: File, password: String, out: File) {
        viewModel.encryptPdf(fileToUri(source), password, fileToUri(out))
    }

    fun doImagesToPdf(images: List<File>, out: File) {
        viewModel.imagesToPdf(images.map { fileToUri(it) }, fileToUri(out))
    }

    fun doPdfToImages(source: File, outDir: File) {
        viewModel.pdfToImages(fileToUri(source), fileToUri(outDir))
    }

    fun doExtractText(source: File) {
        viewModel.extractText(fileToUri(source))
    }
}
