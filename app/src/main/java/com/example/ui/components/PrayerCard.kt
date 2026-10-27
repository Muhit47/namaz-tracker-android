package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import com.example.util.HapticUtil
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.style.TextAlign
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
    isActiveWaqt: Boolean = false,
    remainingMinutes: Int? = null,
    modifier: Modifier = Modifier
) {
    val colors = FinancyTheme.colors
    val currentStatusCode = log?.status ?: "pending"
    val isCompleted = currentStatusCode == "jamaah" || currentStatusCode == "on_time"
    val isSunnahDone = (log?.isSunnah ?: 0) == 1

    var showNotesDialog by remember { mutableStateOf(false) }
    var notesText by remember(log?.notes) { mutableStateOf(log?.notes ?: "") }

    val cardBorderColor = if (isActiveWaqt) colors.emerald
    else if (isCompleted) colors.emerald.copy(alpha = 0.5f)
    else colors.border

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .testTag("prayer_card_${prayerType.keyName}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActiveWaqt && currentStatusCode == "pending") colors.emeraldSoft.copy(alpha = 0.4f) else colors.card
        ),
        border = androidx.compose.foundation.BorderStroke(
            if (isActiveWaqt) 1.5.dp else 1.dp,
            cardBorderColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isActiveWaqt) 3.dp else 1.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Row 1: Prayer icon, name, Waqt Time, Notes icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val waqtEmoji = when (prayerType) {
                        PrayerType.FAJR -> "🌅"
                        PrayerType.DHUHR -> "☀️"
                        PrayerType.ASR -> "🌤️"
                        PrayerType.MAGHRIB -> "🌇"
                        PrayerType.ISHA -> "🌙"
                        PrayerType.WITR -> "✨"
                    }
                    val waqtBgColor = when (prayerType) {
                        PrayerType.FAJR -> if (colors.isDark) Color(0x30E11D48) else Color(0xFFFFF1F2)
                        PrayerType.DHUHR -> if (colors.isDark) Color(0x30F59E0B) else Color(0xFFFEF3C7)
                        PrayerType.ASR -> if (colors.isDark) Color(0x300284C7) else Color(0xFFF0F9FF)
                        PrayerType.MAGHRIB -> if (colors.isDark) Color(0x30D97706) else Color(0xFFFFFBEB)
                        PrayerType.ISHA -> if (colors.isDark) Color(0x306366F1) else Color(0xFFEEF2FF)
                        PrayerType.WITR -> if (colors.isDark) Color(0x308B5CF6) else Color(0xFFF5F3FF)
                    }

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                if (isCompleted || isActiveWaqt) colors.emeraldSoft else waqtBgColor
                            )
                            .border(
                                1.dp,
                                if (isCompleted || isActiveWaqt) colors.emerald.copy(alpha = 0.5f) else colors.border,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = waqtEmoji,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isBangla) prayerType.banglaName else prayerType.englishName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            if (prayerType.isFardh) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = colors.emeraldSoft,
                                    shape = RoundedCornerShape(6.dp),
                                    border = androidx.compose.foundation.BorderStroke(0.8.dp, colors.emerald.copy(alpha = 0.3f))
                                ) {
                                    Text(
                                        text = if (isBangla) "ফরয" else "Fardh",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.emerald
                                    )
                                }
                            }
                            if (isActiveWaqt) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = colors.emerald,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(5.dp)
                                                .clip(CircleShape)
                                                .background(Color.White)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        val activeText = if (remainingMinutes != null) {
                                            val formattedRem = com.example.util.PrayerTimeCalculator.formatRemainingTime(remainingMinutes, isBangla)
                                            if (isBangla) "চলতি ওয়াক্ত • $formattedRem বাকি" else "Active • $formattedRem left"
                                        } else {
                                            if (isBangla) "চলতি ওয়াক্ত" else "Active"
                                        }
                                        Text(
                                            text = activeText,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }

                        Text(
                            text = if (isBangla) prayerType.rakahInfoBn else prayerType.rakahInfoEn,
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary,
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
                            color = colors.emerald
                        )
                        Text(
                            text = prayerType.arabicName,
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textMuted,
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
                            tint = if (!log?.notes.isNullOrBlank()) colors.emerald else colors.textMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Row 2: Status Selection Pills
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
                        colors = CheckboxDefaults.colors(
                            checkedColor = colors.emerald,
                            checkmarkColor = Color.White
                        ),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBangla) "সুন্নাত আদায় সম্পন্ন" else "Sunnah Completed",
                        fontSize = 11.5.sp,
                        fontWeight = if (isSunnahDone) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSunnahDone) colors.emerald else colors.textSecondary
                    )
                }

                if (!log?.notes.isNullOrBlank()) {
                    Text(
                        text = "📝 ${log?.notes}",
                        fontSize = 10.5.sp,
                        color = colors.textMuted,
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
            containerColor = colors.card,
            title = {
                Text(
                    text = if (isBangla) "${prayerType.banglaName} এর নোট" else "Notes for ${prayerType.englishName}",
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
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
                    colors = ButtonDefaults.buttonColors(containerColor = colors.emerald, contentColor = Color.White)
                ) {
                    Text(if (isBangla) "সংরক্ষণ" else "Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNotesDialog = false }) {
                    Text(if (isBangla) "বাতিল" else "Cancel", color = colors.textSecondary)
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
    val colors = FinancyTheme.colors
    val isDark = colors.isDark

    val bg = when {
        isSelected && isDark -> when (status) {
            PrayerStatus.JAMAAH -> Color(0x3310B981)
            PrayerStatus.ON_TIME -> Color(0x330EA5E9)
            PrayerStatus.LATE, PrayerStatus.QAZA -> Color(0x33F59E0B)
            PrayerStatus.MISSED -> Color(0x33F43F5E)
            PrayerStatus.PENDING -> colors.cardSubtle
        }
        isSelected && !isDark -> status.activeBg
        else -> colors.cardSubtle
    }

    val borderCol = when {
        isSelected && isDark -> when (status) {
            PrayerStatus.JAMAAH -> colors.emerald
            PrayerStatus.ON_TIME -> colors.cyan
            PrayerStatus.LATE, PrayerStatus.QAZA -> colors.amber
            PrayerStatus.MISSED -> colors.rose
            PrayerStatus.PENDING -> colors.border
        }
        isSelected && !isDark -> status.activeBorder
        else -> colors.border
    }

    val textColor = when {
        isSelected && isDark -> when (status) {
            PrayerStatus.JAMAAH -> colors.emerald
            PrayerStatus.ON_TIME -> colors.cyan
            PrayerStatus.LATE, PrayerStatus.QAZA -> colors.amber
            PrayerStatus.MISSED -> colors.rose
            PrayerStatus.PENDING -> colors.textMuted
        }
        isSelected && !isDark -> status.activeText
        else -> colors.textSecondary
    }

    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "chip_scale"
    )

    val chipShape = RoundedCornerShape(999.dp)
    val rimBrush = if (isSelected) {
        Brush.linearGradient(
            listOf(
                borderCol.copy(alpha = 0.9f),
                borderCol.copy(alpha = 0.45f)
            )
        )
    } else {
        Brush.linearGradient(
            listOf(
                if (isDark) Color.White.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.8f),
                if (isDark) Color.White.copy(alpha = 0.04f) else Color(0xFFCBD5E1).copy(alpha = 0.4f)
            )
        )
    }

    Box(
        modifier = modifier
            .height(34.dp)
            .scale(scale)
            .then(
                if (isSelected) {
                    Modifier.shadow(
                        elevation = 4.dp,
                        shape = chipShape,
                        spotColor = borderCol.copy(alpha = 0.35f)
                    )
                } else Modifier
            )
            .clip(chipShape)
            .background(bg)
            .border(BorderStroke(1.dp, rimBrush), chipShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    HapticUtil.lightTick(context)
                    onClick()
                }
            ),
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

@Composable
fun FinancyPrayerCard(
    prayerType: PrayerType,
    timeStr: String,
    log: PrayerLogEntity?,
    isBangla: Boolean,
    isCurrentWaqt: Boolean = false,
    remainingMinutes: Int? = null,
    onStatusChange: (PrayerStatus) -> Unit,
    onToggleSunnah: () -> Unit,
    onUpdateNotes: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    PrayerCard(
        prayerType = prayerType,
        timeString = timeStr,
        log = log,
        isBangla = isBangla,
        onStatusSelected = { code -> onStatusChange(PrayerStatus.fromCode(code)) },
        onToggleSunnah = onToggleSunnah,
        onSaveNotes = onUpdateNotes,
        isActiveWaqt = isCurrentWaqt,
        remainingMinutes = remainingMinutes,
        modifier = modifier
    )
}
