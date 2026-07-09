package com.flowbytestudio.rencar.ui.screens.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flowbytestudio.rencar.ui.theme.Background
import com.flowbytestudio.rencar.ui.theme.Danger
import com.flowbytestudio.rencar.ui.theme.DangerLight
import com.flowbytestudio.rencar.ui.theme.Primary
import com.flowbytestudio.rencar.ui.theme.Success
import com.flowbytestudio.rencar.ui.theme.SuccessLight
import com.flowbytestudio.rencar.ui.theme.Surface
import com.flowbytestudio.rencar.ui.theme.TextPrimary
import com.flowbytestudio.rencar.ui.theme.TextSecondary

private val VisaBlue = Color(0xFF1A1F71)
private val MastercardRed = Color(0xFFEB001B)
private val MastercardOrange = Color(0xFFF79E1B)

@Composable
fun WalletScreen(viewModel: WalletViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Cüzdan",
            fontSize = 29.5.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
        )

        Spacer(modifier = Modifier.height(20.dp))

        BalanceCard(
            balance = "₺340,00",
            onAddFunds = {},
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader(
            title = "Kayıtlı kartlar",
            actionLabel = "+ Ekle",
            onAction = {},
        )

        Spacer(modifier = Modifier.height(10.dp))

        SavedCardsCard {
            SavedCardItem(
                cardType = CardType.VISA,
                lastFour = "4291",
                expiry = "08/27",
                isDefault = true,
            )
            CardDivider()
            SavedCardItem(
                cardType = CardType.MASTERCARD,
                lastFour = "7740",
                expiry = "11/26",
                isDefault = false,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader(
            title = "Son işlemler",
            actionLabel = null,
            onAction = {},
        )

        Spacer(modifier = Modifier.height(10.dp))

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = Primary)
                }
            }
            uiState.errorMessage != null -> {
                Text(
                    text = uiState.errorMessage ?: "İşlemler yüklenemedi",
                    fontSize = 14.5.sp,
                    color = Danger,
                )
            }
            uiState.transactions.isEmpty() -> {
                Text(
                    text = "Henüz bir işlemin yok",
                    fontSize = 14.5.sp,
                    color = TextSecondary,
                )
            }
            else -> {
                TransactionsCard {
                    uiState.transactions.forEachIndexed { index, transaction ->
                        TransactionItem(
                            icon = Icons.Outlined.DirectionsCar,
                            iconBg = DangerLight,
                            iconTint = Danger,
                            title = transaction.title,
                            subtitle = transaction.date,
                            amount = "${if (transaction.amount >= 0) "+" else "-"}₺${"%.2f".format(kotlin.math.abs(transaction.amount))}",
                            amountColor = if (transaction.amount >= 0) Success else TextPrimary,
                        )
                        if (index != uiState.transactions.lastIndex) {
                            CardDivider()
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun BalanceCard(
    balance: String,
    onAddFunds: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)),
                )
            )
            .padding(horizontal = 24.dp, vertical = 28.dp),
    ) {
        // Dekoratif daire sağ üst
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(RoundedCornerShape(80.dp))
                .background(Color.White.copy(alpha = 0.07f))
                .align(Alignment.TopEnd)
        )

        Column {
            Text(
                text = "Rencar bakiyesi",
                fontSize = 15.5.sp,
                color = Color.White.copy(alpha = 0.80f),
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = balance,
                fontSize = 39.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onAddFunds,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.20f),
                    contentColor = Color.White,
                ),
            ) {
                Text(
                    text = "+ Bakiye Yükle",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.5.sp,
                    modifier = Modifier.padding(vertical = 2.dp),
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    actionLabel: String?,
    onAction: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            fontSize = 17.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
        )
        if (actionLabel != null) {
            Text(
                text = actionLabel,
                fontSize = 15.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary,
                modifier = Modifier.clickable(onClick = onAction),
            )
        }
    }
}

private enum class CardType { VISA, MASTERCARD }

@Composable
private fun SavedCardsCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Surface,
        tonalElevation = 0.dp,
    ) {
        Column(content = content)
    }
}

@Composable
private fun SavedCardItem(
    cardType: CardType,
    lastFour: String,
    expiry: String,
    isDefault: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {}
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Kart logosu
        Box(
            modifier = Modifier
                .size(width = 48.dp, height = 32.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(
                    when (cardType) {
                        CardType.VISA -> VisaBlue
                        CardType.MASTERCARD -> Color(0xFF252525)
                    }
                ),
            contentAlignment = Alignment.Center,
        ) {
            when (cardType) {
                CardType.VISA -> Text(
                    text = "VISA",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 1.sp,
                )
                CardType.MASTERCARD -> Row(
                    horizontalArrangement = Arrangement.spacedBy((-6).dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(MastercardRed),
                    )
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(MastercardOrange.copy(alpha = 0.9f)),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "•••• $lastFour",
                fontSize = 16.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Son kullanma $expiry",
                fontSize = 14.5.sp,
                color = TextSecondary,
            )
        }

        if (isDefault) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(SuccessLight)
                    .padding(horizontal = 10.dp, vertical = 5.dp),
            ) {
                Text(
                    text = "Varsayılan",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Success,
                )
            }
        }
    }
}

@Composable
private fun TransactionsCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Surface,
        tonalElevation = 0.dp,
    ) {
        Column(content = content)
    }
}

@Composable
private fun TransactionItem(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    amount: String,
    amountColor: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            androidx.compose.material3.Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp),
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.5.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 14.5.sp,
                color = TextSecondary,
            )
        }

        Text(
            text = amount,
            fontSize = 16.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = amountColor,
        )
    }
}

@Composable
private fun CardDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = Background,
        thickness = 1.dp,
    )
}
