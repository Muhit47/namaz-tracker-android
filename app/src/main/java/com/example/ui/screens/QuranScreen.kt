package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuranAyahEntity
import com.example.data.model.QuranSurahEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.NamazUiState
import com.example.ui.viewmodel.NamazViewModel
import com.example.util.DateUtil
import com.example.util.TransliterationUtil

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

        Scaffold(
            containerColor = colors.background,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = if (isBangla) "আল কুরআনুল কারীম" else "Holy Al-Qur'an",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Text(
                                text = if (isBangla) "১১৪টি পূর্ণাঙ্গ সূরা অর্থ ও উচ্চারণসহ" else "114 Surahs with Translation & Transliteration",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = colors.textMuted
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colors.card
                    )
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
                // Search bar
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("quran_search_field"),
                        placeholder = {
                            Text(
                                text = if (isBangla) "সূরা খুঁজুন (যেমন: ফাতিহা, ১, ইয়াসীন...)" else "Search Surah (e.g. Fatiha, 1...)",
                                fontSize = 12.5.sp,
                                color = colors.textMuted
                            )
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = colors.textMuted)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = colors.textMuted)
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = colors.card,
                            unfocusedContainerColor = colors.card,
                            focusedBorderColor = colors.emerald,
                            unfocusedBorderColor = colors.border,
                            focusedTextColor = colors.textPrimary,
                            unfocusedTextColor = colors.textPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Last read resume card if available
                if (uiState.quranLastRead != null && searchQuery.isBlank()) {
                    val lr = uiState.quranLastRead
                    val lrSurah = uiState.surahs.find { it.surahNo == lr.surahNo }
                    if (lrSurah != null) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.openSurah(lrSurah) },
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = colors.emeraldSoft),
                                border = BorderStroke(1.dp, colors.emerald.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = colors.emerald,
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.MenuBook,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                        Column {
                                            Text(
                                                text = if (isBangla) "সর্বশেষ পঠিত আয়াত" else "Last Read Bookmark",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = colors.emerald
                                            )
                                            Text(
                                                text = "${if (isBangla) lrSurah.nameBn else lrSurah.nameEn} • ${if (isBangla) "আয়াত " + DateUtil.toBanglaDigits(lr.ayahNo.toString()) else "Ayah " + lr.ayahNo}",
                                                fontSize = 13.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = colors.textPrimary
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = { viewModel.openSurah(lrSurah) },
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        modifier = Modifier.height(34.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = colors.emerald,
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(if (isBangla) "পড়ুন" else "Read", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }

                // Surah list
                items(filteredSurahs) { surah ->
                    SurahListItem(
                        surah = surah,
                        isBangla = isBangla,
                        onClick = { viewModel.openSurah(surah) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SurahListItem(
    surah: QuranSurahEntity,
    isBangla: Boolean,
    onClick: () -> Unit
) {
    val colors = FinancyTheme.colors
    val isMeccan = surah.revelationType.equals("Meccan", ignoreCase = true)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick)
            .testTag("surah_item_${surah.surahNo}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        border = BorderStroke(1.dp, colors.border),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Surah number pill
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(colors.emeraldSoft)
                        .border(BorderStroke(0.8.dp, colors.emerald.copy(alpha = 0.3f)), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isBangla) DateUtil.toBanglaDigits(surah.surahNo.toString()) else surah.surahNo.toString(),
                        fontWeight = FontWeight.Bold,
                        color = colors.emerald,
                        fontSize = 13.sp
                    )
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isBangla) surah.nameBn else surah.nameEn,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = if (isMeccan) colors.amberSoft else colors.cyanSoft,
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(0.8.dp, if (isMeccan) colors.amber.copy(alpha = 0.4f) else colors.cyan.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = if (isMeccan) (if (isBangla) "মাক্কী" else "Meccan")
                                else (if (isBangla) "মাদানী" else "Medinan"),
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                color = if (isMeccan) colors.amber else colors.cyan
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${if (isBangla) surah.meaningBn else (surah.meaningEn ?: surah.meaningBn)} • ${if (isBangla) DateUtil.toBanglaDigits(surah.totalAyah.toString()) + " আয়াত" else "${surah.totalAyah} Ayahs"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textMuted
                    )
                }
            }

            // Arabic name
            Text(
                text = surah.nameAr,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colors.emerald
            )
        }
    }
}

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

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isBangla) surah.nameBn else surah.nameEn,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "${if (isBangla) surah.meaningBn else (surah.meaningEn ?: surah.meaningBn)} • ${if (isBangla) DateUtil.toBanglaDigits(surah.totalAyah.toString()) + " আয়াত" else "${surah.totalAyah} Ayahs"}",
                            fontSize = 11.sp,
                            color = colors.textMuted
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colors.textPrimary)
                    }
                },
                actions = {
                    Text(
                        text = surah.nameAr,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.emerald,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.card)
            )
        },
        bottomBar = {
            // Floating Audio Player Dock if playing
            if (playingAyahNo != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    color = colors.card,
                    border = BorderStroke(1.dp, colors.emerald),
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = colors.emerald,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.GraphicEq, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                            Column {
                                Text(
                                    text = if (isBangla) "তিলাওয়াত চলছে: আয়াত $playingAyahNo" else "Playing: Ayah $playingAyahNo",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = if (isBangla) surah.nameBn else surah.nameEn,
                                    fontSize = 10.5.sp,
                                    color = colors.emerald
                                )
                            }
                        }

                        IconButton(onClick = { playingAyahNo = null }) {
                            Icon(Icons.Default.Stop, contentDescription = "Stop", tint = colors.rose)
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
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Bismillah card (except Surah Tawbah #9)
            if (surah.surahNo != 9) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        shape = RoundedCornerShape(18.dp),
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
                            Text(
                                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.emerald,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isBangla) "পরম করুণাময় ও অসীম দয়ালু আল্লাহর নামে (শুরু করছি)"
                                else "In the name of Allah, the Entirely Merciful, the Especially Merciful",
                                fontSize = 12.5.sp,
                                color = colors.textSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // List of Ayahs
            items(ayahs) { ayah ->
                val isBookmarked = bookmarks.any { it.surahNo == surah.surahNo && it.ayahNo == ayah.ayahNo }
                val isLastRead = lastRead?.surahNo == surah.surahNo && lastRead?.ayahNo == ayah.ayahNo
                val isPlaying = playingAyahNo == ayah.ayahNo

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .testTag("ayah_${ayah.surahNo}_${ayah.ayahNo}"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isLastRead || isPlaying) colors.emeraldSoft.copy(alpha = 0.5f) else colors.card
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isLastRead || isPlaying) colors.emerald.copy(alpha = 0.5f) else colors.border
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Header: Ayah number pill + audio + copy + bookmark buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isLastRead) colors.emerald else colors.emeraldSoft,
                                border = BorderStroke(0.8.dp, colors.emerald.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = if (isBangla) "আয়াত ${DateUtil.toBanglaDigits(ayah.ayahNo.toString())}"
                                    else "Ayah ${ayah.ayahNo}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp,
                                    color = if (isLastRead) Color.White else colors.emerald,
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Audio play button chip
                                IconButton(
                                    onClick = {
                                        playingAyahNo = if (isPlaying) null else ayah.ayahNo
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.PauseCircleFilled else Icons.Default.PlayCircleFilled,
                                        contentDescription = "Audio Play",
                                        tint = colors.emerald,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                // Copy Ayah button
                                IconButton(
                                    onClick = {
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
                                        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                                        val clip = android.content.ClipData.newPlainText("Ayah", textToCopy)
                                        clipboard?.setPrimaryClip(clip)
                                        android.widget.Toast.makeText(
                                            context,
                                            if (isBangla) "আয়াত কপি করা হয়েছে" else "Ayah copied to clipboard",
                                            android.widget.Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Ayah",
                                        tint = colors.textMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                // Last read button
                                IconButton(
                                    onClick = { onSetLastRead(surah.surahNo, ayah.ayahNo) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isLastRead) Icons.Default.BookmarkAdded else Icons.Default.BookmarkBorder,
                                        contentDescription = "Set Last Read",
                                        tint = if (isLastRead) colors.emerald else colors.textMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                // Bookmark button
                                IconButton(
                                    onClick = { onToggleBookmark(surah.surahNo, ayah.ayahNo) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isBookmarked) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = "Bookmark Ayah",
                                        tint = if (isBookmarked) colors.amber else colors.textMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Arabic Ayah text
                        Text(
                            text = ayah.textAr,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary,
                            lineHeight = 40.sp,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Transliteration
                        val translitText = if (isBangla) ayah.translitBn
                        else if (!ayah.translitEn.isNullOrBlank()) ayah.translitEn!!
                        else TransliterationUtil.transliterateArabicToEnglish(ayah.textAr)

                        if (translitText.isNotBlank()) {
                            Text(
                                text = translitText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.cyan,
                                lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        // Meaning / Translation
                        val translationText = if (isBangla) ayah.transBn
                        else if (!ayah.transEn.isNullOrBlank()) ayah.transEn!!
                        else ayah.transBn

                        Text(
                            text = translationText,
                            fontSize = 13.5.sp,
                            color = colors.textSecondary,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}
