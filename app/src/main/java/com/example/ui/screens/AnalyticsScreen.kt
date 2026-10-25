package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PrayerStatus
import com.example.ui.theme.*
import com.example.ui.viewmodel.NamazUiState
import com.example.util.DateUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    uiState: NamazUiState,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isBangla = uiState.isBangla

    // Calculate metrics
    val streakDays = uiState.currentStreakDays
    val weeklyRate = uiState.weeklyCompletionRate

    // Counts from today's logs
    val jamatCount = uiState.currentDayLogs.count { it.status == "jamaah" }
    val aloneCount = uiState.currentDayLogs.count { it.status == "on_time" }
    val qazaCount = uiState.currentDayLogs.count { it.status == "late" || it.status == "qaza" }

    Scaffold(
        containerColor = Slate50,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBangla) "নামাযের পরিসংখ্যান" else "Salah Analytics",
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
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 1. Streak & Weekly Rate Cards
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("stat_card_streak"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Emerald50
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Emerald100)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "🔥 " + (if (isBangla) "ধারাবাহিকতা" else "Current Streak"),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Emerald700
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isBangla) "${DateUtil.toBanglaDigits(streakDays.toString())} দিন"
                                else "$streakDays Days",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Emerald700
                            )
                            Text(
                                text = if (isBangla) "প্রতিদিন ৫ ওয়াক্ত" else "All 5 daily fardh",
                                fontSize = 11.sp,
                                color = Slate600
                            )
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("stat_card_weekly_rate"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Cyan50
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Cyan200)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "📈 " + (if (isBangla) "সাপ্তাহিক হার" else "Weekly Rate"),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Cyan700
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isBangla) "${DateUtil.toBanglaDigits(weeklyRate.toString())}%"
                                else "$weeklyRate%",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Cyan700
                            )
                            Text(
                                text = if (isBangla) "বিগত ৭ দিনের গড়" else "Past 7-day average",
                                fontSize = 11.sp,
                                color = Slate600
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 2. Status Distribution Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = GlassCardWhite
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isBangla) "আজকের নামাযের ধরণ" else "Today's Prayer Breakdown",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        DistributionRow(
                            title = if (isBangla) "জামাতে আদায়" else "With Jama'at",
                            count = jamatCount,
                            total = 5,
                            color = Emerald500,
                            isBangla = isBangla
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        DistributionRow(
                            title = if (isBangla) "একাকী আদায়" else "Alone / On-time",
                            count = aloneCount,
                            total = 5,
                            color = Cyan600,
                            isBangla = isBangla
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        DistributionRow(
                            title = if (isBangla) "কাযা আদায়" else "Qaza (Late)",
                            count = qazaCount,
                            total = 5,
                            color = Amber600,
                            isBangla = isBangla
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 3. Virtues of Salah & Hadiths
            item {
                Text(
                    text = if (isBangla) "নামাযের ফজিলত ও গুরুত্ব" else "Virtues & Rewards of Prayer",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
                Spacer(modifier = Modifier.height(8.dp))

                VirtueCard(
                    arabicText = "صَلَاةُ الْجَمَاعَةِ تَفْضُلُ صَلَاةَ الْفَذِّ بِسَبْعٍ وَعِشْرِينَ دَرَجَةً",
                    translation = if (isBangla) "“জামাতে নামায আদায় একাকী নামাযের চেয়ে সাতাশ গুণ বেশি মর্যাদা ও সওয়াব রাখে।” [সহীহ বুখারী]"
                    else "“Prayer in congregation is twenty-seven times more rewarding than prayer performed alone.” [Sahih Bukhari]"
                )
                Spacer(modifier = Modifier.height(10.dp))

                VirtueCard(
                    arabicText = "مَنْ صَلَّى الْبَرْدَيْنِ دَخَلَ الْجَنَّةَ",
                    translation = if (isBangla) "“যে ব্যক্তি দুই শীতল সময়ের নামায (ফজর ও আসর) যথাসময়ে আদায় করবে, সে জান্নাতে প্রবেশ করবে।” [সহীহ বুখারী ও মুসলিম]"
                    else "“Whoever prays the two cool prayers (Fajr and Asr) will enter Paradise.” [Sahih Bukhari & Muslim]"
                )
            }
        }
    }
}

@Composable
private fun DistributionRow(
    title: String,
    count: Int,
    total: Int,
    color: Color,
    isBangla: Boolean
) {
    val fraction = (count.toFloat() / total.toFloat()).coerceIn(0f, 1f)
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = Slate900
            )
            Text(
                text = if (isBangla) "${DateUtil.toBanglaDigits(count.toString())}/${DateUtil.toBanglaDigits(total.toString())}"
                else "$count/$total",
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
    }
}

@Composable
private fun VirtueCard(
    arabicText: String,
    translation: String
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = GlassCardWhite
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = arabicText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Emerald700
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = translation,
                fontSize = 12.sp,
                color = Slate600
            )
        }
    }
}
