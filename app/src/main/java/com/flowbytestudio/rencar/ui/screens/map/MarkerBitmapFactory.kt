package com.flowbytestudio.rencar.ui.screens.map

import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.text.TextPaint
import androidx.core.graphics.ColorUtils

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

    fun createClusterBubble(count: Int, backgroundColor: Int, density: Float): Bitmap {
        val text = count.toString()
        val textPaint = TextPaint().apply {
            isAntiAlias = true
            color = Color.WHITE
            textSize = 14f * density
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val strokeWidth = 2.5f * density
        val tailHeight = 9f * density
        val tailHalfWidth = 8f * density
        val shadowRadius = 4f * density
        val shadowOffset = 2f * density

        val diameter = (38f * density).coerceAtLeast(textPaint.measureText(text) + 22f * density)
        val circleSize = diameter.toInt().coerceAtLeast(1)
        val padding = (shadowRadius + shadowOffset).toInt()
        val width = circleSize + padding * 2
        val height = circleSize + tailHeight.toInt() + padding * 2

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val radius = circleSize / 2f
        val centerX = width / 2f
        val centerY = padding + radius

        val lighterColor = ColorUtils.blendARGB(backgroundColor, Color.WHITE, 0.25f)
        val darkerColor = ColorUtils.blendARGB(backgroundColor, Color.BLACK, 0.2f)
        val gradient = LinearGradient(
            centerX,
            centerY - radius,
            centerX,
            centerY + radius,
            lighterColor,
            darkerColor,
            Shader.TileMode.CLAMP,
        )

        val shadowPaint = Paint().apply {
            isAntiAlias = true
            color = ColorUtils.setAlphaComponent(Color.BLACK, 70)
            maskFilter = BlurMaskFilter(shadowRadius, BlurMaskFilter.Blur.NORMAL)
        }
        val bgPaint = Paint().apply {
            isAntiAlias = true
            shader = gradient
        }
        val strokePaint = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            style = Paint.Style.STROKE
            this.strokeWidth = strokeWidth
        }

        val tailPath = Path().apply {
            moveTo(centerX - tailHalfWidth, centerY + radius - strokeWidth)
            lineTo(centerX + tailHalfWidth, centerY + radius - strokeWidth)
            lineTo(centerX, (height - padding).toFloat())
            close()
        }

        canvas.drawCircle(centerX, centerY + shadowOffset, radius - strokeWidth / 2f, shadowPaint)
        canvas.drawPath(tailPath, bgPaint)
        canvas.drawCircle(centerX, centerY, radius - strokeWidth / 2f, bgPaint)
        canvas.drawCircle(centerX, centerY, radius - strokeWidth / 2f, strokePaint)

        val textX = centerX - textPaint.measureText(text) / 2f
        val textY = centerY - (textPaint.ascent() + textPaint.descent()) / 2f
        canvas.drawText(text, textX, textY, textPaint)

        return bitmap
    }
}
