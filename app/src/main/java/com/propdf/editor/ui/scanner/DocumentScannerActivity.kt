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

    private var btnCapture: View? = null
    private var btnSave: View? = null
    private var btnClear: View? = null
    private var btnAuto: View? = null
    private var btnColor: View? = null
    private var btnGrayscale: View? = null
    private var btnBlackWhite: View? = null
    private var rvPages: RecyclerView? = null
    private var tvPageCount: TextView? = null
    private var progressBar: ProgressBar? = null
    private var previewView: PreviewView? = null

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
        btnCapture?.setOnClickListener { }
        btnSave?.setOnClickListener {
            val uri = Uri.fromFile(File(outputDir, "scan_${System.currentTimeMillis()}.pdf"))
        }
        btnClear?.setOnClickListener { rvPages?.adapter = null; tvPageCount?.text = "0" }
        btnAuto?.setOnClickListener { }
        btnColor?.setOnClickListener { }
        btnGrayscale?.setOnClickListener { }
        btnBlackWhite?.setOnClickListener { }
    }
}
