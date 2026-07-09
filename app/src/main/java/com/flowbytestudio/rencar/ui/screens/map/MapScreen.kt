package com.flowbytestudio.rencar.ui.screens.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Looper
import android.view.Gravity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.NearMe
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flowbytestudio.rencar.data.vehicles.VehicleDto
import com.flowbytestudio.rencar.ui.theme.Background
import com.flowbytestudio.rencar.ui.theme.Danger
import com.flowbytestudio.rencar.ui.theme.Primary
import com.flowbytestudio.rencar.ui.theme.TextPrimary
import com.flowbytestudio.rencar.ui.theme.TextSecondary
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.gson.JsonPrimitive
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.plugins.annotation.OnSymbolClickListener
import org.maplibre.android.plugins.annotation.Symbol
import org.maplibre.android.plugins.annotation.SymbolManager
import org.maplibre.android.plugins.annotation.SymbolOptions
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point

private const val ISTANBUL_LAT = 41.0082
private const val ISTANBUL_LON = 28.9784
private const val VEHICLE_REFRESH_INTERVAL_MS = 10_000L
private const val ME_SOURCE_ID = "me"
private val ME_MARKER_COLOR = android.graphics.Color.parseColor("#4285F4")

private const val OSM_RASTER_STYLE = """
{
  "version": 8,
  "sources": {
    "osm-tiles": {
      "type": "raster",
      "tiles": ["https://tile.openstreetmap.org/{z}/{x}/{y}.png"],
      "tileSize": 256,
      "attribution": "&copy; OpenStreetMap contributors"
    }
  },
  "layers": [
    {
      "id": "osm-tiles-layer",
      "type": "raster",
      "source": "osm-tiles",
      "minzoom": 0,
      "maxzoom": 19
    }
  ]
}
"""

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onNavigateToReservation: (String) -> Unit = {},
    viewModel: MapViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val density = context.resources.displayMetrics.density
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState()
    var selectedVehicle by remember { mutableStateOf<VehicleDto?>(null) }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED,
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { grants ->
        hasLocationPermission = grants.values.any { it }
    }
    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            )
        }
    }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (true) {
                delay(VEHICLE_REFRESH_INTERVAL_MS)
                viewModel.loadVehicles()
            }
        }
    }

    var myLocation by remember { mutableStateOf<LatLng?>(null) }
    val fusedClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    DisposableEffect(hasLocationPermission) {
        if (!hasLocationPermission) return@DisposableEffect onDispose {}

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { loc -> myLocation = LatLng(loc.latitude, loc.longitude) }
            }
        }
        startLocationUpdates(fusedClient, callback)
        onDispose { fusedClient.removeLocationUpdates(callback) }
    }

    var mapView by remember { mutableStateOf<MapView?>(null) }
    var mapLibreMap by remember { mutableStateOf<MapLibreMap?>(null) }
    var mapStyle by remember { mutableStateOf<Style?>(null) }
    var symbolManager by remember { mutableStateOf<SymbolManager?>(null) }
    var hasCenteredOnVehicles by remember { mutableStateOf(false) }
    var hasZoomedToUser by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                MapLibre.getInstance(ctx)
                MapView(ctx).apply {
                    onCreate(null)
                    getMapAsync { map ->
                        mapLibreMap = map
                        map.uiSettings.setLogoGravity(Gravity.TOP or Gravity.END)
                        map.uiSettings.setAttributionGravity(Gravity.TOP or Gravity.END)
                        map.cameraPosition = CameraPosition.Builder()
                            .target(LatLng(ISTANBUL_LAT, ISTANBUL_LON))
                            .zoom(12.0)
                            .build()
                        map.setStyle(Style.Builder().fromJson(OSM_RASTER_STYLE)) { style ->
                            style.addSource(GeoJsonSource(ME_SOURCE_ID))
                            style.addLayer(
                                CircleLayer("me-halo-layer", ME_SOURCE_ID).withProperties(
                                    PropertyFactory.circleColor(ME_MARKER_COLOR),
                                    PropertyFactory.circleRadius(20f),
                                    PropertyFactory.circleOpacity(0.2f),
                                    PropertyFactory.circleBlur(0.4f),
                                ),
                            )
                            style.addLayer(
                                CircleLayer("me-layer", ME_SOURCE_ID).withProperties(
                                    PropertyFactory.circleColor(ME_MARKER_COLOR),
                                    PropertyFactory.circleRadius(9f),
                                    PropertyFactory.circleStrokeColor(android.graphics.Color.WHITE),
                                    PropertyFactory.circleStrokeWidth(3f),
                                ),
                            )
                            mapStyle = style
                            symbolManager = SymbolManager(this, map, style).apply {
                                iconAllowOverlap = true
                                iconIgnorePlacement = true
                            }
                        }
                    }
                    mapView = this
                }
            },
        )

        DisposableEffect(lifecycleOwner, mapView) {
            val currentMapView = mapView
            if (currentMapView == null) {
                onDispose {}
            } else {
                val observer = LifecycleEventObserver { _, event ->
                    when (event) {
                        Lifecycle.Event.ON_START -> currentMapView.onStart()
                        Lifecycle.Event.ON_RESUME -> currentMapView.onResume()
                        Lifecycle.Event.ON_PAUSE -> currentMapView.onPause()
                        Lifecycle.Event.ON_STOP -> currentMapView.onStop()
                        Lifecycle.Event.ON_DESTROY -> currentMapView.onDestroy()
                        else -> Unit
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
            }
        }

        LaunchedEffect(mapStyle, myLocation) {
            val style = mapStyle ?: return@LaunchedEffect
            updateMeMarker(style, myLocation)
        }

        LaunchedEffect(mapLibreMap, myLocation) {
            if (hasZoomedToUser) return@LaunchedEffect
            val map = mapLibreMap ?: return@LaunchedEffect
            val location = myLocation ?: return@LaunchedEffect
            hasZoomedToUser = true
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15.0))
        }

        LaunchedEffect(uiState.filteredVehicles, mapStyle, symbolManager) {
            val style = mapStyle ?: return@LaunchedEffect
            val manager = symbolManager ?: return@LaunchedEffect
            manager.deleteAll()

            uiState.filteredVehicles.forEach { vehicle ->
                val imageId = "vehicle-marker-${vehicle.id}"
                val bitmap = MarkerBitmapFactory.create(
                    text = "₺${vehicle.pricePerDay.toInt()}",
                    backgroundColor = VehicleType.colorFor(vehicle.type).toArgb(),
                    density = density,
                )
                style.addImage(imageId, bitmap)
                manager.create(
                    SymbolOptions()
                        .withLatLng(LatLng(vehicle.latitude, vehicle.longitude))
                        .withIconImage(imageId)
                        .withIconAnchor("bottom")
                        .withData(JsonPrimitive(vehicle.id)),
                )
            }

            if (!hasCenteredOnVehicles && uiState.filteredVehicles.isNotEmpty()) {
                hasCenteredOnVehicles = true
                val avgLat = uiState.filteredVehicles.map { it.latitude }.average()
                val avgLon = uiState.filteredVehicles.map { it.longitude }.average()
                mapLibreMap?.easeCamera(CameraUpdateFactory.newLatLngZoom(LatLng(avgLat, avgLon), 12.5), 600)
            }
        }

        DisposableEffect(symbolManager) {
            val manager = symbolManager
            if (manager == null) {
                onDispose {}
            } else {
                val listener = OnSymbolClickListener { symbol: Symbol ->
                    val vehicleId = symbol.data?.asString
                    val vehicle = viewModel.uiState.value.vehicles.find { it.id == vehicleId }
                    if (vehicle != null) {
                        selectedVehicle = vehicle
                    }
                    true
                }
                manager.addClickListener(listener)
                onDispose { manager.removeClickListener(listener) }
            }
        }

        if (uiState.isLoading && uiState.vehicles.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp),
        ) {
            SearchBar()

            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(12.dp))
                ErrorBanner(message = uiState.error.orEmpty(), onRetry = viewModel::loadVehicles)
            }
        }

        IconButton(
            onClick = {
                val map = mapLibreMap
                val location = myLocation
                if (map != null && location != null) {
                    map.easeCamera(CameraUpdateFactory.newLatLngZoom(location, 15.0), 600)
                } else {
                    scope.launch { snackbarHostState.showSnackbar("Konumunuz bulunamadı.") }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 236.dp, end = 20.dp)
                .size(46.dp)
                .clip(CircleShape)
                .background(Color.White),
        ) {
            Icon(Icons.Filled.MyLocation, contentDescription = "Konumuma git", tint = Primary)
        }

        BottomVehiclesCard(
            modifier = Modifier.align(Alignment.BottomCenter),
            uiState = uiState,
            onTypeSelected = { type ->
                viewModel.onTypeSelected(type)
                focusCameraOnVehicles(mapLibreMap, viewModel.uiState.value.filteredVehicles)
            },
            onFindNearestClick = {
                val location = myLocation
                if (location == null) {
                    scope.launch { snackbarHostState.showSnackbar("Konumunuz bulunamadı, konum iznini kontrol edin.") }
                } else {
                    val nearest = viewModel.findNearestVehicle(location.latitude, location.longitude)
                    if (nearest == null) {
                        scope.launch { snackbarHostState.showSnackbar("Uygun araç bulunamadı.") }
                    } else {
                        mapLibreMap?.easeCamera(
                            CameraUpdateFactory.newLatLngZoom(LatLng(nearest.latitude, nearest.longitude), 15.0),
                            700,
                        )
                    }
                }
            },
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 260.dp),
        ) { data ->
            Snackbar(snackbarData = data)
        }

        val vehicle = selectedVehicle
        if (vehicle != null) {
            val distanceLabel = myLocation?.let {
                formatDistanceMeters(haversineMeters(it.latitude, it.longitude, vehicle.latitude, vehicle.longitude))
            }
            VehicleDetailSheet(
                vehicle = vehicle,
                distanceLabel = distanceLabel,
                sheetState = sheetState,
                onDismiss = { selectedVehicle = null },
                onReserve = {
                    selectedVehicle = null
                    onNavigateToReservation(vehicle.id)
                },
                onUnlock = {
                    selectedVehicle = null
                    scope.launch { snackbarHostState.showSnackbar("Kilit açma yakında eklenecek.") }
                },
            )
        }
    }
}

private fun focusCameraOnVehicles(map: MapLibreMap?, vehicles: List<VehicleDto>) {
    if (map == null || vehicles.isEmpty()) return
    if (vehicles.size == 1) {
        val vehicle = vehicles.first()
        map.easeCamera(CameraUpdateFactory.newLatLngZoom(LatLng(vehicle.latitude, vehicle.longitude), 15.0), 700)
        return
    }
    val bounds = LatLngBounds.fromLatLngs(vehicles.map { LatLng(it.latitude, it.longitude) })
    map.easeCamera(CameraUpdateFactory.newLatLngBounds(bounds, 120), 700)
}

private fun formatDistanceMeters(meters: Double): String = if (meters < 1000) {
    "${meters.toInt()} m"
} else {
    "%.1f km".format(meters / 1000)
}

private fun updateMeMarker(style: Style, myLocation: LatLng?) {
    val source = style.getSourceAs<GeoJsonSource>(ME_SOURCE_ID) ?: return
    if (myLocation == null) {
        source.setGeoJson(FeatureCollection.fromFeatures(emptyList()))
    } else {
        source.setGeoJson(Point.fromLngLat(myLocation.longitude, myLocation.latitude))
    }
}

@SuppressLint("MissingPermission")
private fun startLocationUpdates(fusedClient: FusedLocationProviderClient, callback: LocationCallback) {
    val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5_000L)
        .setMinUpdateIntervalMillis(2_000L)
        .build()

    fusedClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            callback.onLocationResult(LocationResult.create(listOf(location)))
        }
    }
    fusedClient.requestLocationUpdates(request, callback, Looper.getMainLooper())
}

@Composable
private fun SearchBar() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 3.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Outlined.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Nereden araç alacaksın?",
                fontSize = 15.sp,
                color = TextSecondary,
                modifier = Modifier.weight(1f),
            )
            Icon(Icons.Outlined.FilterList, contentDescription = "Filtrele", tint = TextSecondary, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun ErrorBanner(message: String, onRetry: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = message, fontSize = 13.sp, color = Danger, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Tekrar dene",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary,
                modifier = Modifier.clickable(onClick = onRetry),
            )
        }
    }
}

@Composable
private fun BottomVehiclesCard(
    modifier: Modifier = Modifier,
    uiState: MapUiState,
    onTypeSelected: (String?) -> Unit,
    onFindNearestClick: () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = Color.White,
        shadowElevation = 8.dp,
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Yakınında ${uiState.filteredVehicles.size} araç",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Haritadaki müsait araçları görüntülüyorsun",
                fontSize = 13.sp,
                color = TextSecondary,
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TypeChip(
                    label = "Tümü",
                    dotColor = Primary,
                    selected = uiState.selectedType == null,
                    onClick = { onTypeSelected(null) },
                )
                uiState.availableTypes.forEach { type ->
                    TypeChip(
                        label = VehicleType.labelFor(type),
                        dotColor = VehicleType.colorFor(type),
                        selected = uiState.selectedType == type,
                        onClick = { onTypeSelected(type) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onFindNearestClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
            ) {
                Icon(Icons.Outlined.NearMe, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "En Yakın Aracı Bul", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun TypeChip(
    label: String,
    dotColor: Color,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = if (selected) dotColor.copy(alpha = 0.14f) else Background,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(dotColor),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (selected) TextPrimary else TextSecondary,
            )
        }
    }
}
