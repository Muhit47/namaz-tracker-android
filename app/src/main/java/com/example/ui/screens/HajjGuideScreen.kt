package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.IosGlassPillButton
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.FinancyTheme
import com.example.util.DateUtil
import com.example.util.HapticUtil

// Data models for Hajj
private data class HajjDayStep(
    val dayBn: String,
    val dayEn: String,
    val dateBn: String,
    val dateEn: String,
    val titleBn: String,
    val titleEn: String,
    val location: String,
    val icon: String,
    val keyPointsBn: List<String>,
    val keyPointsEn: List<String>,
    val rulesBn: String,
    val rulesEn: String
)

private data class HajjDuaItem(
    val titleBn: String,
    val titleEn: String,
    val occasionBn: String,
    val occasionEn: String,
    val arabic: String,
    val pronBn: String,
    val pronEn: String,
    val meaningBn: String,
    val meaningEn: String
)

private data class ChecklistItem(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val categoryBn: String,
    val categoryEn: String,
    val hintBn: String,
    val hintEn: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HajjGuideScreen(
    isBangla: Boolean = true,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val colors = FinancyTheme.colors
    val context = LocalContext.current
    val isDark = colors.isDark

    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    // Lap Counter State
    var tawafLap by rememberSaveable { mutableIntStateOf(0) }
    var saiLap by rememberSaveable { mutableIntStateOf(0) }
    var counterMode by rememberSaveable { mutableIntStateOf(0) } // 0: Tawaf, 1: Sa'i

    // Interactive Checklist checked state
    val checkedItems = remember { mutableStateMapOf<String, Boolean>() }

    // Pre-populate data
    val hajjDays = remember { getHajjDaysData() }
    val hajjDuas = remember { getHajjDuasData() }
    val checklistItems = remember { getChecklistData() }

    val tabs = remember(isBangla) {
        listOf(
            if (isBangla) "📅 হজের ৫ দিন" else "📅 5 Days Timeline",
            if (isBangla) "🕋 ওমরাহ ও মীকাত" else "🕋 Umrah & Miqat",
            if (isBangla) "🤲 দোয়া ও তালবিয়া" else "🤲 Duas & Talbiyah",
            if (isBangla) "💚 মদিনা ও রওজা" else "💚 Madinah Ziyarah",
            if (isBangla) "🧕 নারীদের বিধান" else "🧕 Women's Hajj",
            if (isBangla) "⚠️ ভুলত্রুটি ও দম" else "⚠️ Mistakes & Dam",
            if (isBangla) "🎒 ট্রাভেল চেকলিস্ট" else "🎒 Packing Checklist",
            if (isBangla) "🔄 চক্কর কাউন্টার" else "🔄 Lap Counter"
        )
    }

    val titleColor = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val subtitleColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)
    val accentGold = Color(0xFFD97706)
    val accentEmerald = Color(0xFF059669)

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (isBangla) "পবিত্র হজ ও ওমরাহ গাইড" else "Hajj & Umrah Complete Guide",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = colors.textPrimary
                            )
                        }
                        Text(
                            text = if (isBangla) "ধারাবাহিক আমল, নিয়মাবলী, দোয়া ও নির্দেশিকা" else "Timeline, Rites, Masnun Duas & Guidelines",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = colors.textSecondary
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.surfaceCard
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // -------------------------------------------------------------
            // 1. HERO BANNER: Talbiyah & Spiritual Header
            // -------------------------------------------------------------
            item {
                Spacer(modifier = Modifier.height(6.dp))
                HajjHeroGlassCard(
                    isBangla = isBangla,
                    isDark = isDark,
                    titleColor = titleColor,
                    subtitleColor = subtitleColor
                )
            }

            // -------------------------------------------------------------
            // 2. HORIZONTAL SCROLLABLE CATEGORY TABS
            // -------------------------------------------------------------
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tabs.forEachIndexed { index, title ->
                        val isSelected = selectedTab == index
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) {
                                if (isDark) accentEmerald.copy(alpha = 0.30f) else accentEmerald.copy(alpha = 0.15f)
                            } else {
                                if (isDark) Color.White.copy(alpha = 0.05f) else Color(0xFFF1F5F9)
                            },
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) {
                                    if (isDark) accentEmerald.copy(alpha = 0.8f) else accentEmerald
                                } else {
                                    if (isDark) Color.White.copy(alpha = 0.10f) else Color(0xFFE2E8F0)
                                }
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    HapticUtil.selectionTick(context)
                                    selectedTab = index
                                }
                        ) {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) {
                                    if (isDark) Color(0xFF34D399) else Color(0xFF047857)
                                } else {
                                    if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                                },
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
                            )
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // 3. TAB CONTENT
            // -------------------------------------------------------------
            when (selectedTab) {
                0 -> {
                    // TAB 0: 5 Days of Hajj Timeline
                    item {
                        HajjSectionHeader(
                            badge = if (isBangla) "৮-১৩ জিলহজ" else "8-13 Dhul Hijjah",
                            title = if (isBangla) "হজের ৫ দিনের ধারাবাহিক আমল" else "5-Day Hajj Rites Timeline",
                            desc = if (isBangla) "মিনার তাঁবু থেকে শুরু করে আরাফা, মুজদালিফা ও তাওয়াফে যিয়ারতের সুন্নাহসম্মত নিয়ম"
                            else "Step-by-step authentic sunnah timeline for Mina, Arafat, Muzdalifah & Tawaf",
                            isDark = isDark,
                            titleColor = titleColor,
                            subtitleColor = subtitleColor
                        )
                    }

                    itemsIndexed(hajjDays) { _, day ->
                        HajjDayCard(
                            day = day,
                            isBangla = isBangla,
                            isDark = isDark,
                            titleColor = titleColor,
                            subtitleColor = subtitleColor
                        )
                    }
                }

                1 -> {
                    // TAB 1: Umrah & Miqat
                    item {
                        HajjSectionHeader(
                            badge = if (isBangla) "পবিত্র ওমরাহ" else "UMRAH GUIDE",
                            title = if (isBangla) "ওমরাহর নিয়ম ও মীকাত পরিচয়" else "Umrah Guide & Miqat Boundaries",
                            desc = if (isBangla) "ইহরাম থেকে শুরু করে তাওয়াফ, সাঈ ও মাথা মুণ্ডনের ৪টি ফরজ ও ওয়াজিব আমল"
                            else "Complete 4-step Umrah guide including Miqat points, Tawaf, Sa'i and Halq",
                            isDark = isDark,
                            titleColor = titleColor,
                            subtitleColor = subtitleColor
                        )
                    }

                    item {
                        UmrahCompleteGuideContent(
                            isBangla = isBangla,
                            isDark = isDark,
                            titleColor = titleColor,
                            subtitleColor = subtitleColor
                        )
                    }
                }

                2 -> {
                    // TAB 2: Masnun Duas & Talbiyah
                    item {
                        HajjSectionHeader(
                            badge = if (isBangla) "মাসনুন দোয়া" else "SUPPLICATIONS",
                            title = if (isBangla) "হজ ও ওমরাহর মাসনুন দোয়া ও তালবিয়া" else "Authentic Duas & Talbiyah",
                            desc = if (isBangla) "আরবী উচ্চারণ, বাংলা অর্থ ও ফজিলতসহ পাঠ করুন"
                            else "Arabic calligraphy, phonetics, and authentic translations",
                            isDark = isDark,
                            titleColor = titleColor,
                            subtitleColor = subtitleColor
                        )
                    }

                    itemsIndexed(hajjDuas) { _, dua ->
                        HajjDuaCard(
                            dua = dua,
                            isBangla = isBangla,
                            isDark = isDark,
                            titleColor = titleColor,
                            subtitleColor = subtitleColor
                        )
                    }
                }

                3 -> {
                    // TAB 3: Madinah & Rawdah Ziyarah
                    item {
                        HajjSectionHeader(
                            badge = if (isBangla) "মদিনা মুনাওয়ারা" else "MADINAH ZIYARAH",
                            title = if (isBangla) "মদিনা ও রওজা শরীফ যিয়ারতের আদব" else "Etiquette of Visiting Madinah & Rawdah",
                            desc = if (isBangla) "মসজিদে নববী, রওজা মুবারক, জান্নাতুল বাকি ও উহুদ প্রাঙ্গণে সালাম ও দোয়া"
                            else "Rules and spiritual etiquettes for Masjid an-Nabawi, Rawdah, and historic sites",
                            isDark = isDark,
                            titleColor = titleColor,
                            subtitleColor = subtitleColor
                        )
                    }

                    item {
                        MadinahZiyarahContent(
                            isBangla = isBangla,
                            isDark = isDark,
                            titleColor = titleColor,
                            subtitleColor = subtitleColor
                        )
                    }
                }

                4 -> {
                    // TAB 4: Women's Hajj Guidelines
                    item {
                        HajjSectionHeader(
                            badge = if (isBangla) "মহিলাদের বিধান" else "WOMEN'S RULINGS",
                            title = if (isBangla) "নারীদের হজের বিশেষ বিধিবিধান" else "Specific Rulings for Women Pilgrims",
                            desc = if (isBangla) "শালীন পোশাকের ইহরাম, তালবিয়ার স্বর, রমল বর্জন ও ঋতুস্রাবের শরয়ী সমাধান"
                            else "Ihram dress code, Talbiyah volume, physical ease, and menstruation rulings",
                            isDark = isDark,
                            titleColor = titleColor,
                            subtitleColor = subtitleColor
                        )
                    }

                    item {
                        WomensHajjContent(
                            isBangla = isBangla,
                            isDark = isDark,
                            titleColor = titleColor,
                            subtitleColor = subtitleColor
                        )
                    }
                }

                5 -> {
                    // TAB 5: Mistakes, Penalties, Dam & Kaffarah
                    item {
                        HajjSectionHeader(
                            badge = if (isBangla) "সতর্কতা ও বিধান" else "PENALTIES & KAFFARAH",
                            title = if (isBangla) "হজের সাধারণ ভুলত্রুটি ও দম/কাফফারা" else "Common Mistakes, Dam & Kaffarah",
                            desc = if (isBangla) "কোন কোন ভুল ক্ষমাযোগ্য এবং কোন ভুলে কুরবানি (দম) ওয়াজিব হয় তার বিস্তারিত তালিকা"
                            else "Understanding Fard vs Wajib, prohibitions in Ihram, and restitution rules",
                            isDark = isDark,
                            titleColor = titleColor,
                            subtitleColor = subtitleColor
                        )
                    }

                    item {
                        MistakesAndDamContent(
                            isBangla = isBangla,
                            isDark = isDark,
                            titleColor = titleColor,
                            subtitleColor = subtitleColor
                        )
                    }
                }

                6 -> {
                    // TAB 6: Travel & Packing Checklist
                    item {
                        HajjSectionHeader(
                            badge = if (isBangla) "সফরের প্রস্তুতি" else "TRAVEL CHECKLIST",
                            title = if (isBangla) "হজ সফরের সামগ্রী ও চেকলিস্ট" else "Packing & Travel Checklist",
                            desc = if (isBangla) "প্রয়োজনীয় কাগজপত্র, পোশাক ও নিত্যপ্রয়োজনীয় জিনিসপত্র গুছিয়ে টিক দিন"
                            else "Interactive checklist for essentials, documents, medications and gear",
                            isDark = isDark,
                            titleColor = titleColor,
                            subtitleColor = subtitleColor
                        )
                    }

                    itemsIndexed(checklistItems) { _, itemData ->
                        val isChecked = checkedItems[itemData.id] ?: false
                        ChecklistRowCard(
                            itemData = itemData,
                            isChecked = isChecked,
                            onToggle = {
                                val next = !isChecked
                                checkedItems[itemData.id] = next
                                if (next) {
                                    HapticUtil.vibrateClick(context)
                                } else {
                                    HapticUtil.lightTick(context)
                                }
                            },
                            isBangla = isBangla,
                            isDark = isDark,
                            titleColor = titleColor,
                            subtitleColor = subtitleColor
                        )
                    }
                }

                7 -> {
                    // TAB 7: Interactive Tawaf & Sa'i Counter
                    item {
                        HajjSectionHeader(
                            badge = if (isBangla) "স্মার্ট চক্কর ট্র্যাকার" else "LAP COUNTER",
                            title = if (isBangla) "তাওয়াফ ও সাঈ চক্কর কাউন্টার" else "Tawaf & Sa'i 7-Lap Counter",
                            desc = if (isBangla) "তাওয়াফ বা সাঈ চলাকালীন ভুলে যাওয়া রোধে প্রতি চক্কর শেষে ট্যাপ করে রাখুন"
                            else "Tap for each completed circuit with haptic feedback to prevent confusion",
                            isDark = isDark,
                            titleColor = titleColor,
                            subtitleColor = subtitleColor
                        )
                    }

                    item {
                        TawafSaiCounterGlassCard(
                            mode = counterMode,
                            onModeChange = {
                                counterMode = it
                                HapticUtil.selectionTick(context)
                            },
                            tawafLap = tawafLap,
                            saiLap = saiLap,
                            onIncrement = {
                                if (counterMode == 0) {
                                    if (tawafLap < 7) {
                                        tawafLap++
                                        if (tawafLap == 7) {
                                            HapticUtil.vibrateMilestone(context)
                                        } else {
                                            HapticUtil.vibrateClick(context)
                                        }
                                    }
                                } else {
                                    if (saiLap < 7) {
                                        saiLap++
                                        if (saiLap == 7) {
                                            HapticUtil.vibrateMilestone(context)
                                        } else {
                                            HapticUtil.vibrateClick(context)
                                        }
                                    }
                                }
                            },
                            onReset = {
                                if (counterMode == 0) tawafLap = 0 else saiLap = 0
                                HapticUtil.selectionTick(context)
                            },
                            isBangla = isBangla,
                            isDark = isDark,
                            titleColor = titleColor,
                            subtitleColor = subtitleColor
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SECTION HEADER COMPONENT
// -------------------------------------------------------------
@Composable
private fun HajjSectionHeader(
    badge: String,
    title: String,
    desc: String,
    isDark: Boolean,
    titleColor: Color,
    subtitleColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFFD97706),
                modifier = Modifier.size(6.dp)
            ) {}
            Text(
                text = badge,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color(0xFFFBBF24) else Color(0xFFB45309),
                letterSpacing = 0.5.sp
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = titleColor
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = desc,
            fontSize = 11.5.sp,
            color = subtitleColor,
            lineHeight = 16.sp
        )
    }
}

// -------------------------------------------------------------
// HERO BANNER COMPONENT
// -------------------------------------------------------------
@Composable
private fun HajjHeroGlassCard(
    isBangla: Boolean,
    isDark: Boolean,
    titleColor: Color,
    subtitleColor: Color
) {
    val shape = RoundedCornerShape(24.dp)
    val accentEmerald = Color(0xFF059669)
    val accentGold = Color(0xFFD97706)

    val backgroundBrush = if (isDark) {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFF064E3B).copy(alpha = 0.65f),
                Color(0xFF0F172A).copy(alpha = 0.85f)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFFECFDF5).copy(alpha = 0.95f),
                Color(0xFFFFFBEB).copy(alpha = 0.90f)
            )
        )
    }

    val borderBrush = Brush.linearGradient(
        colors = listOf(
            accentGold.copy(alpha = if (isDark) 0.5f else 0.4f),
            accentEmerald.copy(alpha = if (isDark) 0.3f else 0.2f)
        )
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isDark) 6.dp else 2.dp,
                shape = shape,
                ambientColor = if (isDark) Color.Black.copy(alpha = 0.4f) else accentEmerald.copy(alpha = 0.10f),
                spotColor = accentEmerald.copy(alpha = 0.20f)
            ),
        shape = shape,
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(backgroundBrush)
                .border(BorderStroke(1.2.dp, borderBrush), shape)
                .padding(18.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header badge
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = if (isDark) accentGold.copy(alpha = 0.20f) else accentGold.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, accentGold.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "🕋", fontSize = 11.sp)
                        Text(
                            text = if (isBangla) "পবিত্র হজ ও ওমরাহ নির্দেশিকা" else "HOLY PILGRIMAGE COMPANION",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFFFDE68A) else Color(0xFF92400E)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Arabic Calligraphy: Talbiyah Snippet
                Text(
                    text = "لَبَّيْكَ اللَّهُمَّ لَبَّيْكَ ، لَبَّيْكَ لاَ شَرِيكَ لَكَ لَبَّيْكَ",
                    fontFamily = AmiriFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFF34D399) else Color(0xFF065F46),
                    textAlign = TextAlign.Center,
                    lineHeight = 30.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Meaning
                Text(
                    text = if (isBangla)
                        "\"আমি উপস্থিত হে আল্লাহ! আমি উপস্থিত! আপনার কোনো অংশীদার নেই, আমি উপস্থিত!\""
                    else
                        "\"Here I am, O Allah, here I am. Here I am, You have no partner, here I am.\"",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = subtitleColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Quick stats pill row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HajjStatMiniPill(
                        icon = "📅",
                        label = if (isBangla) "৫ দিন" else "5 Days",
                        desc = if (isBangla) "ধারাবাহিক" else "Timeline",
                        isDark = isDark,
                        modifier = Modifier.weight(1f)
                    )
                    HajjStatMiniPill(
                        icon = "🕋",
                        label = if (isBangla) "৪ আমল" else "4 Rites",
                        desc = if (isBangla) "ওমরাহ" else "Umrah",
                        isDark = isDark,
                        modifier = Modifier.weight(1f)
                    )
                    HajjStatMiniPill(
                        icon = "🤲",
                        label = if (isBangla) "মাসনুন" else "Masnun",
                        desc = if (isBangla) "দোয়া" else "Duas",
                        isDark = isDark,
                        modifier = Modifier.weight(1f)
                    )
                    HajjStatMiniPill(
                        icon = "🔄",
                        label = if (isBangla) "৭ চক্কর" else "7 Laps",
                        desc = if (isBangla) "কাউন্টার" else "Counter",
                        isDark = isDark,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun HajjStatMiniPill(
    icon: String,
    label: String,
    desc: String,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = if (isDark) Color.White.copy(alpha = 0.06f) else Color.White.copy(alpha = 0.70f),
        border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.10f) else Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = icon, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF0F172A)
            )
            Text(
                text = desc,
                fontSize = 9.sp,
                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
            )
        }
    }
}

// -------------------------------------------------------------
// TAB 0: 5 DAYS HAJJ TIMELINE CARD
// -------------------------------------------------------------
@Composable
private fun HajjDayCard(
    day: HajjDayStep,
    isBangla: Boolean,
    isDark: Boolean,
    titleColor: Color,
    subtitleColor: Color
) {
    var isExpanded by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(20.dp)
    val accentGold = Color(0xFFD97706)

    Surface(
        shape = shape,
        color = if (isDark) Color.White.copy(alpha = 0.05f) else Color.White,
        border = BorderStroke(
            1.dp,
            if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFE2E8F0)
        ),
        shadowElevation = if (isDark) 0.dp else 1.5.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .clickable { isExpanded = !isExpanded }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = accentGold.copy(alpha = if (isDark) 0.20f else 0.12f),
                        border = BorderStroke(1.dp, accentGold.copy(alpha = 0.35f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = day.icon, fontSize = 20.sp)
                        }
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = accentGold.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = if (isBangla) day.dateBn else day.dateEn,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color(0xFFFBBF24) else Color(0xFFB45309),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "• ${day.location}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = subtitleColor
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isBangla) day.titleBn else day.titleEn,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = titleColor
                        )
                    }
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Expand",
                    tint = subtitleColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Key Points Preview (Bullet Points)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                val points = if (isBangla) day.keyPointsBn else day.keyPointsEn
                points.forEach { pt ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "✓",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFF34D399) else Color(0xFF059669)
                        )
                        Text(
                            text = pt,
                            fontSize = 11.5.sp,
                            color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF334155),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Expanded Detailed Rules
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isDark) Color.White.copy(alpha = 0.04f) else Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (isBangla) "📖 বিস্তারিত আমল ও সুন্নাহসম্মত বিধান:" else "📖 Detailed Rules & Sunnah Method:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = titleColor
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isBangla) day.rulesBn else day.rulesEn,
                                fontSize = 11.5.sp,
                                color = subtitleColor,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 1: UMRAH & MIQAT CONTENT
// -------------------------------------------------------------
@Composable
private fun UmrahCompleteGuideContent(
    isBangla: Boolean,
    isDark: Boolean,
    titleColor: Color,
    subtitleColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Miqat Boundaries Card
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (isDark) Color.White.copy(alpha = 0.05f) else Color.White,
            border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "📍", fontSize = 18.sp)
                    Text(
                        text = if (isBangla) "৫টি প্রধান মীকাত (ইহরামের সীমানা)" else "5 Major Miqat Boundaries",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleColor
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isBangla)
                        "মীকাত হলো সেই ভৌগোলিক সীমানা যা ইহরাম ব্যতীত অতিক্রম করা কোনো হজ বা ওমরাহকারীর জন্য বৈধ নয়:"
                    else
                        "Miqat is the designated perimeter beyond which pilgrims must not proceed without entering Ihram:",
                    fontSize = 11.5.sp,
                    color = subtitleColor
                )
                Spacer(modifier = Modifier.height(8.dp))

                val miqats = listOf(
                    "১. ইয়ালামলাম (Yalamlam)" to (if (isBangla) "বাংলাদেশ, ভারত, পাকিস্তান ও ইয়েমেনের হাজীদের জন্য প্রধান মীকাত (ফ্লাইটে অবতরণের পূর্বেই ইহরাম বাঁধতে হয়)।" else "Principal Miqat for flights from Bangladesh, India, Pakistan & Yemen."),
                    "২. যুলহুলাইফা (Zul Hulaifah / Abyar Ali)" to (if (isBangla) "মদিনা মুনাওয়ারা থেকে মক্কামুখী হাজীদের মীকাত (মসজিদে শাজারাহ)।" else "Miqat for those traveling from Madinah towards Makkah."),
                    "৩. আল-জুহফাহ (Al-Juhfah / Rabigh)" to (if (isBangla) "সিরিয়া, মিশর, সুদান ও উত্তর আফ্রিকার হাজীদের মীকাত।" else "Miqat for Syria, Egypt, and North African routes."),
                    "৪. কার্নুল মানাযিল (Qarn al-Manazil / As-Sayl)" to (if (isBangla) "রিয়াদ, নজদ ও গালফ দেশসমূহের হাজীদের মীকাত।" else "Miqat for Riyadh, Najd, and Gulf pilgrims."),
                    "৫. যাতু ইরক (Dhat Irq)" to (if (isBangla) "ইরাক ও উত্তর দিক থেকে আগত হাজীদের মীকাত।" else "Miqat for Iraq and Northern pilgrims.")
                )

                miqats.forEach { (name, desc) ->
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(
                            text = name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7)
                        )
                        Text(
                            text = desc,
                            fontSize = 11.sp,
                            color = subtitleColor,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // 4 Steps of Umrah
        val umrahSteps = listOf(
            Triple(
                "১. ইহরাম ও নিয়ত (ফরজ)",
                "1. Ihram & Niyyah (Fard)",
                if (isBangla) "গোসল করে পুরুষদের জন্য ২টি সাদা সেলাইবিহীন চাদর পরিধান, ২ রাকাত সুন্নাত নামায ও মীকাত অতিক্রমের পূর্বে নিয়ত ও উচ্চস্বরে তালবিয়া পাঠ করা।"
                else "Perform Ghusl, wear two white unstitched sheets (men), pray 2 rak'ahs, state intention & recite Talbiyah."
            ),
            Triple(
                "২. কাবা শরীফের তাওয়াফ (ফরজ)",
                "2. Tawaf of Kaaba (Fard)",
                if (isBangla) "হাজরে আসওয়াদ বরাবর থেকে শুরু করে কাবার চারপাশ বামে রেখে ৭ চক্কর তাওয়াফ করা। পুরুষদের ডান কাঁধ উন্মুক্ত রাখা (ইযতিবা) ও প্রথম ৩ চক্করে বীরদর্পে দ্রুত হাঁটা (রমল)।"
                else "Perform 7 circuits around the Kaaba anticlockwise starting from the Black Stone. Men practice Idtiba and Raml."
            ),
            Triple(
                "৩. মাকামে ইবরাহিমে নামায ও জমজম (সুন্নাত)",
                "3. 2 Rak'ahs at Maqam Ibrahim & Zamzam",
                if (isBangla) "তাওয়াফ শেষে মাকামে ইবরাহিমের পেছনে (বা হারামের যেকোনো স্থানে) ২ রাকাত তাওয়াফের নামায আদায় করে প্রাণভরে বরকতময় জমজমের পানি পান ও মাথায় দেওয়া।"
                else "Pray 2 rak'ahs behind Maqam Ibrahim, supplicate earnestly, and drink blessed Zamzam water."
            ),
            Triple(
                "৪. সাফা-মারওয়ায় সাঈ (ওয়াজিব)",
                "4. Sa'i between Safa & Marwah (Wajib)",
                if (isBangla) "সাফা পাহাড় থেকে শুরু করে মারওয়া পাহাড়ে মোট ৭ চক্কর সাঈ করা (সাফা থেকে মারওয়া ১, মারওয়া থেকে সাফা ২... এভাবে ৭ম মারওয়ায় শেষ)। সবুজ বাতির নিচে পুরুষদের দ্রুত দৌড়ানো সুন্নাত।"
                else "Walk 7 circuits starting at Mount Safa and concluding at Mount Marwah. Men jog lightly between the green fluorescent markers."
            ),
            Triple(
                "৫. হলক বা কসর (ওয়াজিব)",
                "5. Halq or Taqsir (Wajib)",
                if (isBangla) "সাঈ সম্পন্ন করার পর পুরুষদের মাথা ন্যাড়া করা (সর্বোত্তম) অথবা সম্পূর্ণ মাথার চুল সমানভাবে ছোট করা। মহিলাদের আঙুলের এক কর (প্রায় ১ ইঞ্চি) পরিমাণ চুল এক সাথে কাটা। এর মাধ্যমে ওমরাহ পূর্ণ হয় এবং ইহরাম শেষ হয়।"
                else "Shave head entirely (Halq, best for men) or trim equally all around. Women trim approximately one fingertip length. Ihram is now lifted!"
            )
        )

        umrahSteps.forEach { (titleBn, titleEn, desc) ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isDark) Color.White.copy(alpha = 0.05f) else Color.White,
                border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.10f) else Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = if (isBangla) titleBn else titleEn,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isDark) Color(0xFF34D399) else Color(0xFF047857)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = desc,
                        fontSize = 11.5.sp,
                        color = subtitleColor,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 2: DUA CARD COMPONENT
// -------------------------------------------------------------
@Composable
private fun HajjDuaCard(
    dua: HajjDuaItem,
    isBangla: Boolean,
    isDark: Boolean,
    titleColor: Color,
    subtitleColor: Color
) {
    val shape = RoundedCornerShape(18.dp)
    Surface(
        shape = shape,
        color = if (isDark) Color.White.copy(alpha = 0.05f) else Color.White,
        border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isBangla) dua.titleBn else dua.titleEn,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = titleColor
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isDark) Color(0xFF10B981).copy(alpha = 0.18f) else Color(0xFF10B981).copy(alpha = 0.12f)
                ) {
                    Text(
                        text = if (isBangla) dua.occasionBn else dua.occasionEn,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFF34D399) else Color(0xFF047857),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Arabic text in Amiri
            Text(
                text = dua.arabic,
                fontFamily = AmiriFontFamily,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color(0xFF5EEAD4) else Color(0xFF0D9488),
                textAlign = TextAlign.Right,
                lineHeight = 30.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Pronunciation
            Text(
                text = if (isBangla) "উচ্চারণ: ${dua.pronBn}" else "Pronunciation: ${dua.pronEn}",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDark) Color(0xFFFDE68A) else Color(0xFF92400E)
            )

            Spacer(modifier = Modifier.height(3.dp))

            // Meaning
            Text(
                text = if (isBangla) "অর্থ: ${dua.meaningBn}" else "Meaning: ${dua.meaningEn}",
                fontSize = 11.sp,
                color = subtitleColor,
                lineHeight = 15.sp
            )
        }
    }
}

// -------------------------------------------------------------
// TAB 3: MADINAH & RAWDAH CONTENT
// -------------------------------------------------------------
@Composable
private fun MadinahZiyarahContent(
    isBangla: Boolean,
    isDark: Boolean,
    titleColor: Color,
    subtitleColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Madinah Intro Card
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (isDark) Color.White.copy(alpha = 0.05f) else Color.White,
            border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "💚", fontSize = 18.sp)
                    Text(
                        text = if (isBangla) "মদিনা মুনাওয়ারার ফজিলত ও আদব" else "Virtues & Etiquette of Madinah",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleColor
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isBangla)
                        "মদিনা রাসূলুল্লাহ ﷺ-এর স্মৃতিবিজড়িত পবিত্র শহর। মসজিদে নববীতে ১ রাকাত নামায অন্যান্য মসজিদের তুলনায় ১,০০০ রাকাতের চেয়েও উত্তম (সহীহ বুখারী)। মদিনায় অবস্থানকালে কোনো প্রকার শোরগোল বা অনর্থক কথাবার্তা পরিহার করা জরুরি।"
                    else
                        "Madinah is the city of our beloved Prophet Muhammad ﷺ. A single prayer in Masjid an-Nabawi is better than 1,000 prayers elsewhere (Bukhari). Maintain reverence, calmness, and humility throughout your stay.",
                    fontSize = 11.5.sp,
                    color = subtitleColor,
                    lineHeight = 16.sp
                )
            }
        }

        // Salam to Prophet (pbuh)
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = if (isDark) Color.White.copy(alpha = 0.05f) else Color.White,
            border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = if (isBangla) "১. রাসূলুল্লাহ ﷺ-এর রওজা মুবারকে সালাম পেশ" else "1. Greeting the Prophet ﷺ at Rawdah",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "السَّلاَمُ عَلَيْكَ يَا رَسُولَ اللَّهِ وَرَحْمَةُ اللَّهِ وَبَرَكَاتُهُ",
                    fontFamily = AmiriFontFamily,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFF34D399) else Color(0xFF047857),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isBangla)
                        "উচ্চারণ: আসসালামু আলাইকা ইয়া রাসূলাল্লাহ ওয়া রাহমাতুল্লাহি ওয়া বারাকাতুহু।"
                    else
                        "Phonetics: Assalamu 'alayka ya Rasool Allah wa rahmatullahi wa barakatuh.",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDark) Color(0xFFFDE68A) else Color(0xFF92400E)
                )
                Text(
                    text = if (isBangla)
                        "অর্থ: হে আল্লাহর রাসূল! আপনার ওপর শান্তি, আল্লাহর রহমত ও বরকত বর্ষিত হোক।"
                    else
                        "Meaning: Peace, mercy and blessings of Allah be upon you, O Messenger of Allah.",
                    fontSize = 11.sp,
                    color = subtitleColor
                )
            }
        }

        // Salam to Abu Bakr & Umar (RA)
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = if (isDark) Color.White.copy(alpha = 0.05f) else Color.White,
            border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = if (isBangla) "২. দুই খলিফা আবু বকর (রা.) ও উমর (রা.)-এর কবরে সালাম" else "2. Salam to Abu Bakr (RA) & Umar (RA)",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isBangla)
                        "রাসূলুল্লাহ ﷺ-এর ডান পাশে এক কদম এগিয়ে হযরত আবু বকর (রা.)-কে সালাম দিন: 'আসসালামু আলাইকা ইয়া আবা বকর আস-সিদ্দিক'।\nঅতঃপর আরও এক কদম ডানে এগিয়ে হযরত উমর (রা.)-কে সালাম দিন: 'আসসালামু আলাইকা ইয়া উমার আল-ফারুক'। সেখানে হাত তুলে কবরের দিকে মুখ করে দোয়া করবেন না; বরং কেবল সালাম দিয়ে কিবলার দিকে মুখ করে দোয়া করবেন।"
                    else
                        "Step one pace to the right and greet Abu Bakr (RA): 'Assalamu 'alayka ya Aba Bakr'. Step one more pace right and greet Umar (RA): 'Assalamu 'alayka ya 'Umar'. Avoid supplicating towards the grave; face the Qiblah to make dua.",
                    fontSize = 11.5.sp,
                    color = subtitleColor,
                    lineHeight = 16.sp
                )
            }
        }

        // Other historic sites in Madinah
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = if (isDark) Color.White.copy(alpha = 0.05f) else Color.White,
            border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = if (isBangla) "৩. মদিনার অন্যান্য বরকতময় স্থানসমূহ" else "3. Blessed Sites to Visit in Madinah",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )
                Spacer(modifier = Modifier.height(6.dp))
                val sites = listOf(
                    "• মসজিদে কুবা (Quba Mosque)" to (if (isBangla) "বাসা/হোটেল থেকে পবিত্র অবস্থায় ওজু করে এসে ২ রাকাত নামায পড়লে ১টি ওমরাহর সমপরিমাণ সওয়াব লাভ হয় (তিরমিযী)।" else "Praying 2 rak'ahs after making wudu at your lodging equals the reward of an Umrah."),
                    "• জান্নাতুল বাকি (Jannat al-Baqi)" to (if (isBangla) "মসজিদে নববীর সংলগ্ন প্রায় ১০,০০০ সাহাবায়ে কেরাম ও উম্মাহাতুল মু'মিনীনের কবরস্থান। সেখানে গিয়ে সালাম পেশ ও মাগফিরাতের দোয়া করা সুন্নাত।" else "Historic resting ground of thousands of Sahabah and wives of the Prophet ﷺ."),
                    "• শুহাদায়ে উহুদ (Martyrs of Uhud)" to (if (isBangla) "উহুদ পাহাড় ও হযরত হামজা (রা.) সহ ৭০ জন শহীদের রওজা যিয়ারত করা এবং তাদের জন্য দোয়া করা।" else "Mount Uhud and the cemetery of 70 revered martyrs including Hamzah (RA).")
                )
                sites.forEach { (site, desc) ->
                    Column(modifier = Modifier.padding(vertical = 3.dp)) {
                        Text(
                            text = site,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7)
                        )
                        Text(
                            text = desc,
                            fontSize = 11.sp,
                            color = subtitleColor,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 4: WOMEN'S HAJJ CONTENT
// -------------------------------------------------------------
@Composable
private fun WomensHajjContent(
    isBangla: Boolean,
    isDark: Boolean,
    titleColor: Color,
    subtitleColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        val rules = listOf(
            Triple(
                "👗 নারীর ইহরামের পোশাক",
                "Ihram Attire for Women",
                if (isBangla) "নারীদের জন্য কোনো নির্দিষ্ট সাদা সেলাইবিহীন কাপড়ের বাধ্যবাধকতা নেই। শালীন, ঢিলেঢালা সাধারণ যে কোনো ইসলামিক পোশাকই ইহরামের পোশাক। তবে মুখমণ্ডল ও দুই হাতের কবজি উন্মুক্ত রাখতে হবে (গায়ের মাহরামের সামনে প্রয়োজনে ক্যাপের মতো কাপড়ের ছাতা দিয়ে পর্দা করা যায় যাতে কাপড়ে মুখ স্পর্শ না করে)।"
                else "Women wear their regular modest, loose-fitting Islamic clothing. No special unstitched sheets are required. Face and hands remain uncovered without synthetic masks directly clinging to facial skin."
            ),
            Triple(
                "🗣️ তালবিয়ার স্বর",
                "Voice Volume for Talbiyah",
                if (isBangla) "পুরুষদের জন্য তালবিয়া উচ্চস্বরে পাঠ করা সুন্নাত হলেও নারীদের জন্য কেবল নিজে শোনার মতো নিচু বা মৃদু স্বরে তালবিয়া পাঠ করা মুস্তাহাব। উচ্চস্বরে পাঠ করা অনুচিত।"
                else "While men raise their voices for Talbiyah, women recite softly so as only to be heard by themselves and their immediate companions."
            ),
            Triple(
                "🚶‍♀️ তাওয়াফ ও সাঈতে রমল নিষেধ",
                "No Running / Raml in Tawaf & Sa'i",
                if (isBangla) "পুরুষদের জন্য তাওয়াফের প্রথম ৩ চক্করে রমল (বুকের ছাতি টান করে বীরদর্পে দ্রুত হাঁটা) এবং সাঈতে দুই সবুজ বাতির মাঝে দ্রুত দৌড়ানো সুন্নাত হলেও নারীদের জন্য সাধারণ গতিতে স্বাভাবিকভাবে হাঁটা নিয়ম; তাদের জন্য রমল বা দ্রুত দৌড়ানো সম্পূর্ণ নিষেধ।"
                else "Women walk at a normal, dignified pace throughout Tawaf and Sa'i. Raml (jogging or brisk strides) does not apply to women."
            ),
            Triple(
                "🩸 ঋতুস্রাব বা হায়েজের বিধান",
                "Menstruation & Purity Rulings",
                if (isBangla) "ঋতুস্রাব অবস্থায় ইহরাম বাঁধা, আরাফায় অবস্থান, মুজদালিফায় রাতযাপন, জামারায় কঙ্কর নিক্ষেপ ও দোয়া—সব আমল বৈধ ও সহীহ। কেবল কাবা শরীফের তাওয়াফ (তাওয়াফে যিয়ারত) স্থগিত থাকবে এবং পবিত্র হওয়ার পর গোসল করে তাওয়াফ আদায় করতে হবে।"
                else "A menstruating woman enters Ihram, stays at Arafat, Muzdalifah, throws pebbles at Jamarat, and makes all supplications normally. Only Tawaf around the Kaaba is postponed until she attains purity and performs Ghusl."
            ),
            Triple(
                "🕊️ বিদায়ী তাওয়াফে বিশেষ ছাড়",
                "Exemption from Farewell Tawaf",
                if (isBangla) "মক্কা ত্যাগ করার সময় কোনো নারী যদি ঋতুবতী থাকেন, তবে তার জন্য তাওয়াফে বিদা (বিদায়ী তাওয়াফ) মাফ হয়ে যায়। এর জন্য কোনো দম (কোরবানি) বা কাফফারা দিতে হবে না (সহীহ বুখারী ১৭৬২)।"
                else "If a woman is menstruating when it is time to depart Makkah, she is completely exempt from Tawaf al-Wida (Farewell Tawaf) with zero penalty or sacrifice required."
            )
        )

        rules.forEach { (titleBn, titleEn, desc) ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isDark) Color.White.copy(alpha = 0.05f) else Color.White,
                border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.10f) else Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isBangla) titleBn else titleEn,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFF472B6) else Color(0xFFDB2777)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = desc,
                        fontSize = 11.5.sp,
                        color = subtitleColor,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 5: MISTAKES & DAM / KAFFARAH CONTENT
// -------------------------------------------------------------
@Composable
private fun MistakesAndDamContent(
    isBangla: Boolean,
    isDark: Boolean,
    titleColor: Color,
    subtitleColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Essential Distinction: Fard vs Wajib
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (isDark) Color.White.copy(alpha = 0.05f) else Color.White,
            border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "⚖️", fontSize = 18.sp)
                    Text(
                        text = if (isBangla) "হজের ৩টি ফরজ ও ওয়াজিবের পার্থক্য" else "3 Fard of Hajj vs Wajib Obligations",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleColor
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isBangla)
                        "• হজের ৩টি ফরজ (রুকন): ১. ইহরাম বাঁধা, ২. আরাফায় অবস্থান (৯ জিলহজ), ৩. তাওয়াফে যিয়ারত (১০-১২ জিলহজ)। এর কোনো একটি বাদ পড়লে দম বা কাফফারা দিয়ে হজ হবে না, হজ বাতিল হয়ে যাবে।\n• ওয়াজিবসমূহ: সাফা-মারওয়ায় সাঈ, মুজদালিফায় অবস্থান, জামারায় কঙ্কর নিক্ষেপ, মাথার চুল কাটা/মুণ্ডন, বহিরাগতদের বিদায়ী তাওয়াফ। কোনো ওয়াজিব ছুটে গেলে তার ক্ষতিপূরণে 'দম' (একটি পশু কোরবানি) দিতে হয়।"
                    else
                        "• 3 Fard Pillars: 1. Ihram, 2. Standing at Arafat, 3. Tawaf az-Ziyarah. Missing any of these voids Hajj; Dam cannot compensate.\n• Wajib Rites: Sa'i, Muzdalifah overnight, Stoning Jamarat, Halq/Taqsir, and Farewell Tawaf. Omitting a Wajib requires Dam (animal sacrifice) within the Haram.",
                    fontSize = 11.5.sp,
                    color = subtitleColor,
                    lineHeight = 16.sp
                )
            }
        }

        // Prohibitions in Ihram & Penalty Details
        val penalties = listOf(
            Triple(
                "❌ সুগন্ধি ব্যবহার ও রূপচর্চা",
                "Perfume & Scents in Ihram",
                if (isBangla) "ইহরাম অবস্থায় আতর, সুগন্ধি তেল বা সুগন্ধযুক্ত সাবান শরীরে বা কাপড়ে ব্যবহার করা নিষিদ্ধ। পূর্ণ কোনো অঙ্গে সুগন্ধি লাগালে 'দম' দিতে হবে; সামান্য হলে সদকায়ে ফিতর পরিমাণ দান করতে হবে।"
                else "Applying perfume or scented soaps to body or Ihram clothes is strictly forbidden. Substantial scented application triggers Dam."
            ),
            Triple(
                "✂️ চুল ও নখ কাটা",
                "Cutting Hair or Trimming Nails",
                if (isBangla) "ইহরাম অবস্থায় শরীরের কোনো অংশের চুল বা নখ কাটা নিষিদ্ধ। ইচ্ছাকৃতভাবে এক অঙ্গের চুল বা চার নখ কাটলে দম দিতে হবে; একটি বা দুটি নখ কাটলে সামান্য সদকা করতে হবে।"
                else "Trimming nails or clipping hair is prohibited. Clipping all nails in one sitting requires Dam; clipping individual nails incurs Sadaqah."
            ),
            Triple(
                "👕 সেলাইযুক্ত পোশাক ও মাথা ঢাকা (পুরুষ)",
                "Stitched Clothes & Covering Head (Men)",
                if (isBangla) "পুরুষদের জন্য শার্ট, প্যান্ট, আন্ডারওয়্যার বা সেলাইযুক্ত কোনো কাপড় পরিধান এবং টুপি বা পাগড়ি দিয়ে মাথা ও কান ঢাকা নিষিদ্ধ। পূর্ণ একদিন বা একরাত সেলাইযুক্ত কাপড় পরলে দম ওয়াজিব হয়।"
                else "Men must not wear tailored clothes (shirts, trousers, underwear) or cover their head/ears. Wearing stitched clothes for a full day incurs Dam."
            ),
            Triple(
                "✈️ মীকাত অতিক্রমের পূর্বে ইহরাম না বাঁধা",
                "Passing Miqat Without Ihram",
                if (isBangla) "হজ বা ওমরাহর নিয়তে মীকাত অতিক্রমের পূর্বে ইহরাম না বাঁধলে ওয়াজিব লঙ্ঘিত হয়। এ ক্ষেত্রে পুনরায় মীকাতে ফিরে গিয়ে ইহরাম বাঁধতে হবে, অন্যথায় হারামের সীমানায় একটি 'দম' দেওয়া ওয়াজিব।"
                else "Crossing the Miqat without entering Ihram is a grave omission. The pilgrim must return to the Miqat to enter Ihram, or sacrifice a sheep in Haram."
            )
        )

        penalties.forEach { (titleBn, titleEn, desc) ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isDark) Color.White.copy(alpha = 0.05f) else Color.White,
                border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.10f) else Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isBangla) titleBn else titleEn,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFF87171) else Color(0xFFDC2626)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = desc,
                        fontSize = 11.5.sp,
                        color = subtitleColor,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 6: CHECKLIST COMPONENT
// -------------------------------------------------------------
@Composable
private fun ChecklistRowCard(
    itemData: ChecklistItem,
    isChecked: Boolean,
    onToggle: () -> Unit,
    isBangla: Boolean,
    isDark: Boolean,
    titleColor: Color,
    subtitleColor: Color
) {
    val shape = RoundedCornerShape(16.dp)
    val accentEmerald = Color(0xFF059669)

    Surface(
        shape = shape,
        color = if (isDark) Color.White.copy(alpha = 0.05f) else Color.White,
        border = BorderStroke(
            1.dp,
            if (isChecked) {
                if (isDark) accentEmerald.copy(alpha = 0.6f) else accentEmerald.copy(alpha = 0.4f)
            } else {
                if (isDark) Color.White.copy(alpha = 0.10f) else Color(0xFFE2E8F0)
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .clickable { onToggle() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Checkbox Circle
            Surface(
                shape = CircleShape,
                color = if (isChecked) accentEmerald else Color.Transparent,
                border = BorderStroke(
                    1.5.dp,
                    if (isChecked) accentEmerald else if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8)
                ),
                modifier = Modifier.size(24.dp)
            ) {
                if (isChecked) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Checked",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (isBangla) itemData.titleBn else itemData.titleEn,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isChecked) {
                            if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                        } else titleColor
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isBangla) itemData.hintBn else itemData.hintEn,
                    fontSize = 11.sp,
                    color = subtitleColor,
                    lineHeight = 15.sp
                )
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFF1F5F9)
            ) {
                Text(
                    text = if (isBangla) itemData.categoryBn else itemData.categoryEn,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = subtitleColor,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 7: TAWAF & SA'I COUNTER COMPONENT
// -------------------------------------------------------------
@Composable
private fun TawafSaiCounterGlassCard(
    mode: Int, // 0: Tawaf, 1: Sa'i
    onModeChange: (Int) -> Unit,
    tawafLap: Int,
    saiLap: Int,
    onIncrement: () -> Unit,
    onReset: () -> Unit,
    isBangla: Boolean,
    isDark: Boolean,
    titleColor: Color,
    subtitleColor: Color
) {
    val currentLap = if (mode == 0) tawafLap else saiLap
    val isComplete = currentLap == 7
    val accentEmerald = Color(0xFF059669)
    val accentGold = Color(0xFFD97706)

    val shape = RoundedCornerShape(24.dp)
    val backgroundBrush = if (isDark) {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFF064E3B).copy(alpha = 0.50f),
                Color(0xFF0F172A).copy(alpha = 0.85f)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFFECFDF5).copy(alpha = 0.95f),
                Color.White.copy(alpha = 0.98f)
            )
        )
    }

    Surface(
        shape = shape,
        color = Color.Transparent,
        border = BorderStroke(
            1.2.dp,
            if (isDark) Color.White.copy(alpha = 0.15f) else Color(0xFFCBD5E1)
        ),
        shadowElevation = if (isDark) 6.dp else 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(backgroundBrush)
                .padding(18.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Segmented Switcher: Tawaf vs Sa'i
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE2E8F0),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onModeChange(0) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (mode == 0) accentEmerald else Color.Transparent
                        ) {
                            Text(
                                text = if (isBangla) "🕋 তাওয়াফ (৭ চক্কর)" else "🕋 Tawaf (7 Laps)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (mode == 0) Color.White else subtitleColor,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onModeChange(1) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (mode == 1) accentGold else Color.Transparent
                        ) {
                            Text(
                                text = if (isBangla) "⛰️ সাঈ (সাফা-মারওয়া)" else "⛰️ Sa'i (Safa-Marwah)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (mode == 1) Color.White else subtitleColor,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Current Circuit Description
                Text(
                    text = if (mode == 0) {
                        if (isBangla) "কাবা শরীফ তাওয়াফ কাউন্টার" else "Kaaba Tawaf Lap Counter"
                    } else {
                        if (isBangla) "সাফা ও মারওয়া পাহাড়ের সাঈ কাউন্টার" else "Safa & Marwah Sa'i Circuit Counter"
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = subtitleColor
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Large Lap Counter Button
                Surface(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .clickable(enabled = !isComplete) { onIncrement() }
                        .shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            ambientColor = if (mode == 0) accentEmerald else accentGold,
                            spotColor = if (mode == 0) accentEmerald else accentGold
                        ),
                    shape = CircleShape,
                    color = if (mode == 0) {
                        if (isDark) Color(0xFF065F46) else Color(0xFF10B981)
                    } else {
                        if (isDark) Color(0xFF92400E) else Color(0xFFF59E0B)
                    },
                    border = BorderStroke(3.dp, Color.White.copy(alpha = 0.35f))
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isBangla) DateUtil.toBanglaDigits(currentLap.toString()) else currentLap.toString(),
                            fontSize = 44.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = if (isBangla) "চক্কর / ৭" else "Lap of 7",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Guidance message for current lap
                val guidanceText = remember(mode, currentLap, isBangla) {
                    if (isComplete) {
                        if (isBangla) "আলহামদুলিল্লাহ! আপনার ৭টি চক্কর পূর্ণ হয়েছে।"
                        else "Alhamdulillah! All 7 circuits completed."
                    } else {
                        if (mode == 0) {
                            when (currentLap) {
                                0 -> if (isBangla) "হাজরে আসওয়াদ বরাবর গিয়ে 'বিসমিল্লাহি আল্লাহু আকবার' বলে ১ম চক্কর শুরু করুন।" else "Align with Hajr al-Aswad, raise hand and recite 'Bismillahi Allahu Akbar' to begin Lap 1."
                                1, 2 -> if (isBangla) "পুরুষরা ডান কাঁধ উন্মুক্ত রাখুন (ইযতিবা) এবং রমল (দ্রুত গতিতে) চলুন।" else "Men: Keep right shoulder uncovered (Idtiba) and walk briskly (Raml)."
                                3, 4, 5 -> if (isBangla) "রুকনে ইয়ামানী ও হাজরে আসওয়াদের মাঝে 'রাব্বানা আতিনা...' পাঠ করুন।" else "Between Yemeni Corner and Black Stone, recite 'Rabbana atina fid-dunya...'."
                                6 -> if (isBangla) "৭ম চক্কর সম্পন্ন করে কাঁধ ঢেকে মাকামে ইবরাহিমে ২ রাকাত নামায পড়ুন।" else "Final lap! Cover shoulder after finishing, then pray 2 rak'ahs at Maqam Ibrahim."
                                else -> ""
                            }
                        } else {
                            when (currentLap) {
                                0 -> if (isBangla) "সাফা পাহাড়ে উঠে কিবলামুখী হয়ে নিয়ত ও তাকবীর বলে মারওয়ার দিকে ১ম চক্কর শুরু করুন।" else "Climb Mount Safa, face Kaaba, state intention & begin Lap 1 towards Marwah."
                                1, 3, 5 -> if (isBangla) "মারওয়া পাহাড়ের দিকে চলছেন (পুরুষরা সবুজ বাতির নিচে দ্রুত হাঁটুন)।" else "Heading towards Marwah (men jog lightly under green lights)."
                                2, 4, 6 -> if (isBangla) "সাফা পাহাড়ের দিকে ফিরছেন।" else "Heading back towards Mount Safa."
                                else -> ""
                            }
                        }
                    }
                }

                Text(
                    text = guidanceText,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF334155),
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Reset Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    IosGlassPillButton(
                        text = if (isBangla) "🔄 নতুন করে শুরু (রিসেট)" else "🔄 Reset Counter",
                        onClick = onReset,
                        isPrimary = false,
                        fontSize = 11.5.sp,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 7.dp)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// DATA REPOSITORIES (SAHIH & AUTHENTIC)
// -------------------------------------------------------------
private fun getHajjDaysData(): List<HajjDayStep> = listOf(
    HajjDayStep(
        dayBn = "১ম দিন",
        dayEn = "Day 1",
        dateBn = "৮ই জিলহজ",
        dateEn = "8th Dhul Hijjah",
        titleBn = "মিনায় গমন (ইয়াওমুত তারবিয়াহ)",
        titleEn = "Departure to Mina (Yawm at-Tarwiyah)",
        location = "মিনা (Mina)",
        icon = "⛺",
        keyPointsBn = listOf(
            "গোসল করে পরিষ্কার হয়ে নিজ হোটেল/বাসা থেকে হজের ইহরাম পরিধান করা।",
            "হজের নিয়ত করা: 'লাব্বাইকা হাজ্জান' এবং উচ্চস্বরে তালবিয়া পাঠ শুরু করা।",
            "মিনায় পৌঁছানো এবং জোহর, আসর, মাগরিব, এশা ও পরের দিনের ফজর মিনায় কসর করে আদায় করা।",
            "মিনায় রাতযাপন করা সুন্নাতে মুয়াক্কাদা।"
        ),
        keyPointsEn = listOf(
            "Perform Ghusl, put on Ihram from hotel, declare Hajj intention & recite Talbiyah.",
            "Depart for Mina and pray Dhuhr, Asr, Maghrib, Isha, and Fajr there (shortened).",
            "Staying overnight in Mina is an emphasized Sunnah."
        ),
        rulesBn = "৮ই জিলহজ সকালবেলা মক্কা বা হোটেল থেকে ইহরাম বেঁধে তালবিয়া পড়তে পড়তে মিনার উদ্দেশ্যে রওয়ানা হবেন। মিনায় তাঁবুতে অবস্থান করে পাঁচ ওয়াক্ত নামায জামাতের সাথে নিজ নিজ সময়ে কসর (৪ রাকাতের নামায ২ রাকাত) করে আদায় করা সুন্নাত। অনর্থক কথাবার্তা পরিহার করে বেশি বেশি তালবিয়া, জিকির ও কুরআন তিলাওয়াতে রত থাকুন।",
        rulesEn = "Enter Ihram from your accommodation, continuously reciting Talbiyah. Spend the entire day and night in Mina. Perform each of the 5 prayers on time without combining, but shortening 4-rak'ah prayers to 2 rak'ahs according to the Sunnah."
    ),
    HajjDayStep(
        dayBn = "২য় দিন (হজের মূল দিন)",
        dayEn = "Day 2 (Greatest Day)",
        dateBn = "৯ই জিলহজ",
        dateEn = "9th Dhul Hijjah",
        titleBn = "আরাফায় অবস্থান ও মুজদালিফায় রাতযাপন",
        titleEn = "Standing at Arafat & Muzdalifah Overnight",
        location = "আরাফা ও মুজদালিফা",
        icon = "⛰️",
        keyPointsBn = listOf(
            "সূর্যোদয়ের পর মিনা থেকে আরাফার ময়দানে রওয়ানা হওয়া।",
            "উকুফে আরাফা (আরাফায় অবস্থান হজের প্রধান ফরজ রুকন - রাসূলুল্লাহ ﷺ বলেন: 'আল-হাজ্জু আরাফাহ')।",
            "জোহর ও আসর এক আযানে দুই ইকামতে জোহরের ওয়াক্তে কসর ও জমা করে আদায় করা।",
            "সূর্যাস্ত পর্যন্ত খুশু-খুজুর সাথে তওবা, কান্নাকাটি ও দোয়ায় রত থাকা।",
            "সূর্যাস্তের পর মাগরিব না পড়ে মুজদালিফায় রওয়ানা হওয়া এবং সেখানে গিয়ে মাগরিব ও এশা একত্রে আদায় করা।",
            "মুজদালিফায় খোলা আকাশের নিচে রাতযাপন ও কঙ্কর সংগ্রহ (৭০টি ছোট পাথর)।"
        ),
        keyPointsEn = listOf(
            "Proceed from Mina to Arafat after sunrise.",
            "Standing at Arafat is the core pillar of Hajj: 'Hajj is Arafah' (Tirmidhi).",
            "Combine and shorten Dhuhr and Asr prayers at Dhuhr time.",
            "Engage in earnest dua, repentance, and Dhikr until sunset.",
            "Depart immediately after sunset without praying Maghrib; reach Muzdalifah.",
            "Pray Maghrib and Isha combined at Muzdalifah, sleep under the stars, collect pebbles."
        ),
        rulesBn = "৯ই জিলহজ হজের সবচেয়ে গুরুত্বপূর্ণ দিন। জোহরের পর থেকে সূর্যাস্ত পর্যন্ত আরাফার ময়দানে যেকোনো স্থানে অবস্থান করা ফরজ। সূর্যাস্তের পূর্বে আরাফা ত্যাগ করা যাবে না। সূর্যাস্তের সাথে সাথে মাগরিব না পড়ে শান্তভাবে মুজদালিফার দিকে রওয়ানা হবেন। মুজদালিফায় পৌঁছে এক আযান ও দুই ইকামতে মাগরিব ও এশা একত্রে আদায় করবেন। মুজদালিফায় রাতযাপন ওয়াজিব এবং সেখান থেকে জামারায় নিক্ষেপের জন্য ছোট ছোলার দানার মতো পাথর সংগ্রহ করবেন।",
        rulesEn = "Standing anywhere within the boundaries of Arafat from Zawāl until sunset is an absolute pillar of Hajj. Beseech Allah tirelessly. After sunset, proceed calmly to Muzdalifah. Combine Maghrib and Isha upon arrival. Spending the night in Muzdalifah is Wajib."
    ),
    HajjDayStep(
        dayBn = "৩য় দিন",
        dayEn = "Day 3",
        dateBn = "১০ই জিলহজ",
        dateEn = "10th Dhul Hijjah",
        titleBn = "কঙ্কর নিক্ষেপ, কোরবানি, মাথা মুণ্ডন ও তাওয়াফে যিয়ারত",
        titleEn = "Rami at Jamarat, Qurbani, Halq & Tawaf az-Ziyarah",
        location = "মিনা ও মক্কা",
        icon = "🎯",
        keyPointsBn = listOf(
            "মুজদালিফায় ফজর পড়ে মাশআরে হারামের নিকট দোয়া করে সূর্যোদয়ের পূর্বে মিনা রওয়ানা।",
            "বড় জামারায় (আকাবা) ৭টি কঙ্কর নিক্ষেপ করা (প্রতি কঙ্করে 'আল্লাহু আকবার' বলা)।",
            "প্রথম কঙ্কর নিক্ষেপের সাথে সাথে তালবিয়া পাঠ বন্ধ করা।",
            "হজে তামাত্তু ও কিরানকারীদের জন্য দমে শোকর (কোরবানি) সম্পন্ন করা।",
            "মাথা মুণ্ডন (হলক) বা চুল ছোট (কসর) করা (এতে প্রথম তাহাল্লুল - স্ত্রী সঙ্গম ছাড়া সব হালাল)।",
            "মক্কায় গিয়ে তাওয়াফে যিয়ারত (ফরজ রুকন) ও সাঈ সম্পন্ন করা এবং মিনায় ফিরে আসা।"
        ),
        keyPointsEn = listOf(
            "Pray Fajr in Muzdalifah, supplicate near Mash'ar al-Haram, proceed to Mina before sunrise.",
            "Stone the Large Pillar (Jamarat al-Aqaba) with 7 pebbles, chanting Takbir with each throw.",
            "Cease reciting Talbiyah upon the first pebble throw.",
            "Perform Qurbani (sacrifice of an animal for Tamattu/Qiran).",
            "Shave (Halq) or trim hair (Taqsir) - this achieves the 1st Tahallul.",
            "Go to Makkah to perform Tawaf az-Ziyarah (Fard) and Sa'i, then return to Mina."
        ),
        rulesBn = "১০ই জিলহজ সবচেয়ে ব্যস্ততম দিন। ধারাবাহিকভাবে ৪টি কাজ করতে হয়: ১. বড় জামারায় ৭টি কঙ্কর মারা, ২. কোরবানি নিশ্চিত করা, ৩. মাথা মুণ্ডন করা (এতে ইহরামের সব নিষেধাজ্ঞা উঠে যায়, কেবল দাম্পত্য মিলন ছাড়া), ৪. মক্কায় গিয়ে তাওয়াফে যিয়ারত ও সাঈ সম্পন্ন করা (এতে দাম্পত্য সম্পর্কও হালাল হয়ে যায়)। এরপর রাতযাপনের জন্য মিনায় ফিরে আসা ওয়াজিব।",
        rulesEn = "Follow the orderly sequence on the 10th: 1. Rami of Jamarat al-Aqaba, 2. Slaughter sacrifice, 3. Halq/Taqsir (First Tahallul lifts all Ihram bans except intimacy), 4. Tawaf az-Ziyarah & Sa'i in Makkah (Second Tahallul completely releases all restrictions). Return to sleep in Mina."
    ),
    HajjDayStep(
        dayBn = "৪র্থ ও ৫ম দিন",
        dayEn = "Days 4 & 5",
        dateBn = "১১ ও ১২ই জিলহজ",
        dateEn = "11th & 12th Dhul Hijjah",
        titleBn = "আইয়ামে তাশরিক ও তিন জামারায় কঙ্কর নিক্ষেপ",
        titleEn = "Tashriq Days: Stoning All 3 Jamarat",
        location = "মিনা (Mina)",
        icon = "🪨",
        keyPointsBn = listOf(
            "মিনায় রাতযাপন করা ওয়াজিব।",
            "প্রতিদিন জোহরের ওয়াক্তের পর ৩টি জামারাতে ধারাবাহিকভাবে ৭টি করে মোট ২১টি কঙ্কর নিক্ষেপ:",
            "১ম: জামারা সগিরা (ছোট জামারা) - ৭টি কঙ্কর ও একপাশে সরে হাত তুলে লম্বা দোয়া।",
            "২য়: জামারা উসতা (মধ্যম জামারা) - ৭টি কঙ্কর ও একপাশে সরে লম্বা দোয়া।",
            "৩য়: জামারা আকাবা (বড় জামারা) - ৭টি কঙ্কর (এরপর দোয়া নেই)।",
            "১২ তারিখ সূর্যাস্তের পূর্বে মিনা ত্যাগ করতে চাইলে মক্কায় ফিরে আসা বৈধ।"
        ),
        keyPointsEn = listOf(
            "Stay overnight in Mina.",
            "Stone all 3 Jamarat after Dhuhr on both days (21 pebbles each day):",
            "1. Jamarah Sughra (small) with 7 pebbles + make long dua facing Qiblah.",
            "2. Jamarah Wusta (middle) with 7 pebbles + make long dua.",
            "3. Jamarah Aqaba (large) with 7 pebbles (no dua afterwards).",
            "Permissible to depart Mina for Makkah before sunset on the 12th."
        ),
        rulesBn = "১১ ও ১২ জিলহজ প্রতিদিন জোহরের ওয়াক্ত শুরু হওয়ার পর কঙ্কর নিক্ষেপ করতে হয়। আগে ছোট, তারপর মধ্যম, তারপর বড় জামারায় ৭টি করে পাথর মারবেন। প্রথম দুটি জামারায় পাথর মারার পর ভিড় এড়িয়ে কিবলামুখী হয়ে হাত তুলে দীর্ঘ সময় কান্নাকাটি করে দোয়া করা অত্যন্ত ফজিলতপূর্ণ সুন্নাত। ১২ তারিখ সূর্যাস্তের পূর্বে মিনা থেকে বের হয়ে মক্কায় ফেরা যায়।",
        rulesEn = "Stoning must take place after Zawal (midday prayer time). Stone the small pillar first, step aside to make heartfelt dua, then the middle pillar and dua, and finally the big pillar without stopping for dua. Pilgrims wishing to leave may depart before sunset on the 12th."
    ),
    HajjDayStep(
        dayBn = "৬ষ্ঠ দিন (ঐচ্ছিক)",
        dayEn = "Day 6 (Optional)",
        dateBn = "১৩ই জিলহজ ও বিদায়",
        dateEn = "13th Dhul Hijjah & Farewell",
        titleBn = "মিনায় অতিরিক্ত দিন ও তাওয়াফে বিদা",
        titleEn = "Final Stoning & Farewell Tawaf (Wida)",
        location = "মিনা ও মক্কা",
        icon = "🕋",
        keyPointsBn = listOf(
            "১২ তারিখ সূর্যাস্তের মধ্যে মিনা ত্যাগ না করলে ১৩ তারিখ রাতও মিনায় কাটাতে হবে।",
            "১৩ তারিখ জোহরের পর একইভাবে ৩টি জামারায় ২১টি কঙ্কর নিক্ষেপ করে মক্কায় ফেরা।",
            "হজের সমাপ্তি ও মক্কা ত্যাগের পূর্বে তাওয়াফে বিদা (বিদায়ী তাওয়াফ) আদায় করা বহিরাগতদের জন্য ওয়াজিব।"
        ),
        keyPointsEn = listOf(
            "If remaining in Mina past sunset on the 12th, staying the 13th becomes obligatory.",
            "Stone all 3 pillars after Zawal with 21 pebbles, then return to Makkah.",
            "Perform Tawaf al-Wida (Farewell Tawaf) immediately before permanently leaving Makkah."
        ),
        rulesBn = "১৩ই জিলহজ অবস্থান করা উত্তম। এ দিনও ২১টি কঙ্কর মেরে মক্কায় ফিরে আসবেন। নিজ দেশে ফেরার ঠিক পূর্বে কাবা শরীফের বিদায়ী তাওয়াফ (তাওয়াফে বিদা) করা ওয়াজিব। তবে ঋতুবতী নারীদের জন্য এ তাওয়াফ মওকুফ।",
        rulesEn = "Staying until the 13th earns additional immense rewards. Before your departure journey home, perform Tawaf al-Wida around the Kaaba (exempt for menstruating women)."
    )
)

private fun getHajjDuasData(): List<HajjDuaItem> = listOf(
    HajjDuaItem(
        titleBn = "পূর্ণাঙ্গ তালবিয়া",
        titleEn = "The Complete Talbiyah",
        occasionBn = "ইহরাম বাঁধার পর থেকে ১০ই জিলহজ ১ম কঙ্কর পর্যন্ত",
        occasionEn = "From entering Ihram until the 1st pebble on 10th",
        arabic = "لَبَّيْكَ اللَّهُمَّ لَبَّيْكَ، لَبَّيْكَ لاَ شَرِيكَ لَكَ لَبَّيْكَ، إِنَّ الْحَمْدَ وَالنِّعْمَةَ لَكَ وَالْمُلْكَ، لاَ شَرِيكَ لَكَ",
        pronBn = "লাব্বাইকা আল্লাহুম্মা লাব্বাইক, লাব্বাইকা লা শারীকা লাকা লাব্বাইক, ইন্নাল হামদা ওয়ান নিমাতা লাকা ওয়াল মুলক, লা শারীকা লাক।",
        pronEn = "Labbayka Allahumma Labbayk, Labbayka La Shareeka Laka Labbayk, Innal-Hamda Wan-Ni'mata Laka Wal-Mulk, La Shareeka Lak.",
        meaningBn = "আমি হাজির হে আল্লাহ! আমি হাজির! আপনার কোনো অংশীদার নেই, আমি হাজির! নিশ্চয়ই সমস্ত প্রশংসা, সকল নিয়ামত এবং সমগ্র রাজত্ব কেবল আপনারই; আপনার কোনো শরীক নেই।",
        meaningEn = "Here I am, O Allah, here I am. Here I am, You have no partner, here I am. Verily all praise, grace, and sovereignty belong to You alone. You have no partner."
    ),
    HajjDuaItem(
        titleBn = "হজের নিয়ত",
        titleEn = "Intention for Hajj",
        occasionBn = "ইহরাম বাঁধার সময়ে",
        occasionEn = "When assuming Ihram",
        arabic = "اللَّهُمَّ إِنِّي أُرِيدُ الْحَجَّ فَيَسِّرْهُ لِي وَتَقَبَّلْهُ مِنِّي ، لَبَّيْكَ حَجًّا",
        pronBn = "আল্লাহুম্মা ইন্নি উরীদুল হাজ্জা ফায়াসসিরহু লী ওয়া তাকাব্বালহু মিন্নী, লাব্বাইকা হাজ্জান।",
        pronEn = "Allahumma inni ureedul-hajja fa-yassirhu lee wa taqabbalhu minnee, labbayka hajjan.",
        meaningBn = "হে আল্লাহ! আমি হজের নিয়ত করছি, আপনি এটি আমার জন্য সহজ করে দিন এবং আমার পক্ষ থেকে কবুল করে নিন।",
        meaningEn = "O Allah, I intend to perform Hajj, so make it easy for me and accept it from me. Here I am answering Your call for Hajj."
    ),
    HajjDuaItem(
        titleBn = "হাজরে আসওয়াদ ইস্তিলামের তাকবীর",
        titleEn = "Greeting Hajr al-Aswad (Black Stone)",
        occasionBn = "তাওয়াফের প্রতিটি চক্কর শুরুর সময়",
        occasionEn = "At the start of every Tawaf lap",
        arabic = "بِسْمِ اللَّهِ وَاللَّهُ أَكْبَرُ",
        pronBn = "বিসমিল্লাহি ওয়াল্লাহু আকবার।",
        pronEn = "Bismillahi Wallahu Akbar.",
        meaningBn = "আল্লাহর নামে শুরু করছি এবং আল্লাহই সর্বশ্রেষ্ঠ।",
        meaningEn = "In the name of Allah, and Allah is the Greatest."
    ),
    HajjDuaItem(
        titleBn = "রুকনে ইয়ামানী ও হাজরে আসওয়াদের দোয়া",
        titleEn = "Dua Between Yemeni Corner & Black Stone",
        occasionBn = "তাওয়াফের প্রতিটি চক্করের শেষ অংশে",
        occasionEn = "During the final portion of every circuit",
        arabic = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
        pronBn = "রাব্বানা আতিনা ফিদ-দুনয়া হাসানাতাওঁ ওয়া ফিল আখিরাতি হাসানাতাওঁ ওয়া কিনা আযাবান নার।",
        pronEn = "Rabbana atina fid-dunya hasanatan wa fil-akhirati hasanatan wa qina 'adhaban-nar.",
        meaningBn = "হে আমাদের প্রতিপালক! আমাদের দুনিয়াতে কল্যাণ দান করুন এবং আখিরাতেও কল্যাণ দান করুন, আর আমাদেরকে জাহান্নামের আগুন থেকে রক্ষা করুন।",
        meaningEn = "Our Lord! Grant us good in this world and good in the Hereafter, and save us from the punishment of the Fire."
    ),
    HajjDuaItem(
        titleBn = "সাফা ও মারওয়ায় আরোহণের দোয়া",
        titleEn = "Dua When Ascending Safa & Marwah",
        occasionBn = "সাঈ শুরু করার সময়",
        occasionEn = "When commencing Sa'i",
        arabic = "إِنَّ الصَّفَا وَالْمَرْوَةَ مِن شَعَائِرِ اللَّهِ ، أَبْدَأُ بِمَا بَدَأَ اللَّهُ بِهِ",
        pronBn = "ইন্নাস সাফা ওয়াল মারওয়াতা মিন শা'আ-ইরিল্লাহ, আবদাউ বিমা বাদা-আল্লাহু বিহী।",
        pronEn = "Innas-Safa wal-Marwata min sha'a'irillah, abda'u bima bada'allahu bih.",
        meaningBn = "নিশ্চয়ই সাফা ও মারওয়া আল্লাহর নিদর্শনসমূহের অন্তর্ভুক্ত। আল্লাহ যা দিয়ে শুরু করেছেন (সাফা), আমিও তা দিয়ে শুরু করছি।",
        meaningEn = "Verily, Safa and Marwah are among the symbols of Allah. I begin with that which Allah began with (Safa)."
    ),
    HajjDuaItem(
        titleBn = "আরাফার দিনের সর্বোত্তম দোয়া",
        titleEn = "The Supreme Dua of Arafah",
        occasionBn = "৯ই জিলহজ আরাফার ময়দানে",
        occasionEn = "Day of Arafah afternoon",
        arabic = "لاَ إِلَهَ إِلاَّ اللَّهُ وَحْدَهُ لاَ شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
        pronBn = "লা ইলাহা ইল্লাল্লাহু ওয়াহদাহু লা শারীকা লাহু, লাহুল মুলকু ওয়া লাহুল হামদু, ওয়া হুওয়া 'আলা কুল্লি শাইয়িন কাদীর।",
        pronEn = "La ilaha illallahu wahdahu la shareeka lah, lahul-mulku wa lahul-hamdu, wa huwa 'ala kulli shay'in qadeer.",
        meaningBn = "এক আল্লাহ ছাড়া কোনো সত্য ইলাহ নেই, তাঁর কোনো অংশীদার নেই, রাজত্ব কেবল তাঁরই, সমস্ত প্রশংসা তাঁরই এবং তিনি সকল কিছুর ওপর ক্ষমতাবান।",
        meaningEn = "None has the right to be worshipped but Allah alone, Who has no partner. His is the dominion and His is the praise, and He is Able to do all things."
    ),
    HajjDuaItem(
        titleBn = "কঙ্কর নিক্ষেপের তাকবীর",
        titleEn = "Takbir While Stoning the Pillars",
        occasionBn = "প্রতিটি পাথর মারার মুহূর্তে",
        occasionEn = "With every pebble thrown at Jamarat",
        arabic = "بِسْمِ اللَّهِ ، اللَّهُ أَكْبَرُ ، رَغْمًا لِلشَّيْطَانِ وَرِضًا لِلرَّحْمَنِ",
        pronBn = "বিসমিল্লাহি, আল্লাহু আকবার, রাগমান লিশ-শায়তানি ওয়া রিদান লির-রাহমান।",
        pronEn = "Bismillahi, Allahu Akbar, raghman lish-shaytani wa ridan lir-Rahman.",
        meaningBn = "আল্লাহর নামে, আল্লাহ সর্বশ্রেষ্ঠ; শয়তানের অপমানের জন্য এবং দয়াময় আল্লাহর সন্তুষ্টির উদ্দেশ্যে।",
        meaningEn = "In the name of Allah, Allah is the Greatest; in defiance of Satan and to please the Most Merciful."
    )
)

private fun getChecklistData(): List<ChecklistItem> = listOf(
    ChecklistItem(
        id = "doc_passport",
        titleBn = "পাসপোর্ট ও ভিসা কপি",
        titleEn = "Passport & Visa Copies",
        categoryBn = "কাগজপত্র",
        categoryEn = "Documents",
        hintBn = "মূল পাসপোর্টের সাথে ৩-৪ সেট রঙিন ফটোকপি ও মোবাইল ড্রাইভে সফটকপি রাখুন।",
        hintEn = "Keep 3-4 physical photocopies plus digital backups on Google Drive."
    ),
    ChecklistItem(
        id = "doc_nusuk",
        titleBn = "নুসুক (Nusuk) অ্যাপ ইনস্টল ও লগইন",
        titleEn = "Nusuk App Setup",
        categoryBn = "অ্যাপ ও ডিজিটাল",
        categoryEn = "Digital",
        hintBn = "রওজা শরীফে সালাত ও যিয়ারতের অনুমতি নুসুক অ্যাপ ছাড়া সম্ভব নয়।",
        hintEn = "Official Saudi permit for Rawdah visit is issued exclusively via Nusuk."
    ),
    ChecklistItem(
        id = "gear_ihram",
        titleBn = "ইহরামের কাপড় (কমপক্ষে ২ সেট)",
        titleEn = "Ihram Sheets (Min 2 Sets)",
        categoryBn = "পোশাক",
        categoryEn = "Attire",
        hintBn = "পুরুষদের জন্য সুতি বা তোয়ালে ফেব্রিকের আরামদায়ক ২ জোড়া সাদা চাদর ও ইহরামের বেল্ট।",
        hintEn = "Two pairs of unstitched absorbent cotton/towel sheets and a secure belt."
    ),
    ChecklistItem(
        id = "gear_sandals",
        titleBn = "আরামদায়ক স্যান্ডেল (গোড়ালি খোলা)",
        titleEn = "Open-Heel Walking Sandals",
        categoryBn = "জুতো",
        categoryEn = "Footwear",
        hintBn = "পুরুষদের ইহরামে পায়ের গোড়ালি ও পাতার ওপর খোলা থাকতে হবে। নরম স্পঞ্জের স্যান্ডেল নিন।",
        hintEn = "Must keep upper foot and heel exposed for men in Ihram. Soft cushioning recommended."
    ),
    ChecklistItem(
        id = "gear_toiletries",
        titleBn = "সুগন্ধিহীন সাবান, ভেসলিন ও লোশন",
        titleEn = "Unscented Toiletries",
        categoryBn = "টয়লেট্রিজ",
        categoryEn = "Toiletries",
        hintBn = "ইহরাম অবস্থায় যেকোনো সুগন্ধি নিষিদ্ধ। গন্ধহীন পেট্রোলিয়াম জেলি উরুর ঘষা প্রতিরোধে জরুরি।",
        hintEn = "All fragrance is prohibited in Ihram. Unscented petroleum jelly prevents chafing."
    ),
    ChecklistItem(
        id = "gear_meds",
        titleBn = "প্রেসক্রিপশনের ওষুধ ও ফার্স্ট এইড",
        titleEn = "Essential Medicines",
        categoryBn = "স্বাস্থ্য",
        categoryEn = "Health",
        hintBn = "ডায়াবেটিস/প্রেসারের ওষুধ, প্যারাসিটামল, ওআরএস স্যালাইন, অ্যান্টাসিড ও পেইন কিলার।",
        hintEn = "Regular prescription drugs with doctor slip, ORS sachets, pain relief, and antacids."
    ),
    ChecklistItem(
        id = "gear_umbrella",
        titleBn = "ছোট ভাঁজ করা ছাতা ও রোদচশমা",
        titleEn = "Foldable Umbrella & Sunglasses",
        categoryBn = "গরম প্রতিরোধ",
        categoryEn = "Weather",
        hintBn = "আরাফা ও মিনার তীব্র রোদে হিটস্ট্রোক থেকে রক্ষা পেতে ছাতা অত্যন্ত কার্যকর।",
        hintEn = "Crucial for protection against heat exhaustion and intense sun in Mina & Arafat."
    ),
    ChecklistItem(
        id = "gear_powerbank",
        titleBn = "পাওয়ার ব্যাংক ও ইউনিভার্সাল অ্যাডাপ্টার",
        titleEn = "Power Bank & Universal Plug",
        categoryBn = "ইলেকট্রনিক্স",
        categoryEn = "Electronics",
        hintBn = "মিনায় ও আরাফায় তাঁবুতে চার্জিং পয়েন্ট সীমিত থাকে। ১০,০০০-২০,০০০ mAh পাওয়ার ব্যাংক রাখুন।",
        hintEn = "Tent charging ports in Mina and Arafat are limited; carry a certified 10,000-20,000 mAh power bank."
    ),
    ChecklistItem(
        id = "gear_pebble_pouch",
        titleBn = "কঙ্কর রাখার ছোট ব্যাগ বা বোতল",
        titleEn = "Small Pebble Pouch / Bottle",
        categoryBn = "হজ সামগ্রী",
        categoryEn = "Hajj Gear",
        hintBn = "মুজদালিফা থেকে ৭০টি কঙ্কর সংগ্রহ করে রাখার জন্য ছোট কাপড়ের থলে বা প্লাস্টিক বোতল।",
        hintEn = "Small drawstring cloth bag to hold 70 picked pebbles from Muzdalifah."
    )
)
