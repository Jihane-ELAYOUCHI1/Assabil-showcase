package com.assabil.ui.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.assabil.ui.navigation.Screen
import com.assabil.ui.theme.AsSabilColors
import com.assabil.viewmodel.KiblaViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlin.math.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun KiblaScreen(
    navController: NavController,
    viewModel: KiblaViewModel = hiltViewModel()
) {
    val locationPermission = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)
    val kiblaInfo by viewModel.kiblaInfo.collectAsState()
    val compassBearing by viewModel.compassBearing.collectAsState()

    // Register sensor
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val rotationMatrix = FloatArray(9)
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                val orientation = FloatArray(3)
                SensorManager.getOrientation(rotationMatrix, orientation)
                val azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                viewModel.updateBearing((azimuth + 360) % 360)
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        sensor?.let { sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI) }
        onDispose { sensorManager.unregisterListener(listener) }
    }

    LaunchedEffect(locationPermission.status.isGranted) {
        if (locationPermission.status.isGranted) viewModel.fetchLocation()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AsSabilColors.Cream)
    ) {
        // ── Header ────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(AsSabilColors.Espresso, Color(0xFF5C3020))
                    )
                )
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text("Direction Qibla", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White)
                    Text("اتجاه القبلة", fontSize = 14.sp, color = AsSabilColors.SandStorm.copy(0.8f))
                }
                IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                    Icon(Icons.Default.Settings, null, tint = Color.White)
                }
            }
        }

        if (!locationPermission.status.isGranted) {
            // ── Permission Request ─────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize().padding(32.dp)
            ) {
                Text("📍", fontSize = 52.sp)
                Spacer(Modifier.height(20.dp))
                Text(
                    "Accès à la localisation requis",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = AsSabilColors.Espresso
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Pour calculer la direction de la Kaaba, l'application a besoin de votre position.",
                    fontSize = 14.sp,
                    color = AsSabilColors.TextLight,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = { locationPermission.launchPermissionRequest() },
                    colors = ButtonDefaults.buttonColors(containerColor = AsSabilColors.Caramel),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Autoriser l'accès", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(Modifier.height(16.dp))

                // ── Compass ───────────────────────────────────────
                kiblaInfo?.let { info ->
                    val qiblaAngleFromNorth = info.qiblaDirection
                    val compassRotation = compassBearing
                    val arrowRotation = (qiblaAngleFromNorth - compassRotation + 360) % 360

                    val animRotation by animateFloatAsState(
                        targetValue = arrowRotation.toFloat(),
                        animationSpec = spring(stiffness = Spring.StiffnessLow),
                        label = "compass_arrow"
                    )

                    KiblaCompass(
                        compassBearing = compassBearing,
                        arrowRotation = animRotation,
                        modifier = Modifier.size(280.dp)
                    )

                    Spacer(Modifier.height(24.dp))

                    // ── Direction degrees ──────────────────────────
                    Text(
                        "${qiblaAngleFromNorth.toInt()}°",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = AsSabilColors.Espresso
                    )
                    Text("Direction de la Kaaba", fontSize = 13.sp, color = AsSabilColors.TextLight)

                    Spacer(Modifier.height(24.dp))

                    // ── Info Cards ────────────────────────────────
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        InfoCard(
                            emoji = "🕋",
                            label = "Distance",
                            value = "${info.distanceToKaaba.toInt()} km",
                            modifier = Modifier.weight(1f)
                        )
                        InfoCard(
                            emoji = "📍",
                            label = "Latitude",
                            value = "%.2f°".format(info.userLatitude),
                            modifier = Modifier.weight(1f)
                        )
                        InfoCard(
                            emoji = "🧭",
                            label = "Longitude",
                            value = "%.2f°".format(info.userLongitude),
                            modifier = Modifier.weight(1f)
                        )
                    }
                } ?: run {
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = AsSabilColors.Caramel)
                            Spacer(Modifier.height(12.dp))
                            Text("Calcul en cours...", color = AsSabilColors.TextLight)
                        }
                    }
                }

                Spacer(Modifier.height(100.dp))
            }
        }
    }
}

// ─── COMPASS CANVAS ───────────────────────────────────────────────
@Composable
private fun KiblaCompass(
    compassBearing: Double,
    arrowRotation: Float,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        // Outer ring
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = this.center
            val radius = size.minDimension / 2f

            // Shadow ring
            drawCircle(
                color = AsSabilColors.Espresso.copy(alpha = 0.08f),
                radius = radius + 8.dp.toPx(),
                center = center + androidx.compose.ui.geometry.Offset(0f, 4.dp.toPx())
            )

            // Outer circle (vintage parchment)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(AsSabilColors.SandStorm, AsSabilColors.Cream),
                    radius = radius
                ),
                radius = radius,
                center = center
            )

            // Compass ring border
            drawCircle(
                color = AsSabilColors.Caramel,
                radius = radius,
                center = center,
                style = Stroke(3.dp.toPx())
            )

            // Inner circle
            drawCircle(
                color = AsSabilColors.White,
                radius = radius * 0.78f,
                center = center
            )
            drawCircle(
                color = AsSabilColors.Leafy.copy(0.4f),
                radius = radius * 0.78f,
                center = center,
                style = Stroke(1.5.dp.toPx())
            )

            // Cardinal directions (N, S, E, W)
            val cardinals = listOf("N" to 0f, "E" to 90f, "S" to 180f, "W" to 270f)
            cardinals.forEach { (dir, angle) ->
                val rad = Math.toRadians((angle - compassBearing).toDouble())
                val tickX = center.x + (radius * 0.88f) * sin(rad).toFloat()
                val tickY = center.y - (radius * 0.88f) * cos(rad).toFloat()
                drawCircle(
                    color = if (dir == "N") AsSabilColors.Cinnamon else AsSabilColors.Caramel,
                    radius = if (dir == "N") 6.dp.toPx() else 4.dp.toPx(),
                    center = androidx.compose.ui.geometry.Offset(tickX, tickY)
                )
            }

            // Tick marks (every 10°)
            for (i in 0 until 36) {
                val angle = i * 10.0 - compassBearing
                val rad = Math.toRadians(angle)
                val outer = radius * 0.96f
                val inner = if (i % 9 == 0) radius * 0.84f else radius * 0.91f
                val startX = center.x + outer * sin(rad).toFloat()
                val startY = center.y - outer * cos(rad).toFloat()
                val endX = center.x + inner * sin(rad).toFloat()
                val endY = center.y - inner * cos(rad).toFloat()
                drawLine(
                    color = AsSabilColors.Caramel.copy(if (i % 9 == 0) 0.8f else 0.3f),
                    start = androidx.compose.ui.geometry.Offset(startX, startY),
                    end = androidx.compose.ui.geometry.Offset(endX, endY),
                    strokeWidth = if (i % 9 == 0) 2.dp.toPx() else 1.dp.toPx()
                )
            }
        }

        // Qibla arrow (rotates to point toward Kaaba)
        Canvas(
            modifier = Modifier
                .fillMaxSize(0.4f)
                .rotate(arrowRotation)
        ) {
            val center = this.center
            val arrowLen = size.minDimension * 0.85f

            // Arrow pointing UP = Qibla direction
            val path = Path().apply {
                moveTo(center.x, center.y - arrowLen / 2f)
                lineTo(center.x + arrowLen * 0.13f, center.y + arrowLen * 0.1f)
                lineTo(center.x, center.y - arrowLen * 0.05f)
                lineTo(center.x - arrowLen * 0.13f, center.y + arrowLen * 0.1f)
                close()
            }
            drawPath(
                path = path,
                brush = Brush.verticalGradient(
                    colors = listOf(AsSabilColors.Caramel, AsSabilColors.Espresso)
                )
            )

            // Center dot
            drawCircle(
                color = AsSabilColors.Espresso,
                radius = 6.dp.toPx(),
                center = center
            )
            drawCircle(
                color = AsSabilColors.SandStorm,
                radius = 3.dp.toPx(),
                center = center
            )
        }

        // Kaaba emoji at top of arrow when aligned
        Text("🕋", fontSize = 14.sp, modifier = Modifier.offset(y = (-110).dp))
    }
}

// ─── INFO CARD ────────────────────────────────────────────────────
@Composable
private fun InfoCard(
    emoji: String,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp)
        ) {
            Text(emoji, fontSize = 20.sp)
            Spacer(Modifier.height(4.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AsSabilColors.Espresso)
            Text(label, fontSize = 10.sp, color = AsSabilColors.TextLight)
        }
    }
}
