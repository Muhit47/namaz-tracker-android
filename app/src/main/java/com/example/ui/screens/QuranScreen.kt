package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuranAyahEntity
import com.example.data.model.QuranSurahEntity
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.FinancyTheme
import com.example.ui.viewmodel.NamazUiState
import com.example.ui.viewmodel.NamazViewModel
import com.example.util.DateUtil
import com.example.util.HapticUtil
import com.example.util.QuranAudioPlayer
import com.example.util.TransliterationUtil

/**
 * iOS Heavy Glass Quran Screen with Desert Gold Makki & Ocean Turquoise Madani cards,
 * razor-sharp light mode contrast, and dynamic island audio player bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranScreen(
    uiState: NamazUiState,
    viewModel: NamazViewModel,
    modifier: Modifier = Modifier
) {
    val colors = FinancyTheme.colors
    val context = LocalContext.current
    val isBangla = uiState.isBangla
    var searchQuery by remember { mutableStateOf("") }
    val activeSurah = uiState.selectedSurah

    BackHandler(enabled = activeSurah != null) {
        viewModel.closeSurah()
    }

    if (activeSurah != null) {
        // Detailed Ayah Reader view for active Surah
        SurahReaderView(
            surah = activeSurah,
            ayahs = uiState.selectedSurahAyahs,
            bookmarks = uiState.quranBookmarks,
            lastRead = uiState.quranLastRead,
            isBangla = isBangla,
            onBack = { viewModel.closeSurah() },
            onToggleBookmark = { surahNo, ayahNo ->
                viewModel.toggleQuranBookmark(surahNo, ayahNo, context)
            },
            onSetLastRead = { surahNo, ayahNo ->
                viewModel.setQuranLastRead(surahNo, ayahNo, context)
            },
            modifier = modifier
        )
    } else {
        // Surah List view
        val filteredSurahs = remember(searchQuery, uiState.surahs) {
            if (searchQuery.isBlank()) uiState.surahs
            else uiState.surahs.filter {
                it.nameBn.contains(searchQuery, ignoreCase = true) ||
                        it.nameEn.contains(searchQuery, ignoreCase = true) ||
                        it.meaningBn.contains(searchQuery, ignoreCase = true) ||
                        it.meaningEn?.contains(searchQuery, ignoreCase = true) == true ||
                        it.surahNo.toString() == searchQuery.trim()
            }
        }

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 120.dp)
        ) {
            // -------------------------------------------------------------
            // 1. FROSTED GLASS SEARCH PILL BAR
            // -------------------------------------------------------------
            item {
                Spacer(modifier = Modifier.height(6.dp))
                val searchShape = RoundedCornerShape(20.dp)
                val searchBg = if (colors.isDark) {
                    Color.White.copy(alpha = 0.07f)
                } else {
                    Color.White.copy(alpha = 0.85f)
                }
                val searchRim = if (colors.isDark) {
                    Color.White.copy(alpha = 0.12f)
                } else {
                    Color(0xFFCBD5E1).copy(alpha = 0.70f)
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = if (colors.isDark) 3.dp else 2.dp,
                            shape = searchShape,
                            spotColor = Color.Black.copy(alpha = 0.10f)
                        )
                        .testTag("quran_search_field"),
                    placeholder = {
                        Text(
                            text = if (isBangla) "সূরা খুঁজুন (যেমন: ফাতিহা, ১, ইয়াসীন...)" else "Search Surah (e.g. Fatiha, 1...)",
                            fontSize = 12.5.sp,
                            color = if (colors.isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = colors.cyan,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = if (colors.isDark) Color(0xFF94A3B8) else Color(0xFF475569)
                                )
                            }
                        }
                    },
                    shape = searchShape,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = searchBg,
                        unfocusedContainerColor = searchBg,
                        focusedBorderColor = colors.cyan,
                        unfocusedBorderColor = searchRim,
                        focusedTextColor = if (colors.isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A),
                        unfocusedTextColor = if (colors.isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // -------------------------------------------------------------
            // 2. RESUME LAST READ HERO CARD
            // -------------------------------------------------------------
            if (uiState.quranLastRead != null && searchQuery.isBlank()) {
                val lr = uiState.quranLastRead
                val lrSurah = uiState.surahs.find { it.surahNo == lr.surahNo }
                if (lrSurah != null) {
                    item {
                        val heroShape = RoundedCornerShape(24.dp)
                        val heroBg = if (colors.isDark) {
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF451A03).copy(alpha = 0.45f),
                                    Color(0xFF083344).copy(alpha = 0.40f),
                                    Color(0xFF0F172A).copy(alpha = 0.70f)
                                )
                            )
                        } else {
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFFFFFBEB).copy(alpha = 0.95f),
                                    Color(0xFFECFEFF).copy(alpha = 0.90f),
                                    Color.White.copy(alpha = 0.95f)
                                )
                            )
                        }

                        val heroRim = Brush.linearGradient(
                            listOf(
                                Color(0xFFF59E0B).copy(alpha = if (colors.isDark) 0.60f else 0.45f),
                                if (colors.isDark) Color.White.copy(alpha = 0.15f) else Color.White
                            )
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(
                                    elevation = if (colors.isDark) 8.dp else 4.dp,
                                    shape = heroShape,
                                    spotColor = Color(0xFFF59E0B).copy(alpha = 0.35f)
                                )
                                .clip(heroShape)
                                .background(heroBg)
                                .border(BorderStroke(1.2.dp, heroRim), heroShape)
                                .clickable {
                                    HapticUtil.lightTick(context)
                                    viewModel.openSurah(lrSurah)
                                }
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(11.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(
                                                if (colors.isDark) Color(0xFFF59E0B).copy(alpha = 0.25f) else Color(0xFFFEF3C7)
                                            )
                                            .border(
                                                0.8.dp,
                                                if (colors.isDark) Color(0xFFFBBF24).copy(alpha = 0.50f) else Color(0xFFF59E0B),
                                                RoundedCornerShape(14.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "📖", fontSize = 19.sp)
                                    }

                                    Column {
                                        Text(
                                            text = if (isBangla) "সর্বশেষ পঠিত আয়াত" else "Last Read Bookmark",
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (colors.isDark) Color(0xFFFBBF24) else Color(0xFFB45309),
                                            letterSpacing = 0.2.sp
                                        )
                                        Text(
                                            text = "${if (isBangla) lrSurah.nameBn else lrSurah.nameEn} • ${if (isBangla) "আয়াত " + DateUtil.toBanglaDigits(lr.ayahNo.toString()) else "Ayah " + lr.ayahNo}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (colors.isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(999.dp),
                                    color = if (colors.isDark) Color(0xFFF59E0B) else Color(0xFFD97706),
                                    border = BorderStroke(0.8.dp, Color.White.copy(alpha = 0.40f))
                                ) {
                                    Text(
                                        text = if (isBangla) "পড়ুন ›" else "Read ›",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            // -------------------------------------------------------------
            // 3. SURAH CARDS LIST (114 Surahs with Desert Gold / Ocean Turquoise)
            // -------------------------------------------------------------
            items(filteredSurahs, key = { it.surahNo }) { surah ->
                SurahListItem(
                    surah = surah,
                    isBangla = isBangla,
                    onClick = {
                        HapticUtil.lightTick(context)
                        viewModel.openSurah(surah)
                    }
                )
            }
        }
    }
}

/**
 * iOS Heavy Glass Surah List Item.
 * Makki Surahs: Desert Gold & Amber (#F59E0B)
 * Madani Surahs: Ocean Turquoise & Cyan (#06B6D4)
 * High-contrast text in Light Mode: #0F172A title, #475569 subtitle.
 */
@Composable
private fun SurahListItem(
    surah: QuranSurahEntity,
    isBangla: Boolean,
    onClick: () -> Unit
) {
    val colors = FinancyTheme.colors
    val isMeccan = surah.revelationType.equals("Meccan", ignoreCase = true)
    val cardShape = RoundedCornerShape(24.dp)

    // Palette Definition: Option 1 (Desert Gold & Ocean Turquoise)
    val bgBrush = if (isMeccan) {
        if (colors.isDark) {
            Brush.linearGradient(
                listOf(
                    Color(0xFF451A03).copy(alpha = 0.50f),
                    Color(0xFF1E140A).copy(alpha = 0.65f),
                    Color(0xFF0F172A).copy(alpha = 0.75f)
                )
            )
        } else {
            Brush.linearGradient(
                listOf(
                    Color(0xFFFFFBEB).copy(alpha = 0.98f),
                    Color(0xFFFEF3C7).copy(alpha = 0.90f),
                    Color.White.copy(alpha = 0.98f)
                )
            )
        }
    } else {
        if (colors.isDark) {
            Brush.linearGradient(
                listOf(
                    Color(0xFF083344).copy(alpha = 0.55f),
                    Color(0xFF0C4A6E).copy(alpha = 0.45f),
                    Color(0xFF0F172A).copy(alpha = 0.75f)
                )
            )
        } else {
            Brush.linearGradient(
                listOf(
                    Color(0xFFECFEFF).copy(alpha = 0.98f),
                    Color(0xFFCFFAFE).copy(alpha = 0.90f),
                    Color.White.copy(alpha = 0.98f)
                )
            )
        }
    }

    val rimBrush = if (isMeccan) {
        Brush.linearGradient(
            listOf(
                Color(0xFFFBBF24).copy(alpha = if (colors.isDark) 0.45f else 0.70f),
                if (colors.isDark) Color.White.copy(alpha = 0.08f) else Color.White
            )
        )
    } else {
        Brush.linearGradient(
            listOf(
                Color(0xFF22D3EE).copy(alpha = if (colors.isDark) 0.45f else 0.70f),
                if (colors.isDark) Color.White.copy(alpha = 0.08f) else Color.White
            )
        )
    }

    val badgeAccent = if (isMeccan) {
        if (colors.isDark) Color(0xFFFBBF24) else Color(0xFF78350F)
    } else {
        if (colors.isDark) Color(0xFF22D3EE) else Color(0xFF0E7490)
    }

    val arabicAccent = if (isMeccan) {
        if (colors.isDark) Color(0xFFFBBF24) else Color(0xFF92400E)
    } else {
        if (colors.isDark) Color(0xFF38BDF8) else Color(0xFF0E7490)
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "surah_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.5.dp)
            .scale(scale)
            .shadow(
                elevation = if (colors.isDark) 5.dp else 2.5.dp,
                shape = cardShape,
                spotColor = if (isMeccan) Color(0xFFF59E0B).copy(alpha = 0.25f) else Color(0xFF06B6D4).copy(alpha = 0.25f)
            )
            .clip(cardShape)
            .background(bgBrush)
            .border(BorderStroke(1.1.dp, rimBrush), cardShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 13.dp)
            .testTag("surah_item_${surah.surahNo}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(13.dp)
            ) {
                // Surah Number Squircle Seal Badge
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(
                            if (isMeccan) {
                                if (colors.isDark) Color(0xFFF59E0B).copy(alpha = 0.22f) else Color(0xFFFEF3C7)
                            } else {
                                if (colors.isDark) Color(0xFF06B6D4).copy(alpha = 0.22f) else Color(0xFFCFFAFE)
                            }
                        )
                        .border(
                            BorderStroke(
                                0.8.dp,
                                if (isMeccan) {
                                    if (colors.isDark) Color(0xFFFBBF24).copy(alpha = 0.50f) else Color(0xFFF59E0B)
                                } else {
                                    if (colors.isDark) Color(0xFF22D3EE).copy(alpha = 0.50f) else Color(0xFF06B6D4)
                                }
                            ),
                            RoundedCornerShape(13.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isBangla) DateUtil.toBanglaDigits(surah.surahNo.toString()) else surah.surahNo.toString(),
                        fontWeight = FontWeight.ExtraBold,
                        color = badgeAccent,
                        fontSize = 13.5.sp
                    )
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isBangla) surah.nameBn else surah.nameEn,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.5.sp,
                            color = if (colors.isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.width(7.dp))
                        Surface(
                            color = if (isMeccan) {
                                if (colors.isDark) Color(0xFFF59E0B).copy(alpha = 0.20f) else Color(0xFFFEF3C7)
                            } else {
                                if (colors.isDark) Color(0xFF06B6D4).copy(alpha = 0.20f) else Color(0xFFCFFAFE)
                            },
                            shape = RoundedCornerShape(999.dp),
                            border = BorderStroke(
                                0.6.dp,
                                if (isMeccan) {
                                    if (colors.isDark) Color(0xFFFBBF24).copy(alpha = 0.40f) else Color(0xFFFDE68A)
                                } else {
                                    if (colors.isDark) Color(0xFF22D3EE).copy(alpha = 0.40f) else Color(0xFFA5F3FC)
                                }
                            )
                        ) {
                            Text(
                                text = if (isMeccan) (if (isBangla) "মাক্কী" else "Meccan")
                                else (if (isBangla) "মাদানী" else "Medinan"),
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                color = badgeAccent
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.5.dp))
                    Text(
                        text = "${if (isBangla) surah.meaningBn else (surah.meaningEn ?: surah.meaningBn)} • ${if (isBangla) DateUtil.toBanglaDigits(surah.totalAyah.toString()) + " আয়াত" else "${surah.totalAyah} Ayahs"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (colors.isDark) Color(0xFF94A3B8) else Color(0xFF475569),
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Arabic Calligraphy
            Text(
                text = surah.nameAr,
                fontFamily = AmiriFontFamily,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = arabicAccent
            )
        }
    }
}

/**
 * Detailed Surah Ayah Reader View with Frosted Glass Ayah Cards
 * and Floating Dynamic Island Audio Player Bar at the bottom.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SurahReaderView(
    surah: QuranSurahEntity,
    ayahs: List<QuranAyahEntity>,
    bookmarks: List<com.example.data.model.QuranBookmarkEntity>,
    lastRead: com.example.data.model.QuranLastReadEntity?,
    isBangla: Boolean,
    onBack: () -> Unit,
    onToggleBookmark: (Int, Int) -> Unit,
    onSetLastRead: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = FinancyTheme.colors
    val context = LocalContext.current
    var playingAyahNo by remember { mutableStateOf<Int?>(null) }
    var isAudioPlaying by remember { mutableStateOf(false) }
    var isAudioBuffering by remember { mutableStateOf(false) }

    DisposableEffect(surah.surahNo) {
        onDispose {
            QuranAudioPlayer.stop()
        }
    }

    fun playAyahAudio(ayahNo: Int) {
        if (playingAyahNo == ayahNo && isAudioPlaying) {
            QuranAudioPlayer.pause()
            isAudioPlaying = false
        } else if (playingAyahNo == ayahNo && !isAudioPlaying && !isAudioBuffering) {
            QuranAudioPlayer.resume()
            isAudioPlaying = true
        } else {
            playingAyahNo = ayahNo
            isAudioBuffering = true
            isAudioPlaying = false
            QuranAudioPlayer.playAyah(
                surahNo = surah.surahNo,
                ayahNo = ayahNo,
                onStart = {
                    isAudioBuffering = false
                    isAudioPlaying = true
                },
                onCompletion = {
                    val nextAyah = ayahNo + 1
                    if (nextAyah <= surah.totalAyah) {
                        playAyahAudio(nextAyah)
                    } else {
                        playingAyahNo = null
                        isAudioPlaying = false
                        isAudioBuffering = false
                    }
                },
                onError = { errorMsg ->
                    isAudioBuffering = false
                    isAudioPlaying = false
                    playingAyahNo = null
                    Toast.makeText(
                        context,
                        if (isBangla) "তেলাওয়াত লোড করা যায়নি। ইন্টারনেট সংযোগ চেক করুন।" else errorMsg,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isBangla) surah.nameBn else surah.nameEn,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (colors.isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                        )
                        Text(
                            text = "${if (isBangla) surah.meaningBn else (surah.meaningEn ?: surah.meaningBn)} • ${if (isBangla) DateUtil.toBanglaDigits(surah.totalAyah.toString()) + " আয়াত" else "${surah.totalAyah} Ayahs"}",
                            fontSize = 11.sp,
                            color = if (colors.isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = if (colors.isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                        )
                    }
                },
                actions = {
                    Text(
                        text = surah.nameAr,
                        fontFamily = AmiriFontFamily,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (surah.revelationType.equals("Meccan", ignoreCase = true)) {
                            if (colors.isDark) Color(0xFFFBBF24) else Color(0xFF92400E)
                        } else {
                            if (colors.isDark) Color(0xFF38BDF8) else Color(0xFF0E7490)
                        },
                        modifier = Modifier.padding(end = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.card.copy(alpha = if (colors.isDark) 0.65f else 0.85f)
                )
            )
        },
        bottomBar = {
            // -------------------------------------------------------------
            // FLOATING DYNAMIC ISLAND AUDIO PLAYER BAR (Docked at Bottom)
            // -------------------------------------------------------------
            if (playingAyahNo != null) {
                val islandShape = RoundedCornerShape(999.dp)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .shadow(elevation = 14.dp, shape = islandShape, spotColor = Color.Black.copy(alpha = 0.40f)),
                    shape = islandShape,
                    color = if (colors.isDark) Color(0xFF0F172A) else Color(0xFF0F172A), // Sleek obsidian capsule in both modes
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.20f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            // Play / Pause Circle
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFF59E0B),
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable {
                                        playingAyahNo?.let { playAyahAudio(it) }
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    if (isAudioBuffering) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),
                                            color = Color.White,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Icon(
                                            imageVector = if (isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = "Audio Play/Pause",
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }

                            Column {
                                Text(
                                    text = if (isBangla) "আয়াত ${DateUtil.toBanglaDigits(playingAyahNo.toString())} তেলাওয়াত চলছে"
                                    else "Reciting Ayah $playingAyahNo",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    text = if (isBangla) "${surah.nameBn} • মিশারি রাশিদ আল-আফাসী" else "${surah.nameEn} • Mishary Alafasy",
                                    fontSize = 10.5.sp,
                                    color = Color(0xFFFBBF24)
                                )
                            }
                        }

                        // Right Action Pills: Bookmark & Copy & Close
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Bookmark Current Ayah
                            IconButton(
                                onClick = {
                                    playingAyahNo?.let { aNo ->
                                        onToggleBookmark(surah.surahNo, aNo)
                                        HapticUtil.lightTick(context)
                                    }
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                val isCurBookmarked = bookmarks.any { it.surahNo == surah.surahNo && it.ayahNo == playingAyahNo }
                                Icon(
                                    imageVector = if (isCurBookmarked) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "Bookmark",
                                    tint = if (isCurBookmarked) Color(0xFFFBBF24) else Color(0xFFE2E8F0),
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Copy Current Ayah
                            IconButton(
                                onClick = {
                                    val currentAyahObj = ayahs.find { it.ayahNo == playingAyahNo }
                                    if (currentAyahObj != null) {
                                        copyAyahText(context, surah, currentAyahObj, isBangla)
                                    }
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = Color(0xFFE2E8F0),
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            // Stop Button
                            IconButton(
                                onClick = {
                                    QuranAudioPlayer.stop()
                                    playingAyahNo = null
                                    isAudioPlaying = false
                                    isAudioBuffering = false
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close Player",
                                    tint = Color(0xFFFDA4AF),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // -------------------------------------------------------------
            // BISMILLAH GLASS CAPSULE (except Surah Tawbah #9)
            // -------------------------------------------------------------
            if (surah.surahNo != 9) {
                item {
                    val bismillahShape = RoundedCornerShape(22.dp)
                    val bismillahBg = if (colors.isDark) {
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF451A03).copy(alpha = 0.35f),
                                Color(0xFF0F172A).copy(alpha = 0.65f)
                            )
                        )
                    } else {
                        Brush.linearGradient(
                            listOf(
                                Color(0xFFFFFBEB).copy(alpha = 0.95f),
                                Color(0xFFFEF3C7).copy(alpha = 0.85f)
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                            .clip(bismillahShape)
                            .background(bismillahBg)
                            .border(
                                BorderStroke(
                                    1.dp,
                                    if (colors.isDark) Color(0xFFFBBF24).copy(alpha = 0.35f) else Color(0xFFFDE68A)
                                ),
                                bismillahShape
                            )
                            .padding(18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                fontFamily = AmiriFontFamily,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (colors.isDark) Color(0xFFFBBF24) else Color(0xFF92400E),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isBangla) "পরম করুণাময় ও অসীম দয়ালু আল্লাহর নামে (শুরু করছি)"
                                else "In the name of Allah, the Entirely Merciful, the Especially Merciful",
                                fontSize = 11.5.sp,
                                color = if (colors.isDark) Color(0xFF94A3B8) else Color(0xFF475569),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // AYAH CARDS (High-Contrast Frosted Glass)
            // -------------------------------------------------------------
            items(ayahs, key = { "${it.surahNo}_${it.ayahNo}" }) { ayah ->
                val isBookmarked = bookmarks.any { it.surahNo == surah.surahNo && it.ayahNo == ayah.ayahNo }
                val isLastRead = lastRead?.surahNo == surah.surahNo && lastRead?.ayahNo == ayah.ayahNo
                val isPlaying = playingAyahNo == ayah.ayahNo

                val ayahCardShape = RoundedCornerShape(22.dp)
                val cardBg = if (colors.isDark) {
                    if (isPlaying) {
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF451A03).copy(alpha = 0.65f),
                                Color(0xFF0F172A).copy(alpha = 0.85f)
                            )
                        )
                    } else if (isLastRead) {
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF083344).copy(alpha = 0.45f),
                                Color(0xFF0F172A).copy(alpha = 0.70f)
                            )
                        )
                    } else {
                        Brush.linearGradient(
                            listOf(
                                Color.White.copy(alpha = 0.05f),
                                Color.White.copy(alpha = 0.02f)
                            )
                        )
                    }
                } else {
                    if (isPlaying) {
                        Brush.linearGradient(
                            listOf(
                                Color(0xFFFEF3C7).copy(alpha = 0.98f),
                                Color(0xFFFFFBEB).copy(alpha = 0.95f)
                            )
                        )
                    } else if (isLastRead) {
                        Brush.linearGradient(
                            listOf(
                                Color(0xFFECFEFF).copy(alpha = 0.98f),
                                Color.White.copy(alpha = 0.95f)
                            )
                        )
                    } else {
                        Brush.linearGradient(
                            listOf(
                                Color.White.copy(alpha = 0.95f),
                                Color(0xFFF8FAFC).copy(alpha = 0.92f)
                            )
                        )
                    }
                }

                val cardRim = if (isPlaying) {
                    BorderStroke(1.5.dp, Color(0xFFF59E0B))
                } else if (isLastRead) {
                    BorderStroke(1.2.dp, Color(0xFF06B6D4))
                } else {
                    BorderStroke(
                        1.dp,
                        if (colors.isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFE2E8F0)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .testTag("ayah_${ayah.surahNo}_${ayah.ayahNo}")
                        .shadow(
                            elevation = if (isPlaying) 6.dp else (if (colors.isDark) 3.dp else 1.5.dp),
                            shape = ayahCardShape,
                            spotColor = if (isPlaying) Color(0xFFF59E0B).copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.08f)
                        )
                        .clip(ayahCardShape)
                        .background(cardBg)
                        .border(cardRim, ayahCardShape)
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Header Row: Ayah Number Squircle Badge + Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isPlaying) {
                                    Color(0xFFF59E0B)
                                } else if (isLastRead) {
                                    Color(0xFF06B6D4)
                                } else {
                                    if (colors.isDark) Color.White.copy(alpha = 0.10f) else Color(0xFFF1F5F9)
                                },
                                border = BorderStroke(
                                    0.8.dp,
                                    if (isPlaying) Color(0xFFFBBF24) else (if (colors.isDark) Color.White.copy(alpha = 0.20f) else Color(0xFFCBD5E1))
                                )
                            ) {
                                Text(
                                    text = if (isBangla) "আয়াত ${DateUtil.toBanglaDigits(ayah.ayahNo.toString())}"
                                    else "Ayah ${ayah.ayahNo}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp,
                                    color = if (isPlaying || isLastRead) Color.White else (if (colors.isDark) Color(0xFFE2E8F0) else Color(0xFF334155)),
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.5.dp)
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                // Audio play button
                                IconButton(
                                    onClick = { playAyahAudio(ayah.ayahNo) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    if (isPlaying && isAudioBuffering) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            color = Color(0xFFF59E0B),
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Icon(
                                            imageVector = if (isPlaying && isAudioPlaying) Icons.Default.PauseCircleFilled else Icons.Default.PlayCircleFilled,
                                            contentDescription = "Audio Play",
                                            tint = if (isPlaying) Color(0xFFF59E0B) else colors.emerald,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }

                                // Copy Ayah button
                                IconButton(
                                    onClick = {
                                        copyAyahText(context, surah, ayah, isBangla)
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Ayah",
                                        tint = if (colors.isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                // Last read button
                                IconButton(
                                    onClick = {
                                        HapticUtil.lightTick(context)
                                        onSetLastRead(surah.surahNo, ayah.ayahNo)
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isLastRead) Icons.Default.BookmarkAdded else Icons.Default.BookmarkBorder,
                                        contentDescription = "Set Last Read",
                                        tint = if (isLastRead) Color(0xFF06B6D4) else (if (colors.isDark) Color(0xFF64748B) else Color(0xFF94A3B8)),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                // Bookmark button
                                IconButton(
                                    onClick = {
                                        HapticUtil.lightTick(context)
                                        onToggleBookmark(surah.surahNo, ayah.ayahNo)
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isBookmarked) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = "Bookmark Ayah",
                                        tint = if (isBookmarked) Color(0xFFFBBF24) else (if (colors.isDark) Color(0xFF64748B) else Color(0xFF94A3B8)),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Arabic Ayah text: Pure White in Dark, Slate 900 in Light Mode
                        Text(
                            text = ayah.textAr,
                            fontFamily = AmiriFontFamily,
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Normal,
                            color = if (colors.isDark) Color.White else Color(0xFF0F172A),
                            lineHeight = 46.sp,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(11.dp))

                        // Transliteration / Pronunciation: High contrast
                        val translitText = if (isBangla) ayah.translitBn
                        else if (!ayah.translitEn.isNullOrBlank()) ayah.translitEn!!
                        else TransliterationUtil.transliterateArabicToEnglish(ayah.textAr)

                        if (translitText.isNotBlank()) {
                            Text(
                                text = translitText,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (colors.isDark) Color(0xFFFBBF24) else Color(0xFFB45309),
                                lineHeight = 19.sp
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                        }

                        // Meaning / Translation: Deep Slate 800 in Light Mode for 100% legibility
                        val translationText = if (isBangla) ayah.transBn
                        else if (!ayah.transEn.isNullOrBlank()) ayah.transEn!!
                        else ayah.transBn

                        Text(
                            text = translationText,
                            fontSize = 13.5.sp,
                            color = if (colors.isDark) Color(0xFFCBD5E1) else Color(0xFF1E293B),
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

private fun copyAyahText(
    context: Context,
    surah: QuranSurahEntity,
    ayah: QuranAyahEntity,
    isBangla: Boolean
) {
    val textToCopy = buildString {
        append(ayah.textAr)
        append("\n\n")
        val translit = if (isBangla) ayah.translitBn
        else if (!ayah.translitEn.isNullOrBlank()) ayah.translitEn!!
        else TransliterationUtil.transliterateArabicToEnglish(ayah.textAr)
        if (translit.isNotBlank()) {
            append(translit)
            append("\n\n")
        }
        val trans = if (isBangla) ayah.transBn
        else if (!ayah.transEn.isNullOrBlank()) ayah.transEn!!
        else ayah.transBn
        append(trans)
        append("\n(${surah.nameEn ?: surah.nameBn} ${ayah.surahNo}:${ayah.ayahNo})")
    }
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    val clip = ClipData.newPlainText("Ayah", textToCopy)
    clipboard?.setPrimaryClip(clip)
    Toast.makeText(
        context,
        if (isBangla) "আয়াত কপি করা হয়েছে" else "Ayah copied to clipboard",
        Toast.LENGTH_SHORT
    ).show()
}
