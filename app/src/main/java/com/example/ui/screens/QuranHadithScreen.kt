package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FinancyTheme
import com.example.ui.viewmodel.NamazUiState
import com.example.ui.viewmodel.NamazViewModel
import com.example.util.HapticUtil

enum class QuranHadithSubTab(
    val titleEn: String,
    val titleBn: String,
    val icon: ImageVector
) {
    QURAN("Al-Qur'an", "আল-কুরআন", Icons.AutoMirrored.Filled.MenuBook),
    HADITH("Hadith Sharif", "হাদীস শরীফ", Icons.AutoMirrored.Filled.LibraryBooks)
}

/**
 * iOS Heavy Glass Quran & Hadith Screen with Floating Frosted Segmented Dock,
 * ambient glowing canvas, and seamless switching between Al-Qur'an and Hadith Sharif.
 */
@Composable
fun QuranHadithScreen(
    uiState: NamazUiState,
    viewModel: NamazViewModel,
    modifier: Modifier = Modifier
) {
    val colors = FinancyTheme.colors
    val context = LocalContext.current
    val isBangla = uiState.isBangla
    var selectedTab by remember { mutableStateOf(QuranHadithSubTab.QURAN) }

    // If surah or hadith chapter is actively open, hide the top segment selector to give full screen focus
    val isSurahOpen = uiState.selectedSurah != null
    val isHadithChapterOpen = uiState.selectedHadithChapter != null

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // -------------------------------------------------------------
        // AMBIENT GLOWING LIGHT CANVAS
        // -------------------------------------------------------------
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            colors.background,
                            if (colors.isDark) Color(0xFF061412) else Color(0xFFF8FAFC),
                            colors.background
                        )
                    )
                )
        )

        Column(modifier = Modifier.fillMaxSize()) {
            if (!isSurahOpen && !isHadithChapterOpen) {
                // -------------------------------------------------------------
                // FROSTED GLASS TOP BAR WITH FLOATING SEGMENTED DOCK
                // -------------------------------------------------------------
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = colors.card.copy(alpha = if (colors.isDark) 0.65f else 0.85f),
                    border = BorderStroke(
                        1.dp,
                        if (colors.isDark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.80f)
                    ),
                    shadowElevation = if (colors.isDark) 4.dp else 2.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        // Title Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (isBangla) "কুরআন ও হাদীস সম্ভার" else "Qur'an & Hadith",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (colors.isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A),
                                    fontSize = 16.5.sp
                                )
                                Text(
                                    text = if (isBangla) "সহিহ বাণী ও হেদায়াতের আলো" else "Authentic Guidance & Light",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = if (colors.isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = if (colors.isDark) colors.cyan.copy(alpha = 0.18f) else colors.cyanSoft,
                                border = BorderStroke(
                                    0.8.dp,
                                    if (colors.isDark) colors.cyan.copy(alpha = 0.40f) else Color.White
                                )
                            ) {
                                Text(
                                    text = if (isBangla) "১১৪ সূরা • ৬ কিতাব" else "114 Surahs • 6 Books",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.cyan,
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.5.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // iOS Floating Frosted Segmented Dock
                        val dockShape = RoundedCornerShape(999.dp)
                        val dockBg = if (colors.isDark) {
                            Brush.linearGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.40f),
                                    Color.Black.copy(alpha = 0.25f)
                                )
                            )
                        } else {
                            Brush.linearGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.85f),
                                    Color(0xFFE2E8F0).copy(alpha = 0.70f)
                                )
                            )
                        }

                        val dockRim = if (colors.isDark) {
                            Color.White.copy(alpha = 0.12f)
                        } else {
                            Color.White.copy(alpha = 0.90f)
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(dockShape)
                                .background(dockBg)
                                .border(BorderStroke(1.dp, dockRim), dockShape)
                                .padding(3.5.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                QuranHadithSubTab.entries.forEach { tab ->
                                    val isSelected = selectedTab == tab
                                    val interactionSource = remember { MutableInteractionSource() }
                                    val isPressed by interactionSource.collectIsPressedAsState()

                                    val scale by androidx.compose.animation.core.animateFloatAsState(
                                        targetValue = if (isPressed) 0.95f else 1.0f,
                                        label = "tab_scale"
                                    )

                                    val pillBg = if (isSelected) {
                                        if (colors.isDark) {
                                            Brush.linearGradient(
                                                listOf(
                                                    colors.cyan.copy(alpha = 0.90f),
                                                    colors.cyan.copy(alpha = 0.75f)
                                                )
                                            )
                                        } else {
                                            Brush.linearGradient(
                                                listOf(
                                                    colors.cyan.copy(alpha = 0.95f),
                                                    colors.cyan.copy(alpha = 0.85f)
                                                )
                                            )
                                        }
                                    } else {
                                        Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                                    }

                                    val pillRim = if (isSelected) {
                                        BorderStroke(
                                            0.8.dp,
                                            if (colors.isDark) Color.White.copy(alpha = 0.40f) else Color.White.copy(alpha = 0.80f)
                                        )
                                    } else {
                                        BorderStroke(0.dp, Color.Transparent)
                                    }

                                    val textColor = if (isSelected) {
                                        Color.White
                                    } else {
                                        if (colors.isDark) Color(0xFF94A3B8) else Color(0xFF475569)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .scale(scale)
                                            .shadow(
                                                elevation = if (isSelected) 4.dp else 0.dp,
                                                shape = dockShape,
                                                spotColor = colors.cyan.copy(alpha = 0.4f)
                                            )
                                            .clip(dockShape)
                                            .background(pillBg)
                                            .border(pillRim, dockShape)
                                            .clickable(
                                                interactionSource = interactionSource,
                                                indication = null,
                                                onClick = {
                                                    HapticUtil.lightTick(context)
                                                    selectedTab = tab
                                                }
                                            )
                                            .padding(vertical = 7.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = tab.icon,
                                                contentDescription = null,
                                                tint = textColor,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isBangla) tab.titleBn else tab.titleEn,
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                                color = textColor
                                            )
                                        }
                                    }
                                }
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
}
