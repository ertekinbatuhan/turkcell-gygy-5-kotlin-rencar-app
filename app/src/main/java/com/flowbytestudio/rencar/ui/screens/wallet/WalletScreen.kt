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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flowbytestudio.rencar.ui.theme.Background
import com.flowbytestudio.rencar.ui.theme.BorderLight
import com.flowbytestudio.rencar.ui.theme.Danger
import com.flowbytestudio.rencar.ui.theme.Primary
import com.flowbytestudio.rencar.ui.theme.PrimaryLight
import com.flowbytestudio.rencar.ui.theme.Success
import com.flowbytestudio.rencar.ui.theme.SuccessLight
import com.flowbytestudio.rencar.ui.theme.TextPrimary
import com.flowbytestudio.rencar.ui.theme.TextSecondary

private val Orange = Color(0xFFF97316)
private val OrangeLight = Color(0xFFFFF7ED)

@Composable
fun WalletScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        BalanceCard(
            balance = "1.250,00 ₺",
            onAddFunds = {},
            onWithdraw = {},
        )

        Spacer(modifier = Modifier.height(12.dp))

        PromoCard(
            code = "RENCAR50",
            description = "İlk kiralama için %50 indirim",
            expiresAt = "30 Temmuz 2026",
            onApply = {},
        )

        Spacer(modifier = Modifier.height(12.dp))

        SectionTitle(title = "Hızlı işlemler")
        Spacer(modifier = Modifier.height(8.dp))
        QuickActionsRow(
            onAddCard = {},
            onPromoCode = {},
            onAutoPayment = {},
        )

        Spacer(modifier = Modifier.height(16.dp))

        SectionTitle(title = "Son işlemler")
        Spacer(modifier = Modifier.height(8.dp))

        TransactionCard {
            TransactionItem(
                icon = Icons.Outlined.DirectionsCar,
                iconTint = Primary,
                iconBg = PrimaryLight,
                title = "Ford Focus kiralama",
                subtitle = "24 Haziran 2026",
                amount = "- 420,00 ₺",
                amountColor = Danger,
            )
            TransactionDivider()
            TransactionItem(
                icon = Icons.Outlined.KeyboardArrowUp,
                iconTint = Success,
                iconBg = SuccessLight,
                title = "Bakiye yükleme",
                subtitle = "20 Haziran 2026",
                amount = "+ 500,00 ₺",
                amountColor = Success,
            )
            TransactionDivider()
            TransactionItem(
                icon = Icons.Outlined.DirectionsCar,
                iconTint = Primary,
                iconBg = PrimaryLight,
                title = "Toyota Corolla kiralama",
                subtitle = "15 Haziran 2026",
                amount = "- 310,00 ₺",
                amountColor = Danger,
            )
            TransactionDivider()
            TransactionItem(
                icon = Icons.Outlined.CardGiftcard,
                iconTint = Orange,
                iconBg = OrangeLight,
                title = "Promosyon kodu uygulandı",
                subtitle = "10 Haziran 2026",
                amount = "+ 80,00 ₺",
                amountColor = Success,
            )
            TransactionDivider()
            TransactionItem(
                icon = Icons.Outlined.KeyboardArrowUp,
                iconTint = Success,
                iconBg = SuccessLight,
                title = "Bakiye yükleme",
                subtitle = "1 Haziran 2026",
                amount = "+ 1.500,00 ₺",
                amountColor = Success,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        AllTransactionsButton(onClick = {})

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun BalanceCard(
    balance: String,
    onAddFunds: () -> Unit,
    onWithdraw: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF3D7BF4), Color(0xFF6B9FFF)),
                )
            )
            .padding(20.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.AccountBalanceWallet,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Mevcut bakiye",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.85f),
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = balance,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onAddFunds,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Primary,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Yükle",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                    )
                }

                OutlinedButton(
                    onClick = onWithdraw,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White,
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Çek",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun PromoCard(
    code: String,
    description: String,
    expiresAt: String,
    onApply: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = OrangeLight,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Orange.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocalOffer,
                    contentDescription = null,
                    tint = Orange,
                    modifier = Modifier.size(22.dp),
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = description,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Son kullanım: $expiresAt",
                    fontSize = 12.sp,
                    color = TextSecondary,
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Orange)
                    .clickable(onClick = onApply)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Text(
                    text = "Kullan",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextPrimary,
    )
}

@Composable
private fun QuickActionsRow(
    onAddCard: () -> Unit,
    onPromoCode: () -> Unit,
    onAutoPayment: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        QuickActionItem(
            modifier = Modifier.weight(1f),
            icon = Icons.Outlined.CreditCard,
            label = "Kart ekle",
            onClick = onAddCard,
        )
        QuickActionItem(
            modifier = Modifier.weight(1f),
            icon = Icons.Outlined.LocalOffer,
            label = "Promosyon",
            onClick = onPromoCode,
        )
        QuickActionItem(
            modifier = Modifier.weight(1f),
            icon = Icons.Outlined.History,
            label = "Otomatik\nödeme",
            onClick = onAutoPayment,
        )
    }
}

@Composable
private fun QuickActionItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        tonalElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(PrimaryLight),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(22.dp),
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
    }
}

@Composable
private fun TransactionCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        tonalElevation = 0.dp,
    ) {
        Column(content = content)
    }
}

@Composable
private fun TransactionItem(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
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
            Icon(
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
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = TextSecondary,
            )
        }

        Text(
            text = amount,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = amountColor,
        )
    }
}

@Composable
private fun TransactionDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 70.dp, end = 16.dp),
        color = Background,
        thickness = 1.dp,
    )
}

@Composable
private fun AllTransactionsButton(onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.History,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Tüm işlemleri gör",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}
