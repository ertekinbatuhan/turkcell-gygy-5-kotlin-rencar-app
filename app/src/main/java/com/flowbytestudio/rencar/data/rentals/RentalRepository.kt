package com.flowbytestudio.rencar.data.rentals

import com.flowbytestudio.rencar.data.network.NetworkModule
import java.io.File
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class RentalRepository(
    private val api: RentalApi = NetworkModule.rentalApi,
) {

    suspend fun getMyRentals(): Result<List<RentalDto>> = runCatching {
        api.getMyRentals()
    }

    suspend fun createRental(
        vehicleId: String,
        plan: String,
        endDate: String? = null,
    ): Result<RentalDto> = runCatching {
        api.createRental(CreateRentalRequest(vehicleId = vehicleId, plan = plan, endDate = endDate))
    }

    suspend fun getRental(id: String): Result<RentalDto> = runCatching {
        api.getRental(id)
    }

    // 404 "aktif kiralama yok" demektir; hata değil null döner.
    suspend fun getActiveRental(): Result<RentalDto?> = runCatching<RentalDto?> {
        api.getActiveRental()
    }.recoverCatching { e ->
        if (e is HttpException && e.code() == 404) null else throw e
    }

    suspend fun getStats(month: String? = null): Result<RentalStatsResponse> = runCatching {
        api.getStats(month)
    }

    suspend fun uploadPhoto(rentalId: String, side: String, file: File): Result<RentalPhotosState> =
        runCatching {
            val body = file.asRequestBody("image/jpeg".toMediaType())
            api.uploadPhoto(
                id = rentalId,
                side = side.toRequestBody("text/plain".toMediaType()),
                file = MultipartBody.Part.createFormData("file", file.name, body),
            )
        }

    suspend fun getPhotos(rentalId: String): Result<RentalPhotosState> = runCatching {
        api.getPhotos(rentalId)
    }

    suspend fun startRental(id: String): Result<RentalDto> = runCatching {
        api.startRental(id)
    }

    suspend fun cancelRental(id: String): Result<Unit> = runCatching {
        api.cancelRental(id)
    }

    suspend fun finishRental(id: String): Result<RentalDto> = runCatching {
        api.finishRental(id)
    }

    suspend fun payRental(
        id: String,
        method: String,
        cardId: String? = null,
        discountCode: String? = null,
        iyzicoPaymentId: String? = null,
    ): Result<PayRentalResponse> = runCatching {
        api.payRental(
            id = id,
            body = PayRentalRequest(
                method = method,
                cardId = cardId,
                discountCode = discountCode?.takeIf { it.isNotBlank() },
                iyzicoPaymentId = iyzicoPaymentId,
            ),
        )
    }
}
