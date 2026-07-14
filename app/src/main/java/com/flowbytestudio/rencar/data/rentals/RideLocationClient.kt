package com.flowbytestudio.rencar.data.rentals

import com.flowbytestudio.rencar.data.auth.AuthRepository
import com.flowbytestudio.rencar.data.auth.AuthSession
import com.flowbytestudio.rencar.data.network.NetworkModule
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

// Aktif yolculuktaki aracın CANLI konumunu dinleyen Socket.IO istemcisi.
// Sunucu sözleşmesi: '/ws/locations' namespace'ine CUSTOMER token'ıyla bağlanılır;
// yalnız kullanıcının kendi aktif kiralamasındaki aracın karesi 'my-vehicle'
// event'iyle gelir (payload: { ts, vehicle: { vehicleId, latitude, longitude, ... } }).
// Aktif kiralama yoksa event hiç gelmez; akış sessiz kalır.
// Token handshake'te auth.token ile gider; süresi dolmuşsa bir kez oturum
// tazelenip yeniden bağlanılır, yine olmazsa akış kapanır.
class RideLocationClient(
    private val authRepository: AuthRepository = AuthRepository(),
) {

    fun vehiclePositionStream(): Flow<VehiclePoint> = callbackFlow {
        var socket: Socket? = null
        var triedRefresh = false

        fun teardown() {
            socket?.let {
                it.off()
                it.disconnect()
                it.close()
            }
            socket = null
        }

        fun connectWith(token: String) {
            val opts = IO.Options().apply {
                auth = mapOf("token" to token)
                forceNew = true
                reconnection = true
            }
            val s = IO.socket(NetworkModule.WS_LOCATIONS_URL, opts)
            s.on(MY_VEHICLE_EVENT) { args ->
                parsePoint(args)?.let { trySend(it) }
            }
            s.on(Socket.EVENT_CONNECT_ERROR) {
                // İlk bağlantı hatasında token süresi dolmuş olabilir: bir kez tazele.
                if (!triedRefresh) {
                    triedRefresh = true
                    val staleToken = token
                    launch {
                        val refreshed = authRepository.refreshSession().isSuccess
                        val fresh = AuthSession.accessToken
                        teardown()
                        // Yalnız tazeleme GERÇEKTEN başarılıysa ve YENİ bir token
                        // ürettiyse yeniden bağlan; aksi halde akışı kapat (aynı
                        // ölü token'la sonsuz reconnect döngüsüne girmemek için).
                        if (refreshed && fresh != null && fresh != staleToken) {
                            connectWith(fresh)
                        } else {
                            close()
                        }
                    }
                }
            }
            socket = s
            s.connect()
        }

        val token = AuthSession.accessToken
        if (token == null) {
            close()
        } else {
            connectWith(token)
        }
        awaitClose { teardown() }
    }

    private fun parsePoint(args: Array<Any?>): VehiclePoint? {
        val root = args.getOrNull(0) as? JSONObject ?: return null
        val vehicle = root.optJSONObject("vehicle") ?: return null
        val lat = vehicle.optDouble("latitude", Double.NaN)
        val lng = vehicle.optDouble("longitude", Double.NaN)
        if (lat.isNaN() || lng.isNaN()) return null
        return VehiclePoint(latitude = lat, longitude = lng)
    }

    private companion object {
        const val MY_VEHICLE_EVENT = "my-vehicle"
    }
}

data class VehiclePoint(
    val latitude: Double,
    val longitude: Double,
)
