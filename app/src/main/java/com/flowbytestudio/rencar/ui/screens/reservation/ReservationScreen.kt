package com.flowbytestudio.rencar.ui.screens.reservation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil.compose.AsyncImage
import com.flowbytestudio.rencar.data.vehicles.VehicleDto
import com.flowbytestudio.rencar.ui.screens.map.VehicleType
import com.flowbytestudio.rencar.ui.theme.Background
import com.flowbytestudio.rencar.ui.theme.BorderLight
import com.flowbytestudio.rencar.ui.theme.Danger
import com.flowbytestudio.rencar.ui.theme.Primary
import com.flowbytestudio.rencar.ui.theme.PrimaryLight
import com.flowbytestudio.rencar.ui.theme.Success
import com.flowbytestudio.rencar.ui.theme.SuccessLight
import com.flowbytestudio.rencar.ui.theme.Surface
import com.flowbytestudio.rencar.ui.theme.TextPrimary
import com.flowbytestudio.rencar.ui.theme.TextSecondary
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationScreen(
    vehicleId: String,
    onBack: () -> Unit,
) {
    val viewModel: ReservationViewModel = viewModel(
        factory = viewModelFactory {
            initializer { ReservationViewModel(vehicleId) }
        },
    )
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.reservationCompleted) {
        if (uiState.reservationCompleted) {
            val total = uiState.completedTotalPrice?.toInt()
            snackbarHostState.showSnackbar("Rezervasyon tamamlandı! Toplam: ₺$total")
            delay(1200)
            onBack()
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Background)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Geri", tint = TextPrimary)
            }
            Text(
                text = "Rezervasyon Onayı",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoadingVehicle -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Primary)
                    }
                }
                uiState.loadError != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
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
                uiState.vehicle != null -> {
                    val vehicle = uiState.vehicle!!
                    Column(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 20.dp),
                        ) {
                            Spacer(modifier = Modifier.height(8.dp))

                            VehicleSummaryCard(vehicle = vehicle)

                            Spacer(modifier = Modifier.height(16.dp))

                            PlanSelector(
                                selected = uiState.selectedPlan,
                                pricePerMinute = vehicle.pricePerMinute,
                                pricePerHour = vehicle.pricePerHour,
                                pricePerDay = vehicle.pricePerDay,
                                onSelect = viewModel::onPlanSelect,
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            when (uiState.selectedPlan) {
                                RentalPlan.DAKIKALIK -> MinutePlanCard(estimate = uiState.minuteEstimate)
                                RentalPlan.SAATLIK -> HourPlanCard(pricePerHour = vehicle.pricePerHour)
                                RentalPlan.GUNLUK -> PriceBreakdownCard(
                                    pricePerDay = vehicle.pricePerDay,
                                    days = uiState.days,
                                    totalPrice = uiState.totalPrice,
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(verticalAlignment = Alignment.Top) {
                                Checkbox(
                                    checked = uiState.termsAccepted,
                                    onCheckedChange = viewModel::onTermsToggle,
                                    colors = CheckboxDefaults.colors(checkedColor = Primary),
                                )
                                Text(
                                    text = "Kullanım şartlarını ve kasko/sigorta koşullarını okudum, onaylıyorum.",
                                    fontSize = 13.sp,
                                    color = TextSecondary,
                                    modifier = Modifier
                                        .padding(top = 14.dp)
                                        .clickable { viewModel.onTermsToggle(!uiState.termsAccepted) },
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))
                        }

                        ReservationBottomBar(
                            isSubmitting = uiState.isSubmitting,
                            enabled = uiState.canSubmit,
                            errorMessage = uiState.submitError,
                            onSubmit = { viewModel.submit(onCompleted = {}) },
                        )
                    }
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter),
            ) { data ->
                Snackbar(snackbarData = data)
            }
        }
    }
}

@Composable
private fun ReservationBottomBar(
    isSubmitting: Boolean,
    enabled: Boolean,
    errorMessage: String?,
    onSubmit: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Surface,
        shadowElevation = 12.dp,
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
            if (errorMessage != null) {
                Text(text = errorMessage, color = Danger, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
            }
            Button(
                onClick = onSubmit,
                enabled = enabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(text = "Rezervasyonu Tamamla", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun VehicleSummaryCard(vehicle: VehicleDto) {
    val typeColor = VehicleType.colorFor(vehicle.type)
    val subtitle = listOfNotNull(
        vehicle.plate,
        vehicle.transmission ?: VehicleType.labelFor(vehicle.type),
        vehicle.seatCount?.let { "$it kişi" },
    ).joinToString(" · ")

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
                    .width(76.dp)
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.linearGradient(colors = listOf(typeColor, typeColor.copy(alpha = 0.7f)))),
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
                    Icon(Icons.Outlined.DirectionsCar, contentDescription = null, tint = Color.White, modifier = Modifier.size(30.dp))
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = "${vehicle.brand} ${vehicle.model}", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, fontSize = 13.sp, color = TextSecondary)
                if (vehicle.fuelPercent != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SuccessLight)
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text(
                            text = "Yakıt %${vehicle.fuelPercent}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Success,
                        )
                    }
                }
            }

            if (vehicle.fuelPercent == null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SuccessLight)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                ) {
                    Text(text = "Müsait", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Success)
                }
            }
        }
    }
}

@Composable
private fun PlanSelector(
    selected: RentalPlan,
    pricePerMinute: Double?,
    pricePerHour: Double?,
    pricePerDay: Double,
    onSelect: (RentalPlan) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Surface,
        shadowElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "Kiralama planı",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                PlanChip(
                    title = "Dakikalık",
                    priceLabel = pricePerMinute?.let { "₺${formatTl(it)}/dk" },
                    selected = selected == RentalPlan.DAKIKALIK,
                    onClick = { onSelect(RentalPlan.DAKIKALIK) },
                    modifier = Modifier.weight(1f),
                )
                PlanChip(
                    title = "Saatlik",
                    priceLabel = pricePerHour?.let { "₺${formatTl(it)}/sa" },
                    selected = selected == RentalPlan.SAATLIK,
                    onClick = { onSelect(RentalPlan.SAATLIK) },
                    modifier = Modifier.weight(1f),
                )
                PlanChip(
                    title = "Günlük",
                    priceLabel = "₺${formatTl(pricePerDay)}",
                    selected = selected == RentalPlan.GUNLUK,
                    onClick = { onSelect(RentalPlan.GUNLUK) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun PlanChip(
    title: String,
    priceLabel: String?,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Fiyatı API'den gelmeyen planlar veri gelene kadar seçilemez.
    val enabled = priceLabel != null
    val shape = RoundedCornerShape(12.dp)

    Column(
        modifier = modifier
            .clip(shape)
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) Primary else BorderLight,
                shape = shape,
            )
            .background(if (selected) PrimaryLight else Surface)
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier.alpha(0.45f))
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) Primary else TextPrimary,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = priceLabel ?: "—",
            fontSize = 12.sp,
            color = if (selected) Primary else TextSecondary,
        )
    }
}

@Composable
private fun MinutePlanCard(estimate: Double?) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Surface,
        shadowElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            PriceRow(label = "Ücretsiz rezervasyon", value = "$FREE_RESERVATION_MINUTES dk")
            Spacer(modifier = Modifier.height(8.dp))
            PriceRow(label = "Başlangıç ücreti", value = "₺${formatTl(MINUTE_PLAN_START_FEE)}")
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Background, thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))
            PriceRow(
                label = "Tahmini ücret ($MINUTE_ESTIMATE_MINUTES dk)",
                value = estimate?.let { "~₺${formatTl(it)}" } ?: "—",
                emphasize = true,
            )
        }
    }
}

@Composable
private fun HourPlanCard(pricePerHour: Double?) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Surface,
        shadowElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            PriceRow(label = "Ücretsiz rezervasyon", value = "$FREE_RESERVATION_MINUTES dk")
            Spacer(modifier = Modifier.height(8.dp))
            PriceRow(label = "Saatlik ücret", value = pricePerHour?.let { "₺${formatTl(it)}" } ?: "—")
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Background, thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))
            PriceRow(
                label = "Tahmini ücret (1 sa)",
                value = pricePerHour?.let { "~₺${formatTl(it)}" } ?: "—",
                emphasize = true,
            )
        }
    }
}

@Composable
private fun PriceBreakdownCard(
    pricePerDay: Double,
    days: Int,
    totalPrice: Double,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Surface,
        shadowElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            PriceRow(label = "Günlük ücret", value = "₺${pricePerDay.toInt()}")
            Spacer(modifier = Modifier.height(8.dp))
            PriceRow(label = "Süre", value = if (days == 1) "1 gün" else "$days gün")
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Background, thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))
            PriceRow(
                label = "Toplam tutar",
                value = "₺${totalPrice.toInt()}",
                emphasize = true,
            )
        }
    }
}

/** ₺1.450 gibi tam sayıları binlik ayraçlı, ₺4,50 gibi değerleri virgüllü iki basamakla yazar. */
private fun formatTl(value: Double): String {
    val turkish = Locale("tr", "TR")
    return if (value % 1.0 == 0.0) {
        String.format(turkish, "%,d", value.toLong())
    } else {
        String.format(turkish, "%,.2f", value)
    }
}

@Composable
private fun PriceRow(label: String, value: String, emphasize: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = label,
            fontSize = if (emphasize) 15.sp else 14.sp,
            fontWeight = if (emphasize) FontWeight.SemiBold else FontWeight.Normal,
            color = if (emphasize) TextPrimary else TextSecondary,
        )
        Text(
            text = value,
            fontSize = if (emphasize) 18.sp else 14.sp,
            fontWeight = if (emphasize) FontWeight.Bold else FontWeight.SemiBold,
            color = if (emphasize) Primary else TextPrimary,
        )
    }
}
