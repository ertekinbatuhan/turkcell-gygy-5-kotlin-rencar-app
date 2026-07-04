package com.flowbytestudio.rencar.data.license

import com.flowbytestudio.rencar.data.network.NetworkModule

class LicenseRepository(
    private val api: LicenseApi = NetworkModule.licenseApi,
) {

    suspend fun getStatus(): Result<LicenseStatusResponse> = runCatching {
        api.getStatus()
    }
}
