package com.propdf.editor.data.repository

import com.propdf.editor.data.local.prefs.SettingsDataStore
import com.propdf.editor.domain.model.DevicePerformanceProfile
import com.propdf.editor.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: SettingsDataStore
) : SettingsRepository {
    override fun isDarkModeEnabled(): Flow<Boolean> = dataStore.darkMode
    override suspend fun setDarkMode(enabled: Boolean) {
        dataStore.setDarkMode(enabled)
    }

    override fun isLowMemoryModeEnabled(): Flow<Boolean> = dataStore.lowMemoryMode
    override suspend fun setLowMemoryMode(enabled: Boolean) {
        dataStore.setLowMemoryMode(enabled)
    }

    override fun getDefaultAnnotationColor(): Flow<Int> = dataStore.annotationColor
    override suspend fun setDefaultAnnotationColor(color: Int) {
        dataStore.setAnnotationColor(color)
    }

    override fun getPerformanceProfile(): Flow<DevicePerformanceProfile> = dataStore.performanceProfile
}
