package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PrayerLogEntity
import com.example.data.model.PrayerStatus
import com.example.data.model.PrayerType
import com.example.ui.components.FinancyPrayerCard
import com.example.ui.theme.FinancyTheme
import com.example.ui.viewmodel.NamazUiState
import com.example.ui.viewmodel.NamazViewModel
import com.example.util.DateUtil
import java.text.SimpleDateFormat
import java.util.*

enum class TrackerSubTab(
    val titleEn: String,
    val titleBn: String
) {
    ANALYTICS("Analytics", "অ্যানালিটিক্স"),
    CALENDAR("Calendar", "ক্যালেন্ডার"),
    AUTO_QAZA("Auto Qaza", "স্বয়ংক্রিয় কাযা")
}

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

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isBangla) "নামায ট্র্যাকার ও হিস্ট্রি" else "Salah Tracker & Insights",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Text(
                                text = if (isBangla) "স্বয়ংক্রিয় হিস্ট্রি, স্ট্রিক ও পারফরম্যান্স" else "Automatic history, streaks & performance",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = colors.textSecondary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = colors.emerald.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, colors.emerald.copy(alpha = 0.3f)),
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Text(
                                text = if (isBangla) "১০০% অটো-সিঙ্ক" else "100% Auto-Sync",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.emerald,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.card)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Top Tab Row
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                color = colors.card,
                border = BorderStroke(1.dp, colors.border)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    TrackerSubTab.entries.forEach { tab ->
                        val isSelected = selectedTab == tab
                        val bg = if (isSelected) colors.emerald else Color.Transparent
                        val textCol = if (isSelected) Color.White else colors.textSecondary

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(bg)
                                .clickable { selectedTab = tab }
                                .padding(vertical = 9.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isBangla) tab.titleBn else tab.titleEn,
                                fontSize = 11.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = textCol,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "tracker_tab_content"
            ) { tab ->
                when (tab) {
                    TrackerSubTab.ANALYTICS -> TrackerAnalyticsTab(uiState = uiState, isBangla = isBangla)
                    TrackerSubTab.CALENDAR -> TrackerCalendarTab(uiState = uiState, viewModel = viewModel, isBangla = isBangla)
                    TrackerSubTab.AUTO_QAZA -> TrackerAutoQazaTab(uiState = uiState, isBangla = isBangla)
                }
            }
        }
    }
}

@Composable
private fun TrackerAnalyticsTab(
    uiState: NamazUiState,
    isBangla: Boolean
) {
    val colors = FinancyTheme.colors
    val streakDays = uiState.currentStreakDays
    val weeklyRate = uiState.weeklyCompletionRate

    val jamatCount = uiState.currentDayLogs.count { it.status == "jamaah" }
    val aloneCount = uiState.currentDayLogs.count { it.status == "on_time" }
    val qazaCount = uiState.currentDayLogs.count { it.status == "late" || it.status == "qaza" }
    val missedCount = uiState.currentDayLogs.count { it.status == "missed" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Streak & Weekly Rate Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.card),
                    border = BorderStroke(1.dp, colors.border)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "🔥 " + if (isBangla) "ধারাবাহিকতা" else "Current Streak",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.emerald
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isBangla) "${DateUtil.toBanglaDigits(streakDays.toString())} দিন" else "$streakDays Days",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isBangla) "একটানা নামায আদায়" else "Consecutive prayer streak",
                            fontSize = 11.sp,
                            color = colors.textSecondary
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.card),
                    border = BorderStroke(1.dp, colors.border)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "⚡ " + if (isBangla) "সাপ্তাহিক হার" else "Weekly Rate",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.cyan
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isBangla) "${DateUtil.toBanglaDigits(weeklyRate.toString())}%" else "$weeklyRate%",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isBangla) "৭ দিনের গড় পারফরম্যান্স" else "7-day completion rate",
                            fontSize = 11.sp,
                            color = colors.textSecondary
                        )
                    }
                }
            }
        }

        // Today's Automatic Distribution
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.card),
                border = BorderStroke(1.dp, colors.border)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isBangla) "আজকের নামাযের অবস্থা" else "Today's Prayer Status",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    StatusProgressBar(
                        title = if (isBangla) "জামাতে আদায়" else "In Jama'at",
                        count = jamatCount,
                        total = 5,
                        barColor = colors.emerald,
                        isBangla = isBangla
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    StatusProgressBar(
                        title = if (isBangla) "একাকী / সময়ে" else "On Time / Alone",
                        count = aloneCount,
                        total = 5,
                        barColor = colors.cyan,
                        isBangla = isBangla
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    StatusProgressBar(
                        title = if (isBangla) "দেরিতে / কাযা" else "Late / Qaza",
                        count = qazaCount,
                        total = 5,
                        barColor = colors.amber,
                        isBangla = isBangla
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    StatusProgressBar(
                        title = if (isBangla) "ছুটে যাওয়া (Missed)" else "Missed",
                        count = missedCount,
                        total = 5,
                        barColor = colors.rose,
                        isBangla = isBangla
                    )
                }
            }
        }

        // Informational Insight Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.emeraldSoft.copy(alpha = 0.5f)),
                border = BorderStroke(1.dp, colors.emerald.copy(alpha = 0.25f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "💡", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isBangla)
                            "হোম স্ক্রিনে পাঁচ ওয়াক্ত নামায লগ করার সাথে সাথে ট্র্যাকার স্বয়ংক্রিয়ভাবে হিস্ট্রি এবং অ্যানালিটিক্স আপডেট করে।"
                        else
                            "Logging your prayers on the Home screen automatically syncs and updates your tracker history and analytics.",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp,
                        color = colors.emeraldDark,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TrackerCalendarTab(
    uiState: NamazUiState,
    viewModel: NamazViewModel,
    isBangla: Boolean
) {
    val colors = FinancyTheme.colors
    val selectedIso = uiState.selectedDate

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Date Selector Pill Row
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = colors.card),
                border = BorderStroke(1.dp, colors.border)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { viewModel.changeDateOffset(-1) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Prev", tint = colors.textPrimary)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = DateUtil.formatDisplayDate(selectedIso, isBangla),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = if (selectedIso == DateUtil.getTodayIso()) (if (isBangla) "আজকের দিন" else "Today")
                            else (if (isBangla) "পূর্ববর্তী হিস্ট্রি" else "Past Record"),
                            fontSize = 11.sp,
                            color = colors.emerald,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    IconButton(
                        onClick = { viewModel.changeDateOffset(1) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next", tint = colors.textPrimary)
                    }
                }
            }
        }

        // Day Summary Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isBangla) "ঐ দিনের নামাযের হিস্ট্রি:" else "Prayer records for this day:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Surface(
                    color = colors.emeraldSoft,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isBangla) "${DateUtil.toBanglaDigits(uiState.totalPrayedToday.toString())}/৫ আদায়"
                        else "${uiState.totalPrayedToday}/5 Prayed",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.emerald,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // List of 5 Prayers with status on the selected date
        items(5) { index ->
            val prayerType = when (index) {
                0 -> PrayerType.FAJR
                1 -> PrayerType.DHUHR
                2 -> PrayerType.ASR
                3 -> PrayerType.MAGHRIB
                else -> PrayerType.ISHA
            }
            val log = uiState.currentDayLogs.find { it.prayerName.equals(prayerType.keyName, ignoreCase = true) }
            val status = log?.status?.let { PrayerStatus.fromCode(it) } ?: PrayerStatus.PENDING

            FinancyPrayerCard(
                prayerType = prayerType,
                timeStr = when (prayerType) {
                    PrayerType.FAJR -> uiState.prayerTimes.fajr
                    PrayerType.DHUHR -> uiState.prayerTimes.dhuhr
                    PrayerType.ASR -> uiState.prayerTimes.asr
                    PrayerType.MAGHRIB -> uiState.prayerTimes.maghrib
                    PrayerType.ISHA -> uiState.prayerTimes.isha
                    else -> ""
                },
                log = log,
                isBangla = isBangla,
                isCurrentWaqt = false,
                onStatusChange = { newStatus ->
                    viewModel.updatePrayerStatus(prayerType.keyName, newStatus.code)
                },
                onToggleSunnah = {
                    viewModel.toggleSunnah(prayerType.keyName)
                },
                onUpdateNotes = { notes ->
                    viewModel.updateNotes(prayerType.keyName, notes)
                }
            )
        }
    }
}

@Composable
private fun TrackerAutoQazaTab(
    uiState: NamazUiState,
    isBangla: Boolean
) {
    val colors = FinancyTheme.colors

    val prayerNames = listOf(
        PrayerType.FAJR to ("ফজর" to "Fajr"),
        PrayerType.DHUHR to ("যোহর" to "Dhuhr"),
        PrayerType.ASR to ("আসর" to "Asr"),
        PrayerType.MAGHRIB to ("মাগরিব" to "Maghrib"),
        PrayerType.ISHA to ("ইশা" to "Isha")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = colors.card),
                border = BorderStroke(1.dp, colors.border)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(36.dp),
                            shape = CircleShape,
                            color = colors.amber.copy(alpha = 0.15f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "⏳", fontSize = 16.sp)
                            }
                        }
                        Column {
                            Text(
                                text = if (isBangla) "স্বয়ংক্রিয় কাযা সারসংক্ষেপ" else "Automatic Qaza Summary",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Text(
                                text = if (isBangla) "হোমের লগের ভিত্তিতে স্বয়ংক্রিয় হিসাব" else "Read-only summary derived from your daily logs",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = colors.textSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    prayerNames.forEach { (type, names) ->
                        val (bn, en) = names
                        val qazaRecord = uiState.qazaList.find { it.prayerName.equals(type.keyName, ignoreCase = true) }
                        val missedCount = qazaRecord?.totalMissed ?: 0
                        val completedCount = qazaRecord?.completed ?: 0

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    modifier = Modifier.size(8.dp),
                                    shape = CircleShape,
                                    color = if (missedCount > 0) colors.amber else colors.emerald
                                ) {}
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (isBangla) bn else en,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = colors.textPrimary
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    color = if (missedCount > 0) colors.amber.copy(alpha = 0.12f) else colors.emeraldSoft,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = if (isBangla) "বাকি কাযা: ${DateUtil.toBanglaDigits(missedCount.toString())}"
                                        else "Remaining: $missedCount",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (missedCount > 0) colors.amber else colors.emerald,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                if (completedCount > 0) {
                                    Surface(
                                        color = colors.cyan.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = if (isBangla) "আদায়: ${DateUtil.toBanglaDigits(completedCount.toString())}"
                                            else "Done: $completedCount",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = colors.cyan,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }
                        HorizontalDivider(color = colors.border.copy(alpha = 0.5f), thickness = 0.6.dp)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusProgressBar(
    title: String,
    count: Int,
    total: Int,
    barColor: Color,
    isBangla: Boolean
) {
    val colors = FinancyTheme.colors
    val progress = if (total > 0) (count.toFloat() / total.toFloat()).coerceIn(0f, 1f) else 0f

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, fontSize = 12.sp, color = colors.textSecondary)
            Text(
                text = if (isBangla) "${DateUtil.toBanglaDigits(count.toString())}/${DateUtil.toBanglaDigits(total.toString())}"
                else "$count/$total",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = barColor,
            trackColor = colors.surfaceSubtle
        )
    }
}
