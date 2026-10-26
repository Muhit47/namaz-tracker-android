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
            // Segmented Header Bar
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
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    QuranHadithSubTab.entries.forEach { tab ->
                        val isSelected = selectedTab == tab
                        val bg = if (isSelected) colors.emerald else Color.Transparent
                        val textCol = if (isSelected) Color.White else colors.textSecondary

                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(bg)
                                .clickable { selectedTab = tab }
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = null,
                                tint = textCol,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isBangla) tab.titleBn else tab.titleEn,
                                fontSize = 13.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = textCol
                            )
                        }
                    }
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
