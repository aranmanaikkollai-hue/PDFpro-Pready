package com.propdf.editor.ui.scanner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.propdf.editor.R
import com.propdf.editor.util.FileUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class DocumentScannerActivity : AppCompatActivity() {

    private lateinit var viewFinder: PreviewView
    private lateinit var btnCapture: MaterialButton
    private lateinit var btnAuto: MaterialButton
    private lateinit var progressBar: CircularProgressIndicator
    private lateinit var overlay: View

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private var isAutoMode = false

    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) startCamera() else finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_scanner)

        viewFinder = findViewById(R.id.previewView)
        btnCapture = findViewById(R.id.btnCapture)
        btnAuto = findViewById(R.id.btnAuto)
        progressBar = findViewById(R.id.progressBar)
        overlay = findViewById(R.id.scanOverlay)

        cameraExecutor = Executors.newSingleThreadExecutor()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            requestPermission.launch(Manifest.permission.CAMERA)
        }

        btnCapture.setOnClickListener { takePhoto() }
        btnAuto.setOnClickListener {
            isAutoMode = !isAutoMode
            btnAuto.text = if (isAutoMode) "Auto: ON" else "Auto: OFF"
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .build()
                .also { it.setSurfaceProvider(viewFinder.surfaceProvider) }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture)
            } catch (e: Exception) {
                Log.e("Scanner", "Camera binding failed", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        progressBar.visibility = View.VISIBLE

        val outFile = FileUtils.createTempPdf(this)

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    lifecycleScope.launch {
                        processImage(image)
                        image.close()
                    }
                }
                override fun onError(exception: ImageCaptureException) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@DocumentScannerActivity, "Capture failed", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private suspend fun processImage(image: ImageProxy) = withContext(Dispatchers.Default) {
        val bitmap = image.toBitmap()
        // TODO: Add document edge detection here (OpenCV or ML Kit)
        // For now, save the captured image directly

        withContext(Dispatchers.Main) {
            progressBar.visibility = View.GONE
            Toast.makeText(this@DocumentScannerActivity, "Document captured", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
