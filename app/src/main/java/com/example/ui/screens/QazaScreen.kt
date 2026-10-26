package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PrayerType
import com.example.data.model.QazaRecordEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.NamazUiState
import com.example.ui.viewmodel.NamazViewModel
import com.example.util.DateUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QazaScreen(
    uiState: NamazUiState,
    viewModel: NamazViewModel,
    modifier: Modifier = Modifier
) {
    val colors = FinancyTheme.colors
    val context = LocalContext.current
    val isBangla = uiState.isBangla
    var editPrayerName by remember { mutableStateOf<String?>(null) }
    var editCountInput by remember { mutableStateOf("") }
    var selectedQazaTab by remember { mutableStateOf(0) } // 0: Steppers, 1: Analytics & Calendar

    val qazaPrayers = listOf(
        PrayerType.FAJR,
        PrayerType.DHUHR,
        PrayerType.ASR,
        PrayerType.MAGHRIB,
        PrayerType.ISHA,
        PrayerType.WITR
    )

    val totalPending = uiState.qazaRecords.sumOf { it.totalMissed }
    val totalCompleted = uiState.qazaRecords.sumOf { it.completed }
    val totalAll = totalPending + totalCompleted
    val progressPercent = if (totalAll > 0) ((totalCompleted.toFloat() / totalAll.toFloat()) * 100).toInt() else 0
    val animatedProgress by animateFloatAsState(
        targetValue = if (totalAll > 0) totalCompleted.toFloat() / totalAll.toFloat() else 0f,
        label = "qaza_progress"
    )

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isBangla) "কাযা আমলনামা ও পোর্টফোলিও" else "Qaza Tracker & Portfolio",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = if (isBangla) "ধারাবাহিকভাবে পূর্বের কাযা আদায়ের হিসাব" else "Track and clear missed prayers systematically",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = colors.textMuted
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.card)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // 1. Fintech Portfolio Balance & Circular Goal Ring Card
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.card),
                    border = BorderStroke(1.2.dp, colors.border),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isBangla) "কাযা আদায়ের সামগ্রিক অগ্রগতি" else "Overall Clearance Progress",
                                color = colors.textMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = colors.emeraldSoft
                            ) {
                                Text(
                                    text = "$progressPercent% সম্পন্ন",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.emerald,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Middle: Progress Ring + Numbers
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Circular Gauge
                            Box(
                                modifier = Modifier.size(80.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    progress = { animatedProgress },
                                    modifier = Modifier.fillMaxSize(),
                                    color = colors.emerald,
                                    trackColor = colors.cardSubtle,
                                    strokeWidth = 8.dp
                                )
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "$progressPercent%",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 16.sp,
                                        color = colors.emerald
                                    )
                                    Text(
                                        text = if (isBangla) "আদায়" else "Cleared",
                                        fontSize = 9.5.sp,
                                        color = colors.textMuted
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // 3 Metric Columns
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                MetricColumn(
                                    title = if (isBangla) "মোট কাজা" else "Total Missed",
                                    value = if (isBangla) DateUtil.toBanglaDigits(totalAll.toString()) else totalAll.toString(),
                                    color = colors.textPrimary
                                )
                                MetricColumn(
                                    title = if (isBangla) "আদায় সম্পন্ন" else "Completed",
                                    value = if (isBangla) DateUtil.toBanglaDigits(totalCompleted.toString()) else totalCompleted.toString(),
                                    color = colors.emerald
                                )
                                MetricColumn(
                                    title = if (isBangla) "বাকি রয়েছে" else "Remaining",
                                    value = if (isBangla) DateUtil.toBanglaDigits(totalPending.toString()) else totalPending.toString(),
                                    color = if (totalPending > 0) colors.amber else colors.emerald
                                )
                            }
                        }

                        if (totalPending > 0) {
                            Spacer(modifier = Modifier.height(14.dp))
                            val daysIfOneDaily = (totalPending + 4) / 5
                            Surface(
                                color = colors.amberSoft,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(0.8.dp, colors.amber.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (isBangla) "💡 প্রতিদিন ১টি করে অতিরিক্ত কাজা পড়লে আনুমানিক ${DateUtil.toBanglaDigits(daysIfOneDaily.toString())} দিনে ঋণমুক্ত হবেন ইনশাআল্লাহ।"
                                    else "💡 Praying 1 extra Qaza per Salah clears all debts in approx $daysIfOneDaily days InshaAllah.",
                                    color = colors.amber,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // 2. Tab Capsule Selector (Steppers vs Analytics)
            item {
                Surface(
                    color = colors.cardSubtle,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, colors.border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedQazaTab == 0) colors.emerald else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedQazaTab = 0 }
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 8.dp)) {
                                Text(
                                    text = if (isBangla) "ওয়াক্ত ভিত্তিক কাযা" else "Waqt Steppers",
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedQazaTab == 0) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedQazaTab == 0) Color.White else colors.textSecondary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedQazaTab == 1) colors.emerald else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedQazaTab = 1 }
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 8.dp)) {
                                Text(
                                    text = if (isBangla) "ধারাবাহিকতা ও অ্যানালিটিক্স" else "Analytics & Stats",
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedQazaTab == 1) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedQazaTab == 1) Color.White else colors.textSecondary
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // 3. Tab Content
            if (selectedQazaTab == 0) {
                // Waqt-based Steppers
                items(qazaPrayers) { prayer ->
                    val qazaRecord = uiState.qazaRecords.find { it.prayerName.equals(prayer.keyName, ignoreCase = true) }
                        ?: QazaRecordEntity(prayerName = prayer.keyName)

                    FinancyQazaStepperCard(
                        prayer = prayer,
                        qazaRecord = qazaRecord,
                        isBangla = isBangla,
                        onPrayed = {
                            viewModel.adjustQaza(prayer.keyName, "prayed", context)
                        },
                        onAddOne = {
                            viewModel.adjustQaza(prayer.keyName, "missed", context)
                        },
                        onAddFive = {
                            viewModel.adjustQaza(prayer.keyName, "missed_5", context)
                        },
                        onEditClick = {
                            editPrayerName = prayer.keyName
                            editCountInput = qazaRecord.totalMissed.toString()
                        }
                    )
                }
            } else {
                // Analytics & Stats View
                item {
                    AnalyticsSectionView(uiState = uiState, isBangla = isBangla)
                }
            }
        }
    }

    if (editPrayerName != null) {
        val prayerObj = qazaPrayers.find { it.keyName.equals(editPrayerName, ignoreCase = true) }
        AlertDialog(
            onDismissRequest = { editPrayerName = null },
            containerColor = colors.card,
            title = {
                Text(
                    text = if (isBangla) "${prayerObj?.banglaName} কাযা সংখ্যা নির্ধারণ"
                    else "Set ${prayerObj?.englishName} Qaza Count",
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = if (isBangla) "বাকি থাকা মোট কাযার সংখ্যা লিখুন:" else "Enter total pending Qaza count:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = editCountInput,
                        onValueChange = { editCountInput = it.filter { ch -> ch.isDigit() } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_qaza_count"),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val count = editCountInput.toIntOrNull() ?: 0
                        editPrayerName?.let { name ->
                            viewModel.setQazaDirect(name, count)
                        }
                        editPrayerName = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.emerald, contentColor = Color.White)
                ) {
                    Text(if (isBangla) "সংরক্ষণ" else "Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editPrayerName = null }) {
                    Text(if (isBangla) "বাতিল" else "Cancel", color = colors.textSecondary)
                }
            }
        )
    }
}

@Composable
private fun MetricColumn(title: String, value: String, color: Color) {
    val colors = FinancyTheme.colors
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = title, fontSize = 10.5.sp, color = colors.textMuted)
    }
}

@Composable
private fun FinancyQazaStepperCard(
    prayer: PrayerType,
    qazaRecord: QazaRecordEntity,
    isBangla: Boolean,
    onPrayed: () -> Unit,
    onAddOne: () -> Unit,
    onAddFive: () -> Unit,
    onEditClick: () -> Unit
) {
    val colors = FinancyTheme.colors

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .testTag("qaza_card_${prayer.keyName}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        border = BorderStroke(1.dp, colors.border),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(colors.emeraldSoft)
                            .border(0.8.dp, colors.emerald.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = prayer.arabicName.take(1),
                            color = colors.emerald,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isBangla) prayer.banglaName else prayer.englishName,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = colors.textPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = prayer.arabicName,
                                fontSize = 12.sp,
                                color = colors.textMuted
                            )
                        }
                        Text(
                            text = if (isBangla) prayer.rakahInfoBn else prayer.rakahInfoEn,
                            fontSize = 11.sp,
                            color = colors.textMuted
                        )
                    }
                }

                IconButton(onClick = onEditClick, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Direct",
                        tint = colors.textMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Counts & Stepper Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = if (isBangla) DateUtil.toBanglaDigits(qazaRecord.totalMissed.toString()) else qazaRecord.totalMissed.toString(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (qazaRecord.totalMissed > 0) colors.amber else colors.emerald
                        )
                        Text(
                            text = if (isBangla) " বাকি" else " pending",
                            fontSize = 12.sp,
                            color = colors.textMuted,
                            modifier = Modifier.padding(bottom = 3.dp)
                        )
                    }
                    Text(
                        text = if (isBangla) "আদায় সম্পন্ন: ${DateUtil.toBanglaDigits(qazaRecord.completed.toString())}"
                        else "Completed: ${qazaRecord.completed}",
                        fontSize = 11.sp,
                        color = colors.textMuted
                    )
                }

                // Quick Action Buttons
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // +1 Missed
                    OutlinedButton(
                        onClick = onAddOne,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("btn_add_missed_${prayer.keyName}"),
                        border = BorderStroke(1.dp, colors.border),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.textSecondary)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(13.dp))
                        Text(text = "১ মিস", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // +1 Prayed
                    Button(
                        onClick = onPrayed,
                        enabled = qazaRecord.totalMissed > 0,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("btn_prayed_${prayer.keyName}"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.emerald,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(text = if (isBangla) "আদায় ✓" else "Prayed ✓", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalyticsSectionView(uiState: NamazUiState, isBangla: Boolean) {
    val colors = FinancyTheme.colors
    val streakDays = uiState.currentStreakDays
    val weeklyRate = uiState.weeklyCompletionRate
    val jamatCount = uiState.currentDayLogs.count { it.status == "jamaah" }
    val aloneCount = uiState.currentDayLogs.count { it.status == "on_time" }
    val qazaCount = uiState.currentDayLogs.count { it.status == "late" || it.status == "qaza" }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Streak & Weekly Rate Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.card),
                border = BorderStroke(1.dp, colors.border)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "🔥 " + (if (isBangla) "ধারাবাহিকতা" else "Streak"),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.amber
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isBangla) "${DateUtil.toBanglaDigits(streakDays.toString())} দিন" else "$streakDays Days",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = colors.textPrimary
                    )
                    Text(text = if (isBangla) "প্রতিদিন ৫ ওয়াক্ত" else "5 daily prayers", fontSize = 11.sp, color = colors.textMuted)
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.card),
                border = BorderStroke(1.dp, colors.border)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "📈 " + (if (isBangla) "সাপ্তাহিক হার" else "Weekly Rate"),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.cyan
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isBangla) "${DateUtil.toBanglaDigits(weeklyRate.toString())}%" else "$weeklyRate%",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = colors.textPrimary
                    )
                    Text(text = if (isBangla) "বিগত ৭ দিনের গড়" else "Past 7-day avg", fontSize = 11.sp, color = colors.textMuted)
                }
            }
        }

        // Status Distribution
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colors.card),
            border = BorderStroke(1.dp, colors.border)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isBangla) "আজকের নামাযের বণ্টন" else "Today's Prayer Distribution",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                DistributionRow("জামাতে আদায়", jamatCount, 5, colors.emerald, isBangla)
                Spacer(modifier = Modifier.height(8.dp))
                DistributionRow("একাকী আদায়", aloneCount, 5, colors.cyan, isBangla)
                Spacer(modifier = Modifier.height(8.dp))
                DistributionRow("কাযা আদায়", qazaCount, 5, colors.amber, isBangla)
            }
        }
    }
}

@Composable
private fun DistributionRow(title: String, count: Int, total: Int, color: Color, isBangla: Boolean) {
    val colors = FinancyTheme.colors
    val fraction = (count.toFloat() / total.toFloat()).coerceIn(0f, 1f)
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, fontSize = 12.sp, color = colors.textSecondary)
            Text(
                text = if (isBangla) "${DateUtil.toBanglaDigits(count.toString())}/${DateUtil.toBanglaDigits(total.toString())}" else "$count/$total",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = colors.cardSubtle
        )
    }
}
