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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    val colors = FinancyTheme.colors
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
                descBn = "সকাল-সন্ধ্যার মাসনুন দু'আ, যিকির ও আমল",
                descEn = "Morning & Evening Duas, daily prayers & dhikr",
                badgeBn = "হিসনুল মুসলিম",
                badgeEn = "Hisnul Muslim",
                accentColor = colors.emerald
            ),
            IbadahFeatureCard(
                id = IbadahSubScreen.TASBIH,
                icon = "📿",
                titleBn = "ডিজিটাল তাসবীহ",
                titleEn = "Digital Tasbih",
                descBn = "হ্যাপটিক ভাইব্রেশনসহ ডিজিটাল জিকির কাউন্টার",
                descEn = "Haptic feedback dhikr counter with custom targets",
                badgeBn = "কাউন্টার",
                badgeEn = "Counter",
                accentColor = colors.cyan
            ),
            IbadahFeatureCard(
                id = IbadahSubScreen.QIBLA,
                icon = "🧭",
                titleBn = "কিবলা কম্পাস",
                titleEn = "Qibla Direction",
                descBn = "সরাসরি কাবার সঠিক দিক ও লাইভ সেন্সর কম্পাস",
                descEn = "Real-time compass pointing to the Kaaba in Makkah",
                badgeBn = "লাইভ দিক",
                badgeEn = "Live Compass",
                accentColor = colors.emerald
            ),
            IbadahFeatureCard(
                id = IbadahSubScreen.PRAYER_GUIDE,
                icon = "📖",
                titleBn = "নামাযের নিয়ম ও শিক্ষা",
                titleEn = "Prayer Guide",
                descBn = "পাঁচ ওয়াক্ত, তাহাজ্জুদ, বিতর ও জানাযার পূর্ণ নিয়ম",
                descEn = "Step-by-step rules for daily, Janazah, Eid & Nafl Salah",
                badgeBn = "নামায শিক্ষা",
                badgeEn = "Salah Rules",
                accentColor = colors.amber
            ),
            IbadahFeatureCard(
                id = IbadahSubScreen.WUDU_GUIDE,
                icon = "💧",
                titleBn = "ওযু ও পবিত্রতা",
                titleEn = "Wudu & Purity",
                descBn = "ওযুর ফরজ, সুন্নত, গোসল ও তায়াম্মুমের পদ্ধতি",
                descEn = "Essential requirements for Wudu, Ghusl & Tayammum",
                badgeBn = "তাহারাত",
                badgeEn = "Taharah",
                accentColor = colors.cyan
            ),
            IbadahFeatureCard(
                id = IbadahSubScreen.ASMAUL_HUSNA,
                icon = "✨",
                titleBn = "আল্লাহর ৯৯ নাম",
                titleEn = "99 Names of Allah",
                descBn = "আসমাউল হুসনা, বাংলা অর্থ ও ফজিলত",
                descEn = "Asmaul Husna with Arabic, meanings and virtues",
                badgeBn = "আসমাউল হুসনা",
                badgeEn = "Divine Names",
                accentColor = colors.emerald
            )
        )
    }

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isBangla) "ইবাদাত ও গাইড" else "Ibadah & Spiritual Tools",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = if (isBangla) "দৈনন্দিন আমল, যিকির ও নামাযের শিক্ষা" else "Daily dhikr, duas & comprehensive guidance",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = colors.textSecondary
                        )
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
            contentPadding = PaddingValues(top = 14.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(features.size) { index ->
                val item = features[index]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onSelect(item.id) },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.card),
                    border = BorderStroke(1.dp, colors.border)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(50.dp),
                            shape = CircleShape,
                            color = item.accentColor.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, item.accentColor.copy(alpha = 0.25f))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = item.icon,
                                    fontSize = 24.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (isBangla) item.titleBn else item.titleEn,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary
                                )
                                Surface(
                                    color = item.accentColor.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (isBangla) item.badgeBn else item.badgeEn,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = item.accentColor,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = if (isBangla) item.descBn else item.descEn,
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.5.sp,
                                color = colors.textSecondary,
                                maxLines = 2
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = null,
                            tint = colors.textSecondary.copy(alpha = 0.6f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
