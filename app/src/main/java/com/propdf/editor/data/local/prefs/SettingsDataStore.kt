package com.propdf.editor.data.local.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.propdf.editor.domain.model.DevicePerformanceProfile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    val darkMode: Flow<Boolean> = dataStore.data.map { it[KEY_DARK_MODE] ?: false }
    val lowMemoryMode: Flow<Boolean> = dataStore.data.map { it[KEY_LOW_MEMORY] ?: false }
    val annotationColor: Flow<Int> = dataStore.data.map { it[KEY_ANNOTATION_COLOR] ?: -0x10000 }
    val performanceProfile: Flow<DevicePerformanceProfile> = dataStore.data.map {
        try {
            DevicePerformanceProfile.valueOf(it[KEY_PERFORMANCE] ?: "HIGH")
        } catch (_: Exception) { DevicePerformanceProfile.HIGH }
    }

    suspend fun setDarkMode(enabled: Boolean) = dataStore.edit { it[KEY_DARK_MODE] = enabled }
    suspend fun setLowMemoryMode(enabled: Boolean) = dataStore.edit { it[KEY_LOW_MEMORY] = enabled }
    suspend fun setAnnotationColor(color: Int) = dataStore.edit { it[KEY_ANNOTATION_COLOR] = color }
    suspend fun setPerformanceProfile(profile: DevicePerformanceProfile) = dataStore.edit {
        it[KEY_PERFORMANCE] = profile.name
    }

    companion object {
        private val KEY_DARK_MODE = booleanPreferencesKey("dark_mode")
        private val KEY_LOW_MEMORY = booleanPreferencesKey("low_memory")
        private val KEY_ANNOTATION_COLOR = intPreferencesKey("annotation_color")
        private val KEY_PERFORMANCE = stringPreferencesKey("performance_profile")
    }
}
