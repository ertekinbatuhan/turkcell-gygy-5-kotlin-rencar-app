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
        selfieUri: Uri,
    ): Result<LicenseResponse> = runCatching {
        val frontPart = createImagePart(context, frontUri, "front")
        val backPart = createImagePart(context, backUri, "back")
        val selfiePart = createImagePart(context, selfieUri, "selfie")
        api.upload(front = frontPart, back = backPart, selfie = selfiePart)
    }

    private fun createImagePart(context: Context, uri: Uri, partName: String): MultipartBody.Part {
        val isPng = context.contentResolver.getType(uri) == "image/png"
        val mimeType = if (isPng) "image/png" else "image/jpeg"
        val extension = if (isPng) "png" else "jpg"
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
