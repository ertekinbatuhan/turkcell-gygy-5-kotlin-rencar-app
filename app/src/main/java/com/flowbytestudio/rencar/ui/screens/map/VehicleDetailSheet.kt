package com.flowbytestudio.rencar.ui.screens.map

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AirlineSeatReclineNormal
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.LocalGasStation
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.NearMe
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.flowbytestudio.rencar.data.vehicles.VehicleDto
import com.flowbytestudio.rencar.ui.theme.BgLight
import com.flowbytestudio.rencar.ui.theme.BorderLight
import com.flowbytestudio.rencar.ui.theme.Danger
import com.flowbytestudio.rencar.ui.theme.DangerLight
import com.flowbytestudio.rencar.ui.theme.Primary
import com.flowbytestudio.rencar.ui.theme.Success
import com.flowbytestudio.rencar.ui.theme.SuccessLight
import com.flowbytestudio.rencar.ui.theme.Surface
import com.flowbytestudio.rencar.ui.theme.TextPrimary
import com.flowbytestudio.rencar.ui.theme.TextSecondary
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleDetailSheet(
    vehicle: VehicleDto,
    distanceLabel: String?,
    canReserve: Boolean,
    canUnlock: Boolean,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onReserve: () -> Unit,
    onUnlock: () -> Unit,
) {
    val typeColor = VehicleType.colorFor(vehicle.type)
    val (statusLabel, statusColor, statusBg) = when (vehicle.status.uppercase()) {
        "AVAILABLE" -> Triple("MÜSAİT", Success, SuccessLight)
        "RENTED" -> Triple("KİRADA", Danger, DangerLight)
        "MAINTENANCE" -> Triple("BAKIMDA", TextSecondary, BgLight)
        else -> Triple(vehicle.status, TextSecondary, BgLight)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Surface,
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${vehicle.brand} ${vehicle.model}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f),
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(text = statusLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = statusColor)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (distanceLabel != null) "${vehicle.plate} · $distanceLabel uzaklıkta" else vehicle.plate,
                fontSize = 13.sp,
                color = TextSecondary,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(colors = listOf(typeColor, typeColor.copy(alpha = 0.7f))),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (vehicle.imageUrl != null) {
                    AsyncImage(
                        model = vehicle.imageUrl,
                        contentDescription = "${vehicle.brand} ${vehicle.model}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.DirectionsCar,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.height(72.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SpecTile(
                    icon = Icons.Outlined.LocalGasStation,
                    label = "Yakıt",
                    value = vehicle.fuelPercent?.let { "%$it" } ?: "—",
                    fuelProgress = vehicle.fuelPercent?.let { it / 100f },
                    modifier = Modifier.weight(1f),
                )
                SpecTile(
                    icon = Icons.Outlined.NearMe,
                    label = "Menzil",
                    value = vehicle.rangeKm?.let { "~$it km" } ?: "—",
                    caption = vehicle.rangeKm?.let { "Dolu depo" },
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SpecTile(
                    icon = Icons.Outlined.Settings,
                    label = "Vites",
                    value = vehicle.transmission ?: "—",
                    modifier = Modifier.weight(1f),
                )
                SpecTile(
                    icon = Icons.Outlined.AirlineSeatReclineNormal,
                    label = "Koltuk",
                    value = vehicle.seatCount?.let { "$it kişi" } ?: "—",
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (vehicle.pricePerMinute != null) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "₺${formatTl(vehicle.pricePerMinute)}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                        )
                        Text(
                            text = " /dk",
                            fontSize = 14.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(bottom = 3.dp),
                        )
                    }
                } else {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "₺${formatTl(vehicle.pricePerDay)}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                        )
                        Text(
                            text = " /gün",
                            fontSize = 14.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(bottom = 3.dp),
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                vehicle.pricePerHour?.let { hourly ->
                    Text(
                        text = "Saatlik ₺${formatTl(hourly)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary,
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onReserve,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    enabled = canReserve,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary),
                ) {
                    Text(text = "Rezerve Et", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
                Button(
                    onClick = onUnlock,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    enabled = canUnlock,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Kilidi Aç", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SpecTile(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    caption: String? = null,
    fuelProgress: Float? = null,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(BgLight)
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(13.dp),
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(text = label, fontSize = 11.sp, color = TextSecondary)
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
        )

        if (caption != null) {
            Text(text = caption, fontSize = 10.sp, color = TextSecondary)
        }

        if (fuelProgress != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BorderLight),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fuelProgress.coerceIn(0f, 1f))
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Success),
                )
            }
        }
    }
}

/** ₺180 gibi tam sayıları ondalıksız, ₺4,50 gibi değerleri virgüllü iki basamakla yazar. */
private fun formatTl(value: Double): String =
    if (value % 1.0 == 0.0) {
        value.toInt().toString()
    } else {
        String.format(Locale.US, "%.2f", value).replace('.', ',')
    }
