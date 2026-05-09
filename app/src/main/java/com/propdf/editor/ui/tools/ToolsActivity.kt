package com.propdf.editor.ui.tools

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.propdf.editor.R
import com.propdf.editor.util.FileUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ToolsActivity : AppCompatActivity() {

    private val viewModel: ToolsViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView

    private var pendingOperation: ((Uri, Uri) -> Unit)? = null

    private val pickPdf = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            val out = FileUtils.createOutputUri(this, "output_${System.currentTimeMillis()}.pdf")
            pendingOperation?.invoke(it, out)
            pendingOperation = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tools)

        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }
        recyclerView = findViewById(R.id.recyclerView)

        val tools = listOf(
            ToolItem(R.drawable.ic_color, "Merge PDFs") { mergePdfs() },
            ToolItem(R.drawable.ic_grayscale, "Split PDF") { pickPdf { src, out -> viewModel.splitPdf(src, listOf(0), out) } },
            ToolItem(R.drawable.ic_black_white, "Compress") { pickPdf { src, out -> viewModel.compressPdf(src, out) } },
            ToolItem(R.drawable.ic_back, "Rotate") { pickPdf { src, out -> viewModel.rotatePages(src, listOf(0), 90, out) } },
            ToolItem(R.drawable.ic_camera, "Delete Pages") { pickPdf { src, out -> viewModel.deletePages(src, listOf(0), out) } },
            ToolItem(R.drawable.bg_pdf_icon, "Watermark") { pickPdf { src, out -> viewModel.addWatermark(src, "ProPDF", out) } },
            ToolItem(R.drawable.ic_camera, "Encrypt") { pickPdf { src, out -> viewModel.encryptPdf(src, "password", out) } },
            ToolItem(R.drawable.ic_color, "Images to PDF") { /* TODO: pick images */ },
            ToolItem(R.drawable.ic_grayscale, "PDF to Images") { pickPdf { src, out -> viewModel.pdfToImages(src, out) } },
            ToolItem(R.drawable.ic_back, "Extract Text") { pickPdf { src, _ -> viewModel.extractText(src) } }
        )

        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = ToolAdapter(tools) { it.action() }

        observeViewModel()
    }

    private fun mergePdfs() {
        // Simplified: pick two PDFs
        pickPdf.launch(arrayOf("application/pdf"))
    }

    private fun pickPdf(action: (Uri, Uri) -> Unit) {
        pendingOperation = action
        pickPdf.launch(arrayOf("application/pdf"))
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.result.collect { it?.let { Toast.makeText(this@ToolsActivity, it, Toast.LENGTH_SHORT).show() } }
                }
                launch {
                    viewModel.error.collect { it?.let { Toast.makeText(this@ToolsActivity, "Error: $it", Toast.LENGTH_LONG).show() } }
                }
            }
        }
    }

    data class ToolItem(val icon: Int, val title: String, val action: () -> Unit)
}
