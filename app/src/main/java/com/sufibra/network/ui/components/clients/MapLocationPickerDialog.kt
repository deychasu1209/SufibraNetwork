package com.sufibra.network.ui.components.clients

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.sufibra.network.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private val LimaCenter = LatLng(-12.0464, -77.0428)

@Composable
fun MapLocationPickerDialog(
    currentLinkMaps: String,
    onDismiss: () -> Unit,
    onLocationSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    val initialTarget = remember(currentLinkMaps) {
        extractLatLngFromMapsLink(currentLinkMaps) ?: LimaCenter
    }
    val coroutineScope = rememberCoroutineScope()
    var useIntroPinAnimation by remember { mutableStateOf(true) }
    val pinTransition = rememberInfiniteTransition(label = "pin-transition")
    val pinOffsetY by pinTransition.animateFloat(
        initialValue = if (useIntroPinAnimation) -16f else -20f,
        targetValue = if (useIntroPinAnimation) -38f else -26f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (useIntroPinAnimation) 420 else 1250),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pin-offset"
    )
    val pinScale by pinTransition.animateFloat(
        initialValue = if (useIntroPinAnimation) 0.98f else 1f,
        targetValue = if (useIntroPinAnimation) 1.16f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (useIntroPinAnimation) 420 else 1250),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pin-scale"
    )
    val pinShadowScale by pinTransition.animateFloat(
        initialValue = if (useIntroPinAnimation) 1.1f else 1f,
        targetValue = if (useIntroPinAnimation) 0.68f else 0.86f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (useIntroPinAnimation) 420 else 1250),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pin-shadow-scale"
    )
    val pinShadowAlpha by pinTransition.animateFloat(
        initialValue = if (useIntroPinAnimation) 0.26f else 0.18f,
        targetValue = if (useIntroPinAnimation) 0.08f else 0.10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (useIntroPinAnimation) 420 else 1250),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pin-shadow-alpha"
    )

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialTarget, 16f)
    }

    var hasLocationPermission by remember { mutableStateOf(context.hasLocationPermission()) }
    var isLocationEnabled by remember { mutableStateOf(context.isLocationEnabled()) }
    var hasCenteredOnCurrentLocation by remember { mutableStateOf(false) }
    var locationHelpText by remember {
        mutableStateOf("Mueve el mapa y deja el pin sobre el punto exacto.")
    }
    val canUseLocation = hasLocationPermission && isLocationEnabled

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        isLocationEnabled = context.isLocationEnabled()
        locationHelpText = when {
            !hasLocationPermission ->
                "Necesitamos permiso de ubicación para seleccionar el Link Maps correctamente."
            !isLocationEnabled ->
                "Activa la ubicación del dispositivo para poder elegir el punto exacto en el mapa."
            else ->
                "Mueve el mapa y deja el pin sobre el punto exacto."
        }
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    LaunchedEffect(Unit) {
        delay(1700)
        useIntroPinAnimation = false
    }

    LaunchedEffect(hasLocationPermission, isLocationEnabled) {
        if (!hasLocationPermission) {
            locationHelpText =
                "Necesitamos permiso de ubicación para seleccionar el Link Maps correctamente."
            return@LaunchedEffect
        }

        if (!isLocationEnabled) {
            locationHelpText =
                "Activa la ubicación del dispositivo para poder elegir el punto exacto en el mapa."
            return@LaunchedEffect
        }

        if (!hasCenteredOnCurrentLocation) {
            val currentLocation = context.getCurrentLatLng()
            if (currentLocation != null) {
                cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(currentLocation, 18f))
                hasCenteredOnCurrentLocation = true
                locationHelpText =
                    "Tu ubicación actual ya está centrada. Ajusta el pin si hace falta."
            } else {
                locationHelpText =
                    "No se pudo obtener tu ubicación exacta todavía. Revisa el GPS e inténtalo nuevamente."
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = colorScheme.background
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                LocationPickerMap(
                    cameraPositionState = cameraPositionState,
                    hasLocationPermission = hasLocationPermission,
                    isLocationEnabled = isLocationEnabled
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.surface.copy(alpha = 0.95f)
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Seleccionar ubicación",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )
                        Text(
                            text = locationHelpText,
                            style = MaterialTheme.typography.bodySmall,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }

                FilledTonalIconButton(
                    onClick = {
                        isLocationEnabled = context.isLocationEnabled()

                        if (!hasLocationPermission) {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                            return@FilledTonalIconButton
                        }

                        if (!isLocationEnabled) {
                            locationHelpText =
                                "Activa la ubicación del dispositivo para poder centrar el mapa en tu posición."
                            context.openLocationSettings()
                            return@FilledTonalIconButton
                        }

                        coroutineScope.launch {
                            val currentLocation = context.getCurrentLatLng()
                            if (currentLocation != null) {
                                cameraPositionState.animate(
                                    update = CameraUpdateFactory.newLatLngZoom(currentLocation, 18f),
                                    durationMs = 900
                                )
                                locationHelpText =
                                    "Mapa centrado en tu ubicación actual. Ajusta el pin si hace falta."
                            } else {
                                locationHelpText =
                                    "No se pudo obtener tu ubicación actual. Verifica el GPS e inténtalo nuevamente."
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 108.dp, end = 16.dp)
                        .size(52.dp)
                        .shadow(8.dp, CircleShape),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = colorScheme.surface,
                        contentColor = colorScheme.primary
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_zona),
                        contentDescription = "Centrar en mi ubicación",
                        tint = colorScheme.primary
                    )
                }

                if (!canUseLocation) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 24.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colorScheme.surface.copy(alpha = 0.98f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 22.dp, vertical = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = if (hasLocationPermission) {
                                    "Activa la ubicación"
                                } else {
                                    "Permite la ubicación"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface
                            )

                            Text(
                                text = if (hasLocationPermission) {
                                    "Para seleccionar el Link Maps correctamente debes encender la ubicación del dispositivo. Mientras esté apagada no se podrá usar este mapa."
                                } else {
                                    "Para seleccionar el Link Maps correctamente necesitamos permiso de ubicación. Sin ese permiso no se podrá usar este mapa."
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = colorScheme.onSurfaceVariant
                            )

                            Button(
                                onClick = {
                                    if (hasLocationPermission) {
                                        context.openLocationSettings()
                                    } else {
                                        permissionLauncher.launch(
                                            arrayOf(
                                                Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.ACCESS_COARSE_LOCATION
                                            )
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(vertical = 14.dp)
                            ) {
                                Text(
                                    if (hasLocationPermission) {
                                        "Activar ubicación"
                                    } else {
                                        "Conceder permiso"
                                    }
                                )
                            }

                            if (hasLocationPermission) {
                                OutlinedButton(
                                    onClick = {
                                        isLocationEnabled = context.isLocationEnabled()
                                        if (isLocationEnabled) {
                                            locationHelpText =
                                                "Ubicación activada. Ya puedes mover el mapa y elegir el punto exacto."
                                            hasCenteredOnCurrentLocation = false
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    contentPadding = PaddingValues(vertical = 14.dp)
                                ) {
                                    Text("Ya activé la ubicación")
                                }
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier.align(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .offset(y = 10.dp)
                            .size(width = 22.dp, height = 8.dp)
                            .graphicsLayer {
                                scaleX = pinShadowScale
                                scaleY = pinShadowScale
                                alpha = pinShadowAlpha
                            }
                            .background(
                                color = colorScheme.scrim.copy(alpha = 0.55f),
                                shape = CircleShape
                            )
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.ic_ubicacion),
                        contentDescription = null,
                        tint = colorScheme.primary,
                        modifier = Modifier
                            .size(44.dp)
                            .graphicsLayer {
                                scaleX = pinScale
                                scaleY = pinScale
                            }
                            .offset(y = pinOffsetY.dp)
                    )
                }

                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        val selectedPoint = cameraPositionState.position.target

                        Text(
                            text = "Punto seleccionado",
                            style = MaterialTheme.typography.labelLarge,
                            color = colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )

                        if (!canUseLocation) {
                            Text(
                                text = "Activa la ubicación y concede el permiso para confirmar el punto.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = "Lat ${selectedPoint.latitude.formatCoordinate()} · Lng ${selectedPoint.longitude.formatCoordinate()}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorScheme.onSurface
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = onDismiss,
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 14.dp)
                            ) {
                                Text("Cancelar")
                            }

                            Button(
                                onClick = {
                                    val selectedMapsLink =
                                        "https://www.google.com/maps/search/?api=1&query=${selectedPoint.latitude},${selectedPoint.longitude}"
                                    onLocationSelected(selectedMapsLink)
                                },
                                enabled = canUseLocation,
                                modifier = Modifier.weight(1.35f),
                                contentPadding = PaddingValues(vertical = 14.dp)
                            ) {
                                Text("Confirmar ubicación")
                            }
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
private fun LocationPickerMap(
    cameraPositionState: CameraPositionState,
    hasLocationPermission: Boolean,
    isLocationEnabled: Boolean
) {
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = hasLocationPermission && isLocationEnabled
        ),
        uiSettings = MapUiSettings(
            compassEnabled = true,
            myLocationButtonEnabled = hasLocationPermission && isLocationEnabled,
            zoomControlsEnabled = false
        )
    )
}

private fun extractLatLngFromMapsLink(link: String): LatLng? {
    val queryIndex = link.indexOf("query=")
    if (queryIndex == -1) return null

    val coordinates = link.substring(queryIndex + 6).substringBefore("&")
    val parts = coordinates.split(",")
    if (parts.size != 2) return null

    val latitude = parts[0].toDoubleOrNull() ?: return null
    val longitude = parts[1].toDoubleOrNull() ?: return null
    return LatLng(latitude, longitude)
}

private fun Double.formatCoordinate(): String {
    return String.format("%.6f", this)
}

private fun Context.hasLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
}

private fun Context.isLocationEnabled(): Boolean {
    val locationManager = getSystemService(Context.LOCATION_SERVICE) as? LocationManager
    return locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true ||
        locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true
}

private fun Context.openLocationSettings() {
    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    })
}

@SuppressLint("MissingPermission")
private suspend fun Context.getCurrentLatLng(): LatLng? {
    if (!isLocationEnabled()) return null

    val client = LocationServices.getFusedLocationProviderClient(this)

    client.lastLocation.await()?.let { lastLocation ->
        return LatLng(lastLocation.latitude, lastLocation.longitude)
    }

    val cancellationTokenSource = CancellationTokenSource()
    val currentLocation = client
        .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
        .await()

    return currentLocation?.let { LatLng(it.latitude, it.longitude) }
}
