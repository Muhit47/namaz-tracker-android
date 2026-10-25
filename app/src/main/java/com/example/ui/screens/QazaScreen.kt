package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
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
    val context = LocalContext.current
    val isBangla = uiState.isBangla
    var editPrayerName by remember { mutableStateOf<String?>(null) }
    var editCountInput by remember { mutableStateOf("") }

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

    Scaffold(
        containerColor = Slate50,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBangla) "আমলনামা ও কাযা ট্র্যাকার" else "Qaza & Deeds Tracker",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
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
            // 1. Total Overview Hero Card (Framer Glassmorphism Bento Card)
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = GlassCardWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Text(
                            text = if (isBangla) "মোট কাযা হিসাব (আমলনামা)" else "Total Missed Prayers (Qaza)",
                            color = Slate600,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (isBangla) "${DateUtil.toBanglaDigits(totalPending.toString())} ওয়াক্ত"
                                    else "$totalPending Waqts",
                                    color = if (totalPending > 0) Amber600 else Emerald700,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isBangla) "আদায় বাকি রয়েছে" else "Pending to Pray",
                                    color = Slate600,
                                    fontSize = 12.sp
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (isBangla) "${DateUtil.toBanglaDigits(totalCompleted.toString())} ওয়াক্ত"
                                    else "$totalCompleted Waqts",
                                    color = Emerald700,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isBangla) "আদায় সম্পন্ন হয়েছে" else "Made-up (Completed)",
                                    color = Slate600,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        if (totalPending > 0) {
                            Spacer(modifier = Modifier.height(12.dp))
                            val daysIfOneDaily = (totalPending + 4) / 5
                            Surface(
                                color = Amber50,
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Amber200)
                            ) {
                                Text(
                                    text = if (isBangla) "💡 দৈনিক ১টি করে অতিরিক্ত কাযা আদায় করলে প্রায় ${DateUtil.toBanglaDigits(daysIfOneDaily.toString())} দিনে শেষ হবে ইনশাআল্লাহ।"
                                    else "💡 Praying 1 extra Qaza with each prayer completes in approx $daysIfOneDaily days InshaAllah.",
                                    color = Amber700,
                                    fontSize = 11.5.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // 2. Section Title
            item {
                Text(
                    text = if (isBangla) "ওয়াক্ত ভিত্তিক কাযা হিসাব" else "Qaza Breakdown By Waqt",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 3. Cards for each prayer
            items(qazaPrayers) { prayer ->
                val qazaRecord = uiState.qazaRecords.find { it.prayerName.equals(prayer.keyName, ignoreCase = true) }
                    ?: QazaRecordEntity(prayerName = prayer.keyName)

                QazaItemCard(
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
        }
    }

    if (editPrayerName != null) {
        val prayerObj = qazaPrayers.find { it.keyName.equals(editPrayerName, ignoreCase = true) }
        AlertDialog(
            onDismissRequest = { editPrayerName = null },
            containerColor = Color.White,
            title = {
                Text(
                    text = if (isBangla) "${prayerObj?.banglaName} কাযা সংখ্যা নির্ধারণ"
                    else "Set ${prayerObj?.englishName} Qaza Count",
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
            },
            text = {
                Column {
                    Text(
                        text = if (isBangla) "বাকি থাকা মোট কাযার সংখ্যা লিখুন:" else "Enter total pending Qaza count:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate800
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = editCountInput,
                        onValueChange = { editCountInput = it.filter { ch -> ch.isDigit() } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_qaza_count")
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
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald500, contentColor = Color.White)
                ) {
                    Text(if (isBangla) "সংরক্ষণ" else "Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editPrayerName = null }) {
                    Text(if (isBangla) "বাতিল" else "Cancel", color = Slate600)
                }
            }
        )
    }
}

@Composable
private fun QazaItemCard(
    prayer: PrayerType,
    qazaRecord: QazaRecordEntity,
    isBangla: Boolean,
    onPrayed: () -> Unit,
    onAddOne: () -> Unit,
    onAddFive: () -> Unit,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .testTag("qaza_card_${prayer.keyName}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GlassCardWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Emerald50)
                            .border(0.8.dp, Emerald100, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = prayer.arabicName.take(1),
                            color = Emerald700,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Column {
                        Text(
                            text = if (isBangla) prayer.banglaName else prayer.englishName,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = Slate900
                        )
                        Text(
                            text = if (isBangla) prayer.rakahInfoBn else prayer.rakahInfoEn,
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate600
                        )
                    }
                }

                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Emerald600,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isBangla) DateUtil.toBanglaDigits(qazaRecord.totalMissed.toString())
                        else qazaRecord.totalMissed.toString(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (qazaRecord.totalMissed > 0) Amber600 else Emerald700
                    )
                    Text(
                        text = if (isBangla) "বাকি (আদায়কৃত: ${DateUtil.toBanglaDigits(qazaRecord.completed.toString())})"
                        else "Pending (Made up: ${qazaRecord.completed})",
                        fontSize = 11.sp,
                        color = Slate600
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = onAddFive,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(36.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Slate900
                        )
                    ) {
                        Text(
                            text = if (isBangla) "+৫" else "+5",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    FilledTonalButton(
                        onClick = onAddOne,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(36.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Slate100,
                            contentColor = Slate900
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add 1", modifier = Modifier.size(16.dp))
                    }

                    Button(
                        onClick = onPrayed,
                        enabled = qazaRecord.totalMissed > 0,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Emerald500,
                            contentColor = Color.White,
                            disabledContainerColor = Slate100,
                            disabledContentColor = Slate400
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(36.dp).testTag("btn_qaza_done_${prayer.keyName}")
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Done", modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isBangla) "আদায়" else "Done",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
