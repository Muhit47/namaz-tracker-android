package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.ripple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TasbihCounterEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.NamazUiState
import com.example.ui.viewmodel.NamazViewModel
import com.example.util.DateUtil

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TasbihScreen(
    uiState: NamazUiState,
    viewModel: NamazViewModel,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val colors = FinancyTheme.colors
    val context = LocalContext.current
    val isBangla = uiState.isBangla
    val activeTasbih = uiState.tasbihs.find { it.id == uiState.selectedTasbihId } ?: uiState.tasbihs.firstOrNull()

    var showAddDialog by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newArabic by remember { mutableStateOf("") }
    var newTranslit by remember { mutableStateOf("") }
    var newTranslationBn by remember { mutableStateOf("") }
    var newTarget by remember { mutableStateOf("33") }

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isBangla) "ডিজিটাল তাসবীহ" else "Digital Tasbih",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = if (isBangla) "মাসনূন যিকির ও হ্যাপটিক কাউন্টার" else "Tactile Dhikr Counter & Presets",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = colors.textMuted
                        )
                    }
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
                actions = {
                    IconButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.testTag("btn_add_tasbih")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Dhikr", tint = colors.emerald)
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
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Preset Dhikr Chips (FlowRow)
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                uiState.tasbihs.forEach { item ->
                    val isSelected = item.id == activeTasbih?.id
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectTasbih(item.id) },
                        label = {
                            Text(
                                text = if (isBangla) item.title else (if (item.transliteration.isNotBlank()) item.transliteration else item.title),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = colors.emeraldSoft,
                            selectedLabelColor = colors.emerald,
                            containerColor = colors.card,
                            labelColor = colors.textSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isSelected) colors.emerald else colors.border,
                            enabled = true,
                            selected = isSelected
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Active Dhikr Card
            if (activeTasbih != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.card),
                    border = BorderStroke(1.dp, colors.border),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Arabic text
                        Text(
                            text = activeTasbih.arabicText,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.emerald,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        // Transliteration
                        Text(
                            text = activeTasbih.transliteration,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        // Meaning
                        Text(
                            text = if (isBangla) activeTasbih.translationBn else activeTasbih.translationEn,
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(0.15f))

                // 3. Tactile Dial & Gauge
                val target = activeTasbih.targetCount.coerceAtLeast(1)
                val currentInCycle = activeTasbih.currentCount % target
                val progress = (currentInCycle.toFloat() / target.toFloat()).coerceIn(0f, 1f)
                val animatedProgress by animateFloatAsState(targetValue = progress, label = "tasbihProgress")

                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .shadow(
                            elevation = if (colors.isDark) 16.dp else 10.dp,
                            shape = CircleShape,
                            ambientColor = if (colors.isDark) Color(0x60000000) else Color(0x20000000)
                        )
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    colors.emeraldDark,
                                    colors.emerald
                                )
                            )
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true, radius = 120.dp),
                            onClick = {
                                viewModel.incrementTasbih(activeTasbih.id, context)
                            }
                        )
                        .testTag("tasbih_counter_button"),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer progress ring
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        color = colors.amber,
                        trackColor = Color.White.copy(alpha = 0.25f),
                        strokeWidth = 8.dp
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (isBangla) DateUtil.toBanglaDigits(activeTasbih.currentCount.toString())
                            else activeTasbih.currentCount.toString(),
                            fontSize = 54.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Surface(
                            color = Color.White.copy(alpha = 0.22f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (isBangla) "লক্ষ্য: ${DateUtil.toBanglaDigits(target.toString())}"
                                else "Target: $target",
                                color = Color.White,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isBangla) "স্পর্শ করে গণনা করুন" else "Tap anywhere to count",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(0.15f))

                // 4. Reset & Lap Controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 30.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.resetTasbih(activeTasbih.id)
                        },
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, colors.border),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.textSecondary),
                        modifier = Modifier
                            .height(44.dp)
                            .testTag("btn_reset_tasbih")
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isBangla) "রিসেট করুন" else "Reset Count", fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp)
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = colors.card,
                        border = BorderStroke(1.dp, colors.border),
                        modifier = Modifier.height(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
                            val laps = activeTasbih.currentCount / target
                            Text(
                                text = if (isBangla) "পূর্ণ চক্কর: ${DateUtil.toBanglaDigits(laps.toString())}"
                                else "Laps completed: $laps",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.emerald
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = colors.card,
            title = {
                Text(
                    text = if (isBangla) "নতুন তাসবীহ / যিকির যোগ করুন" else "Add Custom Dhikr",
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text(if (isBangla) "নাম (যেমন: ইস্তিগফার)" else "Title (e.g. Istighfar)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = newArabic,
                        onValueChange = { newArabic = it },
                        label = { Text(if (isBangla) "আরবি পাঠ" else "Arabic Text") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = newTranslit,
                        onValueChange = { newTranslit = it },
                        label = { Text(if (isBangla) "উচ্চারণ" else "Transliteration") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = newTranslationBn,
                        onValueChange = { newTranslationBn = it },
                        label = { Text(if (isBangla) "অর্থ" else "Meaning") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = newTarget,
                        onValueChange = { newTarget = it.filter { ch -> ch.isDigit() } },
                        label = { Text(if (isBangla) "লক্ষ্য সংখ্যা (যেমন: ৩৩ বা ১০০)" else "Target Count (e.g. 33 or 100)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank()) {
                            val targetInt = newTarget.toIntOrNull() ?: 33
                            viewModel.addCustomTasbih(
                                title = newTitle,
                                arabic = newArabic.ifBlank { newTitle },
                                translit = newTranslit.ifBlank { newTitle },
                                meaning = newTranslationBn,
                                target = targetInt
                            )
                            newTitle = ""
                            newArabic = ""
                            newTranslit = ""
                            newTranslationBn = ""
                            newTarget = "33"
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.emerald, contentColor = Color.White)
                ) {
                    Text(if (isBangla) "যোগ করুন" else "Add", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text(if (isBangla) "বাতিল" else "Cancel", color = colors.textSecondary)
                }
            }
        )
    }
}
