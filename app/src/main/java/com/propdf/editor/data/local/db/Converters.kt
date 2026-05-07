package com.propdf.editor.data.local.db

import androidx.room.TypeConverter
import com.propdf.editor.domain.model.AnnotationType

class Converters {
    @TypeConverter
    fun fromAnnotationType(type: AnnotationType): String {
        return type.name
    }

    @TypeConverter
    fun toAnnotationType(value: String): AnnotationType {
        return try {
            AnnotationType.valueOf(value)
        } catch (e: IllegalArgumentException) {
            AnnotationType.HIGHLIGHT
        }
    }
}
