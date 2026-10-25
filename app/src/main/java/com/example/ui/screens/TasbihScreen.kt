package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
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
        containerColor = Slate50,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBangla) "ডিজিটাল তাসবীহ" else "Digital Tasbih",
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
                actions = {
                    IconButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.testTag("btn_add_tasbih")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Dhikr", tint = Emerald700)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GlassCardWhite
                )
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
            // 1. Responsive Wrapped Dhikr selector (FlowRow)
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
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
                                text = item.title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Emerald50,
                            selectedLabelColor = Emerald700,
                            containerColor = Slate100,
                            labelColor = Slate600
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isSelected) Emerald500 else Slate200,
                            enabled = true,
                            selected = isSelected
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Active Dhikr Info Card
            if (activeTasbih != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = GlassCardWhite),
                    border = BorderStroke(1.dp, GlassBorder),
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
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Emerald700,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        // Transliteration
                        Text(
                            text = activeTasbih.transliteration,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        // Meaning
                        Text(
                            text = if (isBangla) activeTasbih.translationBn else activeTasbih.translationEn,
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate600,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(0.1f))

                // 3. Circular Touch Dial
                val target = activeTasbih.targetCount.coerceAtLeast(1)
                val currentInCycle = activeTasbih.currentCount % target
                val progress = (currentInCycle.toFloat() / target.toFloat()).coerceIn(0f, 1f)
                val animatedProgress by animateFloatAsState(targetValue = progress, label = "tasbihProgress")

                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Emerald500,
                                    Emerald600
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
                        color = Amber500,
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
                            text = if (isBangla) "গণনা করতে স্পর্শ করুন" else "Tap anywhere to count",
                            color = Emerald50,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(0.1f))

                // 4. Lifetime Total & Reset Controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isBangla) "সর্বমোট পঠিত" else "Total Recited",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate600
                        )
                        Text(
                            text = if (isBangla) "${DateUtil.toBanglaDigits(activeTasbih.totalRecited.toString())} বার"
                            else "${activeTasbih.totalRecited} times",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Emerald700
                        )
                    }

                    // Reset button
                    OutlinedButton(
                        onClick = { viewModel.resetTasbih(activeTasbih.id, context) },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Slate200),
                        modifier = Modifier.testTag("btn_reset_tasbih")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset",
                            modifier = Modifier.size(16.dp),
                            tint = Slate600
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBangla) "রিসেট" else "Reset",
                            fontWeight = FontWeight.SemiBold,
                            color = Slate900
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Add Custom Dhikr Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = Color.White,
            title = {
                Text(
                    text = if (isBangla) "নতুন যিকির যোগ করুন" else "Add Custom Dhikr",
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text(if (isBangla) "নাম / শিরোনাম" else "Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newArabic,
                        onValueChange = { newArabic = it },
                        label = { Text(if (isBangla) "আরবী পাঠ (ঐচ্ছিক)" else "Arabic Text (Optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newTranslit,
                        onValueChange = { newTranslit = it },
                        label = { Text(if (isBangla) "উচ্চারণ" else "Pronunciation") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newTranslationBn,
                        onValueChange = { newTranslationBn = it },
                        label = { Text(if (isBangla) "অর্থ / ফজিলত" else "Meaning / Translation") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newTarget,
                        onValueChange = { newTarget = it.filter { ch -> ch.isDigit() } },
                        label = { Text(if (isBangla) "লক্ষ্য সংখ্যা (যেমন: ৩৩ বা ১০০)" else "Target count (e.g. 33 or 100)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank()) {
                            viewModel.addCustomTasbih(
                                TasbihCounterEntity(
                                    title = newTitle,
                                    arabicText = newArabic.ifBlank { newTitle },
                                    transliteration = newTranslit.ifBlank { newTitle },
                                    translationBn = newTranslationBn,
                                    translationEn = newTranslationBn,
                                    targetCount = newTarget.toIntOrNull() ?: 33,
                                    isPreset = false
                                )
                            )
                            newTitle = ""
                            newArabic = ""
                            newTranslit = ""
                            newTranslationBn = ""
                            newTarget = "33"
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald500, contentColor = Color.White)
                ) {
                    Text(if (isBangla) "যোগ করুন" else "Add", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text(if (isBangla) "বাতিল" else "Cancel", color = Slate600)
                }
            }
        )
    }
}
