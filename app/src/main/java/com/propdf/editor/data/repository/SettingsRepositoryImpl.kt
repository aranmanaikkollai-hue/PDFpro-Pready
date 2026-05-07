package com.propdf.editor.data.repository

import android.content.Context
import android.graphics.Color
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.propdf.editor.domain.model.DevicePerformanceProfile
import com.propdf.editor.domain.repository.SettingsRepository
import com.propdf.editor.utils.DeviceCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "propdf_settings")

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    private val dataStore = context.dataStore

    private object PreferencesKeys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val LOW_MEMORY_MODE = booleanPreferencesKey("low_memory_mode")
        val DEFAULT_ANNOTATION_COLOR = intPreferencesKey("default_annotation_color")
        val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
    }

    override suspend fun isDarkModeEnabled(): Boolean {
        return dataStore.data.map { it[PreferencesKeys.DARK_MODE] ?: true }.first()
    }

    override suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { it[PreferencesKeys.DARK_MODE] = enabled }
    }

    override suspend fun isLowMemoryModeEnabled(): Boolean {
        return dataStore.data.map { it[PreferencesKeys.LOW_MEMORY_MODE] ?: DeviceCapabilities.isLowRamDevice }.first()
    }

    override suspend fun setLowMemoryMode(enabled: Boolean) {
        dataStore.edit { it[PreferencesKeys.LOW_MEMORY_MODE] = enabled }
    }

    override suspend fun getDefaultAnnotationColor(): Int {
        return dataStore.data.map { it[PreferencesKeys.DEFAULT_ANNOTATION_COLOR] ?: Color.RED }.first()
    }

    override suspend fun setDefaultAnnotationColor(color: Int) {
        dataStore.edit { it[PreferencesKeys.DEFAULT_ANNOTATION_COLOR] = color }
    }

    override suspend fun getPerformanceProfile(): DevicePerformanceProfile {
        val lowMemoryMode = isLowMemoryModeEnabled()
        val isLowEnd = lowMemoryMode || DeviceCapabilities.isLowRamDevice

        return DevicePerformanceProfile(
            isLowEnd = isLowEnd,
            renderQuality = if (isLowEnd) 150 else 300,
            enableAnimations = !isLowEnd && DeviceCapabilities.shouldEnableAnimations(),
            useCompose = DeviceCapabilities.shouldEnableCompose(),
            maxConcurrentOperations = if (isLowEnd) 1 else DeviceCapabilities.getOptimalThreadPoolSize()
        )
    }
}
