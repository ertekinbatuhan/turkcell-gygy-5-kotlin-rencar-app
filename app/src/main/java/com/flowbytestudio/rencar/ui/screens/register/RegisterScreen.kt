package com.flowbytestudio.rencar.ui.screens.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.PersonAddAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flowbytestudio.rencar.ui.screens.login.AuthFooterText
import com.flowbytestudio.rencar.ui.screens.login.PhoneNumberInput
import com.flowbytestudio.rencar.ui.screens.login.PrimaryAuthButton
import com.flowbytestudio.rencar.ui.theme.*

@Composable
fun RegisterScreen(
    onRegistered: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: RegisterViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isRegistered) {
        if (uiState.isRegistered) onRegistered()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            IconButton(
                onClick = onNavigateToLogin,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BgLight)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Geri",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Hesap oluştur",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Rencar'ı kullanmaya başlamak için birkaç bilgiye ihtiyacımız var.",
                fontSize = 15.sp,
                color = TextSecondary,
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = uiState.fullName,
                onValueChange = viewModel::onFullNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Ad Soyad") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = BorderColor,
                    focusedContainerColor = Surface,
                    unfocusedContainerColor = Surface
                )
            )
            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("E-posta") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = BorderColor,
                    focusedContainerColor = Surface,
                    unfocusedContainerColor = Surface
                )
            )
            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Parola") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = BorderColor,
                    focusedContainerColor = Surface,
                    unfocusedContainerColor = Surface
                )
            )
            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Telefon numarası",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(9.dp))
            PhoneNumberInput(
                value = uiState.phone,
                onValueChange = viewModel::onPhoneChange
            )
            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = uiState.referralCode,
                onValueChange = viewModel::onReferralCodeChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Davet kodu (opsiyonel)") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = BorderColor,
                    focusedContainerColor = Surface,
                    unfocusedContainerColor = Surface
                )
            )

            if (uiState.error != null) {
                Text(
                    text = uiState.error ?: "",
                    color = Danger,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            PrimaryAuthButton(
                text = "Kayıt Ol",
                icon = Icons.Outlined.PersonAddAlt,
                isLoading = uiState.isLoading,
                enabled = uiState.fullName.isNotBlank() &&
                    uiState.email.isNotBlank() &&
                    uiState.password.length >= 6 &&
                    uiState.phone.length == 10 &&
                    !uiState.isLoading,
                onClick = viewModel::onRegister
            )

            AuthFooterText(
                mainText = "Zaten hesabın var mı? ",
                actionText = "Giriş yap",
                onClick = onNavigateToLogin
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
