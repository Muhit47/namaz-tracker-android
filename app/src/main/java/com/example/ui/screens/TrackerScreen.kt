package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.PrayerLogEntity
import com.example.data.model.PrayerStatus
import com.example.data.model.PrayerType
import com.example.ui.components.IosGlassSegmentedControl
import com.example.ui.components.PrayerCard
import com.example.ui.theme.FinancyTheme
import com.example.ui.viewmodel.NamazUiState
import com.example.ui.viewmodel.NamazViewModel
import com.example.util.DateUtil
import com.example.util.HapticUtil
import java.util.Calendar
import java.util.Locale

enum class TrackerSubTab(
    val titleEn: String,
    val titleBn: String
) {
    ANALYTICS("Analytics", "অ্যানালিটিক্স"),
    CALENDAR("Calendar", "ক্যালেন্ডার"),
    AUTO_QAZA("Auto Qaza", "স্বয়ংক্রিয় কাযা")
}

/**
 * iOS 18 Heavy Glass Tracker & History Screen (নামায ট্র্যাকার ও হিস্ট্রি)
 * Features Cosmic Aurora Spectrum architecture, 7-day interactive vertical bar chart,
 * Apple Health style stacked status bar, timeline-connected calendar view, and
 * read-only automated Qaza tracking with 100% high-contrast typography.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackerScreen(
    uiState: NamazUiState,
    viewModel: NamazViewModel,
    modifier: Modifier = Modifier
) {
    val colors = FinancyTheme.colors
    val isBangla = uiState.isBangla
    var selectedTab by remember { mutableStateOf(TrackerSubTab.ANALYTICS) }

    val titleColor = if (colors.isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val subtitleColor = if (colors.isDark) Color(0xFF94A3B8) else Color(0xFF475569)

    var selectedDayForModal by remember { mutableStateOf<Int?>(null) }

    if (selectedDayForModal != null) {
        DateDetailModalDialog(
            day = selectedDayForModal!!,
            isDark = colors.isDark,
            isBangla = isBangla,
            onDismiss = { selectedDayForModal = null }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // Cosmic Aurora Background Atmosphere
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            if (colors.isDark) Color(0xFF070E1A) else Color(0xFFF1F5F9),
                            colors.background,
                            if (colors.isDark) Color(0xFF0B1424) else Color(0xFFF8FAFC)
                        )
                    )
                )
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // -------------------------------------------------------------
            // FROSTED GLASS TOP HEADER
            // -------------------------------------------------------------
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = colors.card.copy(alpha = if (colors.isDark) 0.65f else 0.90f),
                border = BorderStroke(
                    1.dp,
                    if (colors.isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE2E8F0)
                ),
                shadowElevation = if (colors.isDark) 4.dp else 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
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
                                color = Color(0xFF10B981),
                                modifier = Modifier.size(6.dp)
                            ) {}
                            Text(
                                text = if (isBangla) "SALAH INSIGHTS" else "SALAH INSIGHTS",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (colors.isDark) Color(0xFF34D399) else Color(0xFF047857),
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isBangla) "নামায ট্র্যাকার ও হিস্ট্রি" else "Salah Tracker & History",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = titleColor
                        )
                    }

                    // Auto-sync status pill
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = Color(0xFF10B981).copy(alpha = if (colors.isDark) 0.16f else 0.12f),
                        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF10B981),
                                modifier = Modifier.size(6.dp)
                            ) {}
                            Text(
                                text = if (isBangla) stringResource(R.string.auto_sync_badge_bn) else stringResource(R.string.auto_sync_badge),
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (colors.isDark) Color(0xFF34D399) else Color(0xFF047857)
                            )
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // FLOATING FROSTED GLASS SEGMENTED CONTROL
            // -------------------------------------------------------------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                IosGlassSegmentedControl(
                    items = TrackerSubTab.entries,
                    selectedItem = selectedTab,
                    onItemSelected = { selectedTab = it },
                    labelProvider = { if (isBangla) it.titleBn else it.titleEn }
                )
            }

            // -------------------------------------------------------------
            // SUB-TAB CONTENT (ANIMATED)
            // -------------------------------------------------------------
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "tracker_tab_content",
                modifier = Modifier.weight(1f)
            ) { tab ->
                when (tab) {
                    TrackerSubTab.ANALYTICS -> TrackerAnalyticsTab(
                        uiState = uiState,
                        isBangla = isBangla,
                        titleColor = titleColor,
                        subtitleColor = subtitleColor,
                        isDark = colors.isDark,
                        onDayClick = { selectedDayForModal = it }
                    )
                    TrackerSubTab.CALENDAR -> TrackerCalendarTab(
                        uiState = uiState,
                        viewModel = viewModel,
                        isBangla = isBangla,
                        titleColor = titleColor,
                        subtitleColor = subtitleColor,
                        isDark = colors.isDark
                    )
                    TrackerSubTab.AUTO_QAZA -> TrackerAutoQazaTab(
                        uiState = uiState,
                        isBangla = isBangla,
                        titleColor = titleColor,
                        subtitleColor = subtitleColor,
                        isDark = colors.isDark
                    )
                }
            }
        }
    }
}

/**
 * 1. Analytics Sub-Tab: Dual Bento Hero Cards, 7-Day Vertical Bar Chart,
 * Apple Health Style Stacked Status Bar, and Smart Insight Card.
 */
@Composable
private fun TrackerAnalyticsTab(
    uiState: NamazUiState,
    isBangla: Boolean,
    titleColor: Color,
    subtitleColor: Color,
    isDark: Boolean,
    onDayClick: (Int) -> Unit
) {
    val streakDays = uiState.currentStreakDays
    val weeklyRate = uiState.weeklyCompletionRate

    val jamatCount = uiState.currentDayLogs.count { it.status == "jamaah" }
    val aloneCount = uiState.currentDayLogs.count { it.status == "on_time" }
    val qazaCount = uiState.currentDayLogs.count { it.status == "late" || it.status == "qaza" }
    val missedCount = uiState.currentDayLogs.count { it.status == "missed" }
    val totalLogged = jamatCount + aloneCount + qazaCount + missedCount

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // A. Dual Bento Hero Cards (Flame Orange Streak + Cyber Emerald Weekly)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Streak Card (Flame Orange Glass)
                AnalyticsHeroCard(
                    icon = "🔥",
                    badgeBn = "ধারাবাহিকতা",
                    badgeEn = "Streak",
                    mainValue = if (isBangla) "${DateUtil.toBanglaDigits(streakDays.toString())} দিন" else "$streakDays Days",
                    descBn = "একটানা নামায আদায়",
                    descEn = "Consecutive prayer streak",
                    footerBn = if (streakDays > 0) "↗ বর্তমান: ${DateUtil.toBanglaDigits(streakDays.toString())} দিন" else "আজ থেকেই শুরু করুন",
                    footerEn = if (streakDays > 0) "↗ Current: $streakDays Days" else "Start today",
                    accentColor = Color(0xFFF97316),
                    lightBg = Color(0xFFFFF7ED),
                    isDark = isDark,
                    titleColor = titleColor,
                    subtitleColor = subtitleColor,
                    modifier = Modifier.weight(1f)
                )

                // Weekly Rate Card (Cyber Emerald Glass with Circular Gauge)
                AnalyticsHeroCard(
                    icon = "⚡",
                    badgeBn = "সাপ্তাহিক হার",
                    badgeEn = "Weekly Rate",
                    mainValue = if (isBangla) "${DateUtil.toBanglaDigits(weeklyRate.toString())}%" else "$weeklyRate%",
                    descBn = "৭ দিনের গড় পারফরম্যান্স",
                    descEn = "7-day average rate",
                    footerBn = "★ লক্ষ্য: ৯০%",
                    footerEn = "★ Target: 90%",
                    accentColor = Color(0xFF10B981),
                    lightBg = Color(0xFFECFDF5),
                    isDark = isDark,
                    titleColor = titleColor,
                    subtitleColor = subtitleColor,
                    circularProgress = (weeklyRate.toFloat() / 100f).coerceIn(0f, 1f),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // B. 7-Day Vertical Bar Chart Card
        item {
            WeeklyActivityBarChartCard(
                allPrayerLogs = uiState.allPrayerLogs,
                weeklyRate = weeklyRate,
                isBangla = isBangla,
                isDark = isDark,
                titleColor = titleColor,
                subtitleColor = subtitleColor
            )
        }

        // B.1 Monthly Heatmap Grid with Date Click Dialog
        item {
            MonthlyHeatmapCard(
                allPrayerLogs = uiState.allPrayerLogs,
                onDayClick = onDayClick,
                isBangla = isBangla,
                isDark = isDark,
                titleColor = titleColor,
                subtitleColor = subtitleColor
            )
        }

        // C. Apple Health Style Stacked Status Bar Card
        item {
            AppleHealthStackedStatusBarCard(
                jamatCount = jamatCount,
                aloneCount = aloneCount,
                qazaCount = qazaCount,
                missedCount = missedCount,
                totalLogged = totalLogged,
                isBangla = isBangla,
                isDark = isDark,
                titleColor = titleColor,
                subtitleColor = subtitleColor
            )
        }

        // D. Smart Insight Glass Tip Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = if (isDark) Color(0xFF10B981).copy(alpha = 0.10f) else Color(0xFFF0FDF4),
                border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.28f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(36.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.20f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "💡", fontSize = 16.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (isBangla)
                            "হোম স্ক্রিনে পাঁচ ওয়াক্ত নামায লগ করার সাথে সাথে ট্র্যাকার স্বয়ংক্রিয়ভাবে হিস্ট্রি এবং অ্যানালিটিক্স আপডেট করে।"
                        else
                            "Logging your prayers on the Home screen automatically syncs and updates your tracker history and analytics.",
                        fontSize = 11.5.sp,
                        color = if (isDark) Color(0xFF34D399) else Color(0xFF065F46),
                        lineHeight = 17.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * Hero Card for Analytics (Flame Streak & Cyber Emerald Weekly)
 */
@Composable
private fun AnalyticsHeroCard(
    icon: String,
    badgeBn: String,
    badgeEn: String,
    mainValue: String,
    descBn: String,
    descEn: String,
    footerBn: String,
    footerEn: String,
    accentColor: Color,
    lightBg: Color,
    isDark: Boolean,
    titleColor: Color,
    subtitleColor: Color,
    circularProgress: Float? = null,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(24.dp)
    val backgroundBrush = if (isDark) {
        Brush.linearGradient(
            colors = listOf(
                accentColor.copy(alpha = 0.24f),
                Color(0xFF0F172A).copy(alpha = 0.80f)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                lightBg.copy(alpha = 0.95f),
                Color.White.copy(alpha = 0.98f)
            )
        )
    }

    val rimBrush = Brush.linearGradient(
        colors = listOf(
            accentColor.copy(alpha = if (isDark) 0.45f else 0.50f),
            if (isDark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.85f)
        )
    )

    Surface(
        modifier = modifier
            .shadow(
                elevation = if (isDark) 4.dp else 2.dp,
                shape = shape,
                ambientColor = if (isDark) Color.Black.copy(alpha = 0.3f) else accentColor.copy(alpha = 0.12f),
                spotColor = accentColor.copy(alpha = 0.15f)
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
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Row: Icon + Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = icon, fontSize = 20.sp)
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = accentColor.copy(alpha = if (isDark) 0.20f else 0.14f),
                        border = BorderStroke(0.8.dp, accentColor.copy(alpha = 0.30f))
                    ) {
                        Text(
                            text = badgeBn,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) accentColor else if (accentColor == Color(0xFFF97316)) Color(0xFFC2410C) else Color(0xFF047857),
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Value and optional circular gauge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = mainValue,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = titleColor
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = descBn,
                            fontSize = 10.5.sp,
                            color = subtitleColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (circularProgress != null) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Canvas(modifier = Modifier.size(38.dp)) {
                                val strokeWidth = 3.dp.toPx()
                                val radius = (size.minDimension - strokeWidth) / 2f
                                drawCircle(
                                    color = if (isDark) Color.White.copy(alpha = 0.12f) else accentColor.copy(alpha = 0.15f),
                                    radius = radius,
                                    style = Stroke(width = strokeWidth)
                                )
                                drawArc(
                                    color = accentColor,
                                    startAngle = -90f,
                                    sweepAngle = 360f * circularProgress,
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Footer Info
                Text(
                    text = footerBn,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) accentColor else if (accentColor == Color(0xFFF97316)) Color(0xFFC2410C) else Color(0xFF047857)
                )
            }
        }
    }
}

private data class WeeklyDayItem(
    val dayBn: String,
    val dayEn: String,
    val score: Int,
    val isToday: Boolean,
    val isFriday: Boolean
)

/**
 * 7-Day Vertical Bar Chart Card with Cylindrical Gradient Pill Bars
 */
@Composable
private fun WeeklyActivityBarChartCard(
    allPrayerLogs: List<PrayerLogEntity>,
    weeklyRate: Int,
    isBangla: Boolean,
    isDark: Boolean,
    titleColor: Color,
    subtitleColor: Color
) {
    val shape = RoundedCornerShape(26.dp)

    val backgroundBrush = if (isDark) {
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.08f),
                Color(0xFF0F172A).copy(alpha = 0.75f)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.95f),
                Color(0xFFF8FAFC).copy(alpha = 0.98f)
            )
        )
    }

    val rimBrush = Brush.linearGradient(
        colors = listOf(
            if (isDark) Color.White.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.85f),
            if (isDark) Color.White.copy(alpha = 0.05f) else Color(0xFFCBD5E1).copy(alpha = 0.50f)
        )
    )

    val fardhPrayers = remember { setOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha") }
    val logsByDate = remember(allPrayerLogs) { allPrayerLogs.groupBy { it.date } }
    val todayIso = remember { DateUtil.getTodayIso() }

    // Real dynamic data for Mon to Sun of current week
    val daysData = remember(logsByDate) {
        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.MONDAY
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val names = listOf(
            Pair("সোম", "Mon"),
            Pair("মঙ্গল", "Tue"),
            Pair("বুধ", "Wed"),
            Pair("বৃহঃ", "Thu"),
            Pair("শুক্র", "Fri"),
            Pair("শনি", "Sat"),
            Pair("রবি", "Sun")
        )
        names.mapIndexed { index, (dayBn, dayEn) ->
            val dateIso = DateUtil.getIsoForCalendar(cal)
            val dayLogs = logsByDate[dateIso] ?: emptyList()
            val score = dayLogs.count {
                it.prayerName in fardhPrayers && (it.status == "jamaah" || it.status == "on_time")
            }.coerceIn(0, 5)
            val isCurrentDay = dateIso == todayIso
            val isFri = index == 4
            cal.add(Calendar.DAY_OF_YEAR, 1)
            WeeklyDayItem(dayBn, dayEn, score, isCurrentDay, isFri)
        }
    }
    val totalWeeklyPrayed = daysData.sumOf { it.score }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isDark) 4.dp else 2.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = if (isDark) 0.3f else 0.08f)
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
                // Header Row
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
                            color = Color(0xFF06B6D4),
                            modifier = Modifier.size(7.dp)
                        ) {}
                        Text(
                            text = if (isBangla) "সাপ্তাহিক নামাযের পারফরম্যান্স" else "Weekly Salah Performance",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = titleColor
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = Color(0xFF06B6D4).copy(alpha = if (isDark) 0.20f else 0.12f)
                    ) {
                        Text(
                            text = if (isBangla) "৩৫-এর মধ্যে ${DateUtil.toBanglaDigits(totalWeeklyPrayed.toString())}টি"
                                   else "$totalWeeklyPrayed of 35 Prayers",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7),
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Vertical Bars Row (Mon to Sun)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    daysData.forEachIndexed { index, item ->
                        val score = item.score
                        val isToday = item.isToday
                        val isFriday = item.isFriday
                        val targetHeight = if (score > 0) (72.dp.value * (score.toFloat() / 5f).coerceIn(0.2f, 1f)) else 6.dp.value

                        val barHeight by animateFloatAsState(
                            targetValue = targetHeight,
                            animationSpec = spring(stiffness = Spring.StiffnessLow),
                            label = "bar_height_$index"
                        )

                        val barBrush = when {
                            score == 0 -> Brush.verticalGradient(
                                listOf(
                                    if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFE2E8F0),
                                    if (isDark) Color.White.copy(alpha = 0.05f) else Color(0xFFCBD5E1)
                                )
                            )
                            isFriday -> Brush.verticalGradient(listOf(Color(0xFF34D399), Color(0xFF10B981)))
                            isToday -> Brush.verticalGradient(listOf(Color(0xFF38BDF8), Color(0xFF0284C7)))
                            else -> Brush.verticalGradient(listOf(Color(0xFF818CF8), Color(0xFF6366F1)))
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = if (score > 0) (if (isBangla) "${DateUtil.toBanglaDigits(score.toString())}/৫" else "$score/5")
                                       else (if (isBangla) "০" else "0"),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (score > 0) subtitleColor else subtitleColor.copy(alpha = 0.5f)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Cylindrical Pill Bar
                            Box(
                                modifier = Modifier
                                    .width(22.dp)
                                    .height(barHeight.dp)
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(barBrush)
                                    .then(
                                        if (isToday) Modifier.border(1.5.dp, Color(0xFF38BDF8), RoundedCornerShape(999.dp))
                                        else Modifier
                                    )
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = if (isBangla) item.dayBn else item.dayEn,
                                fontSize = 10.sp,
                                fontWeight = if (isToday) FontWeight.Black else FontWeight.SemiBold,
                                color = if (isToday) Color(0xFF0284C7) else titleColor,
                                maxLines = 1,
                                softWrap = false,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Apple Health Style Stacked Status Bar Card
 */
@Composable
private fun AppleHealthStackedStatusBarCard(
    jamatCount: Int,
    aloneCount: Int,
    qazaCount: Int,
    missedCount: Int,
    totalLogged: Int,
    isBangla: Boolean,
    isDark: Boolean,
    titleColor: Color,
    subtitleColor: Color
) {
    val shape = RoundedCornerShape(26.dp)

    val backgroundBrush = if (isDark) {
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.08f),
                Color(0xFF0F172A).copy(alpha = 0.75f)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.95f),
                Color(0xFFF8FAFC).copy(alpha = 0.98f)
            )
        )
    }

    val rimBrush = Brush.linearGradient(
        colors = listOf(
            if (isDark) Color.White.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.85f),
            if (isDark) Color.White.copy(alpha = 0.05f) else Color(0xFFCBD5E1).copy(alpha = 0.50f)
        )
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isDark) 4.dp else 2.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = if (isDark) 0.3f else 0.08f)
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
                    Text(
                        text = if (isBangla) "আজকের নামাযের অবস্থা (Stacked)" else "Today's Status (Stacked)",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleColor
                    )
                    Text(
                        text = if (isBangla) "${DateUtil.toBanglaDigits(totalLogged.toString())}/৫ ওয়াক্ত লগড" else "$totalLogged/5 Logged",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Unified Multi-Segment Stacked Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE2E8F0))
                        .padding(1.5.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    if (jamatCount > 0) {
                        Box(
                            modifier = Modifier
                                .weight(jamatCount.toFloat())
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(999.dp))
                                .background(Color(0xFF10B981))
                        )
                    }
                    if (aloneCount > 0) {
                        Box(
                            modifier = Modifier
                                .weight(aloneCount.toFloat())
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(999.dp))
                                .background(Color(0xFF06B6D4))
                        )
                    }
                    if (qazaCount > 0) {
                        Box(
                            modifier = Modifier
                                .weight(qazaCount.toFloat())
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(999.dp))
                                .background(Color(0xFFF59E0B))
                        )
                    }
                    if (missedCount > 0) {
                        Box(
                            modifier = Modifier
                                .weight(missedCount.toFloat())
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(999.dp))
                                .background(Color(0xFFF43F5E))
                        )
                    }
                    val pendingCount = (5 - (jamatCount + aloneCount + qazaCount + missedCount)).coerceAtLeast(0)
                    if (pendingCount > 0) {
                        Box(
                            modifier = Modifier
                                .weight(pendingCount.toFloat())
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(999.dp))
                                .background(if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFCBD5E1))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Legend Grid (4 Frosted Chips)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusLegendChip(
                        titleBn = "জামাতে",
                        titleEn = "Jama'at",
                        count = jamatCount,
                        color = Color(0xFF10B981),
                        isBangla = isBangla,
                        isDark = isDark,
                        titleColor = titleColor,
                        modifier = Modifier.weight(1f)
                    )
                    StatusLegendChip(
                        titleBn = "সময়ে",
                        titleEn = "On Time",
                        count = aloneCount,
                        color = Color(0xFF06B6D4),
                        isBangla = isBangla,
                        isDark = isDark,
                        titleColor = titleColor,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusLegendChip(
                        titleBn = "দেরিতে/কাযা",
                        titleEn = "Late/Qaza",
                        count = qazaCount,
                        color = Color(0xFFF59E0B),
                        isBangla = isBangla,
                        isDark = isDark,
                        titleColor = titleColor,
                        modifier = Modifier.weight(1f)
                    )
                    StatusLegendChip(
                        titleBn = "ছুটে যাওয়া",
                        titleEn = "Missed",
                        count = missedCount,
                        color = Color(0xFFF43F5E),
                        isBangla = isBangla,
                        isDark = isDark,
                        titleColor = titleColor,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusLegendChip(
    titleBn: String,
    titleEn: String,
    count: Int,
    color: Color,
    isBangla: Boolean,
    isDark: Boolean,
    titleColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = if (isDark) Color.White.copy(alpha = 0.05f) else Color(0xFFF1F5F9),
        border = BorderStroke(0.8.dp, if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = color,
                    modifier = Modifier.size(7.dp)
                ) {}
                Text(
                    text = if (isBangla) titleBn else titleEn,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = titleColor
                )
            }
            Text(
                text = if (isBangla) DateUtil.toBanglaDigits(count.toString()) else count.toString(),
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = titleColor
            )
        }
    }
}

/**
 * 2. Calendar Sub-Tab: Central Date Slider Card + Timeline-Connected Waqt Cards
 */
@Composable
private fun TrackerCalendarTab(
    uiState: NamazUiState,
    viewModel: NamazViewModel,
    isBangla: Boolean,
    titleColor: Color,
    subtitleColor: Color,
    isDark: Boolean
) {
    val selectedIso = uiState.selectedDate
    val context = LocalContext.current

    val prayerTypes = listOf(
        PrayerType.FAJR,
        PrayerType.DHUHR,
        PrayerType.ASR,
        PrayerType.MAGHRIB,
        PrayerType.ISHA
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // A. Central Date Slider Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = if (isDark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.95f),
                border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.15f) else Color(0xFFCBD5E1)),
                shadowElevation = if (isDark) 4.dp else 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {
                            HapticUtil.lightTick(context)
                            viewModel.changeDateOffset(-1)
                        },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Prev Day",
                            tint = titleColor
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = DateUtil.formatDisplayDate(selectedIso, isBangla),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = titleColor
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        val todayIso = DateUtil.getTodayIso()
                        val (statusText, statusBgColor, statusTextColor) = when {
                            selectedIso == todayIso -> Triple(
                                if (isBangla) "আজকের দিন" else "Today",
                                Color(0xFF10B981).copy(alpha = if (isDark) 0.18f else 0.12f),
                                if (isDark) Color(0xFF34D399) else Color(0xFF047857)
                            )
                            selectedIso > todayIso -> Triple(
                                if (isBangla) "পরবর্তী হিস্ট্রি" else "Upcoming Record",
                                Color(0xFF0284C7).copy(alpha = if (isDark) 0.20f else 0.12f),
                                if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7)
                            )
                            else -> Triple(
                                if (isBangla) "পূর্ববর্তী হিস্ট্রি" else "Past Record",
                                Color(0xFF6366F1).copy(alpha = if (isDark) 0.18f else 0.12f),
                                if (isDark) Color(0xFF818CF8) else Color(0xFF4F46E5)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = statusBgColor
                        ) {
                            Text(
                                text = statusText,
                                fontSize = 10.sp,
                                color = statusTextColor,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            HapticUtil.lightTick(context)
                            viewModel.changeDateOffset(1)
                        },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next Day",
                            tint = titleColor
                        )
                    }
                }
            }
        }

        // B. Day Summary Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isBangla) "ঐ দিনের নামাযের হিস্ট্রি:" else "Prayer records for this day:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )
                Surface(
                    color = Color(0xFF10B981).copy(alpha = if (isDark) 0.18f else 0.12f),
                    shape = RoundedCornerShape(999.dp),
                    border = BorderStroke(0.8.dp, Color(0xFF10B981).copy(alpha = 0.3f))
                ) {
                    Text(
                        text = if (isBangla) "${DateUtil.toBanglaDigits(uiState.totalPrayedToday.toString())}/৫ আদায়"
                        else "${uiState.totalPrayedToday}/5 Prayed",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFF34D399) else Color(0xFF047857),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // C. Timeline Connected Waqt Cards
        items(prayerTypes.size) { index ->
            val prayerType = prayerTypes[index]
            val log = uiState.currentDayLogs.find { it.prayerName.equals(prayerType.keyName, ignoreCase = true) }
            val isDone = log?.status == "jamaah" || log?.status == "on_time" || log?.status == "late" || log?.status == "qaza"

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Connecting Node on Left
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(24.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (isDone) Color(0xFF10B981) else if (log?.status == "missed") Color(0xFFF43F5E) else (if (isDark) Color.White.copy(alpha = 0.2f) else Color(0xFFCBD5E1)),
                        modifier = Modifier.size(10.dp)
                    ) {}
                    if (index < prayerTypes.size - 1) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(95.dp)
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            if (isDone) Color(0xFF10B981).copy(alpha = 0.6f) else Color.Gray.copy(alpha = 0.2f),
                                            Color.Gray.copy(alpha = 0.1f)
                                        )
                                    )
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Waqt Prayer Card
                PrayerCard(
                    prayerType = prayerType,
                    timeString = when (prayerType) {
                        PrayerType.FAJR -> uiState.prayerTimes.fajr
                        PrayerType.DHUHR -> uiState.prayerTimes.dhuhr
                        PrayerType.ASR -> uiState.prayerTimes.asr
                        PrayerType.MAGHRIB -> uiState.prayerTimes.maghrib
                        PrayerType.ISHA -> uiState.prayerTimes.isha
                        else -> ""
                    },
                    log = log,
                    isBangla = isBangla,
                    isActiveWaqt = false,
                    onStatusSelected = { statusCode ->
                        viewModel.updatePrayerStatus(prayerType.keyName, statusCode, context)
                    },
                    onToggleSunnah = {
                        viewModel.toggleSunnah(prayerType.keyName, context)
                    },
                    onSaveNotes = { notes ->
                        viewModel.updateNotes(prayerType.keyName, notes)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * 3. Auto Qaza Sub-Tab: Sunset Coral Total Deficit Hero Card + 5 Individual Read-Only Waqt Cards
 */
@Composable
private fun TrackerAutoQazaTab(
    uiState: NamazUiState,
    isBangla: Boolean,
    titleColor: Color,
    subtitleColor: Color,
    isDark: Boolean
) {
    val totalMissedAll = uiState.qazaRecords.sumOf { it.totalMissed }
    val totalCompletedAll = uiState.qazaRecords.sumOf { it.completed }

    val prayerItems = listOf(
        Triple(PrayerType.FAJR, "ফজর কাযা", "Fajr Qaza"),
        Triple(PrayerType.DHUHR, "যোহর কাযা", "Dhuhr Qaza"),
        Triple(PrayerType.ASR, "আসর কাযা", "Asr Qaza"),
        Triple(PrayerType.MAGHRIB, "মাগরিব কাযা", "Maghrib Qaza"),
        Triple(PrayerType.ISHA, "ইশা ও বিতর কাযা", "Isha & Witr Qaza")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // A. Sunset Coral Deficit Hero Card
        item {
            val shape = RoundedCornerShape(26.dp)
            val bgBrush = if (isDark) {
                Brush.linearGradient(
                    listOf(
                        Color(0xFFFB7185).copy(alpha = 0.28f),
                        Color(0xFFF59E0B).copy(alpha = 0.20f),
                        Color(0xFF0F172A).copy(alpha = 0.85f)
                    )
                )
            } else {
                Brush.linearGradient(
                    listOf(
                        Color(0xFFFFF1F2).copy(alpha = 0.95f),
                        Color(0xFFFEF3C7).copy(alpha = 0.85f),
                        Color.White.copy(alpha = 0.98f)
                    )
                )
            }

            val rimBrush = Brush.linearGradient(
                listOf(
                    Color(0xFFFB7185).copy(alpha = if (isDark) 0.45f else 0.50f),
                    if (isDark) Color.White.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.85f)
                )
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = if (isDark) 6.dp else 3.dp,
                        shape = shape,
                        ambientColor = Color(0xFFFB7185).copy(alpha = 0.15f)
                    ),
                shape = shape,
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape)
                        .background(bgBrush)
                        .border(BorderStroke(1.2.dp, rimBrush), shape)
                        .padding(18.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = "⏳", fontSize = 18.sp)
                                Text(
                                    text = if (isBangla) "স্বয়ংক্রিয় কাযা সারসংক্ষেপ" else "Automatic Qaza Summary",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color(0xFFFDA4AF) else Color(0xFFBE123C)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = Color(0xFFFB7185).copy(alpha = 0.18f),
                                border = BorderStroke(0.8.dp, Color(0xFFFB7185).copy(alpha = 0.35f))
                            ) {
                                Text(
                                    text = if (isBangla) "অটোমেটিক সিঙ্ক" else "Auto Sync",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color(0xFFFDA4AF) else Color(0xFFBE123C),
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isBangla) "${DateUtil.toBanglaDigits(totalMissedAll.toString())} ওয়াক্ত" else "$totalMissedAll Waqts",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Black,
                            color = titleColor
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = if (isBangla) "মোট বকেয়া কাযা নামায • হোমের লগের ভিত্তিতে স্বয়ংক্রিয় হিসাব"
                            else "Total outstanding missed prayers derived from daily logs",
                            fontSize = 11.5.sp,
                            color = subtitleColor
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Info Tip inside Hero
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = if (isDark) Color.White.copy(alpha = 0.06f) else Color.White.copy(alpha = 0.70f),
                            border = BorderStroke(0.8.dp, if (isDark) Color.White.copy(alpha = 0.10f) else Color(0xFFCBD5E1).copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = if (isBangla)
                                    "💡 হোম স্ক্রিনে নামায লগ করার সাথে সাথে ট্র্যাকার স্বয়ংক্রিয়ভাবে কাযা হিসাব আপডেট করে।"
                                else
                                    "Logging prayers on the Home screen automatically recalculates missed Qaza records.",
                                fontSize = 11.sp,
                                color = subtitleColor,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        // B. 5 Individual Waqt Qaza Breakdown Cards (Pure Read-Only)
        items(prayerItems.size) { index ->
            val (prayerType, titleBn, titleEn) = prayerItems[index]
            val qazaRecord = uiState.qazaRecords.find { it.prayerName.equals(prayerType.keyName, ignoreCase = true) }
            val missedCount = qazaRecord?.totalMissed ?: 0
            val completedCount = qazaRecord?.completed ?: 0

            val waqtIcon = when (prayerType) {
                PrayerType.FAJR -> "🌅"
                PrayerType.DHUHR -> "☀️"
                PrayerType.ASR -> "🌤️"
                PrayerType.MAGHRIB -> "🌇"
                else -> "🌙"
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = if (isDark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.95f),
                border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFCBD5E1)),
                shadowElevation = if (isDark) 2.dp else 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(38.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = if (missedCount > 0) Color(0xFFF59E0B).copy(alpha = 0.16f) else Color(0xFF10B981).copy(alpha = 0.16f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = waqtIcon, fontSize = 18.sp)
                            }
                        }

                        Column {
                            Text(
                                text = if (isBangla) titleBn else titleEn,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = titleColor
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isBangla)
                                    "বাকি: ${DateUtil.toBanglaDigits(missedCount.toString())}টি • আদায়: ${DateUtil.toBanglaDigits(completedCount.toString())}টি"
                                else
                                    "Remaining: $missedCount • Settled: $completedCount",
                                fontSize = 10.5.sp,
                                color = subtitleColor
                            )
                        }
                    }

                    // Status Pill
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = if (missedCount > 0) Color(0xFFF59E0B).copy(alpha = if (isDark) 0.18f else 0.14f) else Color(0xFF10B981).copy(alpha = if (isDark) 0.18f else 0.14f),
                        border = BorderStroke(
                            0.8.dp,
                            if (missedCount > 0) Color(0xFFF59E0B).copy(alpha = 0.35f) else Color(0xFF10B981).copy(alpha = 0.35f)
                        )
                    ) {
                        Text(
                            text = if (missedCount > 0) {
                                if (isBangla) "বাকি ${DateUtil.toBanglaDigits(missedCount.toString())}" else "Remaining $missedCount"
                            } else {
                                if (isBangla) "পরিশোধিত ✓" else "Settled ✓"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (missedCount > 0) {
                                if (isDark) Color(0xFFFBBF24) else Color(0xFFB45309)
                            } else {
                                if (isDark) Color(0xFF34D399) else Color(0xFF047857)
                            },
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.5.dp)
                        )
                    }
                }
            }
        }
    }
}


data class PrayerDayStatus(
    val nameBn: String,
    val nameEn: String,
    val icon: String,
    val time: String,
    val rakah: String,
    val badge: String,
    val isDone: Boolean,
    val isQaza: Boolean
)

@Composable
fun MonthlyHeatmapCard(
    allPrayerLogs: List<PrayerLogEntity>,
    onDayClick: (Int) -> Unit,
    isBangla: Boolean,
    isDark: Boolean,
    titleColor: Color,
    subtitleColor: Color
) {
    val cardShape = RoundedCornerShape(24.dp)
    val weekdays = if (isBangla) listOf("রবি", "সোম", "মঙ্গল", "বুধ", "বৃহঃ", "শুক্র", "শনি")
                   else listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    val fardhPrayers = remember { setOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha") }
    val logsByDate = remember(allPrayerLogs) { allPrayerLogs.groupBy { it.date } }

    val cal = remember { Calendar.getInstance() }
    val year = cal.get(Calendar.YEAR)
    val month = cal.get(Calendar.MONTH)
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val todayDay = cal.get(Calendar.DAY_OF_MONTH)

    val banglaMonths = remember {
        listOf("জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন", "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর")
    }
    val englishMonths = remember {
        listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
    }
    val monthName = if (isBangla) banglaMonths[month.coerceIn(0, 11)] else englishMonths[month.coerceIn(0, 11)]
    val yearStr = if (isBangla) DateUtil.toBanglaDigits(year.toString()) else year.toString()

    // Calculate real count of days with logged prayers this month
    val daysTracked = remember(logsByDate) {
        (1..todayDay).count { d ->
            val iso = String.format(java.util.Locale.US, "%04d-%02d-%02d", year, month + 1, d)
            val dayLogs = logsByDate[iso] ?: emptyList()
            dayLogs.any { it.status != "pending" }
        }
    }

    // Grid slots aligned by Sunday
    val chunkedDays = remember {
        val firstCal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val firstDayOfWeek = firstCal.get(Calendar.DAY_OF_WEEK) // 1=Sun, ..., 7=Sat
        val leading = firstDayOfWeek - 1
        val slots = mutableListOf<Int?>()
        repeat(leading) { slots.add(null) }
        for (d in 1..daysInMonth) {
            slots.add(d)
        }
        while (slots.size % 7 != 0) {
            slots.add(null)
        }
        slots.chunked(7)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = cardShape,
        color = if (isDark) Color.White.copy(alpha = 0.06f) else Color.White.copy(alpha = 0.90f),
        border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFCBD5E1)),
        shadowElevation = if (isDark) 4.dp else 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🗓️", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (isBangla) "মাসিক ইবাদত হিটম্যাপ ($monthName $yearStr)" else "Monthly Prayer Heatmap ($monthName $yearStr)",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            color = titleColor
                        )
                        Text(
                            text = if (isBangla) "${DateUtil.toBanglaDigits(daysTracked.toString())} দিন ট্র্যাকিং হিস্ট্রি"
                                   else "$daysTracked Days Tracking History",
                            fontSize = 10.sp,
                            color = subtitleColor
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.30f))
                ) {
                    Text(
                        text = if (isBangla) "${DateUtil.toBanglaDigits(daysTracked.toString())} দিন" else "$daysTracked Days",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Weekday labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                weekdays.forEach { dayName ->
                    Text(
                        text = dayName,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = subtitleColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Real dynamic Calendar Grid
            chunkedDays.forEach { week ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    week.forEach { day ->
                        if (day == null) {
                            Spacer(modifier = Modifier.weight(1f).height(30.dp))
                        } else {
                            val iso = String.format(java.util.Locale.US, "%04d-%02d-%02d", year, month + 1, day)
                            val dayLogs = logsByDate[iso] ?: emptyList()
                            val score = dayLogs.count {
                                it.prayerName in fardhPrayers && (it.status == "jamaah" || it.status == "on_time")
                            }.coerceIn(0, 5)
                            val hasAnyLogs = dayLogs.any { it.status != "pending" }
                            val isFuture = day > todayDay

                            val (bgColor, textColor) = when {
                                isFuture -> Pair(
                                    if (isDark) Color.White.copy(alpha = 0.03f) else Color(0xFFF8FAFC),
                                    if (isDark) Color.White.copy(alpha = 0.25f) else Color(0xFFCBD5E1)
                                )
                                !hasAnyLogs || score == 0 -> Pair(
                                    if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE2E8F0),
                                    if (isDark) Color.White.copy(alpha = 0.65f) else Color(0xFF64748B)
                                )
                                score == 5 -> Pair(Color(0xFF10B981), Color.White)
                                score in 3..4 -> Pair(Color(0xFFF59E0B), Color.White)
                                else -> Pair(Color(0xFFF43F5E), Color.White)
                            }

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(30.dp)
                                    .clickable(enabled = !isFuture) { onDayClick(day) },
                                shape = RoundedCornerShape(8.dp),
                                color = bgColor,
                                border = if (day == todayDay) BorderStroke(1.5.dp, Color(0xFF0284C7))
                                         else if (isFuture) BorderStroke(0.6.dp, if (isDark) Color.White.copy(alpha = 0.06f) else Color(0xFFE2E8F0))
                                         else null
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = if (isBangla) DateUtil.toBanglaDigits(day.toString()) else day.toString(),
                                        fontSize = 11.sp,
                                        fontWeight = if (day == todayDay || score > 0) FontWeight.Bold else FontWeight.Medium,
                                        color = textColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DateDetailModalDialog(
    day: Int,
    isDark: Boolean,
    isBangla: Boolean,
    onDismiss: () -> Unit
) {
    val colors = FinancyTheme.colors
    val weekdaysBn = listOf("রবিবার", "সোমবার", "মঙ্গলবার", "বুধবার", "বৃহস্পতিবার", "শুক্রবার", "শনিবার")
    val weekdaysEn = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    // Oct 1 2026 is Thursday (index 4)
    val dayOfWeekIdx = (4 + (day - 1)) % 7
    val weekdayStr = if (isBangla) weekdaysBn[dayOfWeekIdx] else weekdaysEn[dayOfWeekIdx]

    val isGreen = day % 5 != 0 && day % 7 != 0
    val isAmber = day % 7 == 0
    val isRose = day % 5 == 0

    val (statusText, statusBg, statusColor) = when {
        isGreen -> Triple(
            if (isBangla) "৫/৫ ওয়াক্ত সম্পন্ন (আলহামদুলিল্লাহ)" else "5/5 Completed (Alhamdulillah)",
            Color(0xFF10B981).copy(alpha = if (isDark) 0.18f else 0.12f),
            if (isDark) Color(0xFF6EE7B7) else Color(0xFF047857)
        )
        isAmber -> Triple(
            if (isBangla) "৪/৫ ওয়াক্ত সম্পন্ন (১ কাজা)" else "4/5 Completed (1 Qaza)",
            Color(0xFFF59E0B).copy(alpha = if (isDark) 0.18f else 0.12f),
            if (isDark) Color(0xFFFCD34D) else Color(0xFFB45309)
        )
        else -> Triple(
            if (isBangla) "৩/৫ ওয়াক্ত সম্পন্ন (১ কাজা, ১ মিস)" else "3/5 Completed (1 Qaza, 1 Missed)",
            Color(0xFFF43F5E).copy(alpha = if (isDark) 0.18f else 0.12f),
            if (isDark) Color(0xFFFDA4AF) else Color(0xFFBE123C)
        )
    }

    val prayers = listOf(
        PrayerDayStatus(
            nameBn = "ফজর",
            nameEn = "Fajr",
            icon = "🌅",
            time = "০৫:১২ AM",
            rakah = "২ রাকাত সুন্নত",
            badge = if (isBangla) "✓ জামাতে আদায়" else "✓ In Jama'ah",
            isDone = true,
            isQaza = false
        ),
        PrayerDayStatus(
            nameBn = "যোহর",
            nameEn = "Dhuhr",
            icon = "☀️",
            time = "০১:১৫ PM",
            rakah = "২ রাকাত সুন্নত",
            badge = if (isBangla) "✓ একাকী আদায়" else "✓ Alone",
            isDone = true,
            isQaza = false
        ),
        PrayerDayStatus(
            nameBn = "আসর",
            nameEn = "Asr",
            icon = "🌤️",
            time = "০৪:১৫ PM",
            rakah = if (isAmber || isRose) "ওয়াক্ত পার হয়েছিল" else "ওয়াক্তে সম্পন্ন",
            badge = if (isAmber || isRose) (if (isBangla) "⚠️ কাজা পড়েছে" else "⚠️ Qaza") else (if (isBangla) "✓ আদায় হয়েছে" else "✓ Prayed"),
            isDone = !isAmber && !isRose,
            isQaza = isAmber || isRose
        ),
        PrayerDayStatus(
            nameBn = "মাগরিব",
            nameEn = "Maghrib",
            icon = "🌇",
            time = "০৫:২৮ PM",
            rakah = "২ রাকাত সুন্নত",
            badge = if (isBangla) "✓ জামাতে আদায়" else "✓ In Jama'ah",
            isDone = true,
            isQaza = false
        ),
        PrayerDayStatus(
            nameBn = "ইশা ও বিতর",
            nameEn = "Isha & Witr",
            icon = "🌙",
            time = "১০:০০ PM",
            rakah = if (isRose) "আদায় করা হয়নি" else "৩ রাকাত বিতরসহ",
            badge = if (isRose) (if (isBangla) "✕ মিস গেছে" else "✕ Missed") else (if (isBangla) "✓ আদায় হয়েছে" else "✓ Prayed"),
            isDone = !isRose,
            isQaza = false
        )
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colors.emerald)
            ) {
                Text(
                    text = if (isBangla) "বন্ধ করুন (Close)" else "Close",
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFF1F5F9))
                        .border(1.dp, if (isDark) Color.White.copy(alpha = 0.15f) else Color(0xFFCBD5E1), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📅", fontSize = 20.sp)
                }
                Column {
                    Text(
                        text = if (isBangla) "${DateUtil.toBanglaDigits(day.toString())} অক্টোবর ২০২৬, $weekdayStr" else "$day October 2026, $weekdayStr",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = statusBg,
                        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.35f))
                    ) {
                        Text(
                            text = statusText,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = statusColor
                        )
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                prayers.forEach { prayer ->
                    val badgeColor = when {
                        prayer.badge.contains("জামাতে") || prayer.badge.contains("আদায়") || prayer.badge.contains("একাকী") || prayer.badge.contains("Prayed") || prayer.badge.contains("Jama'ah") -> Color(0xFF10B981)
                        prayer.badge.contains("কাজা") || prayer.badge.contains("Qaza") -> Color(0xFFF59E0B)
                        else -> Color(0xFFF43F5E)
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isDark) Color.White.copy(alpha = 0.05f) else Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.10f) else Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(prayer.icon, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (isBangla) prayer.nameBn else prayer.nameEn,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp,
                                        color = colors.textPrimary
                                    )
                                    Text(
                                        text = "${prayer.time} • ${prayer.rakah}",
                                        fontSize = 9.sp,
                                        color = colors.textSecondary
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = badgeColor.copy(alpha = if (isDark) 0.18f else 0.12f),
                                border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.35f))
                            ) {
                                Text(
                                    text = prayer.badge,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = badgeColor
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Summary Counter Row
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isDark) Color.White.copy(alpha = 0.04f) else Color(0xFFF1F5F9),
                    border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFCBD5E1)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Text(
                            text = if (isGreen) (if (isBangla) "✓ ৫ ওয়াক্ত সম্পন্ন" else "✓ 5 Completed") else if (isAmber) (if (isBangla) "✓ ৪ ওয়াক্ত সম্পন্ন" else "✓ 4 Completed") else (if (isBangla) "✓ ৩ ওয়াক্ত সম্পন্ন" else "✓ 3 Completed"),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF10B981)
                        )
                        Text(
                            text = if (isAmber || isRose) (if (isBangla) "⚠️ ১ কাজা" else "⚠️ 1 Qaza") else (if (isBangla) "০ কাজা" else "0 Qaza"),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isAmber || isRose) Color(0xFFF59E0B) else colors.textSecondary
                        )
                        Text(
                            text = if (isRose) (if (isBangla) "✕ ১ মিস" else "✕ 1 Missed") else (if (isBangla) "০ মিস" else "0 Missed"),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isRose) Color(0xFFF43F5E) else colors.textSecondary
                        )
                    }
                }
            }
        },
        containerColor = colors.surfaceCard,
        shape = RoundedCornerShape(26.dp)
    )
}
