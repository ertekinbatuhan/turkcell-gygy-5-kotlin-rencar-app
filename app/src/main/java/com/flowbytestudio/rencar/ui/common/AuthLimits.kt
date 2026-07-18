package com.flowbytestudio.rencar.ui.common

// Giriş/kayıt akışındaki alan kuralları; ekranlar ve ViewModel'ler çıplak sayı
// yerine bunları kullanır (backend sözleşmesiyle birlikte değişirler).
object AuthLimits {
    const val PHONE_LENGTH = 10
    const val OTP_LENGTH = 6
    const val PASSWORD_MIN_LENGTH = 6
    const val OTP_RESEND_SECONDS = 60
}
