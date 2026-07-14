package com.flowbytestudio.rencar.data.reservations

import com.flowbytestudio.rencar.data.network.NetworkModule
import retrofit2.HttpException

class ReservationRepository(
    private val api: ReservationApi = NetworkModule.reservationApi,
) {

    suspend fun createReservation(vehicleId: String): Result<ReservationResponse> = runCatching {
        api.createReservation(CreateReservationRequest(vehicleId = vehicleId))
    }

    // 404 "aktif rezervasyon yok" demektir; hata değil null döner.
    suspend fun getActiveReservation(): Result<ReservationResponse?> =
        runCatching<ReservationResponse?> {
            api.getActiveReservation()
        }.recoverCatching { e ->
            if (e is HttpException && e.code() == 404) null else throw e
        }

    suspend fun cancelReservation(id: String): Result<Unit> = runCatching {
        api.cancelReservation(id)
    }
}
