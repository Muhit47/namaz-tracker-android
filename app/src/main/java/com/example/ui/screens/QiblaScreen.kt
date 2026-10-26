package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.NamazUiState
import com.example.ui.viewmodel.NamazViewModel
import com.example.util.DateUtil
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QiblaScreen(
    uiState: NamazUiState,
    viewModel: NamazViewModel? = null,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    DisposableEffect(viewModel) {
        viewModel?.startCompass()
        onDispose {
            viewModel?.stopCompass()
        }
    }
    val isBangla = uiState.isBangla
    val heading = uiState.compassHeading
    val qiblaBearing = uiState.qiblaBearing
    val isAligned = uiState.isAlignedWithQibla

    val animatedRotation by animateFloatAsState(targetValue = -heading, label = "compassRotate")

    val bannerBg by animateColorAsState(
        targetValue = if (isAligned) Emerald50 else GlassCardWhite,
        label = "bannerColor"
    )

    Scaffold(
        containerColor = Slate50,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBangla) "কিবলা কম্পাস" else "Qibla Direction",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Slate900
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GlassCardWhite
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // 1. Status Banner
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = bannerBg),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isAligned) Emerald500 else GlassBorder
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("qibla_alignment_status")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isAligned) "🕋 " else "🧭 ",
                        fontSize = 20.sp
                    )
                    Text(
                        text = if (isAligned) {
                            if (isBangla) "মাশাআল্লাহ! আপনি সঠিক কিবলামুখী আছেন" else "Aligned with Qibla!"
                        } else {
                            if (isBangla) "ফোনটি সমান রেখে নির্দেশকটি কিবলামুখী করুন" else "Rotate phone until aligned with Kaaba"
                        },
                        fontWeight = FontWeight.Bold,
                        color = if (isAligned) Emerald700 else Slate900,
                        fontSize = 13.5.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 2. Degrees info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Surface(
                    color = GlassCardWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isBangla) "আপনার দিক" else "Device Heading",
                            fontSize = 11.sp,
                            color = Slate600
                        )
                        Text(
                            text = if (isBangla) "${DateUtil.toBanglaDigits(heading.toInt().toString())}°"
                            else "${heading.toInt()}°",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                    }
                }

                Surface(
                    color = Cyan50,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Cyan200),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isBangla) "কিবলার কোণ (${uiState.selectedCity.nameBn})"
                            else "Qibla (${uiState.selectedCity.nameEn.split(" ")[0]})",
                            fontSize = 11.sp,
                            color = Cyan700
                        )
                        Text(
                            text = if (isBangla) "${DateUtil.toBanglaDigits(qiblaBearing.toInt().toString())}°"
                            else "${qiblaBearing.toInt()}°",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Cyan700
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.1f))

            // 3. Canvas Compass Dial (Crisp Clean White Dial with Emerald Qibla pointer)
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(CircleShape)
                    .background(GlassCardWhite)
                    .border(2.dp, Slate200, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val radius = size.width / 2f - 20f

                    // Draw outer dial ring
                    drawCircle(
                        color = Slate200,
                        radius = radius,
                        center = center,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
                    )

                    // Draw cardinal tick marks rotated with heading
                    rotate(animatedRotation, pivot = center) {
                        // 12 ticks
                        for (i in 0 until 12) {
                            val angleRad = Math.toRadians((i * 30).toDouble())
                            val tickLength = if (i % 3 == 0) 22f else 12f
                            val startX = center.x + (radius - tickLength) * sin(angleRad).toFloat()
                            val startY = center.y - (radius - tickLength) * cos(angleRad).toFloat()
                            val endX = center.x + radius * sin(angleRad).toFloat()
                            val endY = center.y - radius * cos(angleRad).toFloat()

                            val tickColor = if (i == 0) Rose600 // North in Rose/Red
                            else Slate400

                            drawLine(
                                color = tickColor,
                                start = Offset(startX, startY),
                                end = Offset(endX, endY),
                                strokeWidth = if (i % 3 == 0) 4f else 2f
                            )
                        }

                        // Qibla needle pointer towards qiblaBearing
                        rotate(qiblaBearing, pivot = center) {
                            // Emerald triangle pointing to Qibla
                            val pointerPath = Path().apply {
                                moveTo(center.x, center.y - radius + 10f)
                                lineTo(center.x - 14f, center.y - radius + 55f)
                                lineTo(center.x + 14f, center.y - radius + 55f)
                                close()
                            }
                            drawPath(pointerPath, color = Emerald500)

                            // Small Emerald circle
                            drawCircle(
                                color = Emerald600,
                                radius = 9f,
                                center = Offset(center.x, center.y - radius + 70f)
                            )
                        }
                    }

                    // Center pivot
                    drawCircle(color = Emerald500, radius = 12f, center = center)
                    drawCircle(color = Color.White, radius = 6f, center = center)
                }

                // Center Kaaba symbol
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🕋", fontSize = 28.sp)
                }
            }

            Spacer(modifier = Modifier.weight(0.1f))

            // 4. Calibration Instructions
            Surface(
                color = GlassCardWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CompassCalibration,
                        contentDescription = null,
                        tint = Emerald600,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isBangla) "কম্পাস সঠিক রাখতে ফোনটিকে অনুভূমিকভাবে সমতল রাখুন এবং প্রয়োজনমতো শূন্যে ৮ (আট) আকারে ঘুরিয়ে নিন।"
                        else "Keep phone flat on a horizontal surface and wave in figure-8 for accurate compass calibration.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
