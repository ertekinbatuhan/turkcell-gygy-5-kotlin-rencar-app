package com.flowbytestudio.rencar.ui.screens.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DirectionsCar
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flowbytestudio.rencar.data.vehicles.VehicleDto
import com.flowbytestudio.rencar.ui.theme.Primary
import com.flowbytestudio.rencar.ui.theme.Success
import com.flowbytestudio.rencar.ui.theme.SuccessLight
import com.flowbytestudio.rencar.ui.theme.TextPrimary
import com.flowbytestudio.rencar.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleDetailSheet(
    vehicle: VehicleDto,
    distanceLabel: String?,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onReserve: () -> Unit,
    onUnlock: () -> Unit,
) {
    val typeColor = VehicleType.colorFor(vehicle.type)

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
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
                        .background(SuccessLight)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(text = "MÜSAİT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Success)
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
                Icon(
                    imageVector = Icons.Outlined.DirectionsCar,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.height(72.dp),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "₺${vehicle.pricePerDay.toInt()}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                    )
                    Text(text = "günlük", fontSize = 13.sp, color = TextSecondary)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(typeColor.copy(alpha = 0.12f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = VehicleType.labelFor(vehicle.type),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = typeColor,
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
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                ) {
                    Text(text = "Kilidi Aç", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
