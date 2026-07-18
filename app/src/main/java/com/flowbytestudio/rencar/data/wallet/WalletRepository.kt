package com.flowbytestudio.rencar.data.wallet

import com.flowbytestudio.rencar.data.network.NetworkModule

class WalletRepository(
    private val api: WalletApi = NetworkModule.walletApi,
) {

    suspend fun getWallet(): Result<WalletResponse> = runCatching {
        api.getWallet()
    }

    suspend fun topup(amount: Double): Result<WalletResponse> = runCatching {
        api.topup(TopupRequest(amount = amount))
    }
}
