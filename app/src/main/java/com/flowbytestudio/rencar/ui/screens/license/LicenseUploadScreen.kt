package com.flowbytestudio.rencar.ui.screens.license

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.flowbytestudio.rencar.R
import com.flowbytestudio.rencar.ui.theme.*
import java.io.File

@Composable
fun LicenseUploadScreen(
    canGoBack: Boolean,
    onFinished: () -> Unit,
    onSkip: () -> Unit,
    viewModel: LicenseUploadViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.isSubmitted) {
        if (uiState.isSubmitted) onFinished()
    }

    val isFirstStep = uiState.step == LicenseUploadStep.LICENSE
    val onBack: () -> Unit = {
        if (isFirstStep) onSkip() else viewModel.onBackToPreviousStep()
    }
    val showBackButton = canGoBack || !isFirstStep

    BackHandler(enabled = showBackButton) { onBack() }

    Surface(modifier = Modifier.fillMaxSize(), color = Background) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (showBackButton) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(13.dp))
                            .background(Surface)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_back),
                            tint = TextPrimary,
                            modifier = Modifier.size(Dimens.IconSizeM)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                }
                Column {
                    Text(
                        text = stringResource(R.string.license_title),
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = stringResource(R.string.license_subtitle),
                        fontSize = 12.5.sp,
                        color = TextSecondary
                    )
                }
            }

            StepProgressBar(step = uiState.step, modifier = Modifier.padding(horizontal = 22.dp, vertical = 10.dp))

            when (uiState.step) {
                LicenseUploadStep.LICENSE -> LicenseStepContent(
                    uiState = uiState,
                    context = context,
                    onFrontPicked = viewModel::onFrontSelected,
                    onBackPicked = viewModel::onBackSelected,
                    onNext = viewModel::onNextFromLicenseStep,
                    onSkip = onSkip,
                )
                LicenseUploadStep.SELFIE -> SelfieStepContent(
                    uiState = uiState,
                    context = context,
                    onSelfiePicked = viewModel::onSelfieSelected,
                    onNext = viewModel::onNextFromSelfieStep,
                    onSkip = onSkip,
                )
                LicenseUploadStep.CONFIRM -> ConfirmStepContent(
                    uiState = uiState,
                    onSubmit = { viewModel.onSubmit(context) },
                    onSkip = onSkip,
                )
            }
        }
    }
}

@Composable
private fun StepProgressBar(step: LicenseUploadStep, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        StepIndicator(index = 1, label = stringResource(R.string.license_step_license), isActive = step.ordinal >= 0, isCurrent = step == LicenseUploadStep.LICENSE)
        StepConnector()
        StepIndicator(index = 2, label = stringResource(R.string.license_selfie_label), isActive = step.ordinal >= 1, isCurrent = step == LicenseUploadStep.SELFIE)
        StepConnector()
        StepIndicator(index = 3, label = stringResource(R.string.license_step_confirm), isActive = step.ordinal >= 2, isCurrent = step == LicenseUploadStep.CONFIRM)
    }
}

@Composable
private fun RowScope.StepIndicator(index: Int, label: String, isActive: Boolean, isCurrent: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (isActive) Primary else BgLight),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = index.toString(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isActive) Color.White else TextSecondary,
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontSize = 11.5.sp,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.SemiBold,
            color = if (isActive) TextPrimary else TextSecondary,
        )
    }
}

@Composable
private fun RowScope.StepConnector() {
    Spacer(
        modifier = Modifier
            .weight(1f)
            .height(2.dp)
            .background(BorderColor)
    )
}

@Composable
private fun ColumnScope.LicenseStepContent(
    uiState: LicenseUploadUiState,
    context: Context,
    onFrontPicked: (Uri) -> Unit,
    onBackPicked: (Uri) -> Unit,
    onNext: () -> Unit,
    onSkip: () -> Unit,
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpaceS),
    ) {
        PhotoPickerCard(
            label = stringResource(R.string.license_front_side_label),
            uri = uiState.frontUri,
            imageUrl = null,
            context = context,
            onImagePicked = onFrontPicked,
        )
        PhotoPickerCard(
            label = stringResource(R.string.license_back_side_label),
            uri = uiState.backUri,
            imageUrl = null,
            context = context,
            onImagePicked = onBackPicked,
        )

        InfoBanner()

        if (uiState.error != null) {
            Text(text = stringResource(uiState.error), color = Danger, fontSize = 13.sp, modifier = Modifier.padding(top = Dimens.SpaceXxs))
        }
    }

    BottomActionArea(
        primaryText = stringResource(R.string.license_continue_button),
        primaryEnabled = uiState.frontUri != null && uiState.backUri != null,
        onPrimaryClick = onNext,
        secondaryText = stringResource(R.string.license_complete_later_button),
        onSecondaryClick = onSkip,
    )
}

@Composable
private fun ColumnScope.SelfieStepContent(
    uiState: LicenseUploadUiState,
    context: Context,
    onSelfiePicked: (Uri) -> Unit,
    onNext: () -> Unit,
    onSkip: () -> Unit,
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpaceS),
    ) {
        Text(
            text = stringResource(R.string.license_selfie_label),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
        )
        SelfiePickerAvatar(
            uri = uiState.selfieUri,
            imageUrl = uiState.selfieUrl,
            context = context,
            onImagePicked = onSelfiePicked,
            modifier = Modifier.fillMaxWidth(),
        )

        if (uiState.selfieUrl != null && uiState.selfieUri == null) {
            Text(
                text = stringResource(R.string.license_selfie_auto_used_hint),
                fontSize = 12.5.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        InfoBanner()

        if (uiState.error != null) {
            Text(text = stringResource(uiState.error), color = Danger, fontSize = 13.sp, modifier = Modifier.padding(top = Dimens.SpaceXxs))
        }
    }

    BottomActionArea(
        primaryText = stringResource(R.string.license_continue_button),
        primaryEnabled = uiState.hasSelfie,
        onPrimaryClick = onNext,
        secondaryText = stringResource(R.string.license_complete_later_button),
        onSecondaryClick = onSkip,
    )
}

@Composable
private fun ColumnScope.ConfirmStepContent(
    uiState: LicenseUploadUiState,
    onSubmit: () -> Unit,
    onSkip: () -> Unit,
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .padding(horizontal = Dimens.SpaceXl),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(SuccessLight),
            contentAlignment = Alignment.Center,
        ) {
            Icon(imageVector = Icons.Filled.Check, contentDescription = null, tint = Success, modifier = Modifier.size(34.dp))
        }
        Spacer(modifier = Modifier.height(Dimens.SpaceL))
        Text(
            text = stringResource(R.string.license_confirm_ready_title),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(Dimens.SpaceXs))
        Text(
            text = stringResource(R.string.license_confirm_ready_body),
            fontSize = 14.sp,
            color = TextSecondary,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(Dimens.SpaceL))
        InfoBanner()

        if (uiState.error != null) {
            Spacer(modifier = Modifier.height(Dimens.SpaceS))
            Text(text = stringResource(uiState.error), color = Danger, fontSize = 13.sp, textAlign = TextAlign.Center)
        }
    }

    BottomActionArea(
        primaryText = stringResource(R.string.license_submit_button),
        primaryEnabled = !uiState.isSubmitting,
        isLoading = uiState.isSubmitting,
        onPrimaryClick = onSubmit,
        secondaryText = stringResource(R.string.license_complete_later_button),
        onSecondaryClick = onSkip,
    )
}

@Composable
private fun InfoBanner() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.CornerCard))
            .background(BgLight)
            .padding(horizontal = 13.dp, vertical = Dimens.SpaceS),
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = stringResource(R.string.license_info_banner_text),
            fontSize = 12.sp,
            color = TextSecondary,
            lineHeight = 18.sp,
        )
    }
}

@Composable
private fun ColumnScope.BottomActionArea(
    primaryText: String,
    primaryEnabled: Boolean,
    onPrimaryClick: () -> Unit,
    isLoading: Boolean = false,
    secondaryText: String? = null,
    onSecondaryClick: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Button(
            onClick = onPrimaryClick,
            modifier = Modifier.fillMaxWidth().height(Dimens.ControlHeight),
            enabled = primaryEnabled,
            shape = RoundedCornerShape(Dimens.CornerButton),
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
                Text(text = primaryText, fontSize = 16.5.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (secondaryText != null && onSecondaryClick != null) {
            Text(
                text = secondaryText,
                fontSize = 14.5.sp,
                color = TextSecondary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onSecondaryClick)
                    .padding(vertical = 6.dp),
                textAlign = TextAlign.Center,
            )
        }
        Spacer(modifier = Modifier.height(Dimens.SpaceXs))
    }
}

private class ImagePickerState(
    val showPicker: MutableState<Boolean>,
    val onCameraClick: () -> Unit,
    val onGalleryClick: () -> Unit,
)

@Composable
private fun rememberImagePickerState(
    context: Context,
    onImagePicked: (Uri) -> Unit,
): ImagePickerState {
    val showPicker = remember { mutableStateOf(false) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { pickedUri -> pickedUri?.let(onImagePicked) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
    ) { success -> if (success) pendingCameraUri?.let(onImagePicked) }

    fun launchCamera() {
        val photoFile = File.createTempFile("license_camera", ".jpg", context.cacheDir)
        val photoUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
        pendingCameraUri = photoUri
        cameraLauncher.launch(photoUri)
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted -> if (granted) launchCamera() }

    return ImagePickerState(
        showPicker = showPicker,
        onCameraClick = {
            showPicker.value = false
            val hasCameraPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA,
            ) == PackageManager.PERMISSION_GRANTED
            if (hasCameraPermission) {
                launchCamera()
            } else {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        },
        onGalleryClick = {
            showPicker.value = false
            galleryLauncher.launch("image/*")
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImagePickerBottomSheet(state: ImagePickerState) {
    if (state.showPicker.value) {
        ModalBottomSheet(onDismissRequest = { state.showPicker.value = false }) {
            Column(modifier = Modifier.padding(bottom = Dimens.SpaceXl)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = state.onCameraClick)
                        .padding(horizontal = Dimens.SpaceXl, vertical = Dimens.SpaceM),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(imageVector = Icons.Outlined.CameraAlt, contentDescription = null, tint = Primary)
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(text = stringResource(R.string.license_picker_camera_option), fontSize = 16.sp, color = TextPrimary)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = state.onGalleryClick)
                        .padding(horizontal = Dimens.SpaceXl, vertical = Dimens.SpaceM),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(imageVector = Icons.Outlined.PhotoLibrary, contentDescription = null, tint = Primary)
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(text = stringResource(R.string.license_picker_gallery_option), fontSize = 16.sp, color = TextPrimary)
                }
            }
        }
    }
}

@Composable
private fun PhotoPickerCard(
    label: String,
    uri: Uri?,
    imageUrl: String?,
    context: Context,
    onImagePicked: (Uri) -> Unit,
    heightDp: Int = 130,
) {
    val pickerState = rememberImagePickerState(context = context, onImagePicked = onImagePicked)
    val displayUri = uri
    val hasImage = displayUri != null || imageUrl != null

    Column {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = Dimens.SpaceXs),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(heightDp.dp)
                .clip(RoundedCornerShape(Dimens.CornerButton))
                .background(Surface)
                .border(
                    width = if (!hasImage) 2.dp else 0.dp,
                    color = BorderColor,
                    shape = RoundedCornerShape(Dimens.CornerButton),
                )
                .clickable { pickerState.showPicker.value = true },
            contentAlignment = Alignment.Center,
        ) {
            if (hasImage) {
                AsyncImage(
                    model = displayUri ?: imageUrl,
                    contentDescription = label,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(Dimens.CornerButton)),
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(Dimens.SpaceXs)
                        .clip(RoundedCornerShape(7.dp))
                        .background(Success)
                        .padding(horizontal = Dimens.SpaceXs, vertical = 3.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp),
                        )
                        Spacer(modifier = Modifier.width(Dimens.SpaceXxs))
                        Text(text = stringResource(R.string.license_uploaded_badge), fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.PhotoLibrary,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(28.dp),
                    )
                    Spacer(modifier = Modifier.height(7.dp))
                    Text(text = stringResource(R.string.license_capture_or_upload_hint), fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                }
            }
        }
    }

    ImagePickerBottomSheet(state = pickerState)
}

@Composable
private fun SelfiePickerAvatar(
    uri: Uri?,
    imageUrl: String?,
    context: Context,
    onImagePicked: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pickerState = rememberImagePickerState(context = context, onImagePicked = onImagePicked)
    val displayUri = uri
    val hasImage = displayUri != null || imageUrl != null

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(148.dp)
                .clip(CircleShape)
                .background(Surface)
                .border(
                    width = if (!hasImage) 2.dp else 0.dp,
                    color = BorderColor,
                    shape = CircleShape,
                )
                .clickable { pickerState.showPicker.value = true },
            contentAlignment = Alignment.Center,
        ) {
            if (hasImage) {
                AsyncImage(
                    model = displayUri ?: imageUrl,
                    contentDescription = stringResource(R.string.license_selfie_label),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(Success)
                        .size(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp),
                    )
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.CameraAlt,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(30.dp),
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = stringResource(R.string.license_capture_or_upload_hint), fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                }
            }
        }
    }

    ImagePickerBottomSheet(state = pickerState)
}
