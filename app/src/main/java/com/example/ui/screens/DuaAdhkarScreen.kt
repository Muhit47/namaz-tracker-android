package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.ui.components.IosGlassChip
import com.example.ui.components.IosGlassPillButton
import com.example.ui.theme.*
import com.example.util.HapticUtil

data class DuaAdhkarItem(
    val id: Int,
    val category: String,
    val titleBn: String,
    val titleEn: String,
    val arabic: String,
    val translitBn: String,
    val translitEn: String,
    val transBn: String,
    val transEn: String,
    val benefitBn: String,
    val referenceBn: String,
    val targetCount: Int = 1
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuaAdhkarScreen(
    isBangla: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("morning") }
    var searchQuery by remember { mutableStateOf("") }

    val categories = listOf(
        "morning" to (if (isBangla) "🌅 সকালের জিকির" else "🌅 Morning Adhkar"),
        "evening" to (if (isBangla) "🌙 সন্ধ্যার জিকির" else "🌙 Evening Adhkar"),
        "daily" to (if (isBangla) "🤲 দৈনন্দিন দোয়া" else "🤲 Daily Duas"),
        "protection" to (if (isBangla) "🛡️ হেফাজত ও বিপদ" else "🛡️ Protection"),
        "forgiveness" to (if (isBangla) "🕊️ তাওবা ও মাগফিরাত" else "🕊️ Forgiveness")
    )

    val allDuas = remember { getDuaList() }

    val filteredDuas = remember(selectedCategory, searchQuery) {
        allDuas.filter { dua ->
            (dua.category == selectedCategory) &&
            (searchQuery.isBlank() ||
             dua.titleBn.contains(searchQuery, ignoreCase = true) ||
             dua.transBn.contains(searchQuery, ignoreCase = true) ||
             dua.translitBn.contains(searchQuery, ignoreCase = true) ||
             dua.arabic.contains(searchQuery, ignoreCase = true))
        }
    }

    Scaffold(
        containerColor = Slate50,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBangla) "দৈনন্দিন দো’আ ও জিকির" else "Daily Duas & Adhkar",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("dua_adhkar_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Slate900
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GlassCardWhite)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Category Tabs Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { (catKey, catLabel) ->
                    val isSelected = selectedCategory == catKey
                    IosGlassChip(
                        text = catLabel,
                        isSelected = isSelected,
                        onClick = {
                            selectedCategory = catKey
                        }
                    )
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = if (isBangla) "দোয়া, অর্থ বা উচ্চারণ খুঁজুন..." else "Search दुआs, meaning or transliteration...",
                        fontSize = 13.sp,
                        color = Slate400
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Slate400,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = Slate400,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = GlassCardWhite,
                    unfocusedContainerColor = GlassCardWhite,
                    focusedBorderColor = Emerald500,
                    unfocusedBorderColor = GlassBorder
                ),
                singleLine = true
            )

            // List of Duas
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredDuas, key = { it.id }) { dua ->
                    DuaCard(dua = dua, isBangla = isBangla)
                }
            }
        }
    }
}

@Composable
private fun DuaCard(
    dua: DuaAdhkarItem,
    isBangla: Boolean
) {
    val context = LocalContext.current
    var count by remember(dua.id) { mutableIntStateOf(0) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GlassCardWhite),
        border = BorderStroke(1.dp, GlassBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isBangla) dua.titleBn else dua.titleEn,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Emerald700,
                    modifier = Modifier.weight(1f)
                )

                if (dua.targetCount > 1) {
                    Surface(
                        color = Emerald50,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Emerald100)
                    ) {
                        Text(
                            text = "${count}/${dua.targetCount} বার",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Emerald700,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Arabic text
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Slate50,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = dua.arabic,
                    fontFamily = AmiriFontFamily,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate900,
                    textAlign = TextAlign.Right,
                    lineHeight = 42.sp,
                    modifier = Modifier.padding(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Transliteration
            Text(
                text = "উচ্চারণ: " + (if (isBangla) dua.translitBn else dua.translitEn),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Slate800,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Meaning / Translation
            Text(
                text = "অর্থ: " + (if (isBangla) dua.transBn else dua.transEn),
                fontSize = 13.sp,
                color = Slate600,
                lineHeight = 18.sp
            )

            if (dua.benefitBn.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Emerald50.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Emerald600,
                        modifier = Modifier
                            .size(14.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBangla) dua.benefitBn else dua.referenceBn,
                        fontSize = 11.5.sp,
                        color = Emerald800,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Interactive Count Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "সূত্র: " + dua.referenceBn,
                    fontSize = 11.sp,
                    color = Slate400
                )

                val isDone = count >= dua.targetCount && dua.targetCount > 0
                IosGlassPillButton(
                    text = if (isDone) "পঠিত (Done)" else "পাঠ করুন (Read)",
                    isPrimary = isDone,
                    fontSize = 11.5.sp,
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = if (isDone) Icons.Default.Check else Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp)
                        )
                    },
                    onClick = {
                        if (dua.targetCount > 1) {
                            if (count < dua.targetCount) {
                                count++
                                if (count == dua.targetCount) {
                                    HapticUtil.vibrateMilestone(context)
                                }
                            } else {
                                count = 0
                            }
                        } else {
                            count = if (count == 0) 1 else 0
                        }
                    }
                )
            }
        }
    }
}

private fun getDuaList(): List<DuaAdhkarItem> = listOf(
    DuaAdhkarItem(
        id = 1,
        category = "morning",
        titleBn = "সকালের আয়াতুল কুরসী",
        titleEn = "Ayat al-Kursi (Morning)",
        arabic = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ",
        translitBn = "আল্লাহু লা ইলাহা ইল্লা হুওয়াল হাইয়্যুল কায়্যুম...",
        translitEn = "Allahu la ilaha illa huwal-Hayyul-Qayyum...",
        transBn = "আল্লাহ, তিনি ছাড়া কোনো সত্যিকারের উপাস্য নেই। তিনি চিরঞ্জীব, সর্বসত্তার ধারক...",
        transEn = "Allah! There is no deity except Him, the Ever-Living, the Sustaining of all existence...",
        benefitBn = "যে ব্যক্তি সকালে এটি পাঠ করবে, সন্ধ্যা পর্যন্ত সে জিন ও শয়তানের অনিষ্ট থেকে হিফাজতে থাকবে।",
        referenceBn = "হিসনুল মুসলিম, নাসায়ী",
        targetCount = 1
    ),
    DuaAdhkarItem(
        id = 2,
        category = "morning",
        titleBn = "সাইয়্যেদুল এস্তেগফার (সকাল)",
        titleEn = "Sayyidul Istighfar",
        arabic = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ",
        translitBn = "আল্লাহুম্মা আন্তা রাব্বী লা ইলাহা ইল্লা আন্তা, খালাকতানী ওয়া আনা আবদুকা, ওয়া আনা আলা আহদিকা ওয়া ওয়া'দিকা মাস্তাত্বা'তু...",
        translitEn = "Allahumma anta Rabbi la ilaha illa anta, khalaqtani wa ana 'abduka...",
        transBn = "হে আল্লাহ! আপনিই আমার রব, আপনি ছাড়া কোনো উপাস্য নেই। আপনি আমাকে সৃষ্টি করেছেন এবং আমি আপনার বান্দা...",
        transEn = "O Allah, You are my Lord, there is no god but You. You created me and I am Your servant...",
        benefitBn = "যে ব্যক্তি দিনে ইয়াকিনের সাথে এটি পাঠ করে সন্ধ্যায় মারা যাবে সে জান্নাতী হবে।",
        referenceBn = "সহীহ বুখারী: ৬৩২৩",
        targetCount = 1
    ),
    DuaAdhkarItem(
        id = 3,
        category = "morning",
        titleBn = "সকালের ৩ বার জিকির",
        titleEn = "Morning 3x Dhikr",
        arabic = "رَضِيتُ بِاللَّهِ رَبًّا، وَبِالْإِسْلَامِ دِينًا، وَبِمُحَمَّدٍ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ نَبِيًّا",
        translitBn = "রাদ্বীতু বিল্লাহি রাব্বান, ওয়া বিল ইসলামি দ্বীনান, ওয়া বি মুহাম্মাদিন সাল্লাল্লাহু আলাইহি ওয়া সাল্লামা নাবিয়্যান।",
        translitEn = "Radeetu billahi Rabban, wa bil-Islami deenan, wa bi-Muhammadin sallallahu 'alayhi wa sallama Nabiyya.",
        transBn = "আমি আল্লাহকে রব হিসেবে, ইসলামকে দ্বীন হিসেবে এবং মুহাম্মদ (সাঃ)-কে নবী হিসেবে পেয়ে সন্তুষ্ট।",
        transEn = "I am pleased with Allah as my Lord, Islam as my religion, and Muhammad (PBUH) as my Prophet.",
        benefitBn = "রাসূল (সাঃ) জামিন হয়েছেন যে, কিয়ামতের দিন আল্লাহ তাকে সন্তুষ্ট করবেন।",
        referenceBn = "আবু দাউদ, তিরমিযী",
        targetCount = 3
    ),
    DuaAdhkarItem(
        id = 4,
        category = "evening",
        titleBn = "সন্ধ্যার নিরাপত্তা দোয়া",
        titleEn = "Evening Protection Dua",
        arabic = "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ",
        translitBn = "বিসমিল্লাহিল্লাযী লা ইয়াদ্বুররু মা'আসমিহী শাইউন ফিল আরদ্বি ওয়া লা ফিস সামা-ই, ওয়া হুওয়াস সামীউল আলীম।",
        translitEn = "Bismillahillazi la yadurru ma'asmihi shay'un fil-ardi wa la fis-sama'i, wa Huwas-Sami'ul-'Afeem.",
        transBn = "আল্লাহর নামে, যাঁর নামের সাথে আসমান ও যমীনের কোনো কিছুই কোনো অনিষ্ট করতে পারে না, আর তিনি সর্বশ্রোতা, সর্বজ্ঞ।",
        transEn = "In the name of Allah with whose name nothing on earth or in heaven can cause harm...",
        benefitBn = "যে ব্যক্তি সকালে ও সন্ধ্যায় ৩ বার পাঠ করবে, কোনো কিছুই তার ক্ষতি করতে পারবে না।",
        referenceBn = "তিরমিযী: ৩৩৮৮, আবু দাউদ",
        targetCount = 3
    ),
    DuaAdhkarItem(
        id = 5,
        category = "daily",
        titleBn = "বিপদের সময় দোয়া (ইউনুস আঃ)",
        titleEn = "Dua of Prophet Yunus",
        arabic = "لَا إِلَهَ إِلَّا أَنْتَ سُبْحَانَكَ إِنِّي كُنْتُ مِنَ الظَّالِمِينَ",
        translitBn = "লা ইলাহা ইল্লা আন্তা সুবহানাকা ইন্নী কুন্তু মিনাজ জ্বালিমীন।",
        translitEn = "La ilaha illa anta subhanaka inni kuntu minaz-zalimin.",
        transBn = "আপনি ছাড়া কোনো ইলাহ নেই, আপনি অতি পবিত্র। নিশ্চয়ই আমি অপরাধীদের অন্তর্ভুক্ত ছিলাম।",
        transEn = "There is no deity except You; exalted are You. Indeed, I have been of the wrongdoers.",
        benefitBn = "যেকোনো কঠিন বিপদ, দুশ্চিন্তা ও সংকটে এই দোয়া পাঠ করলে আল্লাহ তা কবুল করেন।",
        referenceBn = "সূরা আল-আম্বিয়া: ৮৭, তিরমিযী",
        targetCount = 1
    ),
    DuaAdhkarItem(
        id = 6,
        category = "forgiveness",
        titleBn = "পাপ মোচনের ছোট্ট জিকির (১০০ বার)",
        titleEn = "Subhanallahi wa Bihamdihi 100x",
        arabic = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
        translitBn = "সুবহানাল্লাহি ওয়া বিহামদিহী।",
        translitEn = "Subhanallahi wa bihamdihi.",
        transBn = "আল্লাহ পবিত্র এবং সমস্ত প্রশংসা তাঁরই জন্য।",
        transEn = "Glory be to Allah and praise be to Him.",
        benefitBn = "যে ব্যক্তি দিনে ১০০ বার পাঠ করবে, তার পাপ সমুদ্রের ফেনার মতো হলেও ক্ষমা করে দেওয়া হবে।",
        referenceBn = "সহীহ বুখারী: ৬৪০৫",
        targetCount = 100
    )
)
