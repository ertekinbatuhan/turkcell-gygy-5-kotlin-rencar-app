package com.flowbytestudio.rencar.data.license

import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface LicenseApi {

    @Multipart
    @POST("license/upload")
    suspend fun upload(
        @Part front: MultipartBody.Part,
        @Part back: MultipartBody.Part,
    ): LicenseResponse

    @GET("license/status")
    suspend fun getStatus(): LicenseStatusResponse
}
