package com.propdf.editor.data.local.db

import androidx.room.TypeConverter
import com.propdf.editor.domain.model.AnnotationType

class Converters {
    @TypeConverter
    fun fromAnnotationType(value: AnnotationType): String = value.name

    @TypeConverter
    fun toAnnotationType(value: String): AnnotationType =
        try { AnnotationType.valueOf(value) } catch (_: Exception) { AnnotationType.HIGHLIGHT }
}
