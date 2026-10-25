package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
            containerColor = Slate50,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = if (isBangla) "আল কুরআনুল কারীম" else "Holy Al-Qur'an",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                            Text(
                                text = if (isBangla) "১১৪টি পূর্ণাঙ্গ সূরা অর্থ ও উচ্চারণসহ" else "114 Surahs with Translation & Transliteration",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = Slate600
                            )
                        }
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
                // Search bar
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(if (isBangla) "সূরা খুঁজুন (যেমন: ফাতিহা, বাকারা, ১, ৩৬...)" else "Search Surah (e.g. Fatiha, Baqarah, 36...)")
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_quran_search"),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Last read quick resume card
                if (uiState.quranLastRead != null) {
                    val lr = uiState.quranLastRead
                    val lrSurah = uiState.surahs.find { it.surahNo == lr.surahNo }
                    if (lrSurah != null) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.openSurah(lrSurah) }
                                    .padding(bottom = 12.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Emerald50
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Emerald100),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("📖", fontSize = 24.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = if (isBangla) "সর্বশেষ পঠিত সূরা" else "Last Read Surah",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "${if (isBangla) lrSurah.nameBn else lrSurah.nameEn} • ${if (isBangla) "আয়াত " + DateUtil.toBanglaDigits(lr.ayahNo.toString()) else "Ayah " + lr.ayahNo}",
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.titleSmall
                                            )
                                        }
                                    }
                                    FilledTonalButton(
                                        onClick = { viewModel.openSurah(lrSurah) },
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text(if (isBangla) "পড়ুন" else "Read", fontSize = 12.sp)
                                    }
                                }
                            }
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick)
            .testTag("surah_item_${surah.surahNo}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
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
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isBangla) DateUtil.toBanglaDigits(surah.surahNo.toString()) else surah.surahNo.toString(),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp
                    )
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isBangla) surah.nameBn else surah.nameEn,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = if (surah.revelationType.equals("Meccan", ignoreCase = true)) (if (isBangla) "মাক্কী" else "Meccan")
                                else (if (isBangla) "মাদানী" else "Medinan"),
                                fontSize = 9.5.sp,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Text(
                        text = "${if (isBangla) surah.meaningBn else (surah.meaningEn ?: surah.meaningBn)} • ${if (isBangla) DateUtil.toBanglaDigits(surah.totalAyah.toString()) + " আয়াত" else "${surah.totalAyah} Ayahs"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Arabic name
            Text(
                text = surah.nameAr,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
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
    Scaffold(
        containerColor = Slate50,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isBangla) surah.nameBn else surah.nameEn,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Text(
                            text = "${if (isBangla) surah.meaningBn else (surah.meaningEn ?: surah.meaningBn)} • ${if (isBangla) DateUtil.toBanglaDigits(surah.totalAyah.toString()) + " আয়াত" else "${surah.totalAyah} Ayahs"}",
                            fontSize = 11.sp,
                            color = Slate600
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Slate900)
                    }
                },
                actions = {
                    Text(
                        text = surah.nameAr,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Emerald700,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GlassCardWhite)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Bismillah card (except Surah Tawbah #9)
            if (surah.surahNo != 9) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isBangla) "পরম করুণাময় ও অসীম দয়ালু আল্লাহর নামে (শুরু করছি)"
                                else "In the name of Allah, the Entirely Merciful, the Especially Merciful",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
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

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .testTag("ayah_${ayah.surahNo}_${ayah.ayahNo}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isLastRead) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                        else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        // Header: Ayah number + action buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = if (isBangla) "আয়াত ${DateUtil.toBanglaDigits(ayah.ayahNo.toString())}"
                                    else "Ayah ${ayah.ayahNo}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Row {
                                // Last read button
                                IconButton(
                                    onClick = { onSetLastRead(surah.surahNo, ayah.ayahNo) },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isLastRead) Icons.Default.BookmarkAdded else Icons.Default.BookmarkBorder,
                                        contentDescription = "Set Last Read",
                                        tint = if (isLastRead) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                    )
                                }

                                // Bookmark button
                                IconButton(
                                    onClick = { onToggleBookmark(surah.surahNo, ayah.ayahNo) },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isBookmarked) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = "Bookmark Ayah",
                                        tint = if (isBookmarked) Amber600 else MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Arabic Ayah text
                        Text(
                            text = ayah.textAr,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 38.sp,
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
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
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
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}
