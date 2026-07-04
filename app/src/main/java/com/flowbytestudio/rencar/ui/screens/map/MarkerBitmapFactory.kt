package com.flowbytestudio.rencar.ui.screens.map

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.text.TextPaint

object MarkerBitmapFactory {

    fun create(text: String, backgroundColor: Int, density: Float): Bitmap {
        val textPaint = TextPaint().apply {
            isAntiAlias = true
            color = Color.WHITE
            textSize = 13f * density
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val horizontalPadding = 14f * density
        val verticalPadding = 8f * density
        val tailHeight = 7f * density

        val textWidth = textPaint.measureText(text)
        val bubbleWidth = textWidth + horizontalPadding * 2
        val bubbleHeight = (textPaint.descent() - textPaint.ascent()) + verticalPadding * 2

        val width = bubbleWidth.toInt().coerceAtLeast(1)
        val height = (bubbleHeight + tailHeight).toInt().coerceAtLeast(1)

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val bgPaint = Paint().apply {
            isAntiAlias = true
            color = backgroundColor
        }

        val bubbleRect = RectF(0f, 0f, bubbleWidth, bubbleHeight)
        val cornerRadius = bubbleHeight / 2f
        canvas.drawRoundRect(bubbleRect, cornerRadius, cornerRadius, bgPaint)

        val centerX = bubbleWidth / 2f
        val tailPath = Path().apply {
            moveTo(centerX - tailHeight, bubbleHeight - 1f)
            lineTo(centerX + tailHeight, bubbleHeight - 1f)
            lineTo(centerX, bubbleHeight + tailHeight)
            close()
        }
        canvas.drawPath(tailPath, bgPaint)

        val textY = verticalPadding - textPaint.ascent()
        canvas.drawText(text, horizontalPadding, textY, textPaint)

        return bitmap
    }
}
