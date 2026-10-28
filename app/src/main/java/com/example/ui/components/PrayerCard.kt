package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PrayerLogEntity
import com.example.data.model.PrayerStatus
import com.example.data.model.PrayerType
import com.example.ui.theme.FinancyTheme
import com.example.util.HapticUtil

/**
 * iOS Heavy Glass Prayer Card with Waqt-specific vibrant glass gradients,
 * frosted segmented pill status selector, specular rim reflections, and active waqt glow.
 */
@Composable
fun PrayerCard(
    prayerType: PrayerType,
    timeString: String,
    log: PrayerLogEntity?,
    isBangla: Boolean,
    onStatusSelected: (String) -> Unit,
    onToggleSunnah: () -> Unit,
    onSaveNotes: ((String) -> Unit)? = null,
    isActiveWaqt: Boolean = false,
    remainingMinutes: Int? = null,
    modifier: Modifier = Modifier
) {
    val colors = FinancyTheme.colors
    val context = LocalContext.current
    val currentStatusCode = log?.status ?: "pending"
    val isSunnahDone = (log?.isSunnah ?: 0) == 1

    // Waqt-Specific Visual Tokens
    val waqtTheme = remember(prayerType, colors.isDark) {
        getWaqtGlassTheme(prayerType, colors.isDark)
    }

    val cardShape = RoundedCornerShape(26.dp)

    // Specular Rim Highlight Brush
    val rimBrush = if (isActiveWaqt) {
        Brush.linearGradient(
            colors = listOf(
                waqtTheme.accentColor.copy(alpha = 0.90f),
                waqtTheme.accentColor.copy(alpha = 0.35f),
                Color.White.copy(alpha = if (colors.isDark) 0.30f else 0.85f)
            )
        )
    } else {
        waqtTheme.rimBrush
    }

    val elevation = if (isActiveWaqt) (if (colors.isDark) 14.dp else 8.dp) else (if (colors.isDark) 6.dp else 2.dp)
    val spotColor = if (isActiveWaqt) waqtTheme.accentColor.copy(alpha = 0.45f) else Color.Black.copy(alpha = if (colors.isDark) 0.35f else 0.06f)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .testTag("prayer_card_${prayerType.keyName}"),
        shape = cardShape,
        color = Color.Transparent,
        shadowElevation = elevation,
        tonalElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(cardShape)
                .background(waqtTheme.backgroundBrush)
                .border(
                    BorderStroke(
                        width = if (isActiveWaqt) 1.5.dp else 1.dp,
                        brush = rimBrush
                    ),
                    cardShape
                )
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {

                // -------------------------------------------------------------
                // ROW 1: HEADER (Icon Badge, Waqt Name & Rakah, Start-End Time)
                // -------------------------------------------------------------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(11.dp)
                    ) {
                        // Emoji Badge inside a Frosted Squircle
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(waqtTheme.iconBgColor)
                                .border(
                                    BorderStroke(
                                        width = 0.8.dp,
                                        color = if (colors.isDark) Color.White.copy(alpha = 0.20f) else Color.White.copy(alpha = 0.80f)
                                    ),
                                    RoundedCornerShape(14.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = waqtTheme.emoji,
                                fontSize = 21.sp
                            )
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isBangla) prayerType.banglaName else prayerType.englishName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (colors.isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A),
                                    fontSize = 16.sp
                                )

                                if (prayerType.isFardh) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = waqtTheme.accentColor.copy(alpha = if (colors.isDark) 0.22f else 0.14f),
                                        shape = RoundedCornerShape(999.dp),
                                        border = BorderStroke(
                                            0.6.dp,
                                            waqtTheme.accentColor.copy(alpha = if (colors.isDark) 0.45f else 0.35f)
                                        )
                                    ) {
                                        Text(
                                            text = if (isBangla) "ফরয" else "Fardh",
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.5.dp),
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = waqtTheme.accentColor
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = if (isBangla) prayerType.rakahInfoBn else prayerType.rakahInfoEn,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (colors.isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Right Side: Waqt Time Range & Arabic Name
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = timeString,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isActiveWaqt) waqtTheme.accentColor else (if (colors.isDark) Color(0xFFE2E8F0) else Color(0xFF1E293B)),
                            fontSize = 13.5.sp
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            text = prayerType.arabicName,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (colors.isDark) Color(0xFF64748B) else Color(0xFF94A3B8),
                            fontSize = 11.5.sp
                        )
                    }
                }

                // -------------------------------------------------------------
                // ACTIVE WAQT LIVE BADGE (If Currently Active Waqt)
                // -------------------------------------------------------------
                AnimatedVisibility(visible = isActiveWaqt) {
                    Column {
                        Spacer(modifier = Modifier.height(9.dp))
                        Surface(
                            color = waqtTheme.accentColor.copy(alpha = if (colors.isDark) 0.18f else 0.12f),
                            shape = RoundedCornerShape(999.dp),
                            border = BorderStroke(
                                0.8.dp,
                                waqtTheme.accentColor.copy(alpha = if (colors.isDark) 0.50f else 0.35f)
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(waqtTheme.accentColor)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                val activeText = if (remainingMinutes != null) {
                                    val formattedRem = com.example.util.PrayerTimeCalculator.formatRemainingTime(remainingMinutes, isBangla)
                                    if (isBangla) "চলতি ওয়াক্ত • $formattedRem বাকি" else "Active Waqt • $formattedRem left"
                                } else {
                                    if (isBangla) "চলতি ওয়াক্ত" else "Active Waqt"
                                }
                                Text(
                                    text = activeText,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = waqtTheme.accentColor
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(11.dp))

                // -------------------------------------------------------------
                // ROW 2: iOS FROSTED GLASS SEGMENTED PILL DOCK
                // -------------------------------------------------------------
                IosPrayerStatusSegmentedDock(
                    currentStatusCode = currentStatusCode,
                    prayerTypeKey = prayerType.keyName,
                    isBangla = isBangla,
                    onStatusSelected = { status ->
                        HapticUtil.lightTick(context)
                        // If user clicks the already active status, toggle back to pending
                        if (currentStatusCode == status) {
                            onStatusSelected("pending")
                        } else {
                            onStatusSelected(status)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // -------------------------------------------------------------
                // ROW 3: FOOTER (Minimal iOS Glass Sunnah Toggle Pill)
                // -------------------------------------------------------------
                IosGlassSunnahPill(
                    isSunnahDone = isSunnahDone,
                    prayerType = prayerType,
                    isBangla = isBangla,
                    accentColor = waqtTheme.accentColor,
                    onClick = {
                        HapticUtil.lightTick(context)
                        onToggleSunnah()
                    }
                )
            }
        }
    }
}

/**
 * iOS Frosted Segmented Pill Dock with smooth bouncy sliding feedback.
 */
@Composable
private fun IosPrayerStatusSegmentedDock(
    currentStatusCode: String,
    prayerTypeKey: String,
    isBangla: Boolean,
    onStatusSelected: (String) -> Unit
) {
    val colors = FinancyTheme.colors
    val dockShape = RoundedCornerShape(999.dp)

    val dockBackground = if (colors.isDark) {
        Color.Black.copy(alpha = 0.35f)
    } else {
        Color(0xFFE2E8F0).copy(alpha = 0.65f)
    }

    val dockBorder = if (colors.isDark) {
        Color.White.copy(alpha = 0.08f)
    } else {
        Color.White.copy(alpha = 0.85f)
    }

    val items = remember(isBangla) {
        listOf(
            Triple(if (isBangla) "জামাতে" else "Jama'at", "jamaah", Color(0xFF10B981)),
            Triple(if (isBangla) "একাকী" else "Alone", "on_time", Color(0xFF0284C7)),
            Triple(if (isBangla) "কাযা" else "Qaza", "qaza", Color(0xFFF59E0B)),
            Triple(if (isBangla) "মিস" else "Missed", "missed", Color(0xFFF43F5E))
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(dockShape)
            .background(dockBackground)
            .border(BorderStroke(0.9.dp, dockBorder), dockShape)
            .padding(3.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            items.forEach { (label, code, activeTint) ->
                val isSelected = currentStatusCode == code
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()

                val scale by animateFloatAsState(
                    targetValue = if (isPressed) 0.94f else 1.0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "dock_item_scale"
                )

                val pillBg = if (isSelected) {
                    if (colors.isDark) {
                        Brush.linearGradient(
                            listOf(
                                activeTint.copy(alpha = 0.88f),
                                activeTint.copy(alpha = 0.72f)
                            )
                        )
                    } else {
                        Brush.linearGradient(
                            listOf(
                                activeTint.copy(alpha = 0.95f),
                                activeTint.copy(alpha = 0.85f)
                            )
                        )
                    }
                } else {
                    Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                }

                val pillBorder = if (isSelected) {
                    BorderStroke(
                        0.8.dp,
                        if (colors.isDark) Color.White.copy(alpha = 0.40f) else Color.White.copy(alpha = 0.75f)
                    )
                } else {
                    BorderStroke(0.dp, Color.Transparent)
                }

                val textColor by animateColorAsState(
                    targetValue = if (isSelected) {
                        Color.White
                    } else {
                        if (colors.isDark) Color(0xFF94A3B8) else Color(0xFF475569)
                    },
                    label = "dock_item_text"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .scale(scale)
                        .testTag("btn_${code}_$prayerTypeKey")
                        .shadow(
                            elevation = if (isSelected) 4.dp else 0.dp,
                            shape = dockShape,
                            spotColor = if (isSelected) activeTint.copy(alpha = 0.45f) else Color.Transparent
                        )
                        .clip(dockShape)
                        .background(pillBg)
                        .border(pillBorder, dockShape)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = { onStatusSelected(code) }
                        )
                        .padding(vertical = 6.5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                        color = textColor
                    )
                }
            }
        }
    }
}

/**
 * Minimal iOS Glass Sunnah Toggle Pill.
 */
@Composable
private fun IosGlassSunnahPill(
    isSunnahDone: Boolean,
    prayerType: PrayerType,
    isBangla: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    val colors = FinancyTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "sunnah_scale"
    )

    val pillShape = RoundedCornerShape(999.dp)

    val bgBrush = if (isSunnahDone) {
        if (colors.isDark) {
            Brush.linearGradient(
                listOf(
                    colors.emerald.copy(alpha = 0.22f),
                    colors.emeraldDark.copy(alpha = 0.32f)
                )
            )
        } else {
            Brush.linearGradient(
                listOf(
                    colors.emeraldSoft.copy(alpha = 0.70f),
                    colors.emeraldSoft.copy(alpha = 0.90f)
                )
            )
        }
    } else {
        Brush.linearGradient(
            listOf(
                if (colors.isDark) Color.White.copy(alpha = 0.04f) else Color.White.copy(alpha = 0.50f),
                if (colors.isDark) Color.White.copy(alpha = 0.02f) else Color(0xFFF1F5F9).copy(alpha = 0.60f)
            )
        )
    }

    val rimBrush = if (isSunnahDone) {
        Brush.linearGradient(
            listOf(
                colors.emerald.copy(alpha = 0.65f),
                colors.emerald.copy(alpha = 0.25f)
            )
        )
    } else {
        Brush.linearGradient(
            listOf(
                if (colors.isDark) Color.White.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.75f),
                if (colors.isDark) Color.White.copy(alpha = 0.04f) else Color(0xFFCBD5E1).copy(alpha = 0.35f)
            )
        )
    }

    val textColor = if (isSunnahDone) {
        colors.emerald
    } else {
        if (colors.isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .testTag("btn_sunnah_${prayerType.keyName}")
            .clip(pillShape)
            .background(bgBrush)
            .border(BorderStroke(0.8.dp, rimBrush), pillShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                // Checkbox Pill Indicator
                Box(
                    modifier = Modifier
                        .size(17.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSunnahDone) colors.emerald else (if (colors.isDark) Color.White.copy(alpha = 0.10f) else Color(0xFFCBD5E1).copy(alpha = 0.50f))
                        )
                        .border(
                            0.7.dp,
                            if (isSunnahDone) colors.emerald else (if (colors.isDark) Color.White.copy(alpha = 0.25f) else Color(0xFF94A3B8)),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSunnahDone) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(11.dp)
                        )
                    }
                }

                Text(
                    text = if (isBangla) "সুন্নত আদায় করেছি" else "Offered Sunnah",
                    fontSize = 11.5.sp,
                    fontWeight = if (isSunnahDone) FontWeight.Bold else FontWeight.Medium,
                    color = textColor
                )
            }

            // Optional Rakah hint badge
            val sunnahHint = when (prayerType) {
                PrayerType.FAJR -> if (isBangla) "২ রাকাত" else "2 Rakahs"
                PrayerType.DHUHR -> if (isBangla) "৪+২ রাকাত" else "4+2 Rakahs"
                PrayerType.ASR -> if (isBangla) "৪ রাকাত (মুস্তাহাব)" else "4 Rakahs"
                PrayerType.MAGHRIB -> if (isBangla) "২ রাকাত" else "2 Rakahs"
                PrayerType.ISHA -> if (isBangla) "২ রাকাত + ৩ বিতর" else "2 Rakahs + 3 Witr"
                else -> ""
            }

            if (sunnahHint.isNotEmpty()) {
                Text(
                    text = sunnahHint,
                    fontSize = 10.5.sp,
                    color = if (colors.isDark) Color(0xFF64748B) else Color(0xFF94A3B8)
                )
            }
        }
    }
}

/**
 * Waqt Theme Spec definition for dynamic heavy glass styling.
 */
private data class WaqtGlassTheme(
    val emoji: String,
    val accentColor: Color,
    val iconBgColor: Color,
    val backgroundBrush: Brush,
    val rimBrush: Brush
)

private fun getWaqtGlassTheme(prayerType: PrayerType, isDark: Boolean): WaqtGlassTheme {
    return when (prayerType) {
        PrayerType.FAJR -> {
            val accent = if (isDark) Color(0xFFC084FC) else Color(0xFF9333EA)
            val iconBg = if (isDark) Color(0xFF3B0764).copy(alpha = 0.35f) else Color(0xFFF3E8FF).copy(alpha = 0.85f)
            val bg = if (isDark) {
                Brush.linearGradient(
                    listOf(
                        Color(0xFF2E1065).copy(alpha = 0.40f),
                        Color(0xFF1E1B4B).copy(alpha = 0.50f),
                        Color(0xFF0F172A).copy(alpha = 0.70f)
                    )
                )
            } else {
                Brush.linearGradient(
                    listOf(
                        Color(0xFFFAF5FF).copy(alpha = 0.90f),
                        Color(0xFFFDF2F8).copy(alpha = 0.80f),
                        Color.White.copy(alpha = 0.95f)
                    )
                )
            }
            val rim = Brush.linearGradient(
                listOf(
                    if (isDark) Color(0xFFC084FC).copy(alpha = 0.45f) else Color.White,
                    if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE9D5FF).copy(alpha = 0.60f)
                )
            )
            WaqtGlassTheme("🌅", accent, iconBg, bg, rim)
        }
        PrayerType.DHUHR -> {
            val accent = if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7)
            val iconBg = if (isDark) Color(0xFF0369A1).copy(alpha = 0.30f) else Color(0xFFE0F2FE).copy(alpha = 0.85f)
            val bg = if (isDark) {
                Brush.linearGradient(
                    listOf(
                        Color(0xFF082F49).copy(alpha = 0.45f),
                        Color(0xFF0C4A6E).copy(alpha = 0.30f),
                        Color(0xFF0F172A).copy(alpha = 0.70f)
                    )
                )
            } else {
                Brush.linearGradient(
                    listOf(
                        Color(0xFFF0F9FF).copy(alpha = 0.92f),
                        Color(0xFFE0F2FE).copy(alpha = 0.80f),
                        Color.White.copy(alpha = 0.95f)
                    )
                )
            }
            val rim = Brush.linearGradient(
                listOf(
                    if (isDark) Color(0xFF38BDF8).copy(alpha = 0.45f) else Color.White,
                    if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFBAE6FD).copy(alpha = 0.60f)
                )
            )
            WaqtGlassTheme("☀️", accent, iconBg, bg, rim)
        }
        PrayerType.ASR -> {
            val accent = if (isDark) Color(0xFFF59E0B) else Color(0xFFD97706)
            val iconBg = if (isDark) Color(0xFF78350F).copy(alpha = 0.35f) else Color(0xFFFEF3C7).copy(alpha = 0.85f)
            val bg = if (isDark) {
                Brush.linearGradient(
                    listOf(
                        Color(0xFF451A03).copy(alpha = 0.42f),
                        Color(0xFF1E140A).copy(alpha = 0.50f),
                        Color(0xFF0F172A).copy(alpha = 0.70f)
                    )
                )
            } else {
                Brush.linearGradient(
                    listOf(
                        Color(0xFFFFFBEB).copy(alpha = 0.92f),
                        Color(0xFFFEF3C7).copy(alpha = 0.80f),
                        Color.White.copy(alpha = 0.95f)
                    )
                )
            }
            val rim = Brush.linearGradient(
                listOf(
                    if (isDark) Color(0xFFF59E0B).copy(alpha = 0.50f) else Color.White,
                    if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFFDE68A).copy(alpha = 0.60f)
                )
            )
            WaqtGlassTheme("🌤️", accent, iconBg, bg, rim)
        }
        PrayerType.MAGHRIB -> {
            val accent = if (isDark) Color(0xFFFB7185) else Color(0xFFE11D48)
            val iconBg = if (isDark) Color(0xFF881337).copy(alpha = 0.35f) else Color(0xFFFFE4E6).copy(alpha = 0.85f)
            val bg = if (isDark) {
                Brush.linearGradient(
                    listOf(
                        Color(0xFF4C0519).copy(alpha = 0.40f),
                        Color(0xFF3B0764).copy(alpha = 0.40f),
                        Color(0xFF0F172A).copy(alpha = 0.70f)
                    )
                )
            } else {
                Brush.linearGradient(
                    listOf(
                        Color(0xFFFFF1F2).copy(alpha = 0.92f),
                        Color(0xFFFAF5FF).copy(alpha = 0.80f),
                        Color.White.copy(alpha = 0.95f)
                    )
                )
            }
            val rim = Brush.linearGradient(
                listOf(
                    if (isDark) Color(0xFFFB7185).copy(alpha = 0.45f) else Color.White,
                    if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFFECDD3).copy(alpha = 0.60f)
                )
            )
            WaqtGlassTheme("🌇", accent, iconBg, bg, rim)
        }
        PrayerType.ISHA -> {
            val accent = if (isDark) Color(0xFF818CF8) else Color(0xFF4F46E5)
            val iconBg = if (isDark) Color(0xFF1E1B4B).copy(alpha = 0.40f) else Color(0xFFEEF2FF).copy(alpha = 0.85f)
            val bg = if (isDark) {
                Brush.linearGradient(
                    listOf(
                        Color(0xFF1E1B4B).copy(alpha = 0.45f),
                        Color(0xFF0F172A).copy(alpha = 0.60f),
                        Color(0xFF090D16).copy(alpha = 0.75f)
                    )
                )
            } else {
                Brush.linearGradient(
                    listOf(
                        Color(0xFFEEF2FF).copy(alpha = 0.92f),
                        Color(0xFFE0E7FF).copy(alpha = 0.80f),
                        Color.White.copy(alpha = 0.95f)
                    )
                )
            }
            val rim = Brush.linearGradient(
                listOf(
                    if (isDark) Color(0xFF818CF8).copy(alpha = 0.45f) else Color.White,
                    if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFC7D2FE).copy(alpha = 0.60f)
                )
            )
            WaqtGlassTheme("🌙", accent, iconBg, bg, rim)
        }
        else -> {
            val accent = if (isDark) Color(0xFF10B981) else Color(0xFF059669)
            val iconBg = if (isDark) Color(0xFF064E3B).copy(alpha = 0.35f) else Color(0xFFD1FAE5).copy(alpha = 0.85f)
            val bg = Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
            val rim = Brush.linearGradient(listOf(Color.White.copy(alpha = 0.2f), Color.Transparent))
            WaqtGlassTheme("✨", accent, iconBg, bg, rim)
        }
    }
}
