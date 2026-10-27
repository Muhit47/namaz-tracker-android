package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.NamazUiState
import com.example.ui.viewmodel.NamazViewModel
import com.example.util.DateUtil
import com.example.util.TransliterationUtil

private enum class HadithTabMode {
    CATEGORIES,  // বিষয়ভিত্তিক তালিকা (৮টি ক্যাটাগরি)
    BOOKS,       // গ্রন্থসমূহ (সহীহ বুখারী, মুসলিম, তিরমিযী...)
    SEARCH,      // সরাসরি অনুসন্ধান
    BOOKMARKS    // বুকমার্কস
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HadithScreen(
    uiState: NamazUiState,
    viewModel: NamazViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isBangla = uiState.isBangla

    var activeTab by remember { mutableStateOf(HadithTabMode.CATEGORIES) }
    var selectedCategoryForDetail by remember { mutableStateOf<HadithCategoryEntity?>(null) }
    var selectedBookForChapters by remember { mutableStateOf<HadithBookEntity?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val allHadiths = remember(uiState.allHadiths) { uiState.allHadiths }

    // Handle system back button for drill-down screens
    BackHandler(enabled = uiState.selectedHadithChapter != null || selectedBookForChapters != null || selectedCategoryForDetail != null) {
        if (uiState.selectedHadithChapter != null) {
            viewModel.closeHadithChapter()
        } else if (selectedBookForChapters != null) {
            selectedBookForChapters = null
        } else if (selectedCategoryForDetail != null) {
            selectedCategoryForDetail = null
        }
    }

    val colors = FinancyTheme.colors

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
            // Tab row
            Surface(
                color = colors.cardSubtle,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, colors.border),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TabCapsuleButton(
                        label = if (isBangla) "বিষয়ভিত্তিক" else "Categories",
                        isSelected = activeTab == HadithTabMode.CATEGORIES && selectedCategoryForDetail == null,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            activeTab = HadithTabMode.CATEGORIES
                            selectedCategoryForDetail = null
                            selectedBookForChapters = null
                            viewModel.closeHadithChapter()
                        }
                    )

                    TabCapsuleButton(
                        label = if (isBangla) "গ্রন্থসমূহ" else "Books",
                        isSelected = activeTab == HadithTabMode.BOOKS && selectedBookForChapters == null && uiState.selectedHadithChapter == null,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            activeTab = HadithTabMode.BOOKS
                            selectedCategoryForDetail = null
                            selectedBookForChapters = null
                            viewModel.closeHadithChapter()
                        }
                    )

                    TabCapsuleButton(
                        label = if (isBangla) "অনুসন্ধান" else "Search",
                        isSelected = activeTab == HadithTabMode.SEARCH,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            activeTab = HadithTabMode.SEARCH
                            selectedCategoryForDetail = null
                            selectedBookForChapters = null
                            viewModel.closeHadithChapter()
                        }
                    )

                    TabCapsuleButton(
                        label = if (isBangla) "বুকমার্ক (${DateUtil.toBanglaDigits(uiState.hadithBookmarks.size.toString())})" else "Saved (${uiState.hadithBookmarks.size})",
                        isSelected = activeTab == HadithTabMode.BOOKMARKS,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            activeTab = HadithTabMode.BOOKMARKS
                            selectedCategoryForDetail = null
                            selectedBookForChapters = null
                            viewModel.closeHadithChapter()
                        }
                    )
                }
            }

            // Body Content
            when (activeTab) {
                HadithTabMode.CATEGORIES -> {
                    if (selectedCategoryForDetail == null) {
                        CategoriesListView(
                            categories = uiState.hadithCategories,
                            allHadiths = allHadiths,
                            isBangla = isBangla,
                            onSelectCategory = { cat -> selectedCategoryForDetail = cat }
                        )
                    } else {
                        val cat = selectedCategoryForDetail!!
                        val categoryHadiths = remember(allHadiths, cat.id) {
                            allHadiths.filter { it.categoryId == cat.id }
                        }
                        CategoryDetailHadithsView(
                            category = cat,
                            hadiths = categoryHadiths,
                            bookmarks = uiState.hadithBookmarks,
                            isBangla = isBangla,
                            onBack = { selectedCategoryForDetail = null },
                            onToggleBookmark = { id -> viewModel.toggleHadithBookmark(id, context) },
                            onCopy = { hadith -> copyHadithToClipboard(context, hadith, isBangla) }
                        )
                    }
                }

                HadithTabMode.BOOKS -> {
                    if (uiState.selectedHadithChapter != null) {
                        // Chapter Full Hadiths View
                        ChapterFullHadithsView(
                            chapter = uiState.selectedHadithChapter!!,
                            fullHadiths = uiState.selectedHadithChapterFullHadiths,
                            lastRead = uiState.hadithLastRead,
                            isBangla = isBangla,
                            onBack = { viewModel.closeHadithChapter() },
                            onSetLastRead = { bookId, chNo, hNo ->
                                viewModel.setHadithLastRead(bookId, chNo, hNo, context)
                            },
                            onCopyText = { text -> copyToClipboard(context, text, isBangla) }
                        )
                    } else if (selectedBookForChapters != null) {
                        // Book Chapters List View
                        val book = selectedBookForChapters!!
                        BookChaptersListView(
                            book = book,
                            chapters = uiState.selectedHadithBookChapters,
                            isBangla = isBangla,
                            onBack = { selectedBookForChapters = null },
                            onSelectChapter = { ch ->
                                viewModel.openHadithChapter(book.id, ch.chapterNo)
                            }
                        )
                    } else {
                        // Books List View
                        BooksListView(
                            books = uiState.hadithBooks,
                            lastRead = uiState.hadithLastRead,
                            isBangla = isBangla,
                            onSelectBook = { book ->
                                selectedBookForChapters = book
                                viewModel.selectHadithBook(book.id)
                            }
                        )
                    }
                }

                HadithTabMode.SEARCH -> {
                    HadithSearchListView(
                        allHadiths = allHadiths,
                        searchQuery = searchQuery,
                        bookmarks = uiState.hadithBookmarks,
                        isBangla = isBangla,
                        onSearchChange = { searchQuery = it },
                        onToggleBookmark = { id -> viewModel.toggleHadithBookmark(id, context) },
                        onCopy = { hadith -> copyHadithToClipboard(context, hadith, isBangla) }
                    )
                }

                HadithTabMode.BOOKMARKS -> {
                    val bookmarkedHadiths = remember(allHadiths, uiState.hadithBookmarks) {
                        allHadiths.filter { h -> uiState.hadithBookmarks.any { b -> b.hadithId == h.id } }
                    }
                    BookmarksListView(
                        hadiths = bookmarkedHadiths,
                        isBangla = isBangla,
                        onToggleBookmark = { id -> viewModel.toggleHadithBookmark(id, context) },
                        onCopy = { hadith -> copyHadithToClipboard(context, hadith, isBangla) },
                        onExploreAll = { activeTab = HadithTabMode.CATEGORIES }
                    )
                }
            }
        }
    }

@Composable
private fun TabCapsuleButton(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val colors = FinancyTheme.colors
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) colors.emerald else Color.Transparent,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp)
        ) {
            Text(
                text = label,
                fontSize = 11.5.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// -------------------------------------------------------------
// 1. CATEGORIES LIST VIEW
// -------------------------------------------------------------
@Composable
private fun CategoriesListView(
    categories: List<HadithCategoryEntity>,
    allHadiths: List<HadithEntity>,
    isBangla: Boolean,
    onSelectCategory: (HadithCategoryEntity) -> Unit
) {
    val colors = FinancyTheme.colors
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp)
    ) {
        item {
            Text(
                text = if (isBangla) "বিষয়ভিত্তিক হাদীস ক্যাটাগরি" else "Hadith Categories",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                modifier = Modifier.padding(bottom = 8.dp, start = 2.dp)
            )
        }

        items(categories, key = { it.id }) { cat ->
            val count = allHadiths.count { it.categoryId == cat.id }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clickable { onSelectCategory(cat) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.card),
                border = BorderStroke(1.dp, colors.border),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            color = colors.emeraldSoft,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, colors.emerald.copy(alpha = 0.3f)),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = cat.icon, fontSize = 22.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = if (isBangla) cat.nameBn else (cat.nameEn ?: cat.nameBn),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = if (isBangla) "${DateUtil.toBanglaDigits(count.toString())}টি নির্বাচিত সহীহ হাদীস"
                                else "$count Selected Authentic Hadiths",
                                fontSize = 12.sp,
                                color = colors.textMuted
                            )
                        }
                    }

                    Surface(
                        color = colors.cardSubtle,
                        shape = CircleShape,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = "Open Category",
                                tint = colors.textSecondary,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. BOOKS LIST VIEW
// -------------------------------------------------------------
@Composable
private fun BooksListView(
    books: List<HadithBookEntity>,
    lastRead: HadithLastReadEntity?,
    isBangla: Boolean,
    onSelectBook: (HadithBookEntity) -> Unit
) {
    val colors = FinancyTheme.colors

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp)
    ) {
        // Last Read Hero
        if (lastRead != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.cyanSoft),
                    border = BorderStroke(1.dp, colors.cyan.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "Last Read",
                            tint = colors.cyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isBangla) "সর্বশেষ পঠিত হাদীস" else "Last Read Position",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp,
                                color = colors.cyan
                            )
                            Text(
                                text = if (isBangla) "কিতাব: ${lastRead.bookId.uppercase()} • অধ্যায় ${DateUtil.toBanglaDigits(lastRead.chapterNo.toString())} • হাদীস নং ${DateUtil.toBanglaDigits(lastRead.hadithNo.toString())}"
                                else "Book: ${lastRead.bookId.uppercase()} • Chapter ${lastRead.chapterNo} • Hadith #${lastRead.hadithNo}",
                                fontSize = 12.sp,
                                color = colors.textPrimary
                            )
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = if (isBangla) "প্রধান হাদীস গ্রন্থসমূহ (৮টি কিতাব)" else "Major Hadith Collections",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                modifier = Modifier.padding(bottom = 8.dp, start = 2.dp)
            )
        }

        items(books, key = { it.id }) { book ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clickable { onSelectBook(book) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.card),
                border = BorderStroke(1.dp, colors.border),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isBangla) book.nameBn else (book.nameEn ?: book.nameBn),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = colors.emeraldSoft,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = book.nameAr,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.emerald,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isBangla) "সকল অধ্যায় ও সহীহ সনদসহ পড়া যাবে" else "Browse chapters & full Hadith text",
                            fontSize = 12.sp,
                            color = colors.textMuted
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "Open Book",
                        tint = colors.textSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. BOOK CHAPTERS LIST VIEW
// -------------------------------------------------------------
@Composable
private fun BookChaptersListView(
    book: HadithBookEntity,
    chapters: List<HadithChapterEntity>,
    isBangla: Boolean,
    onBack: () -> Unit,
    onSelectChapter: (HadithChapterEntity) -> Unit
) {
    val colors = FinancyTheme.colors

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = colors.emerald
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = if (isBangla) book.nameBn else (book.nameEn ?: book.nameBn),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = if (isBangla) "মোট ${DateUtil.toBanglaDigits(chapters.size.toString())}টি অধ্যায়"
                        else "${chapters.size} Chapters available",
                        fontSize = 11.5.sp,
                        color = colors.textMuted
                    )
                }
            }
        }

        items(chapters, key = { it.id }) { ch ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onSelectChapter(ch) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = colors.card),
                border = BorderStroke(1.dp, colors.border)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(colors.emeraldSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isBangla) DateUtil.toBanglaDigits(ch.chapterNo.toString()) else ch.chapterNo.toString(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.emerald
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isBangla) ch.nameBn else (if (!ch.nameEn.isNullOrBlank()) ch.nameEn!! else ch.nameBn),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                color = colors.textPrimary
                            )
                            if (!ch.nameAr.isNull_or_blank()) {
                                Text(
                                    text = ch.nameAr!!,
                                    fontSize = 12.sp,
                                    color = colors.textMuted
                                )
                            }
                        }
                    }

                    if (ch.totalHadith > 0) {
                        Surface(
                            color = colors.cardSubtle,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (isBangla) "${DateUtil.toBanglaDigits(ch.totalHadith.toString())}টি" else "${ch.totalHadith}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textSecondary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 4. CHAPTER FULL HADITHS VIEW
// -------------------------------------------------------------
@Composable
private fun ChapterFullHadithsView(
    chapter: HadithChapterEntity,
    fullHadiths: List<HadithFullEntity>,
    lastRead: HadithLastReadEntity?,
    isBangla: Boolean,
    onBack: () -> Unit,
    onSetLastRead: (String, Int, Int) -> Unit,
    onCopyText: (String) -> Unit
) {
    val colors = FinancyTheme.colors

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = colors.emerald
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = if (isBangla) chapter.nameBn else (if (!chapter.nameEn.isNullOrBlank()) chapter.nameEn!! else chapter.nameBn),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = if (isBangla) "অধ্যায় নং ${DateUtil.toBanglaDigits(chapter.chapterNo.toString())} • মোট ${DateUtil.toBanglaDigits(fullHadiths.size.toString())}টি হাদীস"
                        else "Chapter ${chapter.chapterNo} • Total ${fullHadiths.size} Hadiths",
                        fontSize = 11.5.sp,
                        color = colors.textMuted
                    )
                }
            }
        }

        items(fullHadiths, key = { it.id }) { item ->
            val isLastRead = lastRead?.bookId == item.bookId && lastRead.chapterNo == item.chapterNo && lastRead.hadithNo == item.hadithNo

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.card),
                border = BorderStroke(1.dp, if (isLastRead) colors.emerald else colors.border)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = colors.emeraldSoft,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (isBangla) "হাদীস নং ${DateUtil.toBanglaDigits(item.hadithNo.toString())}" else "Hadith #${item.hadithNo}",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.emerald,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (!item.gradeBn.isNull_or_blank()) {
                                Surface(
                                    color = colors.amberSoft,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = item.gradeBn ?: "",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.amber,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                            }

                            IconButton(
                                onClick = { onSetLastRead(item.bookId, item.chapterNo, item.hadithNo) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (isLastRead) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Bookmark last read",
                                    tint = if (isLastRead) Emerald700 else Slate400,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (!item.textAr.isNull_or_blank()) {
                        Text(
                            text = item.textAr!!,
                            fontFamily = AmiriFontFamily,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900,
                            textAlign = TextAlign.End,
                            lineHeight = 36.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Auto Transliteration
                    val translit = if (isBangla) (item.translitBn ?: TransliterationUtil.transliterateArabicToBangla(item.textAr))
                    else (if (!item.translitEn.isNullOrBlank()) item.translitEn!! else TransliterationUtil.transliterateArabicToEnglish(item.textAr))

                    if (translit.isNotBlank()) {
                        Text(
                            text = "${if (isBangla) "উচ্চারণ: " else "Pronunciation: "}$translit",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Cyan700,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    val fullHadithText = if (isBangla) item.textBn else (if (!item.textEn.isNullOrBlank()) item.textEn!! else item.textBn)

                    Text(
                        text = fullHadithText,
                        fontSize = 13.5.sp,
                        color = Slate700,
                        lineHeight = 21.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Slate100,
                            border = BorderStroke(0.8.dp, Slate200),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onCopyText(fullHadithText) }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = Slate600,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isBangla) "কপি" else "Copy",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate700
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 5. SEARCH & BOOKMARKS LIST VIEWS
// -------------------------------------------------------------
@Composable
private fun HadithSearchListView(
    allHadiths: List<HadithEntity>,
    searchQuery: String,
    bookmarks: List<HadithBookmarkEntity>,
    isBangla: Boolean,
    onSearchChange: (String) -> Unit,
    onToggleBookmark: (Long) -> Unit,
    onCopy: (HadithEntity) -> Unit
) {
    val searchResults = remember(allHadiths, searchQuery) {
        if (searchQuery.isBlank()) emptyList()
        else allHadiths.filter { h ->
            h.textBn.contains(searchQuery, ignoreCase = true) ||
                    h.textAr.contains(searchQuery, ignoreCase = true) ||
                    h.chapterBn.contains(searchQuery, ignoreCase = true) ||
                    h.narratorBn.contains(searchQuery, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp)
    ) {
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = {
                    Text(
                        if (isBangla) "হাদীস খুঁজুন (যেমন: নামায, নিয়ত, বুখারী...)" else "Search Hadiths...",
                        color = Slate400,
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = Slate400)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = Slate400)
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = GlassCardWhite,
                    unfocusedContainerColor = GlassCardWhite,
                    focusedBorderColor = Emerald500,
                    unfocusedBorderColor = Slate200
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_hadith_search"),
                shape = RoundedCornerShape(14.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (searchQuery.isBlank()) {
            item {
                EmptyStateCard(
                    icon = "🔍",
                    title = if (isBangla) "হাদীস খুঁজুন" else "Search Hadith Collection",
                    subtitle = if (isBangla) "যেকোনো বিষয়ে সহীহ হাদীস দ্রুত খুজতে উপরে সার্চ কীওয়ার্ড লিখুন।" else "Type any word above to search across all Hadiths.",
                    buttonLabel = if (isBangla) "উদাহরণ: নামায" else "Example: Prayer",
                    onAction = { onSearchChange("নামাজ") }
                )
            }
        } else if (searchResults.isEmpty()) {
            item {
                EmptyStateCard(
                    icon = "❌",
                    title = if (isBangla) "কোন হাদীস পাওয়া যায়নি" else "No Results Found",
                    subtitle = if (isBangla) "অন্য শব্দ দিয়ে চেষ্টা করুন।" else "Try different search terms.",
                    buttonLabel = if (isBangla) "ক্লিয়ার করুন" else "Clear Search",
                    onAction = { onSearchChange("") }
                )
            }
        } else {
            items(searchResults, key = { it.id }) { hadith ->
                val isBookmarked = bookmarks.any { it.hadithId == hadith.id }
                HadithCardItem(
                    hadith = hadith,
                    isBookmarked = isBookmarked,
                    isBangla = isBangla,
                    onToggleBookmark = { onToggleBookmark(hadith.id) },
                    onCopy = { onCopy(hadith) }
                )
            }
        }
    }
}

@Composable
private fun BookmarksListView(
    hadiths: List<HadithEntity>,
    isBangla: Boolean,
    onToggleBookmark: (Long) -> Unit,
    onCopy: (HadithEntity) -> Unit,
    onExploreAll: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 120.dp)
    ) {
        if (hadiths.isEmpty()) {
            item {
                EmptyStateCard(
                    icon = "⭐",
                    title = if (isBangla) "কোন বুকমার্ক করা হাদীস নেই" else "No Bookmarked Hadiths",
                    subtitle = if (isBangla) "পছন্দের হাদীস সেভ করে রাখতে স্টারে (⭐) ট্যাপ করুন।"
                    else "Tap the star (⭐) on any Hadith card to bookmark it.",
                    buttonLabel = if (isBangla) "সব হাদীস দেখুন" else "Explore Hadiths",
                    onAction = onExploreAll
                )
            }
        } else {
            item {
                Text(
                    text = if (isBangla) "বুকমার্ক করা হাদীস (${DateUtil.toBanglaDigits(hadiths.size.toString())}টি)"
                    else "Saved Hadiths (${hadiths.size})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate700,
                    modifier = Modifier.padding(bottom = 8.dp, start = 2.dp)
                )
            }

            items(hadiths, key = { it.id }) { hadith ->
                HadithCardItem(
                    hadith = hadith,
                    isBookmarked = true,
                    isBangla = isBangla,
                    onToggleBookmark = { onToggleBookmark(hadith.id) },
                    onCopy = { onCopy(hadith) }
                )
            }
        }
    }
}

@Composable
private fun CategoryDetailHadithsView(
    category: HadithCategoryEntity,
    hadiths: List<HadithEntity>,
    bookmarks: List<HadithBookmarkEntity>,
    isBangla: Boolean,
    onBack: () -> Unit,
    onToggleBookmark: (Long) -> Unit,
    onCopy: (HadithEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp)
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = GlassCardWhite,
                border = BorderStroke(1.dp, GlassBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Emerald700
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Column {
                        Text(
                            text = "${category.icon} ${if (isBangla) category.nameBn else (category.nameEn ?: category.nameBn)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Text(
                            text = if (isBangla) "মোট ${DateUtil.toBanglaDigits(hadiths.size.toString())}টি হাদীস সংকলিত"
                            else "${hadiths.size} Hadiths compiled",
                            fontSize = 11.5.sp,
                            color = Slate600
                        )
                    }
                }
            }
        }

        items(hadiths, key = { it.id }) { hadith ->
            val isBookmarked = bookmarks.any { it.hadithId == hadith.id }
            HadithCardItem(
                hadith = hadith,
                isBookmarked = isBookmarked,
                isBangla = isBangla,
                onToggleBookmark = { onToggleBookmark(hadith.id) },
                onCopy = { onCopy(hadith) }
            )
        }
    }
}

@Composable
private fun HadithCardItem(
    hadith: HadithEntity,
    isBookmarked: Boolean,
    isBangla: Boolean = false,
    onToggleBookmark: () -> Unit,
    onCopy: () -> Unit
) {
    val colors = FinancyTheme.colors

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .testTag("hadith_card_${hadith.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        border = BorderStroke(1.dp, colors.border),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Surface(
                        color = colors.cyanSoft,
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(0.8.dp, colors.cyan.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = if (isBangla) hadith.bookNameBn else (hadith.bookNameEn ?: hadith.bookNameBn),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.cyan,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${if (isBangla) hadith.chapterBn else (if (hadith.chapterEn.isNullOrBlank()) hadith.chapterBn else hadith.chapterEn!!)}",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.textMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = colors.emeraldSoft,
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(0.8.dp, colors.emerald.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = if (isBangla) hadith.gradeBn else (if (hadith.gradeEn.isNullOrBlank()) hadith.gradeBn else hadith.gradeEn!!),
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.emerald,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) colors.amber else colors.textMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (hadith.textAr.isNotBlank()) {
                Text(
                    text = hadith.textAr,
                    fontFamily = AmiriFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    lineHeight = 36.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Auto Phonetic Transliteration
            val translit = if (isBangla) (hadith.translitBn ?: TransliterationUtil.transliterateArabicToBangla(hadith.textAr))
            else TransliterationUtil.transliterateArabicToEnglish(hadith.textAr)

            if (translit.isNotBlank()) {
                Text(
                    text = "${if (isBangla) "উচ্চারণ: " else "Pronunciation: "}$translit",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.cyan,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            val narrator = if (isBangla) hadith.narratorBn else (if (hadith.narratorEn.isNullOrBlank()) hadith.narratorBn else hadith.narratorEn!!)
            if (narrator.isNotBlank()) {
                Text(
                    text = if (isBangla) "$narrator বর্ণনা করেন:" else "Narrated by $narrator:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.emerald
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Text(
                text = if (isBangla) hadith.textBn else (if (hadith.textEn.isNullOrBlank()) hadith.textBn else hadith.textEn!!),
                fontSize = 13.5.sp,
                color = colors.textSecondary,
                lineHeight = 21.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val ref = if (isBangla) hadith.referenceBn else (if (hadith.referenceEn.isNullOrBlank()) hadith.referenceBn else hadith.referenceEn!!)
                Text(
                    text = "${if (isBangla) "হাদিস নং: " else "Hadith No: "}$ref",
                    fontSize = 11.5.sp,
                    color = colors.textMuted
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = colors.cardSubtle,
                    border = BorderStroke(0.8.dp, colors.border),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onCopy() }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = Slate600,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isBangla) "কপি করুন" else "Copy",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate700
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyStateCard(
    icon: String,
    title: String,
    subtitle: String,
    buttonLabel: String,
    onAction: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = GlassCardWhite),
        border = BorderStroke(1.dp, GlassBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = icon, fontSize = 36.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                fontSize = 12.5.sp,
                color = Slate600,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onAction,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Emerald500)
            ) {
                Text(
                    text = buttonLabel,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

private fun String?.isNull_or_blank(): Boolean = this == null || this.trim().isEmpty()

private fun copyToClipboard(context: Context, text: String, isBangla: Boolean = false) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Hadith", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, if (isBangla) "টেক্সট কপি করা হয়েছে" else "Text copied to clipboard", Toast.LENGTH_SHORT).show()
}

private fun copyHadithToClipboard(context: Context, hadith: HadithEntity, isBangla: Boolean = false) {
    val shareText = if (isBangla) {
        buildString {
            append("[হাদীস শরীফ • ${hadith.bookNameBn} | ${hadith.referenceBn}]\n")
            append("বিষয়: ${hadith.chapterBn}\n")
            append("বর্ণনাকারী: ${hadith.narratorBn}\n\n")
            if (hadith.textAr.isNotBlank()) {
                append("${hadith.textAr}\n\n")
            }
            append("${hadith.textBn}\n\n")
            append("মান: ${hadith.gradeBn}")
        }
    } else {
        val bName = if (!hadith.bookNameEn.isNullOrBlank()) hadith.bookNameEn!! else hadith.bookNameBn
        val ch = if (!hadith.chapterEn.isNullOrBlank()) hadith.chapterEn!! else hadith.chapterBn
        val narrator = if (!hadith.narratorEn.isNullOrBlank()) hadith.narratorEn!! else hadith.narratorBn
        val text = if (!hadith.textEn.isNullOrBlank()) hadith.textEn!! else hadith.textBn
        val grade = if (!hadith.gradeEn.isNullOrBlank()) hadith.gradeEn!! else hadith.gradeBn
        val ref = if (!hadith.referenceEn.isNullOrBlank()) hadith.referenceEn!! else hadith.referenceBn
        buildString {
            append("[Hadith Sharif • $bName | Hadith No: $ref]\n")
            append("Chapter: $ch\n")
            append("Narrated by: $narrator\n\n")
            if (hadith.textAr.isNotBlank()) {
                append("${hadith.textAr}\n\n")
            }
            append("$text\n\n")
            append("Grade: $grade")
        }
    }
    copyToClipboard(context, shareText, isBangla)
}
