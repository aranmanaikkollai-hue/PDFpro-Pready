package com.propdf.editor.ui.viewer.annotation

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class AnnotationOverlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    enum class Tool { NONE, HIGHLIGHT, PEN, TEXT }

    private var currentTool = Tool.NONE
    private val paths = mutableListOf<Pair<Path, Paint>>()
    private var currentPath: Path? = null
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 8f
        color = Color.YELLOW
        alpha = 120
    }

    fun setTool(tool: Tool) {
        currentTool = tool
        paint.color = when (tool) {
            Tool.HIGHLIGHT -> Color.YELLOW
            Tool.PEN -> Color.RED
            Tool.TEXT -> Color.BLUE
            else -> Color.TRANSPARENT
        }
        paint.alpha = if (tool == Tool.HIGHLIGHT) 120 else 255
    }

    fun clear() {
        paths.clear()
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (currentTool == Tool.NONE) return false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                currentPath = Path().apply { moveTo(event.x, event.y) }
            }
            MotionEvent.ACTION_MOVE -> {
                currentPath?.lineTo(event.x, event.y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                currentPath?.let { paths.add(it to Paint(paint)) }
                currentPath = null
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paths.forEach { canvas.drawPath(it.first, it.second) }
        currentPath?.let { canvas.drawPath(it, paint) }
    }
}
