package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PrayerLogEntity
import com.example.data.model.PrayerStatus
import com.example.data.model.PrayerType
import com.example.ui.theme.*

@Composable
fun PrayerCard(
    prayerType: PrayerType,
    timeString: String,
    log: PrayerLogEntity?,
    isBangla: Boolean,
    onStatusSelected: (String) -> Unit,
    onToggleSunnah: () -> Unit,
    onSaveNotes: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentStatusCode = log?.status ?: "pending"
    val currentStatus = PrayerStatus.fromCode(currentStatusCode)
    val isCompleted = currentStatusCode == "jamaah" || currentStatusCode == "on_time"
    val isSunnahDone = (log?.isSunnah ?: 0) == 1

    var showNotesDialog by remember { mutableStateOf(false) }
    var notesText by remember(log?.notes) { mutableStateOf(log?.notes ?: "") }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .testTag("prayer_card_${prayerType.keyName}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GlassCardWhite),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (currentStatusCode != "pending") currentStatus.activeBorder.copy(alpha = 0.5f) else GlassBorder
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCompleted) 1.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Row 1: Prayer name, Arabic, Waqt Time, Notes icon
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
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (isCompleted) Emerald50 else Slate100
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = prayerType.arabicName.take(1),
                            fontWeight = FontWeight.Bold,
                            color = if (isCompleted) Emerald600 else Slate600,
                            fontSize = 17.sp
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isBangla) prayerType.banglaName else prayerType.englishName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                            if (prayerType.isFardh) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = Emerald50,
                                    shape = RoundedCornerShape(6.dp),
                                    border = androidx.compose.foundation.BorderStroke(0.8.dp, Emerald100)
                                ) {
                                    Text(
                                        text = if (isBangla) "ফরয" else "Fardh",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Emerald700
                                    )
                                }
                            }
                        }

                        Text(
                            text = if (isBangla) prayerType.rakahInfoBn else prayerType.rakahInfoEn,
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate600,
                            fontSize = 11.5.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = timeString,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Emerald600
                        )
                        Text(
                            text = prayerType.arabicName,
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate400,
                            fontSize = 11.sp
                        )
                    }

                    IconButton(
                        onClick = { showNotesDialog = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = "Notes",
                            tint = if (!log?.notes.isNullOrBlank()) Emerald600 else Slate400
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Row 2: Status Selection Pills matching user's Framer pills exactly
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                StatusPill(
                    title = if (isBangla) "জামাতে" else "Jama'at",
                    isSelected = currentStatusCode == "jamaah",
                    status = PrayerStatus.JAMAAH,
                    modifier = Modifier.weight(1f).testTag("btn_jamaah_${prayerType.keyName}"),
                    onClick = { onStatusSelected("jamaah") }
                )
                StatusPill(
                    title = if (isBangla) "একাকী" else "Alone",
                    isSelected = currentStatusCode == "on_time",
                    status = PrayerStatus.ON_TIME,
                    modifier = Modifier.weight(1f).testTag("btn_on_time_${prayerType.keyName}"),
                    onClick = { onStatusSelected("on_time") }
                )
                StatusPill(
                    title = if (isBangla) "দেরিতে" else "Late",
                    isSelected = currentStatusCode == "late",
                    status = PrayerStatus.LATE,
                    modifier = Modifier.weight(1f).testTag("btn_late_${prayerType.keyName}"),
                    onClick = { onStatusSelected("late") }
                )
                StatusPill(
                    title = if (isBangla) "কাযা" else "Qaza",
                    isSelected = currentStatusCode == "qaza",
                    status = PrayerStatus.QAZA,
                    modifier = Modifier.weight(1f).testTag("btn_qaza_${prayerType.keyName}"),
                    onClick = { onStatusSelected("qaza") }
                )
                StatusPill(
                    title = if (isBangla) "মিস" else "Missed",
                    isSelected = currentStatusCode == "missed",
                    status = PrayerStatus.MISSED,
                    modifier = Modifier.weight(1f).testTag("btn_missed_${prayerType.keyName}"),
                    onClick = { onStatusSelected("missed") }
                )
            }

            // Row 3: Sunnah Checkbox & Notes preview
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onToggleSunnah() }
                        .padding(vertical = 2.dp)
                ) {
                    Checkbox(
                        checked = isSunnahDone,
                        onCheckedChange = { onToggleSunnah() },
                        colors = CheckboxDefaults.colors(checkedColor = Emerald500),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBangla) "সুন্নাত আদায় সম্পন্ন" else "Sunnah Completed",
                        fontSize = 11.5.sp,
                        fontWeight = if (isSunnahDone) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSunnahDone) Emerald700 else Slate600
                    )
                }

                if (!log?.notes.isNullOrBlank()) {
                    Text(
                        text = "📝 ${log?.notes}",
                        fontSize = 11.sp,
                        color = Emerald600,
                        maxLines = 1,
                        modifier = Modifier
                            .clickable { showNotesDialog = true }
                            .padding(end = 4.dp)
                    )
                }
            }
        }
    }

    if (showNotesDialog) {
        AlertDialog(
            onDismissRequest = { showNotesDialog = false },
            containerColor = Color.White,
            title = {
                Text(
                    text = if (isBangla) "${prayerType.banglaName} এর নোট" else "Notes for ${prayerType.englishName}",
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
            },
            text = {
                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    placeholder = { Text(if (isBangla) "নোট লিখুন (যেমন: জামাতে ১ম রাকাত পেয়েছি...)" else "Add notes...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSaveNotes(notesText)
                        showNotesDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald500, contentColor = Color.White)
                ) {
                    Text(if (isBangla) "সংরক্ষণ" else "Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNotesDialog = false }) {
                    Text(if (isBangla) "বাতিল" else "Cancel", color = Slate600)
                }
            }
        )
    }
}

@Composable
private fun StatusPill(
    title: String,
    isSelected: Boolean,
    status: PrayerStatus,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bg = if (isSelected) status.activeBg else StatusDefaultBg
    val borderCol = if (isSelected) status.activeBorder else StatusDefaultBorder
    val textColor = if (isSelected) status.activeText else StatusDefaultText

    Box(
        modifier = modifier
            .height(34.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .border(1.dp, borderCol, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 2.dp)
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(11.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
            }
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                color = textColor,
                maxLines = 1
            )
        }
    }
}
