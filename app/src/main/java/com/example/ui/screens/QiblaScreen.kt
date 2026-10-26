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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
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
    val colors = FinancyTheme.colors
    val isBangla = uiState.isBangla
    val heading = uiState.compassHeading
    val qiblaBearing = uiState.qiblaBearing
    val isAligned = uiState.isAlignedWithQibla

    val animatedRotation by animateFloatAsState(targetValue = -heading, label = "compassRotate")

    val bannerBg by animateColorAsState(
        targetValue = if (isAligned) colors.neonGreen.copy(alpha = 0.15f) else colors.surfaceCard,
        label = "bannerColor"
    )

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBangla) "কিবলা কম্পাস" else "Qibla Direction",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = colors.textPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.surfaceCard
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
            Spacer(modifier = Modifier.height(12.dp))

            // 1. Status Banner with Glow
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = bannerBg),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isAligned) colors.neonGreen else colors.borderSubtle
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
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
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAligned) {
                            if (isBangla) "মাশাআল্লাহ! আপনি সঠিক কিবলামুখী আছেন" else "Aligned with Qibla!"
                        } else {
                            if (isBangla) "ফোনটি অনুভূমিক রেখে নির্দেশকটি কিবলামুখী করুন" else "Rotate phone until aligned with Kaaba"
                        },
                        fontWeight = FontWeight.Bold,
                        color = if (isAligned) colors.neonGreen else colors.textPrimary,
                        fontSize = 13.5.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Financy Bento Degree Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.borderSubtle)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isBangla) "আপনার দিক" else "Device Heading",
                            fontSize = 11.5.sp,
                            color = colors.textSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isBangla) "${DateUtil.toBanglaDigits(heading.toInt().toString())}°"
                            else "${heading.toInt()}°",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = colors.textPrimary
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.neonGreen.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isBangla) "কিবলার কোণ (${uiState.selectedCity.nameBn})"
                            else "Qibla (${uiState.selectedCity.nameEn.split(" ")[0]})",
                            fontSize = 11.5.sp,
                            color = colors.neonGreen,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isBangla) "${DateUtil.toBanglaDigits(qiblaBearing.toInt().toString())}°"
                            else "${qiblaBearing.toInt()}°",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = colors.neonGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.1f))

            // 3. Financy Tactile Compass Dial
            val dialBorderColor = if (isAligned) colors.neonGreen else colors.borderSubtle
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(CircleShape)
                    .background(colors.surfaceCard)
                    .border(2.dp, dialBorderColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val radius = size.width / 2f - 20f

                    // Draw outer dial ring
                    drawCircle(
                        color = colors.borderSubtle,
                        radius = radius,
                        center = center,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f)
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
                            else colors.textSecondary.copy(alpha = 0.5f)

                            drawLine(
                                color = tickColor,
                                start = Offset(startX, startY),
                                end = Offset(endX, endY),
                                strokeWidth = if (i % 3 == 0) 4f else 2f
                            )
                        }

                        // Qibla needle pointer towards qiblaBearing
                        rotate(qiblaBearing, pivot = center) {
                            // Emerald neon triangle pointing to Qibla
                            val pointerPath = Path().apply {
                                moveTo(center.x, center.y - radius + 10f)
                                lineTo(center.x - 14f, center.y - radius + 55f)
                                lineTo(center.x + 14f, center.y - radius + 55f)
                                close()
                            }
                            drawPath(pointerPath, color = colors.neonGreen)

                            // Small indicator dot
                            drawCircle(
                                color = colors.emeraldPrimary,
                                radius = 9f,
                                center = Offset(center.x, center.y - radius + 70f)
                            )
                        }
                    }

                    // Center pivot
                    drawCircle(color = colors.neonGreen, radius = 12f, center = center)
                    drawCircle(color = colors.surfaceCard, radius = 6f, center = center)
                }

                // Center Kaaba symbol
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🕋", fontSize = 28.sp)
                }
            }

            Spacer(modifier = Modifier.weight(0.1f))

            // 4. Financy Calibration Guide Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.borderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = colors.neonGreen.copy(alpha = 0.12f),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CompassCalibration,
                                contentDescription = null,
                                tint = colors.neonGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (isBangla) "কম্পাস সঠিক রাখতে ফোনটিকে সমতল রাখুন এবং প্রয়োজনমতো শূন্যে ৮ (আট) আকারে ঘুরিয়ে ক্যালিব্রেট করুন।"
                        else "Keep phone flat horizontally and wave in figure-8 for accurate compass calibration.",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary,
                        fontSize = 11.5.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
