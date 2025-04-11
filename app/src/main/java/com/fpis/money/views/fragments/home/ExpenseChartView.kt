package com.fpis.money.views.fragments.home

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class ExpenseChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    data class ExpenseCategory(
        val name: String,
        val amount: Float,
        val color: String
    )

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 40f
    }

    private val rect = RectF()
    private var categories = listOf<ExpenseCategory>()
    private var totalAmount = 0f
    private var centerX = 0f
    private var centerY = 0f
    private var radius = 0f
    private var strokeWidth = 30f

    init {
        paint.strokeWidth = strokeWidth
    }

    fun setData(categories: List<ExpenseCategory>) {
        this.categories = categories
        totalAmount = categories.sumOf { it.amount.toDouble() }.toFloat()
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h / 2f
        radius = (min(w, h) / 2f) - strokeWidth

        // Set the rectangle for the arc
        rect.set(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (categories.isEmpty() || totalAmount <= 0) return

        var startAngle = -90f // Start from the top

        // Draw each category arc with spacing between them
        categories.forEach { category ->
            val sweepAngle = (category.amount / totalAmount) * 360f

            // Leave a small gap between segments (5 degrees)
            val adjustedSweepAngle = sweepAngle - 5f

            paint.color = Color.parseColor(category.color)
            canvas.drawArc(rect, startAngle, adjustedSweepAngle, false, paint)

            // Move to the next segment position
            startAngle += sweepAngle
        }

        // Draw the "Expense" text in the center
        // This is handled by the layout XML with separate TextViews
    }
}