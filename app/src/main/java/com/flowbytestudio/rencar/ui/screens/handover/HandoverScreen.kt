package com.flowbytestudio.rencar.ui.screens.handover

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.flowbytestudio.rencar.ui.theme.Background
import com.flowbytestudio.rencar.ui.theme.BgLight
import com.flowbytestudio.rencar.ui.theme.BorderLight
import com.flowbytestudio.rencar.ui.theme.Danger
import com.flowbytestudio.rencar.ui.theme.Primary
import com.flowbytestudio.rencar.ui.theme.PrimaryLight
import com.flowbytestudio.rencar.ui.theme.Success
import com.flowbytestudio.rencar.ui.theme.SuccessLight
import com.flowbytestudio.rencar.ui.theme.Surface
import com.flowbytestudio.rencar.ui.theme.TextPrimary
import com.flowbytestudio.rencar.ui.theme.TextSecondary

private val WarningAmber = Color(0xFFF59E0B)

@Composable
fun HandoverScreen(
    vehicleId: String,
    onBack: () -> Unit,
    onRentalStarted: (rentalId: String) -> Unit,
) {
    val viewModel: HandoverViewModel = viewModel(
        factory = viewModelFactory {
            initializer { HandoverViewModel(vehicleId) }
        },
    )
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.startedRentalId) {
        uiState.startedRentalId?.let(onRentalStarted)
    }

    Column(modifier = Modifier.fillMaxSize().background(Background)) {
        HandoverHeader(onBack = onBack)

        when {
            uiState.isLoadingVehicle -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            }
            uiState.loadError != null -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(text = uiState.loadError.orEmpty(), color = Danger, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = viewModel::loadVehicle) {
                        Text("Tekrar dene")
                    }
                }
            }
            else -> {
                val vehicle = uiState.vehicle ?: return@Column

                Column(modifier = Modifier.weight(1f).padding(horizontal = 20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${vehicle.brand} ${vehicle.model} · ${vehicle.plate}",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = "${uiState.capturedCount} / ${PhotoSide.entries.size} çekildi",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Primary,
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val nextSide = PhotoSide.entries.firstOrNull { it !in uiState.capturedSides }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        PhotoSlot(
                            side = PhotoSide.ON,
                            captured = PhotoSide.ON in uiState.capturedSides,
                            highlighted = nextSide == PhotoSide.ON,
                            onClick = viewModel::onCapture,
                            onRetake = viewModel::onRetake,
                            modifier = Modifier.weight(1f),
                        )
                        PhotoSlot(
                            side = PhotoSide.ARKA,
                            captured = PhotoSide.ARKA in uiState.capturedSides,
                            highlighted = nextSide == PhotoSide.ARKA,
                            onClick = viewModel::onCapture,
                            onRetake = viewModel::onRetake,
                            modifier = Modifier.weight(1f),
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        PhotoSlot(
                            side = PhotoSide.SOL,
                            captured = PhotoSide.SOL in uiState.capturedSides,
                            highlighted = nextSide == PhotoSide.SOL,
                            onClick = viewModel::onCapture,
                            onRetake = viewModel::onRetake,
                            modifier = Modifier.weight(1f),
                        )
                        PhotoSlot(
                            side = PhotoSide.SAG,
                            captured = PhotoSide.SAG in uiState.capturedSides,
                            highlighted = nextSide == PhotoSide.SAG,
                            onClick = viewModel::onCapture,
                            onRetake = viewModel::onRetake,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                HandoverBottomBar(
                    remainingCount = uiState.remainingCount,
                    isStarting = uiState.isStarting,
                    enabled = uiState.canStart,
                    errorMessage = uiState.startError,
                    onStart = viewModel::startRental,
                )
            }
        }
    }
}

@Composable
private fun HandoverHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(shape = CircleShape, color = Surface, shadowElevation = 2.dp) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Geri", tint = TextPrimary)
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = "Araç durumu",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
            )
            Text(
                text = "Başlamadan önce ${PhotoSide.entries.size} yönü çek",
                fontSize = 13.sp,
                color = TextSecondary,
            )
        }
    }
}

@Composable
private fun PhotoSlot(
    side: PhotoSide,
    captured: Boolean,
    highlighted: Boolean,
    onClick: (PhotoSide) -> Unit,
    onRetake: (PhotoSide) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(16.dp)
    val borderColor = BorderLight

    Box(
        modifier = modifier
            .height(128.dp)
            .clip(shape)
            .background(if (captured) SuccessLight else Surface)
            .then(
                if (captured) {
                    Modifier.clickable { onRetake(side) }
                } else {
                    Modifier
                        .drawBehind {
                            drawRoundRect(
                                color = borderColor,
                                style = Stroke(
                                    width = 1.5.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 12f)),
                                ),
                                cornerRadius = CornerRadius(16.dp.toPx()),
                            )
                        }
                        .clickable { onClick(side) }
                },
            ),
    ) {
        // Sol üst köşe yön etiketi
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (captured) TextPrimary else BgLight)
                .padding(horizontal = 10.dp, vertical = 4.dp),
        ) {
            Text(
                text = side.label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (captured) Background else TextSecondary,
            )
        }

        if (captured) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(Success),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "${side.label} çekildi",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp),
                )
            }
            // Foto upload endpoint'i gelene kadar çekilen kare yerine araç silueti gösteriliyor.
            Icon(
                imageVector = Icons.Outlined.DirectionsCar,
                contentDescription = null,
                tint = Success.copy(alpha = 0.45f),
                modifier = Modifier.align(Alignment.Center).size(56.dp),
            )
        } else {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (highlighted) Primary else PrimaryLight),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PhotoCamera,
                        contentDescription = "${side.label} fotoğrafı çek",
                        tint = if (highlighted) Color.White else Primary,
                        modifier = Modifier.size(22.dp),
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Fotoğraf çek", fontSize = 12.sp, color = TextSecondary)
            }
        }
    }
}

@Composable
private fun HandoverBottomBar(
    remainingCount: Int,
    isStarting: Boolean,
    enabled: Boolean,
    errorMessage: String?,
    onStart: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Surface,
        shadowElevation = 12.dp,
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.WarningAmber,
                    contentDescription = null,
                    tint = WarningAmber,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Hasarları net çek — teslim sonrası anlaşmazlığı önler.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = errorMessage, color = Danger, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onStart,
                enabled = enabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
            ) {
                if (isStarting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(
                        text = if (remainingCount > 0) {
                            "Kiralamayı Başlat · $remainingCount foto kaldı"
                        } else {
                            "Kiralamayı Başlat"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}
