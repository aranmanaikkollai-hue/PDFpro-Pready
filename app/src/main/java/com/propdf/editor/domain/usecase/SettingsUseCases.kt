package com.propdf.editor.domain.usecase

import com.propdf.editor.domain.model.DevicePerformanceProfile
import com.propdf.editor.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPerformanceProfileUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke(): Flow<DevicePerformanceProfile> = repository.getPerformanceProfile()
}
