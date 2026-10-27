package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.util.HapticUtil

data class AsmaName(
    val id: Int,
    val arabic: String,
    val translitBn: String,
    val translitEn: String,
    val meaningBn: String,
    val meaningEn: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsmaulHusnaScreen(
    isBangla: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedName by remember { mutableStateOf<AsmaName?>(null) }

    val names = remember { getAsmaulHusnaList() }

    val filteredNames = remember(searchQuery) {
        if (searchQuery.isBlank()) names
        else {
            names.filter {
                it.translitBn.contains(searchQuery, ignoreCase = true) ||
                it.meaningBn.contains(searchQuery, ignoreCase = true) ||
                it.translitEn.contains(searchQuery, ignoreCase = true) ||
                it.arabic.contains(searchQuery, ignoreCase = true) ||
                it.id.toString() == searchQuery.trim()
            }
        }
    }

    Scaffold(
        containerColor = Slate50,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isBangla) "আল্লাহর ৯৯টি সুন্দর নাম" else "99 Names of Allah (Asma-ul-Husna)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Text(
                            text = if (isBangla) "أسماء الله الحسنى" else "The Most Beautiful Names",
                            style = MaterialTheme.typography.labelSmall,
                            color = Emerald700
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("asmaul_husna_back_btn")
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
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = if (isBangla) "নাম বা অর্থ অনুসন্ধান করুন (যেমন: রহমান)..." else "Search names or meaning...",
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
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = GlassCardWhite,
                    unfocusedContainerColor = GlassCardWhite,
                    focusedBorderColor = Emerald500,
                    unfocusedBorderColor = GlassBorder
                ),
                singleLine = true
            )

            // Grid of 99 Names
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 120.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredNames, key = { it.id }) { item ->
                    NameCard(
                        item = item,
                        isBangla = isBangla,
                        onClick = {
                            HapticUtil.vibrateClick(context)
                            selectedName = item
                        }
                    )
                }
            }
        }
    }

    // Detail Dialog
    selectedName?.let { name ->
        AlertDialog(
            onDismissRequest = { selectedName = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Emerald100,
                        shape = CircleShape
                    ) {
                        Text(
                            text = "#${name.id}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Emerald800,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = name.arabic,
                        fontFamily = AmiriFontFamily,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Emerald800,
                        textAlign = TextAlign.Right
                    )
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider(color = Slate200)

                    Text(
                        text = if (isBangla) "উচ্চারণ: ${name.translitBn}" else "Transliteration: ${name.translitEn}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )

                    Text(
                        text = if (isBangla) "অর্থ: ${name.meaningBn}" else "Meaning: ${name.meaningEn}",
                        fontSize = 14.sp,
                        color = Slate700,
                        lineHeight = 20.sp
                    )

                    Surface(
                        color = Emerald50,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                    ) {
                        Text(
                            text = if (isBangla) "ফজিলত ও শিক্ষা: আল্লাহর এই গুণবাচক নাম জিকির করলে অন্তরে প্রশান্তি ও ঈমানী শক্তি বৃদ্ধি পায়।"
                            else "Virtue: Reciting this name increases spiritual peace and devotion.",
                            fontSize = 12.sp,
                            color = Emerald900,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedName = null }) {
                    Text(
                        text = if (isBangla) "বন্ধ করুন" else "Close",
                        fontWeight = FontWeight.Bold,
                        color = Emerald700
                    )
                }
            },
            containerColor = GlassCardWhite,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun NameCard(
    item: AsmaName,
    isBangla: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GlassCardWhite),
        border = BorderStroke(1.dp, GlassBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Surface(
                    color = Slate100,
                    shape = CircleShape
                ) {
                    Text(
                        text = "${item.id}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate600,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.arabic,
                fontFamily = AmiriFontFamily,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Emerald700,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isBangla) item.translitBn else item.translitEn,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = Slate900,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = if (isBangla) item.meaningBn else item.meaningEn,
                fontSize = 11.5.sp,
                color = Slate600,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

private fun getAsmaulHusnaList(): List<AsmaName> = listOf(
    AsmaName(1, "الرَّحْمَنُ", "আর-রহমান", "Ar-Rahman", "পরম দয়ালু", "The Most Gracious"),
    AsmaName(2, "الرَّحِيمُ", "আর-রহীম", "Ar-Raheem", "অতি দয়ালু", "The Most Merciful"),
    AsmaName(3, "الْمَلِكُ", "আল-মালিক", "Al-Malik", "সার্বভৌম ক্ষমতার অধিকারী", "The King"),
    AsmaName(4, "الْقُدُّوسُ", "আল-কুদ্দুস", "Al-Quddus", "মহাপবিত্র", "The Most Holy"),
    AsmaName(5, "السَّلَامُ", "আস-সালাম", "As-Salam", "শান্তির উৎস", "The Source of Peace"),
    AsmaName(6, "الْمُؤْمِنُ", "আল-মু'মিন", "Al-Mu'min", "নিরাপত্তা দানকারী", "The Giver of Security"),
    AsmaName(7, "الْمُهَيْمِنُ", "আল-মুহাইমিন", "Al-Muhaymin", "রক্ষণাবেক্ষণকারী", "The Guardian"),
    AsmaName(8, "الْعَزِيزُ", "আল-আযীয", "Al-Aziz", "পরাক্রমশালী", "The Almighty"),
    AsmaName(9, "الْجَبَّارُ", "আল-জাব্বার", "Al-Jabbar", "সর্বশক্তিমান", "The Compeller"),
    AsmaName(10, "الْمُتَكَبِّرُ", "আল-মুতাকাব্বির", "Al-Mutakabbir", "অসীম মর্যাদার অধিকারী", "The Supreme"),
    AsmaName(11, "الْخَالِقُ", "আল-খালিক", "Al-Khaliq", "সৃষ্টিকর্তা", "The Creator"),
    AsmaName(12, "الْبَارِئُ", "আল-বারী", "Al-Bari", "সঠিক রূপদাতা", "The Maker of Order"),
    AsmaName(13, "الْمُصَوِّرُ", "আল-মুসাব্বির", "Al-Musawwir", "আকৃতি দানকারী", "The Fashioner"),
    AsmaName(14, "الْغَفَّارُ", "আল-গাফ্ফার", "Al-Ghaffar", "পরম ক্ষমাশীল", "The All-Forgiving"),
    AsmaName(15, "الْقَهَّارُ", "আল-কাহ্হার", "Al-Qahhar", "কঠোর নিয়ন্ত্রণকারী", "The Subduer"),
    AsmaName(16, "الْوَهَّابُ", "আল-ওয়াহ্হাব", "Al-Wahhab", "মহাদানকারী", "The Bestower"),
    AsmaName(17, "الرَّزَّاقُ", "আর-রাজ্জাক", "Ar-Razzaq", "রিজিকদাতা", "The Provider"),
    AsmaName(18, "الْفَتَّاحُ", "আল-ফাত্তাহ", "Al-Fattah", "বিজয় ও পথ উন্মোচনকারী", "The Opener"),
    AsmaName(19, "الْعَلِيمُ", "আল-আলীম", "Al-Aleem", "সর্বজ্ঞানী", "The All-Knowing"),
    AsmaName(20, "الْقَابِضُ", "আল-কাবিদ্ব", "Al-Qabid", "সংকুচিতকারী", "The Withholder")
)
