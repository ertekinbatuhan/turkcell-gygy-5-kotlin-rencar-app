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

    // Meşgul (RENTED/RESERVED) araçların gri "devre dışı" marker'ı için sabit renkler.
    private val DISABLED_BG = Color.parseColor("#9AA3AF")
    private val DISABLED_TEXT = Color.parseColor("#F3F4F6")

    fun create(text: String, backgroundColor: Int, density: Float, disabled: Boolean = false): Bitmap {
        val fillColor = if (disabled) DISABLED_BG else backgroundColor
        val textPaint = TextPaint().apply {
            isAntiAlias = true
            color = if (disabled) DISABLED_TEXT else Color.WHITE
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
            color = fillColor
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

    // Aktif kiralama haritasındaki canlı konum için ikon: splash ekranıyla aynı
    // Material "DirectionsCar" silueti, marka mavisi daire halka üzerinde beyaz.
    // Path verisi ic_splash_car.xml ile birebir aynıdır (24x24 viewport).
    fun createVehicleIcon(backgroundColor: Int, density: Float): Bitmap {
        val diameter = (34f * density).toInt().coerceAtLeast(1)
        val strokeWidth = 3f * density
        val shadowRadius = 4f * density
        val padding = shadowRadius.toInt() + 2
        val size = diameter + padding * 2

        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val center = size / 2f
        val radius = diameter / 2f

        val shadowPaint = Paint().apply {
            isAntiAlias = true
            color = ColorUtils.setAlphaComponent(Color.BLACK, 60)
            maskFilter = BlurMaskFilter(shadowRadius, BlurMaskFilter.Blur.NORMAL)
        }
        canvas.drawCircle(center, center + strokeWidth / 2f, radius - strokeWidth / 2f, shadowPaint)

        val bgPaint = Paint().apply {
            isAntiAlias = true
            color = backgroundColor
        }
        canvas.drawCircle(center, center, radius - strokeWidth / 2f, bgPaint)

        val strokePaint = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            style = Paint.Style.STROKE
            this.strokeWidth = strokeWidth
        }
        canvas.drawCircle(center, center, radius - strokeWidth / 2f, strokePaint)

        val carPaint = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
        }
        val iconSize = diameter * 0.56f
        val iconLeft = center - iconSize / 2f
        val iconTop = center - iconSize / 2f
        val scale = iconSize / 24f
        canvas.save()
        canvas.translate(iconLeft, iconTop)
        canvas.scale(scale, scale)
        canvas.drawPath(directionsCarPath(), carPaint)
        canvas.restore()

        return bitmap
    }

    // Material Icons "DirectionsCar" (Outlined) — 24x24 viewport, aynı SVG path
    // res/drawable/ic_splash_car.xml içindekiyle birebir aynı.
    private fun directionsCarPath(): Path = Path().apply {
        moveTo(18.92f, 6.01f)
        cubicTo(18.72f, 5.42f, 18.16f, 5f, 17.5f, 5f)
        lineTo(6.5f, 5f)
        cubicTo(5.84f, 5f, 5.29f, 5.42f, 5.08f, 6.01f)
        lineTo(3f, 12f)
        lineTo(3f, 20f)
        cubicTo(3f, 20.55f, 3.45f, 21f, 4f, 21f)
        lineTo(5f, 21f)
        cubicTo(5.55f, 21f, 6f, 20.55f, 6f, 20f)
        lineTo(6f, 19f)
        lineTo(18f, 19f)
        lineTo(18f, 20f)
        cubicTo(18f, 20.55f, 18.45f, 21f, 19f, 21f)
        lineTo(20f, 21f)
        cubicTo(20.55f, 21f, 21f, 20.55f, 21f, 20f)
        lineTo(21f, 12f)
        lineTo(18.92f, 6.01f)
        close()
        moveTo(6.5f, 16f)
        cubicTo(5.67f, 16f, 5f, 15.33f, 5f, 14.5f)
        cubicTo(5f, 13.67f, 5.67f, 13f, 6.5f, 13f)
        cubicTo(7.33f, 13f, 8f, 13.67f, 8f, 14.5f)
        cubicTo(8f, 15.33f, 7.33f, 16f, 6.5f, 16f)
        close()
        moveTo(17.5f, 16f)
        cubicTo(16.67f, 16f, 16f, 15.33f, 16f, 14.5f)
        cubicTo(16f, 13.67f, 16.67f, 13f, 17.5f, 13f)
        cubicTo(18.33f, 13f, 19f, 13.67f, 19f, 14.5f)
        cubicTo(19f, 15.33f, 18.33f, 16f, 17.5f, 16f)
        close()
        moveTo(5f, 11f)
        lineTo(6.5f, 6.5f)
        lineTo(17.5f, 6.5f)
        lineTo(19f, 11f)
        lineTo(5f, 11f)
        close()
    }
}
