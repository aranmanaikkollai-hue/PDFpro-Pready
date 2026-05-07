package com.propdf.editor.utils

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Process

object DeviceCapabilities {

    private var _isLowRamDevice: Boolean = false
    private var _totalRamMb: Long = 0
    private var _availableProcessors: Int = 1
    private var _is64Bit: Boolean = false

    val isLowRamDevice: Boolean get() = _isLowRamDevice
    val totalRamMb: Long get() = _totalRamMb
    val availableProcessors: Int get() = _availableProcessors
    val is64Bit: Boolean get() = _is64Bit

    fun initialize(context: Context) {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        // Check low RAM (API 19+)
        _isLowRamDevice = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activityManager.isLowRamDevice
        } else {
            // Fallback: detect based on total RAM
            val memoryInfo = ActivityManager.MemoryInfo()
            activityManager.getMemoryInfo(memoryInfo)
            memoryInfo.totalMem < 2L * 1024 * 1024 * 1024 // < 2GB
        }

        // Total RAM
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        _totalRamMb = memoryInfo.totalMem / (1024 * 1024)

        // CPU info
        _availableProcessors = Runtime.getRuntime().availableProcessors()
        _is64Bit = Build.SUPPORTED_64_BIT_ABIS?.isNotEmpty() == true
    }

    fun getOptimalBitmapPoolSize(): Int {
        return when {
            _isLowRamDevice -> 2
            _totalRamMb < 4096 -> 4
            else -> 8
        }
    }

    fun getOptimalPdfRenderQuality(): Int {
        return when {
            _isLowRamDevice -> 150 // Low DPI for low-end
            _totalRamMb < 4096 -> 200
            else -> 300
        }
    }

    fun getOptimalThreadPoolSize(): Int {
        return when {
            _isLowRamDevice -> 2
            else -> _availableProcessors.coerceAtMost(4)
        }
    }

    fun shouldEnableAnimations(): Boolean {
        return !_isLowRamDevice && _totalRamMb >= 3072
    }

    fun shouldEnableCompose(): Boolean {
        // Use Compose on API 24+ with sufficient RAM
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && _totalRamMb >= 2048
    }
}
