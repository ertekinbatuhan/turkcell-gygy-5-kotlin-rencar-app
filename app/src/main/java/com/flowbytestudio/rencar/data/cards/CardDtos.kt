package com.flowbytestudio.rencar.data.cards

import kotlinx.serialization.Serializable

@Serializable
data class CardDto(
    val id: String,
    // VISA / MASTERCARD
    val brand: String,
    val last4: String,
    val expMonth: Int,
    val expYear: Int,
    val isDefault: Boolean,
    val createdAt: String,
)

// PCI kapsamı gereği tam kart numarası/CVV alanı YOKTUR;
// bilinmeyen alan gönderilirse sunucu 400 döner.
@Serializable
data class CreateCardRequest(
    val brand: String,
    val last4: String,
    val expMonth: Int,
    val expYear: Int,
)
