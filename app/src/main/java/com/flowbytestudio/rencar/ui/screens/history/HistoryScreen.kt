package com.flowbytestudio.rencar.ui.screens.history

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flowbytestudio.rencar.ui.theme.Background
import com.flowbytestudio.rencar.ui.theme.Danger
import com.flowbytestudio.rencar.ui.theme.Primary
import com.flowbytestudio.rencar.ui.theme.PrimaryLight
import com.flowbytestudio.rencar.ui.theme.Success
import com.flowbytestudio.rencar.ui.theme.SuccessLight
import com.flowbytestudio.rencar.ui.theme.TextPrimary
import com.flowbytestudio.rencar.ui.theme.TextSecondary

@Composable
fun HistoryScreen(viewModel: HistoryViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HistoryContent(uiState = uiState)
}

@Composable
private fun HistoryContent(uiState: HistoryUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
            Text(
                text = "Kiralamalarım",
                fontSize = 25.5.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
            )
            if (uiState.rentals.isNotEmpty()) {
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Bu ay ${uiState.tripCountThisMonth} yolculuk · ₺${"%.0f".format(uiState.totalSpentThisMonth)} harcama",
                    fontSize = 14.5.sp,
                    color = TextSecondary,
                )
            }
        }

        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            }
            uiState.rentals.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Henüz bir kiralaman yok",
                        fontSize = 16.5.sp,
                        color = TextSecondary,
                    )
                }
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(uiState.rentals, key = { it.id }) { rental ->
                        RentalCard(rental)
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun RentalCard(rental: RentalUiModel) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            tonalElevation = 0.dp,
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.Top,
            ) {
                RouteThumbnail(rental = rental)

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = rental.vehicleLabel,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                        )
                        Text(
                            text = "₺${"%.2f".format(rental.totalPrice)}",
                            fontSize = 16.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = rental.startDate,
                        fontSize = 13.5.sp,
                        color = TextSecondary,
                    )

                    Spacer(modifier = Modifier.height(9.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        InfoChip(text = "${rental.durationMinutes} dk")
                        InfoChip(text = "${"%.1f".format(rental.distanceKm)} km")
                    }
                }
            }
        }

        StatusDot(
            status = rental.status,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 12.dp, end = 12.dp),
        )
    }
}

@Composable
private fun InfoChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Background)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(
            text = text,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary,
        )
    }
}

private fun statusVisual(status: RentalStatus): Color = when (status) {
    RentalStatus.ACTIVE -> Primary
    RentalStatus.COMPLETED -> Success
    RentalStatus.CANCELLED -> Danger
}

@Composable
private fun StatusDot(status: RentalStatus, modifier: Modifier = Modifier) {
    val color = statusVisual(status)

    Box(
        modifier = modifier
            .size(18.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.70f)),
        contentAlignment = Alignment.Center,
    ) {
        when (status) {
            RentalStatus.COMPLETED -> Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Tamamlandı",
                tint = Color.White,
                modifier = Modifier.size(11.dp),
            )
            RentalStatus.CANCELLED -> Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "İptal edildi",
                tint = Color.White,
                modifier = Modifier.size(11.dp),
            )
            RentalStatus.ACTIVE -> Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Color.White),
            )
        }
    }
}

// Route thumbnail is mock-only: the API exposes no GPS trace or waypoints for a rental.
// The end-point dot color doubles as the status indicator, matching the mockup.
@Composable
private fun RouteThumbnail(rental: RentalUiModel) {
    val statusColor = statusVisual(rental.status)
    val seed = rental.id.hashCode()
    val startColor = Primary

    Box(
        modifier = Modifier
            .size(84.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Background),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val startX = w * (0.15f + (seed % 10) / 50f)
            val startY = h * 0.78f
            val endX = w * (0.75f - (seed % 7) / 40f)
            val endY = h * 0.22f
            val ctrlX = w * 0.5f
            val ctrlY = h * 0.5f

            val path = androidx.compose.ui.graphics.Path().apply {
                moveTo(startX, startY)
                quadraticTo(ctrlX, ctrlY, endX, endY)
            }
            drawPath(
                path = path,
                color = startColor,
                style = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round),
            )
            drawCircle(color = startColor, radius = 3.dp.toPx(), center = Offset(startX, startY))
            drawCircle(color = statusColor, radius = 3.dp.toPx(), center = Offset(endX, endY))
        }
    }
}
