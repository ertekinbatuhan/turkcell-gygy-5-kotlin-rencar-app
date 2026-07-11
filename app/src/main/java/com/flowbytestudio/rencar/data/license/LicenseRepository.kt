package com.flowbytestudio.rencar.data.license

import android.content.Context
import android.net.Uri
import com.flowbytestudio.rencar.data.network.NetworkModule
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class LicenseRepository(
    private val api: LicenseApi = NetworkModule.licenseApi,
) {

    suspend fun upload(
        context: Context,
        frontUri: Uri,
        backUri: Uri,
    ): Result<LicenseResponse> = runCatching {
        val frontPart = createImagePart(context, frontUri, "front")
        val backPart = createImagePart(context, backUri, "back")
        api.upload(front = frontPart, back = backPart)
    }

    private fun createImagePart(context: Context, uri: Uri, partName: String): MultipartBody.Part {
        val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
        val extension = if (mimeType == "image/png") "png" else "jpg"
        val tempFile = File.createTempFile(partName, ".$extension", context.cacheDir)
        context.contentResolver.openInputStream(uri)?.use { input ->
            tempFile.outputStream().use { output -> input.copyTo(output) }
        }
        val requestBody = tempFile.asRequestBody(mimeType.toMediaType())
        return MultipartBody.Part.createFormData(partName, tempFile.name, requestBody)
    }

    suspend fun getStatus(): Result<LicenseStatusResponse> = runCatching {
        api.getStatus()
    }
}
