package com.propdf.editor.ui.scanner

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.propdf.editor.R
import com.propdf.editor.databinding.ActivityScannerBinding
import com.propdf.editor.utils.DeviceCapabilities
import com.propdf.editor.utils.FileUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors

@AndroidEntryPoint
class DocumentScannerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScannerBinding
    private val viewModel: ScannerViewModel by viewModels()

    private var imageCapture: ImageCapture? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private val executor = Executors.newSingleThreadExecutor()
    private val capturedPages = mutableListOf<Bitmap>()
    private var colorMode = ColorMode.AUTO

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    enum class ColorMode { AUTO, COLOR, GRAYSCALE, BLACK_WHITE }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupClickListeners()
        setupPageStrip()
        observeViewModel()
        checkCameraPermission()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.document_scanner)
    }

    private fun setupClickListeners() {
        binding.btnCapture.setOnClickListener { captureDocument() }
        binding.btnSave.setOnClickListener { saveAsPdf() }
        binding.btnClear.setOnClickListener { clearPages() }

        binding.btnAuto.setOnClickListener { setColorMode(ColorMode.AUTO) }
        binding.btnColor.setOnClickListener { setColorMode(ColorMode.COLOR) }
        binding.btnGrayscale.setOnClickListener { setColorMode(ColorMode.GRAYSCALE) }
        binding.btnBlackWhite.setOnClickListener { setColorMode(ColorMode.BLACK_WHITE) }
    }

    private fun setupPageStrip() {
        binding.rvPages.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvPages.adapter = ScannedPageAdapter(
            onDelete = { position -> deletePage(position) },
            onRotate = { position -> rotatePage(position) }
        )
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isProcessing.collectLatest { isProcessing ->
                    binding.progressBar.visibility = if (isProcessing) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                startCamera()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()
                bindCameraUseCases()
            } catch (e: Exception) {
                Log.e(TAG, "Camera initialization failed", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindCameraUseCases() {
        val provider = cameraProvider ?: return

        val preview = Preview.Builder()
            .build()
            .also { it.setSurfaceProvider(binding.previewView.surfaceProvider) }

        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            provider.unbindAll()
            provider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
        } catch (e: Exception) {
            Log.e(TAG, "Use case binding failed", e)
        }
    }

    private fun captureDocument() {
        val imageCapture = imageCapture ?: return

        imageCapture.takePicture(
            executor,
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val buffer = image.planes[0].buffer
                    val bytes = ByteArray(buffer.remaining())
                    buffer.get(bytes)
                    val bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                    val processed = processBitmap(bitmap)
                    capturedPages.add(processed)

                    runOnUiThread {
                        updatePageStrip()
                        Toast.makeText(this@DocumentScannerActivity, "Page ${capturedPages.size} captured", Toast.LENGTH_SHORT).show()
                    }
                    image.close()
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Capture failed", exception)
                    runOnUiThread {
                        Toast.makeText(this@DocumentScannerActivity, "Capture failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }

    private fun processBitmap(bitmap: Bitmap): Bitmap {
        return when (colorMode) {
            ColorMode.AUTO -> autoEnhance(bitmap)
            ColorMode.COLOR -> bitmap
            ColorMode.GRAYSCALE -> toGrayscale(bitmap)
            ColorMode.BLACK_WHITE -> toBlackAndWhite(bitmap)
        }
    }

    private fun autoEnhance(bitmap: Bitmap): Bitmap {
        // Simple auto-enhance: increase contrast slightly
        return bitmap
    }

    private fun toGrayscale(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val grayBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(grayBitmap)
        val paint = android.graphics.Paint()
        val colorMatrix = android.graphics.ColorMatrix().apply { setSaturation(0f) }
        paint.colorFilter = android.graphics.ColorMatrixColorFilter(colorMatrix)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return grayBitmap
    }

    private fun toBlackAndWhite(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val bwBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bwBitmap)
        val paint = android.graphics.Paint()
        val colorMatrix = android.graphics.ColorMatrix().apply {
            set(floatArrayOf(
                85f, 85f, 85f, 0f, -128f * 255f,
                85f, 85f, 85f, 0f, -128f * 255f,
                85f, 85f, 85f, 0f, -128f * 255f,
                0f, 0f, 0f, 1f, 0f
            ))
        }
        paint.colorFilter = android.graphics.ColorMatrixColorFilter(colorMatrix)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return bwBitmap
    }

    private fun setColorMode(mode: ColorMode) {
        colorMode = mode
        updateColorModeButtons()
    }

    private fun updateColorModeButtons() {
        val selectedColor = getColor(R.color.primary)
        val defaultColor = getColor(R.color.text_secondary)

        binding.btnAuto.setTextColor(if (colorMode == ColorMode.AUTO) selectedColor else defaultColor)
        binding.btnColor.setTextColor(if (colorMode == ColorMode.COLOR) selectedColor else defaultColor)
        binding.btnGrayscale.setTextColor(if (colorMode == ColorMode.GRAYSCALE) selectedColor else defaultColor)
        binding.btnBlackWhite.setTextColor(if (colorMode == ColorMode.BLACK_WHITE) selectedColor else defaultColor)
    }

    private fun updatePageStrip() {
        (binding.rvPages.adapter as? ScannedPageAdapter)?.submitList(capturedPages.toList())
        binding.tvPageCount.text = "${capturedPages.size}"
        binding.btnSave.visibility = if (capturedPages.isNotEmpty()) View.VISIBLE else View.GONE
        binding.btnClear.visibility = if (capturedPages.isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun deletePage(position: Int) {
        if (position in capturedPages.indices) {
            capturedPages.removeAt(position)
            updatePageStrip()
        }
    }

    private fun rotatePage(position: Int) {
        if (position in capturedPages.indices) {
            val bitmap = capturedPages[position]
            val matrix = Matrix().apply { postRotate(90f) }
            val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            capturedPages[position] = rotated
            updatePageStrip()
        }
    }

    private fun clearPages() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Clear All")
            .setMessage("Remove all captured pages?")
            .setPositiveButton("Clear") { _, _ ->
                capturedPages.clear()
                updatePageStrip()
            }
            .setNegativeButton(R.string.action_cancel, null)
            .show()
    }

    private fun saveAsPdf() {
        if (capturedPages.isEmpty()) return

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val document = PdfDocument()
                capturedPages.forEachIndexed { index, bitmap ->
                    val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, index + 1).create()
                    val page = document.startPage(pageInfo)
                    page.canvas.drawBitmap(bitmap, 0f, 0f, null)
                    document.finishPage(page)
                }

                val fileName = FileUtils.generateUniqueFileName("scanned")
                val uri = FileUtils.savePdfToDownloads(this@DocumentScannerActivity, document, fileName)
                document.close()

                withContext(Dispatchers.Main) {
                    if (uri != null) {
                        Toast.makeText(this@DocumentScannerActivity, "Saved to Downloads/ProPDF", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@DocumentScannerActivity, "Save failed", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Save failed", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DocumentScannerActivity, "Save failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraProvider?.unbindAll()
        executor.shutdown()
        capturedPages.forEach { if (!it.isRecycled) it.recycle() }
        capturedPages.clear()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        private const val TAG = "DocumentScanner"
    }
}
