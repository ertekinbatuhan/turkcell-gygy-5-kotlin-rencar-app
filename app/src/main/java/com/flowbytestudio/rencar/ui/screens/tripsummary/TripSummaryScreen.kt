package com.flowbytestudio.rencar.ui.screens.tripsummary

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.flowbytestudio.rencar.ui.theme.Background
import com.flowbytestudio.rencar.ui.theme.BgLight
import com.flowbytestudio.rencar.ui.theme.Danger
import com.flowbytestudio.rencar.ui.theme.Primary
import com.flowbytestudio.rencar.ui.theme.Success
import com.flowbytestudio.rencar.ui.theme.Surface
import com.flowbytestudio.rencar.ui.theme.TextPrimary
import com.flowbytestudio.rencar.ui.theme.TextSecondary
import java.util.Locale
import kotlinx.coroutines.launch

@Composable
fun TripSummaryScreen(
    rentalId: String,
    onDone: () -> Unit,
) {
    val viewModel: TripSummaryViewModel = viewModel(
        factory = viewModelFactory {
            initializer { TripSummaryViewModel(rentalId) }
        },
    )
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        when {
            uiState.isLoading -> {
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
                    Button(onClick = viewModel::load) {
                        Text("Tekrar dene")
                    }
                }
            }
            else -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(modifier = Modifier.height(32.dp))

                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(Success),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp),
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Yolculuk tamamlandı",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        uiState.vehicle?.let { vehicle ->
                            Text(
                                text = "${vehicle.brand} ${vehicle.model} · ${vehicle.plate}",
                                fontSize = 13.sp,
                                color = TextSecondary,
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            SummaryTile(
                                label = "Süre",
                                value = uiState.durationMinutes?.let(::formatDuration) ?: "—",
                                modifier = Modifier.weight(1f),
                            )
                            // Araç telemetrisi API'de olmadığı için mesafe veri gelene kadar "—".
                            SummaryTile(
                                label = "Mesafe",
                                value = "—",
                                modifier = Modifier.weight(1f),
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        BreakdownCard(uiState = uiState)

                        Spacer(modifier = Modifier.height(16.dp))

                        PaymentMethodCard(
                            cardLast4 = uiState.cardLast4,
                            cardLabel = uiState.cardLabel,
                            onChangeClick = {
                                scope.launch { snackbarHostState.showSnackbar("Ödeme yöntemleri yakında eklenecek.") }
                            },
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Surface,
                        shadowElevation = 12.dp,
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
                            Button(
                                // Ödeme endpoint'i backend'e eklenince gerçek tahsilata bağlanacak;
                                // şimdilik özeti kapatıp haritaya döner.
                                onClick = onDone,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            ) {
                                Text(
                                    text = uiState.totalAmount?.let { "₺${formatTl(it)} Öde" } ?: "Tamam",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 90.dp),
        ) { data ->
            Snackbar(snackbarData = data)
        }
    }
}

@Composable
private fun SummaryTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Surface,
        shadowElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = label, fontSize = 12.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
    }
}

@Composable
private fun BreakdownCard(uiState: TripSummaryUiState) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Surface,
        shadowElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val durationSuffix = uiState.durationMinutes?.let { " (${formatDuration(it)})" }.orEmpty()
            BreakdownRow(
                label = "Kiralama ücreti$durationSuffix",
                value = uiState.rental?.totalPrice?.let { "₺${formatTl(it)}" } ?: "—",
            )

            uiState.startFee?.let { fee ->
                Spacer(modifier = Modifier.height(8.dp))
                BreakdownRow(label = "Başlangıç ücreti", value = "₺${formatTl(fee)}")
            }

            uiState.serviceFee?.let { fee ->
                Spacer(modifier = Modifier.height(8.dp))
                BreakdownRow(label = "Hizmet bedeli", value = "₺${formatTl(fee)}")
            }

            if (uiState.discountAmount != null) {
                Spacer(modifier = Modifier.height(8.dp))
                BreakdownRow(
                    label = "İndirim${uiState.discountLabel?.let { " · $it" }.orEmpty()}",
                    value = "−₺${formatTl(uiState.discountAmount)}",
                    color = Success,
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Background, thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "Toplam",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                )
                Text(
                    text = uiState.totalAmount?.let { "₺${formatTl(it)}" } ?: "—",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )
            }
        }
    }
}

@Composable
private fun BreakdownRow(
    label: String,
    value: String,
    color: Color = Color.Unspecified,
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (color == Color.Unspecified) FontWeight.Normal else FontWeight.SemiBold,
            color = if (color == Color.Unspecified) TextSecondary else color,
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (color == Color.Unspecified) TextPrimary else color,
        )
    }
}

@Composable
private fun PaymentMethodCard(
    cardLast4: String?,
    cardLabel: String?,
    onChangeClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Surface,
        shadowElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .width(44.dp)
                    .height(30.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (cardLast4 != null) Color(0xFF1A1F71) else BgLight),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.CreditCard,
                    contentDescription = null,
                    tint = if (cardLast4 != null) Color.White else TextSecondary,
                    modifier = Modifier.size(18.dp),
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cardLast4?.let { "•••• $it" } ?: "Kayıtlı kart yok",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                )
                Text(
                    text = cardLabel ?: "Ödeme yöntemi ekle",
                    fontSize = 12.sp,
                    color = TextSecondary,
                )
            }

            Text(
                text = if (cardLast4 != null) "Değiştir" else "Ekle",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onChangeClick)
                    .padding(6.dp),
            )
        }
    }
}

private fun formatDuration(minutes: Long): String {
    val hours = minutes / 60
    val remaining = minutes % 60
    return when {
        hours <= 0 -> "$minutes dk"
        remaining == 0L -> "$hours sa"
        else -> "$hours sa $remaining dk"
    }
}

private fun formatTl(value: Double): String {
    val turkish = Locale("tr", "TR")
    return if (value % 1.0 == 0.0) {
        String.format(turkish, "%,d", value.toLong())
    } else {
        String.format(turkish, "%,.2f", value)
    }
}
