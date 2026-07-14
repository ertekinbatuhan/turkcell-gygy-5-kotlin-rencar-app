package com.flowbytestudio.rencar.ui.common

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File

// Sistem kamerasıyla çekim yaptırıp sonucu dosya olarak veren yardımcı.
// Manifest CAMERA iznini bildirdiğinden ACTION_IMAGE_CAPTURE runtime izni ister;
// çekimden önce izin kontrol edilip gerekiyorsa istenir.
class CameraCaptureState internal constructor(
    private val onLaunch: () -> Unit,
) {
    fun capture() = onLaunch()
}

@Composable
fun rememberCameraCapture(onCaptured: (File) -> Unit): CameraCaptureState {
    val context = LocalContext.current
    val currentOnCaptured by rememberUpdatedState(onCaptured)
    var pendingFile by remember { mutableStateOf<File?>(null) }
    var pendingUri by remember { mutableStateOf<Uri?>(null) }

    val takePicture = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture(),
    ) { success ->
        val file = pendingFile
        pendingFile = null
        pendingUri = null
        if (success && file != null && file.length() > 0) {
            currentOnCaptured(file)
        } else {
            file?.delete()
        }
    }

    val requestPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        val uri = pendingUri
        if (granted && uri != null) {
            takePicture.launch(uri)
        } else {
            pendingFile?.delete()
            pendingFile = null
            pendingUri = null
        }
    }

    return remember(context) {
        CameraCaptureState {
            val dir = File(context.cacheDir, "captures").apply { mkdirs() }
            val file = File.createTempFile("capture_", ".jpg", dir)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file,
            )
            pendingFile = file
            pendingUri = uri
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA,
            ) == PackageManager.PERMISSION_GRANTED
            if (granted) {
                takePicture.launch(uri)
            } else {
                requestPermission.launch(Manifest.permission.CAMERA)
            }
        }
    }
}
