package com.flowbytestudio.rencar.data.license

import retrofit2.http.GET

interface LicenseApi {

    @GET("license/status")
    suspend fun getStatus(): LicenseStatusResponse
}
