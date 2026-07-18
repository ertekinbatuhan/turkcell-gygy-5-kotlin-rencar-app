package com.flowbytestudio.rencar.data.cards

import com.flowbytestudio.rencar.data.network.NetworkModule

class CardRepository(
    private val api: CardApi = NetworkModule.cardApi,
) {

    suspend fun getCards(): Result<List<CardDto>> = runCatching {
        api.getCards()
    }

    suspend fun createCard(
        brand: String,
        last4: String,
        expMonth: Int,
        expYear: Int,
    ): Result<CardDto> = runCatching {
        api.createCard(
            CreateCardRequest(
                brand = brand,
                last4 = last4,
                expMonth = expMonth,
                expYear = expYear,
            )
        )
    }

    suspend fun setDefault(id: String): Result<CardDto> = runCatching {
        api.setDefault(id)
    }

    suspend fun deleteCard(id: String): Result<Unit> = runCatching {
        api.deleteCard(id)
    }
}
