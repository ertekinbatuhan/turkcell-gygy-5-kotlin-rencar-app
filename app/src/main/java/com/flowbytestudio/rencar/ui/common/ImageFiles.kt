package com.flowbytestudio.rencar.ui.common

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import java.io.File

object ImageFiles {

    // Sunucu 5MB üstünü 413 ile reddediyor; kamera çıktısını yüklemeden önce
    // küçültüp JPEG'e sıkıştırır. EXIF yönü bitmap'e işlenir (küçültme EXIF'i düşürür).
    fun compressForUpload(source: File, maxDimension: Int = 1600, quality: Int = 85): File {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(source.absolutePath, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return source

        var sampleSize = 1
        while (
            bounds.outWidth / (sampleSize * 2) >= maxDimension ||
            bounds.outHeight / (sampleSize * 2) >= maxDimension
        ) {
            sampleSize *= 2
        }

        val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        val decoded = BitmapFactory.decodeFile(source.absolutePath, options) ?: return source
        val oriented = applyExifRotation(source, decoded)

        val target = File(source.parentFile, "${source.nameWithoutExtension}_upload.jpg")
        target.outputStream().use { stream ->
            oriented.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        }
        if (oriented !== decoded) decoded.recycle()
        oriented.recycle()
        return target
    }

    private fun applyExifRotation(source: File, bitmap: Bitmap): Bitmap {
        val orientation = runCatching {
            ExifInterface(source.absolutePath)
                .getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        }.getOrDefault(ExifInterface.ORIENTATION_NORMAL)

        val degrees = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> return bitmap
        }
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}
