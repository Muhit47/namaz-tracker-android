package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

// --- Data Models for Rak'ah Table & Guides ---
data class PrayerRakahInfo(
    val nameBn: String,
    val nameEn: String,
    val icon: String,
    val beforeFarz: String,
    val isBeforeGhair: Boolean = false,
    val farz: String,
    val afterFarz: String,
    val naflOrWitr: String,
    val isWitr: Boolean = false,
    val totalRakahsBn: String,
    val totalRakahsEn: String,
    val isJumuah: Boolean = false,
    val virtuesBn: String,
    val virtuesEn: String,
    val niyyahBn: String,
    val niyyahEn: String,
    val rulesBn: String,
    val rulesEn: String
)

data class DuaItem(
    val title: String,
    val arabic: String,
    val pron: String,
    val meaning: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuidesScreen(
    isBangla: Boolean = false,
    initialSection: Int = 0,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedSection by remember(initialSection) { mutableIntStateOf(initialSection) }
    var selectedPrayerForDetail by remember { mutableStateOf<PrayerRakahInfo?>(null) }
    val colors = FinancyTheme.colors
    val isDark = colors.isDark

    // Dialog for Interactive Prayer Details (Matching iOS Heavy Glass Screenshot 3)
    if (selectedPrayerForDetail != null) {
        val prayer = selectedPrayerForDetail!!
        AlertDialog(
            onDismissRequest = { selectedPrayerForDetail = null },
            confirmButton = {
                Button(
                    onClick = { selectedPrayerForDetail = null },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.emerald)
                ) {
                    Text(
                        if (isBangla) "বুঝেছি (Close)" else "Understood (Close)",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFF1F5F9))
                            .border(1.dp, if (isDark) Color.White.copy(alpha = 0.15f) else Color(0xFFCBD5E1), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(prayer.icon, fontSize = 24.sp)
                    }
                    Column {
                        Text(
                            text = if (isBangla) "${prayer.nameBn} (${prayer.nameEn})" else "${prayer.nameEn} (${prayer.nameBn})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = colors.emerald.copy(alpha = if (isDark) 0.18f else 0.12f),
                            border = BorderStroke(1.dp, colors.emerald.copy(alpha = 0.35f))
                        ) {
                            Text(
                                text = if (isBangla) "মোট রাকআত: ${prayer.totalRakahsBn}" else "Total Rakahs: ${prayer.totalRakahsEn}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = colors.emerald
                            )
                        }
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Cyan-bordered Virtues & Hadith Quote Callout
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF06B6D4).copy(alpha = if (isDark) 0.12f else 0.08f),
                        border = BorderStroke(1.dp, Color(0xFF06B6D4).copy(alpha = 0.30f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(10.dp)) {
                            Box(
                                modifier = Modifier
                                    .width(3.5.dp)
                                    .height(50.dp)
                                    .background(Color(0xFF06B6D4), RoundedCornerShape(2.dp))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isBangla) "📖 ফযিলত ও হাদিস:" else "📖 Virtues & Hadith:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = Color(0xFF06B6D4)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isBangla) prayer.virtuesBn else prayer.virtuesEn,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isDark) Color(0xFFCFFAFE) else Color(0xFF0E7490),
                                    lineHeight = 17.sp,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            }
                        }
                    }

                    // Niyyah Card
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isDark) Color.White.copy(alpha = 0.05f) else Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.10f) else Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = if (isBangla) "🤲 নিয়ত:" else "🤲 Niyyah / Intention:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = if (isBangla) prayer.niyyahBn else prayer.niyyahEn,
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.textSecondary,
                                lineHeight = 17.sp
                            )
                        }
                    }

                    // Rules & Instructions Card
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isDark) Color.White.copy(alpha = 0.05f) else Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.10f) else Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = if (isBangla) "📌 আদায়ের পদ্ধতি ও জরুরি নিয়মাবলী:" else "📌 Authentic Method & Rules:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = if (isBangla) prayer.rulesBn else prayer.rulesEn,
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.textSecondary,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            },
            containerColor = colors.surfaceCard,
            shape = RoundedCornerShape(26.dp)
        )
    }

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isBangla) "দ্বীনি শিক্ষা ও জ্ঞান হাব" else "Islamic Knowledge & Guides",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = if (isBangla) "নামাজ, ওজু ও দোয়ার পূর্ণাঙ্গ গাইড" else "Complete guide for Salah, Wudu & Duas",
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
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // 1. Floating iOS 18 Segmented Control (3 Sub-Tabs)
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isDark) Color(0xFF131D18).copy(alpha = 0.85f) else Color(0xFFE2E8F0),
                    border = BorderStroke(1.dp, if (isDark) Color(0xFF22352C) else Color(0xFFCBD5E1)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Tab 0: নামাযের নিয়ম
                        val isTab0 = selectedSection == 0
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedSection = 0 },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isTab0) AmberAccentDark else Color.Transparent,
                            shadowElevation = if (isTab0) 3.dp else 0.dp
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 9.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isBangla) "📖 নামাযের নিয়ম" else "📖 Salah Steps",
                                    fontWeight = if (isTab0) FontWeight.ExtraBold else FontWeight.Bold,
                                    fontSize = 11.5.sp,
                                    color = if (isTab0) Color.White else if (isDark) Color(0xFF94A3B8) else Color(0xFF1E293B)
                                )
                            }
                        }

                        // Tab 1: ওজু ও তাহারাত
                        val isTab1 = selectedSection == 1
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedSection = 1 },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isTab1) CyanAccent else Color.Transparent,
                            shadowElevation = if (isTab1) 3.dp else 0.dp
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 9.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isBangla) "💧 ওজু ও তাহারাত" else "💧 Wudu & Purity",
                                    fontWeight = if (isTab1) FontWeight.ExtraBold else FontWeight.Bold,
                                    fontSize = 11.5.sp,
                                    color = if (isTab1) Color.White else if (isDark) Color(0xFF94A3B8) else Color(0xFF1E293B)
                                )
                            }
                        }

                        // Tab 2: দোয়া ভান্ডার
                        val isTab2 = selectedSection == 2
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedSection = 2 },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isTab2) VioletAccent else Color.Transparent,
                            shadowElevation = if (isTab2) 3.dp else 0.dp
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 9.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isBangla) "🤲 দোয়া ভান্ডার" else "🤲 Dua Vault",
                                    fontWeight = if (isTab2) FontWeight.ExtraBold else FontWeight.Bold,
                                    fontSize = 11.5.sp,
                                    color = if (isTab2) Color.White else if (isDark) Color(0xFF94A3B8) else Color(0xFF1E293B)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ===================================================================
            // SECTION 0: নামাযের নিয়ম & RAK'AH TABLE
            // ===================================================================
            if (selectedSection == 0) {
                // HERO ITEM: The User's Rak'ah Table Component (5 Prayers + Jumu'ah)
                item {
                    RakahTableComponent(
                        isBangla = isBangla,
                        onPrayerClick = { selectedPrayerForDetail = it }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // 8 Authentic Salah Steps Header
                item {
                    Text(
                        text = if (isBangla) "📖 সহিহ নামাজের ধারাবাহিক ৮টি নিয়ম" else "📖 8 Authentic Step-by-Step Salah Guide",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = colors.emerald
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                val steps = if (isBangla) listOf(
                    "১. কিবলার দিকে দাঁড়ানো ও নিয়ত" to "পবিত্র পোশাকে কিবলামুখী হয়ে সোজা হয়ে দাঁড়ানো এবং মনে মনে সংকল্প করা যে আমি অমুক ওয়াক্তের নামাজ আদায় করছি। (বুখারি: ১)",
                    "২. তাকবীরে তাহরীমা" to "উভয় হাত কান বা কাঁধ পর্যন্ত উঠিয়ে 'আল্লাহু আকবার' বলে হাত নাভির নিচে বা বুকের ওপর বাঁধা। (বুখারি: ৭৩৬)",
                    "৩. ছানা ও সূরা ফাতিহা পাঠ" to "প্রথমে 'সুবহানাকা আল্লাহুম্মা...' ছানা পাঠ করা, তারপর আউযুবিল্লাহ-বিসমিল্লাহ সহকারে সূরা ফাতিহা ও অন্য একটি সূরা মেলানো। (তিরমিজি: ২৪৩)",
                    "৪. রুকু আদায়" to "'আল্লাহু আকবার' বলে রুকুতে যাওয়া, পিঠ সমান্তরাল রাখা এবং কমপক্ষে ৩ বার 'সুবহানা রাব্বিয়াল আজীম' বলা। (আবু দাউদ: ৮৭০)",
                    "৫. কওমা (সোজা হয়ে দাঁড়ানো)" to "'সামিআল্লাহু লিমান হামিদাহ' বলে সোজা হয়ে দাঁড়ানো এবং 'রাব্বানা লাকাল হামদ' বলা। (বুখারি: ৭৯৫)",
                    "৬. সিজদা আদায়" to "'আল্লাহু আকবার' বলে মাটিতে সিজদা করা (কপাল, নাক, দুই হাত, দুই হাঁটু ও পায়ের আঙ্গুল মাটিতে থাকবে) এবং ৩ বার 'সুবহানা রাব্বিয়াল আ'লা' বলা। (মুসলিম: ২২৬)",
                    "৭. দুই সিজদার মাঝের বৈঠক" to "সিজদা থেকে উঠে সোজা হয়ে বসে 'রাব্বিগফিরলী' বলা, তারপর দ্বিতীয় সিজদা সম্পন্ন করা। (আবু দাউদ: ৮৭৪)",
                    "৮. শেষ বৈঠক ও সালাম" to "তাশাহহুদ (আত্তাহিয়্যাতু), দরূদ শরীফ ও দোয়া মাছুরা পড়ে ডানে ও বামে 'আসসালামু আলাইকুম ওয়া রাহমাতুল্লাহ' বলে সালাম ফেরানো। (বুখারি: ৮৩১)"
                ) else listOf(
                    "1. Facing Qibla & Niyyah" to "Stand upright facing the Qibla with clean attire, making the heart intention for the specific prayer. (Bukhari: 1)",
                    "2. Takbir al-Ihram" to "Raise both hands to ears or shoulders saying 'Allahu Akbar' and fold hands below navel or on chest. (Bukhari: 736)",
                    "3. Reciting Thana & Surah Al-Fatiha" to "Recite Thana ('Subhanaka Allahumma...'), then seeking refuge and reciting Surah Al-Fatiha followed by another Surah. (Tirmidhi: 243)",
                    "4. Performing Ruku (Bowing)" to "Say 'Allahu Akbar' and bow down with back straight, reciting 'Subhana Rabbiyal Azim' at least 3 times. (Abu Dawud: 870)",
                    "5. Qauma (Standing after Ruku)" to "Rise saying 'Sami'Allahu liman hamidah' and say 'Rabbana lakal hamd'. (Bukhari: 795)",
                    "6. Performing Sujood (Prostration)" to "Prostrate saying 'Allahu Akbar' touching forehead, nose, palms, knees and toes to ground, reciting 'Subhana Rabbiyal A'la' 3 times. (Muslim: 226)",
                    "7. Jalsa (Sitting between Sujood)" to "Sit upright between the two prostrations saying 'Rabbighfirli', then perform the second prostration. (Abu Dawud: 874)",
                    "8. Tashahhud & Taslim" to "Sit for Tashahhud, Durood Ibrahim and Dua Masura, then turn head right and left saying 'Assalamu Alaikum wa Rahmatullah'. (Bukhari: 831)"
                )

                itemsIndexed(steps) { idx, step ->
                    val (title, desc) = step
                    GuideStepCard(stepNum = (idx + 1).toString(), title = title, desc = desc)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Gender differences Accordion
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    AccordionCard(
                        title = if (isBangla) "👥 নারী ও পুরুষের নামাজের পার্থক্যের গাইড" else "👥 Prayer Differences for Men & Women",
                        content = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = if (isBangla) "👨 পুরুষের নিয়ম: তাকবীরে হাত কান পর্যন্ত তোলা, নাভির নিচে বা বুকে হাত বাঁধা, রুকু-সিজদায় কনুই ফাঁকা রাখা, বাম পায়ের ওপর বসা ও ডান পা খাড়া রাখা। (বুখারি: ৮২৮)"
                                    else "👨 Men's method: Raise hands to ears for Takbir, place hands below navel/chest, keep elbows away from body in Sujood, sit on left foot with right foot upright. (Bukhari: 828)",
                                    fontSize = 12.5.sp,
                                    lineHeight = 18.sp,
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = if (isBangla) "🧕 নারীর নিয়ম: তাকবীরে হাত কাঁধ পর্যন্ত তোলা, বুকের ওপর হাত বাঁধা, রুকু-সিজদায় শরীর সংকুচিত রাখা, দুই পা ডান দিকে বের করে বসা। (ইবনে আবি শাইবা: ২৭৭৮)"
                                    else "🧕 Women's method: Raise hands to shoulders for Takbir, place hands on chest, remain compact in Sujood, sit with both feet extending to the right side. (Ibn Abi Shaybah: 2778)",
                                    fontSize = 12.5.sp,
                                    lineHeight = 18.sp,
                                    color = colors.textSecondary
                                )
                            }
                        }
                    )
                }
            }

            // ===================================================================
            // SECTION 1: ওজু ও পবিত্রতা
            // ===================================================================
            if (selectedSection == 1) {
                item {
                    Text(
                        text = if (isBangla) "💧 ওজু ও পবিত্রতার সুন্নাহ নিয়ম" else "💧 Sunnah Method of Wudu & Purification",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = colors.cyan
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                val wuduFardh = if (isBangla) listOf(
                    "১. সমস্ত মুখমণ্ডল ধোয়া" to "কপালের চুলের গোড়া থেকে থুতনির নিচ এবং এক কানের লতি থেকে অন্য কান পর্যন্ত ১ বার ধোয়া ফরজ (৩ বার সুন্নাত)। (সূরা মায়িদা: ৬)",
                    "২. দুই হাত কনুইসহ ধোয়া" to "আঙ্গুলের ডগা থেকে শুরু করে দুই হাতের কনুই পর্যন্ত পুরোপুরি পানি পৌঁছানো। (সূরা মায়িদা: ৬)",
                    "৩. মাথার চারভাগের একভাগ মাসেহ করা" to "ভেজা হাতে মাথার কমপক্ষে এক চতুর্থাংশ মাসেহ করা ফরজ (পুরো মাথা মাসেহ করা সুন্নাত)। (বুখারি: ১৮৫)",
                    "৪. দুই পা টাখনুসহ ধোয়া" to "পায়ের আঙ্গুল থেকে শুরু করে পায়ের গোড়ালি ও টাখনুসহ ভালোভাবে ধৌত করা। (সূরা মায়িদা: ৬)"
                ) else listOf(
                    "1. Washing Entire Face" to "Washing from the hairline to below chin, and ear to ear once is Fardh (thrice is Sunnah). (Surah Al-Ma'idah: 6)",
                    "2. Washing Hands up to Elbows" to "Washing both arms thoroughly including elbows without leaving any dry spot. (Surah Al-Ma'idah: 6)",
                    "3. Wiping One-Fourth of Head" to "Wiping at least one-fourth of the head with wet hands (wiping whole head is Sunnah). (Bukhari: 185)",
                    "4. Washing Feet including Ankles" to "Washing both feet thoroughly including ankles and heels. (Surah Al-Ma'idah: 6)"
                )

                item {
                    Text(
                        text = if (isBangla) "⭐ ওজুর ৪টি ফরজ (আবশ্যিক)" else "⭐ 4 Fardh of Wudu (Mandatory)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                itemsIndexed(wuduFardh) { idx, step ->
                    val (title, desc) = step
                    GuideStepCard(stepNum = (idx + 1).toString(), title = title, desc = desc)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Dua after Wudu Card
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                        border = BorderStroke(1.dp, colors.cyan.copy(alpha = 0.35f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = if (isBangla) "✨ ওজু শেষের দোয়া (জান্নাতের ৮টি দরজা উন্মুক্ত হওয়ার সুসংবাদ)" else "✨ Dua After Wudu (8 Gates of Jannah Open)",
                                fontWeight = FontWeight.ExtraBold,
                                color = colors.cyan,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "أَشْهَدُ أَنْ لَا إِلٰهَ إِلَّا اللهُ وَحْدَهُ لَا شَرِيكَ لَهُ، وَأَشْهَدُ أَنَّ مُحَمَّدًا عَبْدُهُ وَرَسُولُهُ",
                                fontFamily = AmiriFontFamily,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth(),
                                lineHeight = 32.sp,
                                color = colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isBangla) "উচ্চারণ: আশহাদু আল-লা ইলাহা ইল্লাল্লাহু ওয়াহদাহু লা শারীকা লাহু, ওয়া আশহাদু আন্না মুহাম্মাদান আবদুহু ওয়া রাসুলুহু।"
                                else "Pronunciation: Ash-hadu an la ilaha illallahu wahdahu la sharika lahu, wa ash-hadu anna Muhammadan 'abduhu wa rasuluh.",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.cyan
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isBangla) "ফজিলত: যে ব্যক্তি উত্তমরূপে ওজু করে এই দোয়া পাঠ করবে, তার জন্য জান্নাতের ৮টি দরজাই উন্মুক্ত করে দেওয়া হবে। (সহিহ মুসলিম: ২৩৪)"
                                else "Virtue: Whosoever performs Wudu properly and recites this dua, all eight gates of Paradise will be opened for him. (Sahih Muslim: 234)",
                                fontSize = 11.5.sp,
                                color = colors.textSecondary,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }

                // Ghusl & Tayammum Accordions
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    AccordionCard(
                        title = if (isBangla) "🚿 গোসলের ৩টি ফরজ ও সুন্নাহ পদ্ধতি" else "🚿 3 Fardh of Ghusl & Sunnah Method",
                        content = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = if (isBangla) "১. গড়গড়া সহকারে ভালো করে কুলি করা।\n২. নাকের নরম স্থান পর্যন্ত পানি পৌঁছানো।\n৩. সমস্ত শরীরে এমনভাবে পানি পৌঁছানো যেন একটি পশমের গোড়াও শুকনা না থাকে।"
                                    else "1. Rinsing mouth thoroughly with gargle.\n2. Sniffing water into nostrils up to soft bone.\n3. Washing entire body completely without leaving a single hair dry.",
                                    fontSize = 12.5.sp,
                                    lineHeight = 18.sp,
                                    color = colors.textPrimary
                                )
                            }
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    AccordionCard(
                        title = if (isBangla) "🏜️ তায়াম্মুমের নিয়ম ও ফরজসমূহ" else "🏜️ Tayammum (Dry Ablution) Rules",
                        content = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = if (isBangla) "পানি না পাওয়া গেলে বা অসুস্থতার কারণে পানি ব্যবহারে ক্ষতির আশঙ্কা থাকলে পবিত্র মাটি দ্বারা তায়াম্মুম করা যায়। ফরজ ৩টি: নিয়ত করা, মাটিতে হাত মেরে মুখমণ্ডল মাসেহ করা, পুনরায় মাটিতে হাত মেরে দুই হাত কনুইসহ মাসেহ করা। (সূরা মায়িদা: ৬)"
                                    else "When water is unavailable or its use is harmful due to illness, Tayammum is permitted using clean earth. 3 Fardh: Niyyah, striking hands on earth & wiping face, striking hands again & wiping both arms up to elbows. (Surah Al-Ma'idah: 6)",
                                    fontSize = 12.5.sp,
                                    lineHeight = 18.sp,
                                    color = colors.textPrimary
                                )
                            }
                        }
                    )
                }
            }

            // ===================================================================
            // SECTION 2: দোয়া ভান্ডার (DUA VAULT)
            // ===================================================================
            if (selectedSection == 2) {
                item {
                    Text(
                        text = if (isBangla) "🤲 নামাজের সকল প্রয়ােজনীয় দোয়া ও আরবী" else "🤲 Essential Duas for Prayer with Arabic",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = colors.violet
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                val duas = if (isBangla) listOf(
                    DuaItem("১. ছানা (নামাজের শুরুতে)", "سُبْحَانَكَ اللَّهُمَّ وَبِحَمْدِكَ وَتَبَارَكَ اسْمُكَ وَتَعَالَىٰ جَدُّكَ وَلَا إِلٰهَ غَيْرُكَ", "সুবহানাকা আল্লাহুম্মা ওয়া বিহামদিকা, ওয়া তাবারাকাসমুকা ওয়া তা'আলা জাদ্দুকা, ওয়া লা ইলাহা গাইরুক।", "হে আল্লাহ! আমি আপনার পবিত্রতা বর্ণনা করছি এবং আপনার প্রশংসা করছি। আপনার নাম বরকতময়, আপনার মর্যাদা অতি উচ্চ এবং আপনি ছাড়া কোনো ইলাহ নেই।"),
                    DuaItem("২. রুকুর তাসবীহ", "سُبْحَانَ رَبِّيَ الْعَظِيمِ", "সুবহানা রাব্বিয়াল আজীম।", "পবিত্র আমার মহান রব।"),
                    DuaItem("৩. রুকু থেকে ওঠার দোয়া (কওমা)", "رَبَّنَا لَكَ الْحَمْدُ حَمْدًا كَثِيرًا طَيِّبًا مُبَارَكًا فِيهِ", "রাব্বানা লাকাল হামদ, হামদান কাসীরান তাইয়্যেবান মুবারাকান ফীহ।", "হে আমাদের প্রতিপালক! আপনার জন্যই সমস্ত প্রশংসা, এমন প্রশংসা যা প্রচুর, পবিত্র ও বরকতময়।"),
                    DuaItem("৪. সিজদার তাসবীহ", "سُبْحَانَ رَبِّيَ الْأَعْلَىٰ", "সুবহানা রাব্বিয়াল আ'লা।", "পবিত্র আমার সর্বোচ্চ রব।"),
                    DuaItem("৫. তাশাহহুদ (আত্তাহিয়্যাতু)", "التَّحِيَّاتُ لِلّٰهِ وَالصَّلَوَاتُ وَالطَّيِّبَاتُ، السَّلَامُ عَلَيْكَ أَيُّهَا النَّبِيُّ وَرَحْمَةُ اللهِ وَبَرَكَاتُهُ، السَّلَامُ عَلَيْنَا وَعَلَىٰ عِبَادِ اللهِ الصَّالِحِينَ، أَشْهَدُ أَنْ لَا إِلٰهَ إِلَّا اللهُ وَأَشْهَدُ أَنَّ مُحَمَّدًا عَبْدُهُ وَرَسُولُهُ", "আত্তাহিয়্যাতু লিল্লাহি ওয়াস সালাওয়াতু ওয়াত ত্বাইয়্যিবাতু, আসসালামু আলাইকা আইয়্যুহান নাবিয়্যু ওয়া রাহমাতুল্লাহি ওয়া বারাকাতুহু, আসসালামু আলাইনা ওয়া আলা ইবাদিল্লাহিস সালিহীন, আশহাদু আল-লা ইলাহা ইল্লাল্লাহু ওয়া আশহাদু আন্না মুহাম্মাদান আবদুহু ওয়া রাসুলুহ।", "সমস্ত মৌখিক, শারীরিক ও আর্থিক ইবাদত একমাত্র আল্লাহর জন্য। হে নবী! আপনার ওপর শান্তি, আল্লাহর রহমত ও বরকত বর্ষিত হোক। আমাদের ওপর এবং আল্লাহর নেক বান্দাদের ওপর শান্তি বর্ষিত হোক। আমি সাক্ষ্য দিচ্ছি যে আল্লাহ ছাড়া কোনো মাবুদ নেই এবং মুহাম্মদ (সা.) তাঁর বান্দা ও রাসুল।"),
                    DuaItem("৬. দরূদে ইব্রাহিম", "اللَّهُمَّ صَلِّ عَلَىٰ مُحَمَّدٍ وَعَلَىٰ آلِ مُحَمَّدٍ كَمَا صَلَّيْتَ عَلَىٰ إِبْرَاهِيمَ وَعَلَىٰ آلِ إِبْرَاهِيمَ إِنَّكَ حَمِيدٌ مَجِيدٌ، اللَّهُمَّ بَارِكْ عَلَىٰ مُحَمَّدٍ وَعَلَىٰ آلِ مُحَمَّدٍ كَمَا بَارَكْتَ عَلَىٰ إِبْرَاهِيمَ وَعَلَىٰ آلِ إِبْرَاهِيمَ إِنَّكَ حَمِيدٌ مَجِيدٌ", "আল্লাহুম্মা সাল্লি আলা মুহাম্মাদিঁউ ওয়া আলা আলি মুহাম্মাদ, কামা সাল্লাইতা আলা ইব্রাহীমা ওয়া আলা আলি ইব্রাহীম, ইন্নাকা হামীদুম মাজীদ। আল্লাহুম্মা বারিক আলা মুহাম্মাদিঁউ ওয়া আলা আলি মুহাম্মাদ, কামা বারাকতা আলা ইব্রাহীমা ওয়া আলা আলি ইব্রাহীম, ইন্নাকা হামীদুম মাজীদ।", "হে আল্লাহ! মুহাম্মদ (সা.) ও তাঁর বংশধরদের ওপর রহমত বর্ষণ করুন, যেমন আপনি ইব্রাহিম (আ.) ও তাঁর বংশধরদের ওপর রহমত বর্ষণ করেছিলেন। নিশ্চয়ই আপনি প্রশংসিত ও মহিমান্বিত। হে আল্লাহ! মুহাম্মদ (সা.) ও তাঁর বংশধরদের ওপর বরকত বর্ষণ করুন, যেমন আপনি ইব্রাহিম (আ.) ও তাঁর বংশধরদের ওপর বরকত বর্ষণ করেছিলেন।"),
                    DuaItem("৭. দোআয়ে মাসূরা", "اللَّهُمَّ إِنِّي ظَلَمْتُ نَفْسِي ظُلْمًا كَثِيرًا وَلَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ، فَاغْفِرْ لِي مَغْفِرَةً مِنْ عِنْدِكَ وَارْحَمْنِي إِنَّكَ أَنْتَ الْغَفُورُ الرَّحِيمُ", "আল্লাহুম্মা ইন্নি জালামতু নাফসি জুলমান কাসীরাঁও ওয়ালা ইয়াগফিরুজ জুনুবা ইল্লা আনতা, ফাগফির লী মাগফিরাতাম মিন ইনদিকা ওয়ারহামনী, ইন্নাকা আনতাল গাফুরুর রাহীম।", "হে আল্লাহ! আমি আমার নিজের ওপর অনেক জুলুম করেছি। আপনি ছাড়া গুনাহ ক্ষমা করার কেউ নেই। অতএব আপনার পক্ষ থেকে আমাকে বিশেষ ক্ষমা দান করুন এবং আমার ওপর দয়া করুন। নিশ্চয়ই আপনি পরম ক্ষমাশীল ও দয়ালু।"),
                    DuaItem("৮. দোআয়ে কুনূত (বিতর নামাজের জন্য)", "اللَّهُمَّ إِنَّا نَسْتَعِينُكَ وَنَسْتَغْفِرُكَ وَنُؤْمِنُ بِكَ وَنَتَوَكَّلُ عَلَيْكَ وَنُثْنِي عَلَيْكَ الْخَيْرَ وَنَشْكُرُكَ وَلَا نَكْفُرُكَ وَنَخْلَعُ وَنَتْرُكُ مَنْ يَفْجُرُكَ، اللَّهُمَّ إِيَّاكَ نَعْبُدُ وَلَكَ نُصَلِّي وَنَسْجُدُ وَإِلَيْكَ نَسْعَىٰ وَنَحْفِدُ وَنَرْجُو رَحْمَتَكَ وَنَخْشَىٰ عَذَابَكَ إِنَّ عَذَابَكَ بِالْكُفَّارِ مُلْحِقٌ", "আল্লাহুম্মা ইন্না নাস্তা'ঈনুকা ওয়া নাস্তাগফিরুকা ওয়া নু'মিনু বিকা ওয়া নাতাওয়াক্কালু 'আলাইকা ওয়া নুছনী 'আলাইকাল খাইর, ওয়া নাশ্কুরুকা ওয়ালা নাকফুরুকা, ওয়া নাখলা'উ ওয়া নাতরুকু মাইঁ ইয়াফজুরুক। আল্লাহুম্মা ইয়্যাকা না'বুদু ওয়ালাকা নুসাল্লী ওয়া নাসজুদু, ওয়া ইলাইকা নাস'আ ওয়া নাহফিদু, ওয়া নারজু রাহমাতাকা ওয়া নাখশা আজাবাক, ইন্না আজাবাকা বিল কুফফারি মুলহিক্ব।", "হে আল্লাহ! আমরা আপনার নিকট সাহায্য প্রার্থনা করছি, আপনার নিকট ক্ষমা ভিক্ষা করছি, আপনার ওপর ঈমান রাখছি এবং আপনার ওপর ভরসা করছি। আমরা আপনার সর্বোত্তম প্রশংসা করছি, আপনার কৃতজ্ঞতা প্রকাশ করছি এবং আপনার অকৃতজ্ঞ হচ্ছি না। যারা আপনার নাফরমানি করে আমরা তাদের সম্পর্ক ত্যাগ করছি। হে আল্লাহ! আমরা কেবল আপনারই ইবাদত করি, আপনার জন্যই নামাজ পড়ি ও সিজদা করি। আমরা আপনার রহমতের আশা করি এবং আপনার শাস্তিকে ভয় করি। নিশ্চয়ই আপনার শাস্তি কাফেরদের ওপর আপতিত হবে।"),
                    DuaItem("৯. সিজদায়ে সাহুর নিয়ম ও দোয়া", "سَجَدَ وَجْهِيَ لِلَّذِي خَلَقَهُ وَشَقَّ سَمْعَهُ وَبَصَرَهُ بِحَوْلِهِ وَقُوَّتِهِ", "সাজাদা ওয়াজহিয়া লিল্লাজি খালাকাহু ওয়া শাক্কা সাম'আহু ওয়া বাসারাহু বিহাউলিহী ওয়া কুওওয়াতিহী।", "নামাজে ওয়াজিব ছুটে গেলে শেষ বৈঠকে তাশাহহুদ পড়ে ডানে এক সালাম ফিরিয়ে দুটি অতিরিক্ত সিজদা করে পুনরায় তাশাহহুদ, দরূদ ও দোয়া মাসূরা পড়ে উভয় দিকে সালাম ফেরাতে হয়।")
                ) else listOf(
                    DuaItem("1. Thana (Opening of Salah)", "سُبْحَانَكَ اللَّهُمَّ وَبِحَمْدِكَ وَتَبَارَكَ اسْمُكَ وَتَعَالَىٰ جَدُّكَ وَلَا إِلٰهَ غَيْرُكَ", "Subhanaka Allahumma wa bihamdika wa tabarakasmuka wa ta'ala jadduka wa la ilaha ghairuk.", "Glory be to You, O Allah, and praise. Blessed is Your Name and exalted is Your Majesty, and there is no deity besides You."),
                    DuaItem("2. Tasbih of Ruku", "سُبْحَانَ رَبِّيَ الْعَظِيمِ", "Subhana Rabbiyal 'Azim.", "Glory be to my Lord, the Magnificent."),
                    DuaItem("3. Standing from Ruku (Qauma)", "رَبَّنَا لَكَ الْحَمْدُ حَمْدًا كَثِيرًا طَيِّبًا مُبَارَكًا فِيهِ", "Rabbana lakal hamd, hamdan kathiran tayyiban mubarakan feeh.", "Our Lord, to You belongs all praise, abundant, pure and blessed praise."),
                    DuaItem("4. Tasbih of Sujood", "سُبْحَانَ رَبِّيَ الْأَعْلَىٰ", "Subhana Rabbiyal A'la.", "Glory be to my Lord, the Most High."),
                    DuaItem("5. Tashahhud (Attahiyyat)", "التَّحِيَّاتُ لِلّٰهِ وَالصَّلَوَاتُ وَالطَّيِّبَاتُ، السَّلَامُ عَلَيْكَ أَيُّهَا النَّبِيُّ وَرَحْمَةُ اللهِ وَبَرَكَاتُهُ، السَّلَامُ عَلَيْنَا وَعَلَىٰ عِبَادِ اللهِ الصَّالِحِينَ، أَشْهَدُ أَنْ لَا إِلٰهَ إِلَّا اللهُ وَأَشْهَدُ أَنَّ مُحَمَّدًا عَبْدُهُ وَرَسُولُهُ", "Attahiyyatu lillahi was-salawatu wat-tayyibat, as-salamu 'alaika ayyuhan-Nabiyyu wa rahmatullahi wa barakatuh, as-salamu 'alaina wa 'ala 'ibadillahis-salihin, ash-hadu an la ilaha illallahu wa ash-hadu anna Muhammadan 'abduhu wa rasuluh.", "All compliments, prayers and pure words are due to Allah. Peace be upon you, O Prophet, and the mercy of Allah and His blessings. Peace be upon us and upon the righteous servants of Allah. I bear witness that there is no true god except Allah, and I bear witness that Muhammad is His servant and messenger."),
                    DuaItem("6. Durood Ibrahim", "اللَّهُمَّ صَلِّ عَلَىٰ مُحَمَّدٍ وَعَلَىٰ آلِ مُحَمَّدٍ كَمَا صَلَّيْتَ عَلَىٰ إِبْرَاهِيمَ وَعَلَىٰ آلِ إِبْرَاهِيمَ إِنَّكَ حَمِيدٌ مَجِيدٌ", "Allahumma salli 'ala Muhammadin wa 'ala ali Muhammad, kama sallaita 'ala Ibrahima wa 'ala ali Ibrahim, innaka Hamidum Majid.", "O Allah, send blessings upon Muhammad and upon the family of Muhammad, as You sent blessings upon Ibrahim and upon the family of Ibrahim. Indeed, You are Praiseworthy and Glorious."),
                    DuaItem("7. Dua Masura", "اللَّهُمَّ إِنِّي ظَلَمْتُ نَفْسِي ظُلْمًا كَثِيرًا وَلَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ، فَاغْفِرْ لِي مَغْفِرَةً مِنْ عِنْدِكَ وَارْحَمْنِي إِنَّكَ أَنْتَ الْغَفُورُ الرَّحِيمُ", "Allahumma inni zalamtu nafsi zulman kathiran wa la yaghfirudh-dhunuba illa Anta, faghfir li maghfiratan min 'indika warhamni, innaka Antal-Ghafurur-Rahim.", "O Allah, I have wronged myself greatly and no one forgives sins except You. So grant me forgiveness from Yourself and have mercy upon me. Indeed, You are the Forgiving, the Merciful."),
                    DuaItem("8. Dua Qunut (For Witr)", "اللَّهُمَّ إِنَّا نَسْتَعِينُكَ وَنَسْتَغْفِرُكَ وَنُؤْمِنُ بِكَ وَنَتَوَكَّلُ عَلَيْكَ وَنُثْنِي عَلَيْكَ الْخَيْرَ", "Allahumma inna nasta'inuka wa nastaghfiruka wa nu'minu bika wa natawakkalu 'alaika wa nuthni 'alaikal-khair.", "O Allah, we seek Your help and Your forgiveness, we believe in You, put our trust in You, and praise You for all good."),
                    DuaItem("9. Sajdah Sahw Rule & Dua", "سَجَدَ وَجْهِيَ لِلَّذِي خَلَقَهُ وَشَقَّ سَمْعَهُ وَبَصَرَهُ", "Sajada wajhiya lilladhi khalaqahu wa shaqqa sam'ahu wa basarahu.", "If a mandatory act (Wajib) is missed unintentionally in prayer, perform two prostrations of forgetfulness after one Taslim to the right, then repeat Tashahhud, Durood, and complete with Taslim.")
                )

                itemsIndexed(duas) { _, dua ->
                    DuaDisplayCard(item = dua, isBangla = isBangla)
                }
            }
        }
    }
}

// =======================================================================
// RAK'AH TABLE COMPONENT (5 Prayers + Jumu'ah)
// =======================================================================
@Composable
fun RakahTableComponent(
    isBangla: Boolean,
    onPrayerClick: (PrayerRakahInfo) -> Unit
) {
    val colors = FinancyTheme.colors
    val isDark = colors.isDark

    val prayers = remember {
        listOf(
            PrayerRakahInfo(
                nameBn = "ফজর",
                nameEn = "Fajr",
                icon = "🌅",
                beforeFarz = "২ সুন্নাত",
                isBeforeGhair = false,
                farz = "২ ফরজ",
                afterFarz = "—",
                naflOrWitr = "—",
                isWitr = false,
                totalRakahsBn = "৪ রাকাত",
                totalRakahsEn = "4 Rakahs",
                isJumuah = false,
                virtuesBn = "ফজরের দুই রাকাত সুন্নাত দুনিয়া ও দুনিয়ার মাঝে যা কিছু আছে তার চেয়ে উত্তম। (সহিহ মুসলিম: ৭২৫)। যে ব্যক্তি ফজরের নামাজ জামাতে আদায় করে সে যেন সারা রাত জেগে নামাজ পড়ল। (সহিহ মুসলিম: ৬৫৬)।",
                virtuesEn = "The two rakahs of Sunnah before Fajr are better than the entire world and everything in it. (Sahih Muslim: 725).",
                niyyahBn = "আমি আল্লাহর সন্তুষ্টির জন্য কিবলামুখী হয়ে আজকের ফজরের ২ রাকাত সুন্নাত / ২ রাকাত ফরজ নামাজ আদায়ের নিয়ত করছি।",
                niyyahEn = "I intend to pray the 2 Sunnah / 2 Fardh of Fajr facing the Qibla for the sake of Allah.",
                rulesBn = "ফজরের সুন্নাত অত্যন্ত গুরুত্বপূর্ণ ও সুন্নাতে মুয়াক্কাদাহ। ওয়াক্তের শেষ সীমানা সূর্যোদয়ের পূর্ব মুহূর্ত পর্যন্ত।",
                rulesEn = "The Sunnah of Fajr is strongly emphasized (Sunnah Mu'akkadah). Must be completed before sunrise."
            ),
            PrayerRakahInfo(
                nameBn = "যোহর",
                nameEn = "Dhuhr",
                icon = "☀️",
                beforeFarz = "৪ সুন্নাত",
                isBeforeGhair = false,
                farz = "৪ ফরজ",
                afterFarz = "২ সুন্নাত",
                naflOrWitr = "২ নফল",
                isWitr = false,
                totalRakahsBn = "১২ রাকাত",
                totalRakahsEn = "12 Rakahs",
                isJumuah = false,
                virtuesBn = "যে ব্যক্তি যোহরের পূর্বে চার রাকাত এবং পরে চার রাকাত (২ সুন্নাত + ২ নফল) যত্নসহকারে পড়ে, আল্লাহ তার জন্য জাহান্নামের আগুন হারাম করে দেন। (তিরমিজি: ৪২৮)।",
                virtuesEn = "Whoever performs 4 rakahs before Dhuhr and 4 rakahs after it, Allah makes him forbidden to Hellfire. (Tirmidhi: 428).",
                niyyahBn = "যোহরের ৪ রাকাত সুন্নাত, ৪ রাকাত ফরজ, ২ রাকাত সুন্নাত ও ২ রাকাত নফল পৃথকভাবে আদায়ের নিয়ত করা।",
                niyyahEn = "Intend to pray the 4 Sunnah, 4 Fardh, 2 Sunnah and 2 Nafl of Dhuhr for Allah.",
                rulesBn = "যোহরের আগের চার রাকাত এক সালামে আদায় করা সুন্নাতে মুয়াক্কাদাহ। জোহরের ওয়াক্ত সূর্য মাথার ওপর থেকে পশ্চিমে ঢলে পড়ার পর শুরু হয়।",
                rulesEn = "The 4 Sunnah before Dhuhr are Sunnah Mu'akkadah prayed with one Taslim."
            ),
            PrayerRakahInfo(
                nameBn = "জুমু'আ",
                nameEn = "Jumu'ah",
                icon = "🕌",
                beforeFarz = "৪ কাবলাল",
                isBeforeGhair = false,
                farz = "২ ফরজ",
                afterFarz = "৪ বা'দাল",
                naflOrWitr = "২সু.+২ন.",
                isWitr = false,
                totalRakahsBn = "১৪ রাকাত",
                totalRakahsEn = "14 Rakahs",
                isJumuah = true,
                virtuesBn = "দিনসমূহের মধ্যে সর্বোত্তম দিন হলো জুমু'আর দিন। এ দিনে এমন একটি মুহূর্ত রয়েছে যখন বান্দা কোনো নেক দোয়া করলে আল্লাহ তা ফিরিয়ে দেন না। (বুখারি: ৮৯৩, মুসলিম: ৮৫৫)।",
                virtuesEn = "The best day on which the sun has risen is Friday. It has an hour during which any supplication is accepted. (Bukhari: 893).",
                niyyahBn = "জুমু'আর দিনে ৪ রাকাত কাবলাল জুমু'আ, খুতবার পর ইমামের পেছনে ২ রাকাত ফরজ এবং এরপর ৪ রাকাত বা'দাল জুমু'আ পড়ার নিয়ত করা।",
                niyyahEn = "Intend to pray 4 Qablal Jumu'ah, 2 Fardh behind Imam after Khutbah, and 4 Ba'dal Jumu'ah.",
                rulesBn = "প্রাপ্তবয়স্ক পুরুষদের জন্য জুমু'আ জামাতে আদায় ফরজ। খুতবা চলাকালীন নিঃশব্দে মনোযোগ দিয়ে শোনা ওয়াজিব।",
                rulesEn = "Jumu'ah is mandatory for adult men. Listening silently to the Khutbah is strictly obligatory."
            ),
            PrayerRakahInfo(
                nameBn = "আসর",
                nameEn = "Asr",
                icon = "🌤️",
                beforeFarz = "৪ সুন্নাত",
                isBeforeGhair = true,
                farz = "৪ ফরজ",
                afterFarz = "—",
                naflOrWitr = "—",
                isWitr = false,
                totalRakahsBn = "৮ রাকাত",
                totalRakahsEn = "8 Rakahs",
                isJumuah = false,
                virtuesBn = "আল্লাহর রাসূল (সা.) বলেছেন: যে ব্যক্তি আসরের পূর্বে চার রাকাত সুন্নাত পড়ে, আল্লাহ তার ওপর রহম করেন। (তিরমিজি: ৪৩০)। যে আসরের নামাজ তরক করে তার আমল বরবাদ হয়ে যায়। (বুখারি: ৫৫৩)।",
                virtuesEn = "May Allah have mercy on the one who prays 4 rakahs before Asr. (Tirmidhi: 430). Whoever misses Asr, his deeds are ruined. (Bukhari: 553).",
                niyyahBn = "আসরের ৪ রাকাত সুন্নাতে গাইরে মুয়াক্কাদাহ ও ৪ রাকাত ফরজ আদায়ের সংকল্প।",
                niyyahEn = "Intend to pray 4 Sunnah Ghair Mu'akkadah and 4 Fardh of Asr.",
                rulesBn = "আসরের পূর্বের ৪ রাকাত সুন্নাত গাইরে মুয়াক্কাদাহ (নফল)। আসরের পর সূর্যাস্ত পর্যন্ত কোনো নফল নামাজ পড়া নিষিদ্ধ।",
                rulesEn = "The 4 Sunnah before Asr are optional/recommended. No voluntary prayer is allowed between Asr and Maghrib."
            ),
            PrayerRakahInfo(
                nameBn = "মাগরিব",
                nameEn = "Maghrib",
                icon = "🌇",
                beforeFarz = "—",
                isBeforeGhair = false,
                farz = "৩ ফরজ",
                afterFarz = "২ সুন্নাত",
                naflOrWitr = "২ নফল",
                isWitr = false,
                totalRakahsBn = "৭ রাকাত",
                totalRakahsEn = "7 Rakahs",
                isJumuah = false,
                virtuesBn = "মাগরিবের পর দুই রাকাত সুন্নাতের পাশাপাশি নফল (আউয়াবিন) নামাজ পড়ার বিশেষ ফজিলত হাদিসে বর্ণিত হয়েছে। (তিরমিজি: ৪৩৫)।",
                virtuesEn = "Maghrib must be prayed promptly after sunset. Voluntary prayer after Maghrib brings vast blessings. (Tirmidhi: 435).",
                niyyahBn = "মাগরিবের ৩ রাকাত ফরজ, ২ রাকাত সুন্নাতে মুয়াক্কাদাহ ও ২ রাকাত নফল আদায়ের সংকল্প।",
                niyyahEn = "Intend to pray 3 Fardh, 2 Sunnah Mu'akkadah and 2 Nafl of Maghrib.",
                rulesBn = "সূর্যাস্তের পরপরই মাগরিব বিলম্ব না করে দ্রুত আদায় করা মুস্তাহাব।",
                rulesEn = "It is strongly recommended to pray Maghrib immediately upon sunset without delay."
            ),
            PrayerRakahInfo(
                nameBn = "ইশা",
                nameEn = "Isha",
                icon = "🌙",
                beforeFarz = "৪ সুন্নাত",
                isBeforeGhair = true,
                farz = "৪ ফরজ",
                afterFarz = "২ সুন্নাত",
                naflOrWitr = "৩ বিতর",
                isWitr = true,
                totalRakahsBn = "১৭ রাকাত",
                totalRakahsEn = "17 Rakahs",
                isJumuah = false,
                virtuesBn = "যে ব্যক্তি ইশার নামাজ জামাতে পড়ে সে যেন অর্ধরাত ইবাদত করল। (সহিহ মুসলিম: ৬৫৬)। বিতর নামাজ আল্লাহর অত্যন্ত প্রিয়, তোমরা বিতর আদায় করো। (তিরমিজি: ৪৫৩)।",
                virtuesEn = "Whoever prays Isha in congregation has prayed as if for half the night. (Sahih Muslim: 656).",
                niyyahBn = "ইশার ৪ রাকাত ফরজ, ২ রাকাত সুন্নাত এবং ৩ রাকাত বিতর ওয়াজিব নামাজ আদায়ের নিয়ত করা।",
                niyyahEn = "Intend to pray 4 Fardh, 2 Sunnah, and 3 Witr Wajib of Isha for the sake of Allah.",
                rulesBn = "বিতর নামাজ হানাফী মাযহাবে ওয়াজিব। ৩ রাকাত এক সালামে পড়া হয় এবং ৩য় রাকাতে সূরা মেলানোর পর তাকবীর বলে হাত উঠিয়ে দোআয়ে কুনূত পড়তে হয়। মোট ১৭ রাকাতের মধ্যে ৪ সুন্নাত + ৪ ফরজ + ২ সুন্নাত + ২ নফল + ৩ বিতর + ২ নফল রয়েছে।",
                rulesEn = "Witr is Wajib in the Hanafi school. Prayed in 3 rakahs with Takbir of Qunut and Dua Qunut in the 3rd rakah."
            )
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
        border = BorderStroke(1.2.dp, AmberAccentDark.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("📊", fontSize = 18.sp)
                    Text(
                        text = if (isBangla) "৫ ওয়াক্ত ও জুমু'আর রাকাত তালিকা" else "Daily Prayers & Jumu'ah Rak'ah Table",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.5.sp,
                        color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = colors.emerald.copy(alpha = if (isDark) 0.22f else 0.15f),
                    border = BorderStroke(1.dp, colors.emerald.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = if (isBangla) "সহিহ চার্ট" else "Authentic",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = colors.emerald
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Horizontally Scrollable Table
            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
            ) {
                Column(modifier = Modifier.width(445.dp)) {
                    // Header Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBangla) "ওয়াক্ত" else "Prayer",
                            modifier = Modifier.width(82.dp),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                        )
                        Text(
                            text = if (isBangla) "ফরজের আগে" else "Before Fardh",
                            modifier = Modifier.width(76.dp),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                        )
                        Text(
                            text = if (isBangla) "ফরজ" else "Fardh",
                            modifier = Modifier.width(66.dp),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                        )
                        Text(
                            text = if (isBangla) "ফরজের পরে" else "After Fardh",
                            modifier = Modifier.width(76.dp),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                        )
                        Text(
                            text = if (isBangla) "বিতর/নফল" else "Witr/Nafl",
                            modifier = Modifier.width(76.dp),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                        )
                        Text(
                            text = if (isBangla) "মোট" else "Total",
                            modifier = Modifier.width(66.dp),
                            textAlign = TextAlign.End,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                        )
                    }

                    HorizontalDivider(
                        thickness = 1.dp,
                        color = if (isDark) Color(0xFF22352C) else Color(0xFFCBD5E1)
                    )

                    // Prayer Rows
                    prayers.forEach { prayer ->
                        val rowBg = if (prayer.isJumuah) {
                            colors.emerald.copy(alpha = if (isDark) 0.14f else 0.10f)
                        } else Color.Transparent

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPrayerClick(prayer) },
                            color = rowBg,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 7.dp, horizontal = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Column 1: Waqt Name
                                Row(
                                    modifier = Modifier.width(82.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(prayer.icon, fontSize = 13.sp)
                                    Text(
                                        text = if (isBangla) prayer.nameBn else prayer.nameEn,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.5.sp,
                                        color = if (prayer.isJumuah) colors.emerald else if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                                    )
                                }

                                // Column 2: Before Farz
                                Box(
                                    modifier = Modifier.width(76.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (prayer.beforeFarz != "—") {
                                        PillBadge(
                                            text = prayer.beforeFarz,
                                            type = if (prayer.isBeforeGhair) PillType.GHAIR else PillType.SUNNAH,
                                            isDark = isDark
                                        )
                                    } else {
                                        Text("—", fontWeight = FontWeight.Bold, color = if (isDark) Color(0xFF64748B) else Color(0xFF64748B), fontSize = 13.sp)
                                    }
                                }

                                // Column 3: Farz
                                Box(
                                    modifier = Modifier.width(66.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    PillBadge(
                                        text = prayer.farz,
                                        type = PillType.FARZ,
                                        isDark = isDark
                                    )
                                }

                                // Column 4: After Farz
                                Box(
                                    modifier = Modifier.width(76.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (prayer.afterFarz != "—") {
                                        PillBadge(
                                            text = prayer.afterFarz,
                                            type = PillType.SUNNAH,
                                            isDark = isDark
                                        )
                                    } else {
                                        Text("—", fontWeight = FontWeight.Bold, color = if (isDark) Color(0xFF64748B) else Color(0xFF64748B), fontSize = 13.sp)
                                    }
                                }

                                // Column 5: Witr/Nafl
                                Box(
                                    modifier = Modifier.width(76.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (prayer.naflOrWitr != "—") {
                                        PillBadge(
                                            text = prayer.naflOrWitr,
                                            type = if (prayer.isWitr) PillType.WITR else PillType.NAFL,
                                            isDark = isDark
                                        )
                                    } else {
                                        Text("—", fontWeight = FontWeight.Bold, color = if (isDark) Color(0xFF64748B) else Color(0xFF64748B), fontSize = 13.sp)
                                    }
                                }

                                // Column 6: Total Rakahs
                                Box(
                                    modifier = Modifier.width(66.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Text(
                                        text = if (isBangla) prayer.totalRakahsBn else prayer.totalRakahsEn,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.5.sp,
                                        color = if (prayer.isJumuah) colors.emerald else if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                                    )
                                }
                            }
                        }

                        HorizontalDivider(
                            thickness = 0.6.dp,
                            color = if (isDark) Color(0xFF22352C).copy(alpha = 0.5f) else Color(0xFFE2E8F0)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Legend Container (100% High Contrast in both Light & Dark modes)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isDark) Color(0xFF131D18) else Color(0xFFF8FAFC),
                border = BorderStroke(1.2.dp, if (isDark) Color(0xFF22352C) else Color(0xFFCBD5E1)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 9.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LegendItemDot(color = Color(0xFF2563EB), label = if (isBangla) "ফরজ" else "Fardh", isDark = isDark)
                    LegendItemDot(color = Color(0xFF059669), label = if (isBangla) "সুন্নাত" else "Sunnah", isDark = isDark)
                    LegendItemDot(color = Color(0xFFD97706), label = if (isBangla) "ওয়াজিব" else "Wajib", isDark = isDark)
                    LegendItemDot(color = Color(0xFF0891B2), label = if (isBangla) "গাইরে মুয়াক্কাদাহ" else "Ghair", isDark = isDark)
                    LegendItemDot(color = Color(0xFF475569), label = if (isBangla) "নফল" else "Nafl", isDark = isDark)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer Tip (High Contrast Deep Forest Emerald)
            Text(
                text = if (isBangla) "💡 যেকোনো নামাজের সারিতে ট্যাপ করে ফজিলত, নিয়ত ও বিস্তারিত নিয়ম দেখুন"
                else "💡 Click on any prayer row to view virtues, niyyah and detailed rules",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = if (isDark) Color(0xFF34D399) else Color(0xFF065F46)
            )
        }
    }
}

// --- Pill Types & Badges ---
enum class PillType {
    FARZ, SUNNAH, GHAIR, WITR, NAFL
}

@Composable
fun PillBadge(text: String, type: PillType, isDark: Boolean) {
    val (bg, border, fg) = when (type) {
        PillType.FARZ -> Triple(
            if (isDark) Color(0xFF1E3A8A).copy(alpha = 0.45f) else Color(0xFFDBEAFE),
            if (isDark) Color(0xFF3B82F6).copy(alpha = 0.6f) else Color(0xFF93C5FD),
            if (isDark) Color(0xFF93C5FD) else Color(0xFF1D4ED8)
        )
        PillType.SUNNAH -> Triple(
            if (isDark) Color(0xFF064E3B).copy(alpha = 0.45f) else Color(0xFFD1FAE5),
            if (isDark) Color(0xFF10B981).copy(alpha = 0.6f) else Color(0xFF6EE7B7),
            if (isDark) Color(0xFF6EE7B7) else Color(0xFF047857)
        )
        PillType.GHAIR -> Triple(
            if (isDark) Color(0xFF164E63).copy(alpha = 0.45f) else Color(0xFFCFFAFE),
            if (isDark) Color(0xFF06B6D4).copy(alpha = 0.6f) else Color(0xFF67E8F9),
            if (isDark) Color(0xFF67E8F9) else Color(0xFF0E7490)
        )
        PillType.WITR -> Triple(
            if (isDark) Color(0xFF78350F).copy(alpha = 0.45f) else Color(0xFFFEF3C7),
            if (isDark) Color(0xFFF59E0B).copy(alpha = 0.6f) else Color(0xFFFCD34D),
            if (isDark) Color(0xFFFCD34D) else Color(0xFFB45309)
        )
        PillType.NAFL -> Triple(
            if (isDark) Color(0xFF334155).copy(alpha = 0.45f) else Color(0xFFF1F5F9),
            if (isDark) Color(0xFF64748B).copy(alpha = 0.5f) else Color(0xFFCBD5E1),
            if (isDark) Color(0xFFCBD5E1) else Color(0xFF334155)
        )
    }

    Surface(
        shape = RoundedCornerShape(7.dp),
        color = bg,
        border = BorderStroke(1.dp, border)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.5.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = fg
        )
    }
}

@Composable
fun LegendItemDot(color: Color, label: String, isDark: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF0F172A)
        )
    }
}

@Composable
private fun GuideStepCard(stepNum: String, title: String, desc: String) {
    val colors = FinancyTheme.colors
    val isDark = colors.isDark

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
        border = BorderStroke(1.dp, if (isDark) colors.border else Color(0xFFCBD5E1))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = CircleShape,
                color = colors.emerald.copy(alpha = if (isDark) 0.20f else 0.15f),
                border = BorderStroke(1.dp, colors.emerald.copy(alpha = 0.4f)),
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = stepNum,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = colors.emerald
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun AccordionCard(title: String, content: @Composable () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val colors = FinancyTheme.colors
    val isDark = colors.isDark

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
        border = BorderStroke(1.dp, if (isDark) colors.border else Color(0xFFCBD5E1))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.textPrimary
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = colors.textSecondary
                )
            }
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    content()
                }
            }
        }
    }
}

@Composable
private fun DuaDisplayCard(item: DuaItem, isBangla: Boolean) {
    val colors = FinancyTheme.colors
    val isDark = colors.isDark

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
        border = BorderStroke(1.dp, if (isDark) colors.border else Color(0xFFCBD5E1))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = item.title,
                fontWeight = FontWeight.ExtraBold,
                color = colors.violet,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = item.arabic,
                fontFamily = AmiriFontFamily,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth(),
                lineHeight = 34.sp,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "${if (isBangla) "উচ্চারণ: " else "Pronunciation: "}${item.pron}",
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 18.sp,
                color = if (isDark) Color(0xFF34D399) else Color(0xFF059669)
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "${if (isBangla) "অর্থ: " else "Meaning: "}${item.meaning}",
                fontSize = 12.sp,
                color = colors.textSecondary,
                lineHeight = 18.sp
            )
        }
    }
}
