package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FinancyTheme
import com.example.ui.viewmodel.NamazUiState
import com.example.ui.viewmodel.NamazViewModel

enum class QuranHadithSubTab(
    val titleEn: String,
    val titleBn: String,
    val icon: ImageVector
) {
    QURAN("Al-Qur'an", "আল-কুরআন", Icons.AutoMirrored.Filled.MenuBook),
    HADITH("Hadith Sharif", "হাদীস শরীফ", Icons.AutoMirrored.Filled.LibraryBooks)
}

@Composable
fun QuranHadithScreen(
    uiState: NamazUiState,
    viewModel: NamazViewModel,
    modifier: Modifier = Modifier
) {
    val colors = FinancyTheme.colors
    val isBangla = uiState.isBangla
    var selectedTab by remember { mutableStateOf(QuranHadithSubTab.QURAN) }

    // If surah or hadith chapter is actively open, hide the top segment selector to give full screen focus
    val isSurahOpen = uiState.selectedSurah != null
    val isHadithChapterOpen = uiState.selectedHadithChapter != null

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        if (!isSurahOpen && !isHadithChapterOpen) {
            // Unified Top Header Bar with Integrated Switcher
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = colors.card,
                border = BorderStroke(1.dp, colors.border.copy(alpha = 0.5f)),
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    // Header Title Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isBangla) "কুরআন ও হাদীস সম্ভার" else "Qur'an & Hadith",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Text(
                                text = if (isBangla) "সহিহ বাণী ও হেদায়াতের আলো" else "Authentic Guidance & Light",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = colors.textMuted
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = colors.emerald.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, colors.emerald.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = if (isBangla) "১১৪ সূরা • ৬ কিতাব" else "114 Surahs • 6 Books",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.emerald,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // iOS-style Frosted Glass Segmented Control
                    com.example.ui.components.IosGlassSegmentedControl(
                        items = QuranHadithSubTab.entries,
                        selectedItem = selectedTab,
                        onItemSelected = { selectedTab = it },
                        labelProvider = { tab -> if (isBangla) tab.titleBn else tab.titleEn },
                        iconProvider = { tab, iconTint ->
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = null,
                                tint = iconTint,
                                modifier = Modifier.size(15.dp)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "quran_hadith_switch"
        ) { tab ->
            when (tab) {
                QuranHadithSubTab.QURAN -> {
                    QuranScreen(
                        uiState = uiState,
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                QuranHadithSubTab.HADITH -> {
                    HadithScreen(
                        uiState = uiState,
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
