package com.propdf.editor.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionUtils {

    fun hasStoragePermission(context: Context): Boolean {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED
            }
            else -> {
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            }
        }
    }

    fun requestStoragePermission(activity: Activity, requestCode: Int) {
        val permission = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE ->
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                Manifest.permission.READ_MEDIA_IMAGES
            else -> Manifest.permission.READ_EXTERNAL_STORAGE
        }
        ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
    }

    fun hasCameraPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestCameraPermission(activity: Activity, requestCode: Int) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.CAMERA),
            requestCode
        )
    }

    fun shouldShowStorageRationale(activity: Activity): Boolean {
        val permission = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                Manifest.permission.READ_MEDIA_IMAGES
            else -> Manifest.permission.READ_EXTERNAL_STORAGE
        }
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }
}
