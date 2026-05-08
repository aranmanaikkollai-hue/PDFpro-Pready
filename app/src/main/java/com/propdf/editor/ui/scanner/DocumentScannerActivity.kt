package com.propdf.editor.ui.scanner

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import androidx.recyclerview.widget.RecyclerView
import com.propdf.editor.R
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class DocumentScannerActivity : AppCompatActivity() {

    private lateinit var btnCapture: View
    private lateinit var btnSave: View
    private lateinit var btnClear: View
    private lateinit var btnAuto: View
    private lateinit var btnColor: View
    private lateinit var btnGrayscale: View
    private lateinit var btnBlackWhite: View
    private lateinit var rvPages: RecyclerView
    private lateinit var tvPageCount: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var previewView: PreviewView

    private val outputDir by lazy { File(cacheDir, "scans").apply { mkdirs() } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_scanner)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        btnCapture = findViewById(R.id.btnCapture)
        btnSave = findViewById(R.id.btnSave)
        btnClear = findViewById(R.id.btnClear)
        btnAuto = findViewById(R.id.btnAuto)
        btnColor = findViewById(R.id.btnColor)
        btnGrayscale = findViewById(R.id.btnGrayscale)
        btnBlackWhite = findViewById(R.id.btnBlackWhite)
        rvPages = findViewById(R.id.rvPages)
        tvPageCount = findViewById(R.id.tvPageCount)
        progressBar = findViewById(R.id.progressBar)
        previewView = findViewById(R.id.previewView)
    }

    private fun setupListeners() {
        btnCapture.setOnClickListener { captureDocument() }
        btnSave.setOnClickListener { saveDocument() }
        btnClear.setOnClickListener { clearPages() }
        btnAuto.setOnClickListener { applyFilter(ScanFilter.AUTO) }
        btnColor.setOnClickListener { applyFilter(ScanFilter.COLOR) }
        btnGrayscale.setOnClickListener { applyFilter(ScanFilter.GRAYSCALE) }
        btnBlackWhite.setOnClickListener { applyFilter(ScanFilter.BLACK_WHITE) }
    }

    private fun captureDocument() {
        progressBar.visibility = View.VISIBLE
        // TODO: Implement camera capture
    }

    private fun saveDocument() {
        val uri = Uri.fromFile(File(outputDir, "scan_${System.currentTimeMillis()}.pdf"))
        // TODO: Implement save logic using uri
    }

    private fun clearPages() {
        rvPages.adapter = null
        tvPageCount.text = "0"
    }

    private fun applyFilter(filter: ScanFilter) {
        // TODO: Implement filter
    }

    enum class ScanFilter {
        AUTO, COLOR, GRAYSCALE, BLACK_WHITE
    }
}
