package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FinancyTheme
import com.example.ui.viewmodel.NamazUiState
import com.example.ui.viewmodel.NamazViewModel

enum class IbadahSubScreen {
    NONE,
    DUA,
    TASBIH,
    QIBLA,
    PRAYER_GUIDE,
    WUDU_GUIDE,
    ASMAUL_HUSNA
}

private data class IbadahFeatureCard(
    val id: IbadahSubScreen,
    val icon: String,
    val titleBn: String,
    val titleEn: String,
    val descBn: String,
    val descEn: String,
    val badgeBn: String,
    val badgeEn: String,
    val accentColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IbadahScreen(
    uiState: NamazUiState,
    viewModel: NamazViewModel,
    modifier: Modifier = Modifier
) {
    val isBangla = uiState.isBangla
    var activeSubScreen by remember { mutableStateOf(IbadahSubScreen.NONE) }

    BackHandler(enabled = activeSubScreen != IbadahSubScreen.NONE) {
        activeSubScreen = IbadahSubScreen.NONE
    }

    AnimatedContent(
        targetState = activeSubScreen,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "ibadah_subscreen_nav"
    ) { screen ->
        when (screen) {
            IbadahSubScreen.DUA -> DuaAdhkarScreen(
                isBangla = isBangla,
                onBack = { activeSubScreen = IbadahSubScreen.NONE }
            )
            IbadahSubScreen.TASBIH -> TasbihScreen(
                uiState = uiState,
                viewModel = viewModel,
                onBack = { activeSubScreen = IbadahSubScreen.NONE }
            )
            IbadahSubScreen.QIBLA -> QiblaScreen(
                uiState = uiState,
                viewModel = viewModel,
                onBack = { activeSubScreen = IbadahSubScreen.NONE }
            )
            IbadahSubScreen.PRAYER_GUIDE -> GuidesScreen(
                isBangla = isBangla,
                initialSection = 0,
                onBack = { activeSubScreen = IbadahSubScreen.NONE }
            )
            IbadahSubScreen.WUDU_GUIDE -> GuidesScreen(
                isBangla = isBangla,
                initialSection = 1,
                onBack = { activeSubScreen = IbadahSubScreen.NONE }
            )
            IbadahSubScreen.ASMAUL_HUSNA -> AsmaulHusnaScreen(
                isBangla = isBangla,
                onBack = { activeSubScreen = IbadahSubScreen.NONE }
            )
            IbadahSubScreen.NONE -> {
                IbadahHubContent(
                    isBangla = isBangla,
                    onSelect = { activeSubScreen = it },
                    modifier = modifier
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IbadahHubContent(
    isBangla: Boolean,
    onSelect: (IbadahSubScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = FinancyTheme.colors

    val features = remember {
        listOf(
            IbadahFeatureCard(
                id = IbadahSubScreen.DUA,
                icon = "🤲",
                titleBn = "দোয়া ও আযকার",
                titleEn = "Dua & Adhkar",
                descBn = "সকাল-সন্ধ্যার মাসনুন দু'আ",
                descEn = "Morning & Evening Duas",
                badgeBn = "হিসনুল মুসলিম",
                badgeEn = "Hisnul Muslim",
                accentColor = Color(0xFF059669)
            ),
            IbadahFeatureCard(
                id = IbadahSubScreen.TASBIH,
                icon = "📿",
                titleBn = "ডিজিটাল তাসবীহ",
                titleEn = "Digital Tasbih",
                descBn = "ভাইব্রেশনসহ জিকির গণনা",
                descEn = "Haptic dhikr counter",
                badgeBn = "কাউন্টার",
                badgeEn = "Counter",
                accentColor = Color(0xFF0891B2)
            ),
            IbadahFeatureCard(
                id = IbadahSubScreen.QIBLA,
                icon = "🧭",
                titleBn = "কিবলা কম্পাস",
                titleEn = "Qibla Direction",
                descBn = "কাবার সঠিক দিক ও কোণ",
                descEn = "Accurate Kaaba compass",
                badgeBn = "লাইভ দিক",
                badgeEn = "Live Compass",
                accentColor = Color(0xFF2563EB)
            ),
            IbadahFeatureCard(
                id = IbadahSubScreen.PRAYER_GUIDE,
                icon = "📖",
                titleBn = "নামাযের নিয়ম",
                titleEn = "Prayer Guide",
                descBn = "ফরজ, বিতর ও জানাযা",
                descEn = "Daily & Nafl Salah rules",
                badgeBn = "নামায শিক্ষা",
                badgeEn = "Salah Rules",
                accentColor = Color(0xFFD97706)
            ),
            IbadahFeatureCard(
                id = IbadahSubScreen.WUDU_GUIDE,
                icon = "💧",
                titleBn = "ওযু ও পবিত্রতা",
                titleEn = "Wudu & Purity",
                descBn = "ফরজ, গোসল ও তায়াম্মুম",
                descEn = "Wudu, Ghusl & Tayammum",
                badgeBn = "তাহারাত",
                badgeEn = "Taharah",
                accentColor = Color(0xFF16A34A)
            ),
            IbadahFeatureCard(
                id = IbadahSubScreen.ASMAUL_HUSNA,
                icon = "✨",
                titleBn = "আল্লাহর ৯৯ নাম",
                titleEn = "99 Names of Allah",
                descBn = "আসমাউল হুসনা ও অর্থ",
                descEn = "Divine Names & virtues",
                badgeBn = "৯৯ নাম",
                badgeEn = "99 Names",
                accentColor = Color(0xFF9333EA)
            )
        )
    }

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isBangla) "ইবাদাত ও আত্মশুদ্ধি" else "Ibadah & Spiritual Tools",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Text(
                                text = if (isBangla) "মাসনুন আমল, জিকির ও সার্বিক গাইডলাইন" else "Daily dhikr, duas & comprehensive guidance",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = colors.textSecondary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = colors.emerald.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, colors.emerald.copy(alpha = 0.3f)),
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Text(
                                text = if (isBangla) "৬টি টুলস" else "6 Tools",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.emerald,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.card)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. Daily Dhikr Hero Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onSelect(IbadahSubScreen.TASBIH) },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.emeraldSoft),
                    border = BorderStroke(1.dp, colors.emerald.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = "📿", fontSize = 15.sp)
                                Text(
                                    text = if (isBangla) "আজকের বিশেষ আমল" else "Dhikr of the Day",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.emerald
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = colors.emerald
                            ) {
                                Text(
                                    text = if (isBangla) "১০০ বার" else "100 Times",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isBangla) "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ" else "Subhanallahi Wa Bihamdihi",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isBangla) "\"সমুদ্রের ফেনা পরিমাণ গুনাহও ক্ষমা করা হয়\"" else "\"Whoever says it 100 times, sins are forgiven\"",
                                    fontSize = 11.sp,
                                    color = colors.textSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Button(
                                onClick = { onSelect(IbadahSubScreen.TASBIH) },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colors.emerald,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = if (isBangla) "জপুন ▶" else "Dhikr ▶",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // 2. 2-Column Bento Grid
            val pairs = features.chunked(2)
            items(pairs.size) { rowIndex ->
                val pair = pairs[rowIndex]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    pair.forEach { item ->
                        IbadahBentoTile(
                            item = item,
                            isBangla = isBangla,
                            onClick = { onSelect(item.id) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (pair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun IbadahBentoTile(
    item: IbadahFeatureCard,
    isBangla: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = FinancyTheme.colors

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        border = BorderStroke(1.dp, colors.border),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(13.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Row: Icon + Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(38.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = item.accentColor.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, item.accentColor.copy(alpha = 0.25f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = item.icon, fontSize = 20.sp)
                    }
                }

                Surface(
                    color = item.accentColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (isBangla) item.badgeBn else item.badgeEn,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = item.accentColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Info: Title & Subtitle
            Text(
                text = if (isBangla) item.titleBn else item.titleEn,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = if (isBangla) item.descBn else item.descEn,
                fontSize = 10.5.sp,
                color = colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
