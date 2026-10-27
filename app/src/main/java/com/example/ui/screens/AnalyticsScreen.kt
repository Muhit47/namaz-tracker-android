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
    val colors = FinancyTheme.colors
    val isBangla = uiState.isBangla

    // Calculate metrics
    val streakDays = uiState.currentStreakDays
    val weeklyRate = uiState.weeklyCompletionRate

    // Counts from today's logs
    val jamatCount = uiState.currentDayLogs.count { it.status == "jamaah" }
    val aloneCount = uiState.currentDayLogs.count { it.status == "on_time" }
    val qazaCount = uiState.currentDayLogs.count { it.status == "late" || it.status == "qaza" }

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBangla) "নামাযের পরিসংখ্যান" else "Salah Analytics",
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
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // 1. Financy Metric Bento Cards (Streak & Weekly Rate)
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("stat_card_streak"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colors.surfaceCard
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.borderSubtle)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "🔥 " + (if (isBangla) "ধারাবাহিকতা" else "Current Streak"),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.neonGreen
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isBangla) "${DateUtil.toBanglaDigits(streakDays.toString())} দিন"
                                else "$streakDays Days",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isBangla) "প্রতিদিন ৫ ওয়াক্ত" else "All 5 daily fardh",
                                fontSize = 11.sp,
                                color = colors.textSecondary
                            )
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("stat_card_weekly_rate"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colors.surfaceCard
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.borderSubtle)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "📈 " + (if (isBangla) "সাপ্তাহিক হার" else "Weekly Rate"),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.neonGreen
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isBangla) "${DateUtil.toBanglaDigits(weeklyRate.toString())}%"
                                else "$weeklyRate%",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = colors.neonGreen
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isBangla) "বিগত ৭ দিনের গড়" else "Past 7-day average",
                                fontSize = 11.sp,
                                color = colors.textSecondary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 2. Status Distribution Bento Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colors.surfaceCard
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.borderSubtle)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isBangla) "আজকের নামাযের ধরণ" else "Today's Prayer Breakdown",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        DistributionRow(
                            title = if (isBangla) "জামাতে আদায়" else "With Jama'at",
                            count = jamatCount,
                            total = 5,
                            color = colors.neonGreen,
                            isBangla = isBangla
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        DistributionRow(
                            title = if (isBangla) "একাকী আদায়" else "Alone / On-time",
                            count = aloneCount,
                            total = 5,
                            color = colors.emeraldPrimary,
                            isBangla = isBangla
                        )
                        Spacer(modifier = Modifier.height(12.dp))
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
                    color = colors.neonGreen
                )
                Spacer(modifier = Modifier.height(10.dp))

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
    val colors = FinancyTheme.colors
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
                color = colors.textPrimary
            )
            Text(
                text = if (isBangla) "${DateUtil.toBanglaDigits(count.toString())}/${DateUtil.toBanglaDigits(total.toString())}"
                else "$count/$total",
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = colors.surfaceSubtle
        )
    }
}

@Composable
private fun VirtueCard(
    arabicText: String,
    translation: String
) {
    val colors = FinancyTheme.colors
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surfaceCard
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.borderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = arabicText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colors.neonGreen,
                lineHeight = 24.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = translation,
                fontSize = 12.5.sp,
                color = colors.textSecondary,
                lineHeight = 18.sp
            )
        }
    }
}
