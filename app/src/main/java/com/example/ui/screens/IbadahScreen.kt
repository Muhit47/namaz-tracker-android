package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.IosGlassPillButton
import com.example.ui.theme.FinancyTheme
import com.example.ui.viewmodel.NamazUiState
import com.example.ui.viewmodel.NamazViewModel
import com.example.util.DateUtil
import com.example.util.HapticUtil
import java.util.Calendar

enum class IbadahSubScreen {
    NONE,
    DUA,
    TASBIH,
    QIBLA,
    PRAYER_GUIDE,
    WUDU_GUIDE,
    ASMAUL_HUSNA
}

/**
 * iOS 18 Heavy Glassmorphic Ibadah Hub Screen (ইবাদাহ ও আমল)
 * Features Modern Pastel Aura Bento Architecture, in-card direct interactive Dhikr tap counter,
 * smart contextual live data, and 100% high-contrast typography in both Dark and Light modes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IbadahScreen(
    uiState: NamazUiState,
    viewModel: NamazViewModel,
    modifier: Modifier = Modifier
) {
    val isBangla = uiState.isBangla
    var activeSubScreen by rememberSaveable { mutableStateOf(IbadahSubScreen.NONE) }

    BackHandler(enabled = activeSubScreen != IbadahSubScreen.NONE) {
        activeSubScreen = IbadahSubScreen.NONE
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = activeSubScreen == IbadahSubScreen.NONE,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            IbadahHubContent(
                uiState = uiState,
                viewModel = viewModel,
                isBangla = isBangla,
                onSelect = { activeSubScreen = it },
                modifier = modifier
            )
        }

        AnimatedVisibility(
            visible = activeSubScreen == IbadahSubScreen.DUA,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            DuaAdhkarScreen(
                isBangla = isBangla,
                onBack = { activeSubScreen = IbadahSubScreen.NONE }
            )
        }

        AnimatedVisibility(
            visible = activeSubScreen == IbadahSubScreen.TASBIH,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            TasbihScreen(
                uiState = uiState,
                viewModel = viewModel,
                onBack = { activeSubScreen = IbadahSubScreen.NONE }
            )
        }

        AnimatedVisibility(
            visible = activeSubScreen == IbadahSubScreen.QIBLA,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            QiblaScreen(
                uiState = uiState,
                viewModel = viewModel,
                onBack = { activeSubScreen = IbadahSubScreen.NONE }
            )
        }

        AnimatedVisibility(
            visible = activeSubScreen == IbadahSubScreen.PRAYER_GUIDE,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            GuidesScreen(
                isBangla = isBangla,
                initialSection = 0,
                onBack = { activeSubScreen = IbadahSubScreen.NONE }
            )
        }

        AnimatedVisibility(
            visible = activeSubScreen == IbadahSubScreen.WUDU_GUIDE,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            GuidesScreen(
                isBangla = isBangla,
                initialSection = 1,
                onBack = { activeSubScreen = IbadahSubScreen.NONE }
            )
        }

        AnimatedVisibility(
            visible = activeSubScreen == IbadahSubScreen.ASMAUL_HUSNA,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            AsmaulHusnaScreen(
                isBangla = isBangla,
                onBack = { activeSubScreen = IbadahSubScreen.NONE }
            )
        }
    }
}

@Composable
private fun IbadahHubContent(
    uiState: NamazUiState,
    viewModel: NamazViewModel,
    isBangla: Boolean,
    onSelect: (IbadahSubScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = FinancyTheme.colors
    val context = LocalContext.current

    // In-Card Interactive Dhikr Counter State (Default target 100)
    var dailyDhikrCount by rememberSaveable { mutableIntStateOf(33) }
    val dhikrTarget = 100

    val titleColor = if (colors.isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val subtitleColor = if (colors.isDark) Color(0xFF94A3B8) else Color(0xFF475569)

    // Hijri date string
    val hijriDateStr = remember(isBangla) {
        DateUtil.getApproximateHijriDate(Calendar.getInstance(), isBangla)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // Ambient Pastel Glow Canvas
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            if (colors.isDark) Color(0xFF08141F) else Color(0xFFF1F5F9),
                            colors.background,
                            if (colors.isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)
                        )
                    )
                )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // -------------------------------------------------------------
            // 1. HEADER: Spiritual Hub Title & Hijri Date Pill
            // -------------------------------------------------------------
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF14B8A6).copy(alpha = 0.2f),
                                modifier = Modifier.size(6.dp)
                            ) {}
                            Text(
                                text = if (isBangla) "আধ্যাত্মিক হাব" else "SPIRITUAL HUB",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (colors.isDark) Color(0xFF2DD4BF) else Color(0xFF0D9488),
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isBangla) "ইবাদাহ ও আমল" else "Ibadah & Supplications",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = titleColor
                        )
                    }

                    // Hijri Date Pill (Glass)
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (colors.isDark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.90f),
                        border = BorderStroke(
                            1.dp,
                            if (colors.isDark) Color.White.copy(alpha = 0.15f) else Color(0xFFCBD5E1)
                        ),
                        shadowElevation = if (colors.isDark) 0.dp else 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(text = "🌙", fontSize = 12.sp)
                            Text(
                                text = hijriDateStr,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = titleColor
                            )
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // 2. HERO CARD: Daily Dhikr with In-Card Circular Tap Counter
            // -------------------------------------------------------------
            item {
                HeroDhikrGlassCard(
                    isBangla = isBangla,
                    isDark = colors.isDark,
                    titleColor = titleColor,
                    subtitleColor = subtitleColor,
                    count = dailyDhikrCount,
                    target = dhikrTarget,
                    onIncrement = {
                        if (dailyDhikrCount < dhikrTarget) {
                            dailyDhikrCount++
                            if (dailyDhikrCount == dhikrTarget) {
                                HapticUtil.vibrateMilestone(context)
                            } else {
                                HapticUtil.lightTick(context)
                            }
                        } else {
                            dailyDhikrCount = 1
                            HapticUtil.vibrateClick(context)
                        }
                    },
                    onOpenTasbih = { onSelect(IbadahSubScreen.TASBIH) }
                )
            }

            // -------------------------------------------------------------
            // 3. ASYMMETRIC BENTO GRID TILES
            // -------------------------------------------------------------

            // Row 1: Dua & Adhkar (Mint Glass) + Digital Tasbih (Sky Blue Glass)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Tile 1: Dua & Adhkar
                    BentoGlassCard(
                        icon = "🤲",
                        categoryBn = "নিত্যদিনের আমল",
                        categoryEn = "Daily Supplications",
                        titleBn = "দু'আ ও হিসনুল মুসলিম",
                        titleEn = "Dua & Adhkar",
                        descBn = "সকাল-সন্ধ্যার মাসনুন দু'আ",
                        descEn = "Morning & Evening Duas",
                        badgeBn = "৮০+ দু'আ",
                        badgeEn = "80+ Duas",
                        accentColor = Color(0xFF10B981),
                        lightAccentTint = Color(0xFFECFDF5),
                        isDark = colors.isDark,
                        titleColor = titleColor,
                        subtitleColor = subtitleColor,
                        isBangla = isBangla,
                        onClick = { onSelect(IbadahSubScreen.DUA) },
                        modifier = Modifier.weight(1f)
                    )

                    // Tile 2: Digital Tasbih
                    val tasbihRecitedCount = uiState.tasbihs.sumOf { it.totalRecited }
                    val tasbihBadgeBn = if (tasbihRecitedCount > 0) {
                        "${DateUtil.toBanglaDigits(tasbihRecitedCount.toString())} পঠিত"
                    } else "কাউন্টার"
                    val tasbihBadgeEn = if (tasbihRecitedCount > 0) "$tasbihRecitedCount Recited" else "Counter"

                    BentoGlassCard(
                        icon = "📿",
                        categoryBn = "স্মার্ট জপমালা",
                        categoryEn = "Smart Rosary",
                        titleBn = "ডিজিটাল তাসবীহ",
                        titleEn = "Digital Tasbih",
                        descBn = "কাস্টম টার্গেট ও জিকির",
                        descEn = "Custom targets & Dhikr",
                        badgeBn = tasbihBadgeBn,
                        badgeEn = tasbihBadgeEn,
                        accentColor = Color(0xFF0284C7),
                        lightAccentTint = Color(0xFFF0F9FF),
                        isDark = colors.isDark,
                        titleColor = titleColor,
                        subtitleColor = subtitleColor,
                        isBangla = isBangla,
                        onClick = { onSelect(IbadahSubScreen.TASBIH) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Row 2: Qibla Compass (Soft Indigo) + 99 Names of Allah (Lavender)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Tile 3: Qibla Compass
                    val bearingVal = uiState.qiblaBearing.toInt()
                    val qiblaBadgeBn = "${DateUtil.toBanglaDigits(bearingVal.toString())}° মক্কা"
                    val qiblaBadgeEn = "$bearingVal° Makkah"

                    BentoGlassCard(
                        icon = "🧭",
                        categoryBn = "সঠিক দিকনির্ণয়",
                        categoryEn = "Live Compass",
                        titleBn = "কিবলা কম্পাস",
                        titleEn = "Qibla Direction",
                        descBn = "অটো সেন্সর ও লাইভ কোণ",
                        descEn = "Accurate Kaaba Bearing",
                        badgeBn = qiblaBadgeBn,
                        badgeEn = qiblaBadgeEn,
                        accentColor = Color(0xFF6366F1),
                        lightAccentTint = Color(0xFFEEF2FF),
                        isDark = colors.isDark,
                        titleColor = titleColor,
                        subtitleColor = subtitleColor,
                        isBangla = isBangla,
                        onClick = { onSelect(IbadahSubScreen.QIBLA) },
                        modifier = Modifier.weight(1f)
                    )

                    // Tile 4: 99 Names of Allah
                    BentoGlassCard(
                        icon = "✨",
                        categoryBn = "আসমাউল হুসনা",
                        categoryEn = "Divine Names",
                        titleBn = "৯৯টি গুণবাচক নাম",
                        titleEn = "99 Names of Allah",
                        descBn = "الرَّحْمَٰنُ • পরম দয়ালু",
                        descEn = "Ar-Rahman • Gracious",
                        badgeBn = "৯৯ নাম",
                        badgeEn = "99 Names",
                        accentColor = Color(0xFF9333EA),
                        lightAccentTint = Color(0xFFFAF5FF),
                        isDark = colors.isDark,
                        titleColor = titleColor,
                        subtitleColor = subtitleColor,
                        isBangla = isBangla,
                        onClick = { onSelect(IbadahSubScreen.ASMAUL_HUSNA) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Row 3: Salah & Wudu Complete Guide (Spanning 2 columns / Full Width Banner)
            item {
                GuidesGlassBanner(
                    isBangla = isBangla,
                    isDark = colors.isDark,
                    titleColor = titleColor,
                    subtitleColor = subtitleColor,
                    onOpenSalah = { onSelect(IbadahSubScreen.PRAYER_GUIDE) },
                    onOpenWudu = { onSelect(IbadahSubScreen.WUDU_GUIDE) }
                )
            }
        }
    }
}

/**
 * Hero Card: Daily Dhikr with In-Card Circular Tap Counter & Progress Ring
 */
@Composable
private fun HeroDhikrGlassCard(
    isBangla: Boolean,
    isDark: Boolean,
    titleColor: Color,
    subtitleColor: Color,
    count: Int,
    target: Int,
    onIncrement: () -> Unit,
    onOpenTasbih: () -> Unit
) {
    val shape = RoundedCornerShape(26.dp)

    val backgroundBrush = if (isDark) {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFF0D9488).copy(alpha = 0.35f),
                Color(0xFF0F172A).copy(alpha = 0.85f),
                Color(0xFF064E3B).copy(alpha = 0.30f)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFFCCFBF1).copy(alpha = 0.95f),
                Color(0xFFF0FDF4).copy(alpha = 0.90f),
                Color.White.copy(alpha = 0.98f)
            )
        )
    }

    val rimBrush = Brush.linearGradient(
        colors = listOf(
            if (isDark) Color(0xFF2DD4BF).copy(alpha = 0.45f) else Color(0xFF14B8A6).copy(alpha = 0.50f),
            if (isDark) Color.White.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.80f)
        )
    )

    val animatedProgress by animateFloatAsState(
        targetValue = (count.toFloat() / target.toFloat()).coerceIn(0f, 1f),
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "hero_dhikr_progress"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isDark) 6.dp else 4.dp,
                shape = shape,
                ambientColor = if (isDark) Color.Black.copy(alpha = 0.3f) else Color(0xFF0D9488).copy(alpha = 0.15f),
                spotColor = if (isDark) Color(0xFF14B8A6).copy(alpha = 0.2f) else Color(0xFF0D9488).copy(alpha = 0.2f)
            ),
        shape = shape,
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(backgroundBrush)
                .border(BorderStroke(1.2.dp, rimBrush), shape)
                .padding(18.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Top Row: Badge & Target
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF10B981),
                            modifier = Modifier.size(7.dp)
                        ) {}
                        Text(
                            text = if (isBangla) "আজকের বিশেষ জিকির" else "Dhikr of the Day",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFF34D399) else Color(0xFF065F46)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = if (isDark) Color(0xFF14B8A6).copy(alpha = 0.20f) else Color(0xFF14B8A6).copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, if (isDark) Color(0xFF2DD4BF).copy(alpha = 0.4f) else Color(0xFF0D9488).copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = if (isBangla) "টার্গেট: ১০০ বার" else "Target: 100 Times",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFF2DD4BF) else Color(0xFF0F766E),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Arabic Dhikr Calligraphy
                Text(
                    text = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ ، سُبْحَانَ اللَّهِ الْعَظِيمِ",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFF2DD4BF) else Color(0xFF0F766E),
                    textAlign = TextAlign.Center,
                    lineHeight = 30.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Translation
                Text(
                    text = if (isBangla)
                        "\"আল্লাহ্‌র প্রশংসাসহ তাঁর পবিত্রতা ঘোষণা করছি, মহান আল্লাহ অতি পবিত্র।\""
                    else
                        "\"Glory and praise be to Allah, Glory be to Allah the Almighty.\"",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = subtitleColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // In-Card Direct Interactive Tap Counter Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            if (isDark) Color.White.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.65f)
                        )
                        .border(
                            BorderStroke(
                                1.dp,
                                if (isDark) Color.White.copy(alpha = 0.10f) else Color(0xFFCCFBF1)
                            ),
                            RoundedCornerShape(18.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isBangla) "সরাসরি পাঠ করুন" else "Direct Tap Counter",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                        )
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = if (isBangla) DateUtil.toBanglaDigits(count.toString()) else count.toString(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = titleColor
                            )
                            Text(
                                text = if (isBangla) "/ ১০০" else "/ 100",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = subtitleColor,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Open Full Tasbih Button
                        IosGlassPillButton(
                            text = if (isBangla) "বিস্তারিত ▶" else "Tasbih ▶",
                            onClick = onOpenTasbih,
                            fontSize = 11.sp,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        )

                        // Circular Tap Button with SVG Progress Ring
                        CircularDhikrTapButton(
                            progress = animatedProgress,
                            isDark = isDark,
                            onClick = onIncrement
                        )
                    }
                }
            }
        }
    }
}

/**
 * Animated Circular Tap Button with smooth sweep progress arc and haptic bounce
 */
@Composable
private fun CircularDhikrTapButton(
    progress: Float,
    isDark: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "button_scale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(56.dp)
            .scale(scale)
    ) {
        // Circular Progress Ring
        Canvas(modifier = Modifier.size(56.dp)) {
            val strokeWidth = 3.5.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2f

            // Background Track
            drawCircle(
                color = if (isDark) Color.White.copy(alpha = 0.15f) else Color(0xFF0D9488).copy(alpha = 0.15f),
                radius = radius,
                style = Stroke(width = strokeWidth)
            )

            // Progress Arc
            drawArc(
                brush = Brush.sweepGradient(
                    listOf(
                        Color(0xFF2DD4BF),
                        Color(0xFF10B981),
                        Color(0xFF38BDF8),
                        Color(0xFF2DD4BF)
                    )
                ),
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        // Inner Glowing Tap Pill
        Surface(
            modifier = Modifier
                .size(44.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                ),
            shape = CircleShape,
            color = Color(0xFF14B8A6),
            shadowElevation = 4.dp
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF2DD4BF), Color(0xFF0D9488))
                        )
                    )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "ট্যাপ",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "+১",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

/**
 * Bento Glass Card for Asymmetric Grid (Option 2 Modern Pastel Aura)
 */
@Composable
private fun BentoGlassCard(
    icon: String,
    categoryBn: String,
    categoryEn: String,
    titleBn: String,
    titleEn: String,
    descBn: String,
    descEn: String,
    badgeBn: String,
    badgeEn: String,
    accentColor: Color,
    lightAccentTint: Color,
    isDark: Boolean,
    titleColor: Color,
    subtitleColor: Color,
    isBangla: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "bento_scale"
    )

    val shape = RoundedCornerShape(24.dp)

    val backgroundBrush = if (isDark) {
        Brush.linearGradient(
            colors = listOf(
                accentColor.copy(alpha = 0.22f),
                Color(0xFF0F172A).copy(alpha = 0.75f)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                lightAccentTint.copy(alpha = 0.95f),
                Color.White.copy(alpha = 0.98f)
            )
        )
    }

    val rimBrush = Brush.linearGradient(
        colors = listOf(
            if (isDark) accentColor.copy(alpha = 0.40f) else accentColor.copy(alpha = 0.45f),
            if (isDark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.85f)
        )
    )

    // Dark high contrast badge text for light mode
    val badgeTextColor = if (isDark) {
        accentColor
    } else {
        when (accentColor) {
            Color(0xFF10B981) -> Color(0xFF065F46) // Mint Dark
            Color(0xFF0284C7) -> Color(0xFF0369A1) // Sky Dark
            Color(0xFF6366F1) -> Color(0xFF4338CA) // Indigo Dark
            Color(0xFF9333EA) -> Color(0xFF7E22CE) // Purple Dark
            else -> Color(0xFF92400E)              // Gold Dark
        }
    }

    Surface(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = if (isDark) 4.dp else 2.dp,
                shape = shape,
                ambientColor = if (isDark) Color.Black.copy(alpha = 0.3f) else accentColor.copy(alpha = 0.12f),
                spotColor = if (isDark) accentColor.copy(alpha = 0.18f) else accentColor.copy(alpha = 0.15f)
            ),
        shape = shape,
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(backgroundBrush)
                .border(BorderStroke(1.2.dp, rimBrush), shape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {
                        HapticUtil.lightTick(context)
                        onClick()
                    }
                )
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Row: Icon container + Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(38.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = accentColor.copy(alpha = if (isDark) 0.18f else 0.15f),
                        border = BorderStroke(
                            1.dp,
                            accentColor.copy(alpha = if (isDark) 0.35f else 0.30f)
                        )
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = icon, fontSize = 18.sp)
                        }
                    }

                    Surface(
                        color = accentColor.copy(alpha = if (isDark) 0.20f else 0.16f),
                        shape = RoundedCornerShape(999.dp),
                        border = BorderStroke(0.8.dp, accentColor.copy(alpha = 0.30f))
                    ) {
                        Text(
                            text = if (isBangla) badgeBn else badgeEn,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeTextColor,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Category Subtitle
                Text(
                    text = if (isBangla) categoryBn else categoryEn,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = badgeTextColor,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Main Title
                Text(
                    text = if (isBangla) titleBn else titleEn,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = titleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(3.dp))

                // Description / Subtext
                Text(
                    text = if (isBangla) descBn else descEn,
                    fontSize = 11.sp,
                    color = subtitleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Full Width Bento Banner: Salah & Wudu Complete Guide with dual quick access pills
 */
@Composable
private fun GuidesGlassBanner(
    isBangla: Boolean,
    isDark: Boolean,
    titleColor: Color,
    subtitleColor: Color,
    onOpenSalah: () -> Unit,
    onOpenWudu: () -> Unit
) {
    val shape = RoundedCornerShape(24.dp)
    val accentGold = Color(0xFFF59E0B)

    val backgroundBrush = if (isDark) {
        Brush.linearGradient(
            colors = listOf(
                accentGold.copy(alpha = 0.20f),
                Color(0xFF0F172A).copy(alpha = 0.78f)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFFBEB).copy(alpha = 0.95f),
                Color.White.copy(alpha = 0.98f)
            )
        )
    }

    val rimBrush = Brush.linearGradient(
        colors = listOf(
            accentGold.copy(alpha = if (isDark) 0.38f else 0.45f),
            if (isDark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.85f)
        )
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isDark) 4.dp else 2.dp,
                shape = shape,
                ambientColor = if (isDark) Color.Black.copy(alpha = 0.3f) else accentGold.copy(alpha = 0.12f),
                spotColor = accentGold.copy(alpha = 0.15f)
            ),
        shape = shape,
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(backgroundBrush)
                .border(BorderStroke(1.2.dp, rimBrush), shape)
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(42.dp),
                            shape = RoundedCornerShape(14.dp),
                            color = accentGold.copy(alpha = if (isDark) 0.22f else 0.15f),
                            border = BorderStroke(1.dp, accentGold.copy(alpha = 0.35f))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "📖", fontSize = 20.sp)
                            }
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (isBangla) "সহজ নির্দেশিকা" else "COMPLETE GUIDES",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color(0xFFFBBF24) else Color(0xFFB45309),
                                    letterSpacing = 0.5.sp
                                )
                                Surface(
                                    shape = RoundedCornerShape(999.dp),
                                    color = accentGold.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = if (isBangla) "চিত্রসহ" else "Illustrated",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) Color(0xFFFBBF24) else Color(0xFF92400E),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isBangla) "নামায ও ওযুর সম্পূর্ণ নিয়ম" else "Salah & Wudu Complete Rules",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = titleColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (isBangla)
                        "ফরজ, ওয়াজিব, সুন্নাত ও তাহারাতের সহীহ নিয়ত ও নিয়মাবলী"
                    else
                        "Step-by-step rules for Fard, Wajib, Sunnah & Taharah",
                    fontSize = 11.5.sp,
                    color = subtitleColor
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Dual Action Buttons (Salah Guide Pill & Wudu Guide Pill)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IosGlassPillButton(
                        text = if (isBangla) "🕌 নামায শিক্ষা" else "🕌 Salah Guide",
                        onClick = onOpenSalah,
                        fontSize = 11.5.sp,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    )

                    IosGlassPillButton(
                        text = if (isBangla) "💧 ওযু ও পবিত্রতা" else "💧 Wudu Guide",
                        onClick = onOpenWudu,
                        fontSize = 11.5.sp,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    )
                }
            }
        }
    }
}
