package com.propdf.editor.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.propdf.editor.domain.model.DevicePerformanceProfile
import com.propdf.editor.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context
) : SettingsRepository {

    private val prefs: SharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    private val darkModeFlow = MutableStateFlow(prefs.getBoolean("dark_mode", false))
    private val lowMemoryFlow = MutableStateFlow(prefs.getBoolean("low_memory", false))
    private val annotationColorFlow = MutableStateFlow(prefs.getInt("annotation_color", -0x10000))
    private val performanceFlow = MutableStateFlow(
        try {
            DevicePerformanceProfile.valueOf(prefs.getString("performance", "HIGH") ?: "HIGH")
        } catch (_: Exception) { DevicePerformanceProfile.HIGH }
    )

    override fun isDarkModeEnabled(): Flow<Boolean> = darkModeFlow
    override suspend fun setDarkMode(enabled: Boolean) {
        prefs.edit { putBoolean("dark_mode", enabled) }
        darkModeFlow.value = enabled
    }

    override fun isLowMemoryModeEnabled(): Flow<Boolean> = lowMemoryFlow
    override suspend fun setLowMemoryMode(enabled: Boolean) {
        prefs.edit { putBoolean("low_memory", enabled) }
        lowMemoryFlow.value = enabled
    }

    override fun getDefaultAnnotationColor(): Flow<Int> = annotationColorFlow
    override suspend fun setDefaultAnnotationColor(color: Int) {
        prefs.edit { putInt("annotation_color", color) }
        annotationColorFlow.value = color
    }

    override fun getPerformanceProfile(): Flow<DevicePerformanceProfile> = performanceFlow
}
