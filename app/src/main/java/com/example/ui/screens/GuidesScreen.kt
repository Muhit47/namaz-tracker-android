package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuidesScreen(
    isBangla: Boolean = false,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedSection by remember { mutableStateOf(0) } // 0 = Prayer Steps, 1 = Wudu, 2 = Dua Vault
    val colors = FinancyTheme.colors

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBangla) "নামাজ ও ওজু শিক্ষা গাইড" else "Salah & Wudu Guide",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.surfaceCard)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Financy TabRow
            item {
                Spacer(modifier = Modifier.height(10.dp))
                PrimaryTabRow(
                    selectedTabIndex = selectedSection,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = colors.surfaceCard,
                    contentColor = colors.neonGreen
                ) {
                    Tab(
                        selected = selectedSection == 0,
                        onClick = { selectedSection = 0 },
                        text = {
                            Text(
                                if (isBangla) "নামাজের নিয়ম" else "Salah Steps",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp,
                                color = if (selectedSection == 0) colors.neonGreen else colors.textSecondary
                            )
                        }
                    )
                    Tab(
                        selected = selectedSection == 1,
                        onClick = { selectedSection = 1 },
                        text = {
                            Text(
                                if (isBangla) "ওজু ও পবিত্রতা" else "Wudu & Purity",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp,
                                color = if (selectedSection == 1) colors.neonGreen else colors.textSecondary
                            )
                        }
                    )
                    Tab(
                        selected = selectedSection == 2,
                        onClick = { selectedSection = 2 },
                        text = {
                            Text(
                                if (isBangla) "দোয়া ভান্ডার" else "Dua Vault",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp,
                                color = if (selectedSection == 2) colors.neonGreen else colors.textSecondary
                            )
                        }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // SECTION 0: নামাজের নিয়ম
            if (selectedSection == 0) {
                item {
                    Text(
                        text = if (isBangla) "📖 সহিহ নামাজের ধারাবাহিক নিয়ম" else "📖 Step-by-Step Authentic Salah Guide",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.neonGreen
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
                    "৮. শেষ বৈঠক ও সালাম" to "তাশাহহুদ (আত্তাহিয়্যাতু), দরূদ শরীফ ও দোয়া মাছুরা পড়ে ডানে ও বামে 'আসসালামু আলাইকুম ওয়া রাহমাতুল্লাহ' বলে নামাজ শেষ করা। (বুখারি: ৮৩১)"
                ) else listOf(
                    "1. Facing Qibla & Intention (Niyyah)" to "Stand upright facing Qibla in clean clothes and make intention in the heart for the specific prayer. (Bukhari: 1)",
                    "2. Takbir al-Tahrimah" to "Raise both hands to ears or shoulders saying 'Allahu Akbar' and fold hands below navel or on chest. (Bukhari: 736)",
                    "3. Reciting Thana & Surah Al-Fatiha" to "Recite Thana ('Subhanaka Allahumma...'), then seeking refuge and reciting Surah Al-Fatiha followed by another Surah. (Tirmidhi: 243)",
                    "4. Performing Ruku (Bowing)" to "Say 'Allahu Akbar' and bow down with back straight, reciting 'Subhana Rabbiyal Azim' at least 3 times. (Abu Dawud: 870)",
                    "5. Qauma (Standing after Ruku)" to "Rise saying 'Sami'Allahu liman hamidah' and say 'Rabbana lakal hamd'. (Bukhari: 795)",
                    "6. Performing Sujood (Prostration)" to "Prostrate saying 'Allahu Akbar' touching forehead, nose, palms, knees and toes to ground, reciting 'Subhana Rabbiyal A'la' 3 times. (Muslim: 226)",
                    "7. Jalsa (Sitting between Sujood)" to "Sit upright between the two prostrations saying 'Rabbighfirli', then perform the second prostration. (Abu Dawud: 874)",
                    "8. Tashahhud & Taslim" to "Sit for Tashahhud, Durood Ibrahim and Dua Masura, then turn head right and left saying 'Assalamu Alaikum wa Rahmatullah'. (Bukhari: 831)"
                )

                items(steps.size) { idx ->
                    val (title, desc) = steps[idx]
                    GuideStepCard(stepNum = (idx + 1).toString(), title = title, desc = desc)
                }

                // Rakah table
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = if (isBangla) "📊 ৫ ওয়াক্তের রাকাতের তালিকা" else "📊 Summary of Daily Prayer Rakahs",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.neonGreen
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    RakahSummaryCard(isBangla = isBangla)
                }

                // Gender differences
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

            // SECTION 1: ওজু ও পবিত্রতা
            if (selectedSection == 1) {
                item {
                    Text(
                        text = if (isBangla) "💧 ওজু ও পবিত্রতার সুন্নাহ নিয়ম" else "💧 Sunnah Method of Wudu & Purification",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.neonGreen
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                val wuduFardh = if (isBangla) listOf(
                    "১. সমস্ত মুখমণ্ডল ধোয়া" to "কপালের চুলের গোড়া থেকে থুতনির নিচ এবং এক কানের লতি থেকে অন্য কান পর্যন্ত ১ বার ধোয়া ফরজ (৩ বার সুন্নাত)। (সূরা মায়িদা: ৬)",
                    "২. দুই হাত কনুইসহ ধোয়া" to "ডান হাত ও বাম হাত আঙ্গুলের ডগা থেকে কনুই পর্যন্ত ভালোভাবে ধোয়া। (সূরা মায়িদা: ৬)",
                    "৩. মাথার চারভাগের একভাগ মাসেহ করা" to "ভেজা হাত দিয়ে মাথা মাসেহ করা (পুরো মাথা মাসেহ করা সুন্নাত)। (বুখারি: ১৮৫)",
                    "৪. দুই পা টাখনুসহ ধোয়া" to "উভয় পা আঙ্গুল থেকে গোড়ালি ও টাখনু পর্যন্ত ভালোভাবে ধোয়া। (বুখারি: ১৬৩)"
                ) else listOf(
                    "1. Washing the entire face" to "Wash from hairline to chin and earlobe to earlobe once mandatory (3 times Sunnah). (Surah Al-Ma'idah: 6)",
                    "2. Washing both hands including elbows" to "Thoroughly wash right then left hand from fingertips up to elbows. (Surah Al-Ma'idah: 6)",
                    "3. Wiping (Masah) over head" to "Wipe over head with wet hands (wiping entire head is Sunnah). (Bukhari: 185)",
                    "4. Washing both feet including ankles" to "Thoroughly wash both feet from toes to ankles. (Bukhari: 163)"
                )

                items(wuduFardh.size) { idx ->
                    val (title, desc) = wuduFardh[idx]
                    GuideStepCard(stepNum = (idx + 1).toString(), title = title, desc = desc)
                }

                // ওজুর পরের দোয়া
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                        border = BorderStroke(1.dp, colors.neonGreen.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = if (isBangla) "✨ ওজু শেষের সর্বশ্রেষ্ঠ দোয়া (জান্নাতের ৮টি দরজা খোলার ফজিলত)"
                                else "✨ Virtuous Dua After Completing Wudu (Opens 8 Gates of Jannah)",
                                fontWeight = FontWeight.Bold,
                                color = colors.neonGreen,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "أَشْهَدُ أَنْ لَا إِلٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ وَأَشْهَدُ أَنَّ مُحَمَّدًا عَبْدُهُ وَرَسُولُهُ",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth(),
                                color = colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isBangla) "উচ্চারণ: আশহাদু আল-লা ইলাহা ইল্লাল্লাহু ওয়াহদাহু লা শারীকা লাহু, ওয়া আশহাদু আন্না মুহাম্মাদান আবদুহু ওয়া রাসুলুহু।"
                                else "Transliteration: Ash-hadu allaa ilaaha illallaahu wahdahu laa shareeka lahu, wa ash-hadu anna Muhammadan 'abduhu wa Rasooluhu.",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.neonGreen
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isBangla) "অর্থ: আমি সাক্ষ্য দিচ্ছি আল্লাহ ছাড়া কোনো উপাস্য নেই, তিনি একক; এবং মুহাম্মদ (সা.) তাঁর বান্দা ও রাসুল। [সহীহ মুসলিম: ২৩৪]"
                                else "Meaning: I bear witness that there is no deity worthy of worship except Allah alone, with no partner, and I bear witness that Muhammad is His servant and Messenger. [Sahih Muslim: 234]",
                                fontSize = 12.sp,
                                color = colors.textSecondary
                            )
                        }
                    }
                }

                // ওজু ভঙ্গের কারণসমূহ
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    AccordionCard(
                        title = if (isBangla) "⚠️ ওজু ভঙ্গের প্রধান কারণসমূহ" else "⚠️ Main Nullifiers of Wudu",
                        content = {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(if (isBangla) "১. প্রস্রাব-পায়খানার রাস্তা দিয়ে বায়ু বা কোনো অপবিত্র বস্তু বের হওয়া। (বুখারি: ১৩৫)" else "1. Discharge of wind, urine, or impurities. (Bukhari: 135)", fontSize = 12.5.sp, color = colors.textPrimary)
                                Text(if (isBangla) "২. শরীর থেকে রক্ত, পুঁজ বা পানি বের হয়ে গড়িয়ে পড়া। (দারা কুতনী: ৫৫৩)" else "2. Flowing blood, pus, or fluid from body. (Daraqutni: 553)", fontSize = 12.5.sp, color = colors.textPrimary)
                                Text(if (isBangla) "৩. মুখ ভরে বমি হওয়া। (তিরমিজি: ৮৭)" else "3. Vomiting a mouthful. (Tirmidhi: 87)", fontSize = 12.5.sp, color = colors.textPrimary)
                                Text(if (isBangla) "৪. কোনো কিছুর সাথে হেলান দিয়ে বা শুয়ে ঘুমিয়ে পড়া। (আবু দাউদ: ২০৩)" else "4. Deep sleep while lying down or leaning. (Abu Dawud: 203)", fontSize = 12.5.sp, color = colors.textPrimary)
                                Text(if (isBangla) "৫. অজ্ঞান, মাতাল বা ওষুধজনিত কারণে বুদ্ধি লোপ পাওয়া।" else "5. Loss of consciousness or intoxication.", fontSize = 12.5.sp, color = colors.textPrimary)
                                Text(if (isBangla) "৬. রুকু-সিজদাবিশিষ্ট নামাজে উচ্চস্বরে অট্টহাসি দেওয়া। (দারা কুতনী: ৬১২)" else "6. Laughing loudly during prayer with Ruku/Sujood. (Daraqutni: 612)", fontSize = 12.5.sp, color = colors.textPrimary)
                            }
                        }
                    )
                }

                // তায়াম্মুমের নিয়ম
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    AccordionCard(
                        title = if (isBangla) "🏜️ তায়াম্মুমের সংক্ষিপ্ত নিয়ম" else "🏜️ Summary Guide for Tayammum (Dry Ablution)",
                        content = {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(if (isBangla) "১. পবিত্রতার নিয়ত করে 'বিসমিল্লাহ' বলা।" else "1. Make intention for purification and say 'Bismillah'.", fontSize = 12.5.sp, color = colors.textPrimary)
                                Text(if (isBangla) "২. পবিত্র মাটিতে উভয় হাতের তালু মৃদু মেরে পুরো মুখমণ্ডল একবার মাসেহ করা। (বুখারি: ৩৩৮)" else "2. Strike clean earth with palms and wipe entire face once. (Bukhari: 338)", fontSize = 12.5.sp, color = colors.textPrimary)
                                Text(if (isBangla) "৩. পুনরায় মাটিতে হাত মেরে উভয় হাতের কনুই পর্যন্ত মাসেহ করা। (দারা কুতনী: ৭১২)" else "3. Strike earth again and wipe arms up to elbows. (Daraqutni: 712)", fontSize = 12.5.sp, color = colors.textPrimary)
                            }
                        }
                    )
                }
            }

            // SECTION 2: দোয়া ভান্ডার
            if (selectedSection == 2) {
                item {
                    Text(
                        text = if (isBangla) "🤲 নামাজের সকল প্রয়ােজনীয় দোয়া" else "🤲 Essential Duas for Prayer",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.neonGreen
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                val duas = if (isBangla) listOf(
                    DuaItem("১. ছানা (নামাজের শুরুতে)", "سُبْحَانَكَ اللَّهُمَّ وَبِحَمْدِكَ وَتَبَارَكَ اسْمُكَ وَتَعَالَىٰ جَدُّكَ وَلَا إِلٰهَ غَيْرُكَ", "সুবহানাকা আল্লাহুম্মা ওয়া বিহামদিকা, ওয়া তাবারাকাসমুকা ওয়া তা'আলা জাদ্দুকা, ওয়া লা ইলাহা গাইরুক।", "হে আল্লাহ! আপনার প্রশংসার সাথে পবিত্রতা ঘোষণা করছি, আপনার নাম বরকতময়, আপনার মর্যাদা অতি উচ্চ এবং আপনি ছাড়া কোনো সত্য উপাস্য নেই। [তিরমিজি: ২৪৩]"),
                    DuaItem("২. তাশাহহুদ (আত্তাহিয়্যাতু)", "التَّحِيَّاتُ لِلَّهِ وَالصَّلَوَاتُ وَالطَّيِّبَاتُ، السَّلَامُ عَلَيْكَ أَيُّهَا النَّبِيُّ وَرَحْمَةُ اللَّهِ وَبَرَكَاتُهُ، السَّلَامُ عَلَيْنَا وَعَلَىٰ عِبَادِ اللَّهِ الصَّالِحِينَ، أَشْهَدُ أَنْ لَا إِلٰهَ إِلَّا اللَّهُ وَأَشْهَدُ أَنَّ مُحَمَّدًا عَبْدُهُ وَرَسُولُهُ", "আত্তাহিয়্যাতু লিল্লাহি ওয়াস-সালাওয়াতু ওয়াত-তাইয়্যিবাতু, আসসালামু আলাইকা আইয়্যুহান নাবিয়্যু ওয়া রাহমাতুল্লাহি ওয়া বারাকাতুহু, আসসালামু আলাইনা ওয়া আলা ইবাদিল্লাহিস সালিহীন, আশহাদু আল-লা ইলাহা ইল্লাল্লাহু ওয়া আশহাদু আন্না মুহাম্মাদান আবদুহু ওয়া রাসুলুহু।", "সকল মৌখিক, শারীরিক ও আর্থিক ইবাদত একমাত্র আল্লাহর জন্য। হে নবী! আপনার ওপর শান্তি, আল্লাহর রহমত ও বরকত বর্ষিত হোক। আমাদের ওপর এবং আল্লাহর সৎ বান্দাদের ওপর শান্তি বর্ষিত হোক। আমি সাক্ষ্য দিচ্ছি আল্লাহ ছাড়া কোনো উপাস্য নেই এবং মুহাম্মদ (সা.) তাঁর বান্দা ও রাসুল। [বুখারি: ৮৩১]"),
                    DuaItem("৩. দরূদে ইব্রাহিম", "اللَّهُمَّ صَلِّ عَلَىٰ مُحَمَّدٍ وَعَلَىٰ آلِ مُحَمَّدٍ كَمَا صَلَّيْتَ عَلَىٰ إِبْرَاهِيمَ وَعَلَىٰ آلِ إِبْرَاهِيمَ إِنَّكَ حَمِيدٌ مَجِيدٌ، اللَّهُمَّ بَارِكْ عَلَىٰ مُحَمَّدٍ وَعَلَىٰ آلِ مُحَمَّدٍ كَمَا بَارَكْتَ عَلَىٰ إِبْرَاهِيمَ وَعَلَىٰ آلِ إِبْرَاهِيمَ إِنَّكَ حَمِيدٌ مَجِيدٌ", "আল্লাহুম্মা সাল্লি আলা মুহাম্মাদিওঁ ওয়া আলা আলি মুহাম্মাদ, কামা সাল্লাইতা আলা ইব্রাহীমা ওয়া আলা আলি ইব্রাহীম, ইন্নাকা হামিদুম মাজিদ। আল্লাহুম্মা বারিক আলা মুহাম্মাদিওঁ ওয়া আলা আলি মুহাম্মাদ, কামা বারাকতা আলা ইব্রাহীমা ওয়া আলা আলি ইব্রাহীম, ইন্নাকা হামিদুম মাজিদ।", "হে আল্লাহ! আপনি মুহাম্মদ (সা.) এবং তাঁর বংশধরদের ওপর রহমত বর্ষণ করুন, যেমন আপনি ইব্রাহিম (আ.) এবং তাঁর বংশধরদের ওপর রহমত বর্ষণ করেছিলেন। নিশ্চয় আপনি প্রশংসিত ও মহিমান্বিত। হে আল্লাহ! আপনি বরকত দান করুন... [বুখারি: ৩৩৭০]"),
                    DuaItem("৪. দোয়ায়ে কুনুত (বিতর নামাজে)", "اللَّهُمَّ إِنَّا نَسْتَعِينُكَ وَنَسْتَغْفِرُكَ وَنُؤْمِنُ بِكَ وَنَتَوَكَّلُ عَلَيْكَ وَنُثْنِي عَلَيْكَ الْخَيْرَ وَنَشْكُرُكَ وَلا نَكْفُرُكَ وَنَخْلَعُ وَنَتْرُكُ مَنْ يَفْجُرُكَ", "আল্লাহুম্মা ইন্না নাসতাঈনুকা ওয়া নাসতাগফিরুকা ওয়া নু’মিনু বিকা ওয়া নাতাওয়াক্কালু আলাইকা ওয়া নুছনী আলাইকাল খাইর, ওয়া নাশ কুরুকা ওয়ালা নাকফুরুকা ওয়া নাখলাউ ওয়া নাতরুকু মাই ইয়াফজুরুক...", "হে আল্লাহ! আমরা আপনারই সাহায্য চাই, আপনার কাছেই ক্ষমা প্রার্থনা করি, আপনার প্রতি ঈমান রাখি এবং আপনার ওপর ভরসা করি... [ইবনে আবি শাইবা: ৬৯৬৫]")
                ) else listOf(
                    DuaItem("1. Thana (Opening supplication)", "سُبْحَانَكَ اللَّهُمَّ وَبِحَمْدِكَ وَتَبَارَكَ اسْمُكَ وَتَعَالَىٰ جَدُّكَ وَلَا إِلٰهَ غَيْرُكَ", "Subhanaka Allahumma wa bihamdika, wa tabarakasmuka wa ta'ala jadduka, wa la ilaha ghairuk.", "Glory be to You O Allah, and praise be to You, and blessed is Your name, and exalted is Your majesty, and there is no deity worthy of worship besides You. [Tirmidhi: 243]"),
                    DuaItem("2. Tashahhud (At-Tahiyyat)", "التَّحِيَّاتُ لِلَّهِ وَالصَّلَوَاتُ وَالطَّيِّبَاتُ، السَّلَامُ عَلَيْكَ أَيُّهَا النَّبِيُّ وَرَحْمَةُ اللَّهِ وَبَرَكَاتُهُ، السَّلَامُ عَلَيْنَا وَعَلَىٰ عِبَادِ اللَّهِ الصَّالِحِينَ، أَشْهَدُ أَنْ لَا إِلٰهَ إِلَّا اللَّهُ وَأَشْهَدُ أَنَّ مُحَمَّدًا عَبْدُهُ وَرَسُولُهُ", "At-Tahiyyatu lillahi was-salawatu wat-tayyibat, as-salamu 'alaika ayyuhan-Nabiyyu wa rahmatullahi wa barakatuh, as-salamu 'alaina wa 'ala 'ibadillahis-salihin. Ash-hadu alla ilaha illallah, wa ash-hadu anna Muhammadan 'abduhu wa Rasuluh.", "All compliments, prayers and pure deeds are for Allah. Peace be upon you, O Prophet, and the mercy of Allah and His blessings. Peace be upon us and upon the righteous servants of Allah. I bear witness that there is no deity worthy of worship except Allah, and I bear witness that Muhammad is His servant and Messenger. [Bukhari: 831]"),
                    DuaItem("3. Durood Ibrahim", "اللَّهُمَّ صَلِّ عَلَىٰ مُحَمَّدٍ وَعَلَىٰ آلِ مُحَمَّدٍ كَمَا صَلَّيْتَ عَلَىٰ إِبْرَاهِيمَ وَعَلَىٰ آلِ إِبْرَاهِيمَ إِنَّكَ حَمِيدٌ مَجِيدٌ", "Allahumma salli 'ala Muhammadin wa 'ala ali Muhammadin kama sallaita 'ala Ibrahima wa 'ala ali Ibrahima innaka Hamidun Majid.", "O Allah, send peace upon Muhammad and upon the family of Muhammad, as You sent peace upon Ibrahim and upon the family of Ibrahim. Indeed, You are Praiseworthy and Glorious... [Bukhari: 3370]"),
                    DuaItem("4. Dua Qunut (In Witr prayer)", "اللَّهُمَّ إِنَّا نَسْتَعِينُكَ وَنَسْتَغْفِرُكَ وَنُؤْمِنُ بِكَ وَنَتَوَكَّلُ عَلَيْكَ", "Allahumma inna nasta'inuka wa nastaghfiruka wa nu'minu bika wa natawakkalu 'alaik...", "O Allah! We seek Your help and seek Your forgiveness, and we believe in You and rely upon You... [Ibn Abi Shaybah: 6965]")
                )

                items(duas.size) { idx ->
                    val d = duas[idx]
                    DuaDisplayCard(item = d, isBangla = isBangla)
                }
            }
        }
    }
}

data class DuaItem(val title: String, val arabic: String, val pron: String, val meaning: String)

@Composable
private fun GuideStepCard(stepNum: String, title: String, desc: String) {
    val colors = FinancyTheme.colors
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
        border = BorderStroke(1.dp, colors.borderSubtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(colors.neonGreen),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stepNum, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                Spacer(modifier = Modifier.height(3.dp))
                Text(text = desc, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary, lineHeight = 18.sp)
            }
        }
    }
}

@Composable
private fun RakahSummaryCard(isBangla: Boolean) {
    val colors = FinancyTheme.colors
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
        border = BorderStroke(1.dp, colors.borderSubtle)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(if (isBangla) "🌅 ফজর: ২ সুন্নাত + ২ ফরয = ৪ রাকাত" else "🌅 Fajr: 2 Sunnah + 2 Fardh = 4 Rakahs", fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp, color = colors.textPrimary)
            Text(if (isBangla) "☀️ যোহর: ৪ সুন্নাত + ৪ ফরয + ২ সুন্নাত + ২ নফল = ১২ রাকাত" else "☀️ Dhuhr: 4 Sunnah + 4 Fardh + 2 Sunnah + 2 Nafl = 12 Rakahs", fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp, color = colors.textPrimary)
            Text(if (isBangla) "⛅ আসর: ৪ সুন্নাত + ৪ ফরয = ৮ রাকাত" else "⛅ Asr: 4 Sunnah + 4 Fardh = 8 Rakahs", fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp, color = colors.textPrimary)
            Text(if (isBangla) "🌇 মাগরিব: ৩ ফরয + ২ সুন্নাত + ২ নফল = ৭ রাকাত" else "🌇 Maghrib: 3 Fardh + 2 Sunnah + 2 Nafl = 7 Rakahs", fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp, color = colors.textPrimary)
            Text(if (isBangla) "🌌 ইশা: ৪ সুন্নাত + ৪ ফরয + ২ সুন্নাত + ২ নফল + ৩ বিতর = ১৫ রাকাত" else "🌌 Isha: 4 Sunnah + 4 Fardh + 2 Sunnah + 2 Nafl + 3 Witr = 15 Rakahs", fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp, color = colors.textPrimary)
            Text(if (isBangla) "🕌 জুমু'আ: ৪ কাবলাল জুমু'আ + ২ ফরয + ৪ বা'দাল জুমু'আ = ১০ রাকাত" else "🕌 Jumu'ah: 4 Qablal Jumu'ah + 2 Fardh + 4 Ba'dal Jumu'ah = 10 Rakahs", fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp, color = colors.textPrimary)
        }
    }
}

@Composable
private fun AccordionCard(title: String, content: @Composable () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val colors = FinancyTheme.colors
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
        border = BorderStroke(1.dp, colors.borderSubtle)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
        border = BorderStroke(1.dp, colors.borderSubtle)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = item.title, fontWeight = FontWeight.Bold, color = colors.neonGreen, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = item.arabic, fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth(), lineHeight = 28.sp, color = colors.textPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "${if (isBangla) "উচ্চারণ: " else "Pronunciation: "}${item.pron}", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, lineHeight = 18.sp, color = colors.neonGreen)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "${if (isBangla) "অর্থ: " else "Meaning: "}${item.meaning}", fontSize = 12.sp, color = colors.textSecondary, lineHeight = 17.sp)
        }
    }
}
