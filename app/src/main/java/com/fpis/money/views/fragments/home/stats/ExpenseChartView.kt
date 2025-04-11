package com.fpis.money.views.fragments.home.stats

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
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
        val color: Int
    )

    data class ExpenseCategoryInput(
        val name: String,
        val amount: Float,
        val color: String // hex string like "#FF9366"
    )

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
    }
    private val rectF = RectF()
    private val categories = mutableListOf<ExpenseCategory>()
    private var totalAmount = 0f
    private var centerX = 0f
    private var centerY = 0f
    private var radius = 0f
    private val strokeWidth = 30f
    private val gapAngle = 5f // Gap between segments in degrees
    private var expenseText = "Expense"
    private var amountText = "₸0"

    init {
        // Initialize paint for the arcs
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
        paint.strokeCap = Paint.Cap.ROUND

        // Initialize text paint for the center text
        textPaint.textSize = 16f * resources.displayMetrics.density
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    fun setData(categories: List<ExpenseCategoryInput>) {
        clearCategories()
        for (category in categories) {
            val colorInt = try {
                Color.parseColor(category.color)
            } catch (e: IllegalArgumentException) {
                Color.GRAY // fallback if color string is bad
            }
            addCategory(category.name, category.amount, colorInt)
        }

        // Update the amount text based on total
        updateAmountText()
    }

    fun setCenterText(expense: String, amount: String) {
        this.expenseText = expense
        this.amountText = amount
        invalidate()
    }

    private fun updateAmountText() {
        if (totalAmount > 0) {
            amountText = "₸${String.format("%,.2f", totalAmount)}"
        }
        invalidate()
    }

    fun addCategory(name: String, amount: Float, color: Int) {
        categories.add(ExpenseCategory(name, amount, color))
        totalAmount = categories.sumOf { it.amount.toDouble() }.toFloat()
        updateAmountText()
        invalidate()
    }

    fun clearCategories() {
        categories.clear()
        totalAmount = 0f
        amountText = "₸0"
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h / 2f
        radius = min(w, h) / 2f - strokeWidth

        // Set the rectangle for the arc
        rectF.set(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (categories.isEmpty() || totalAmount <= 0) {
            // Draw empty circle
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = strokeWidth
            paint.color = Color.parseColor("#1AFFFFFF")
            canvas.drawCircle(centerX, centerY, radius, paint)

            // Still draw the center text
            drawCenterText(canvas)
            return
        }

        // Draw arcs for each category
        var startAngle = -90f // Start from top
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
        paint.strokeCap = Paint.Cap.ROUND

        for (category in categories) {
            val sweepAngle = 360f * (category.amount / totalAmount)
            paint.color = category.color

            // Leave a gap between segments by reducing the sweep angle
            canvas.drawArc(rectF, startAngle, sweepAngle - gapAngle, false, paint)
            startAngle += sweepAngle
        }

        // Draw the center text
        drawCenterText(canvas)
    }

    private fun drawCenterText(canvas: Canvas) {
        // Draw "Expense" text
        textPaint.textSize = 16f * resources.displayMetrics.density
        canvas.drawText(expenseText, centerX, centerY - textPaint.textSize / 2, textPaint)

        // Draw amount text
        textPaint.textSize = 24f * resources.displayMetrics.density
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(amountText, centerX, centerY + textPaint.textSize / 2 + 8, textPaint)
    }
}