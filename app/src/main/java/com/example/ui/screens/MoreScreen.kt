package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import com.example.ui.viewmodel.NamazUiState
import com.example.ui.viewmodel.NamazViewModel

enum class MoreSubScreen {
    INDEX,
    GUIDE,
    ANALYTICS,
    TASBIH,
    QIBLA
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    uiState: NamazUiState,
    viewModel: NamazViewModel,
    activeSubScreen: MoreSubScreen,
    onNavigateSub: (MoreSubScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val isBangla = uiState.isBangla

    BackHandler(enabled = activeSubScreen != MoreSubScreen.INDEX) {
        onNavigateSub(MoreSubScreen.INDEX)
    }

    when (activeSubScreen) {
        MoreSubScreen.GUIDE -> GuidesScreen(
            isBangla = isBangla,
            onBack = { onNavigateSub(MoreSubScreen.INDEX) },
            modifier = modifier
        )
        MoreSubScreen.ANALYTICS -> AnalyticsScreen(
            uiState = uiState,
            onBack = { onNavigateSub(MoreSubScreen.INDEX) },
            modifier = modifier
        )
        MoreSubScreen.TASBIH -> TasbihScreen(
            uiState = uiState,
            viewModel = viewModel,
            onBack = { onNavigateSub(MoreSubScreen.INDEX) },
            modifier = modifier
        )
        MoreSubScreen.QIBLA -> QiblaScreen(
            uiState = uiState,
            viewModel = viewModel,
            onBack = { onNavigateSub(MoreSubScreen.INDEX) },
            modifier = modifier
        )
        MoreSubScreen.INDEX -> {
            Scaffold(
                containerColor = Slate50,
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = if (isBangla) "আমল, সেটিংস ও প্রয়ােজনীয় টুলস" else "Settings & Islamic Tools",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
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
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    // 1. Language Toggle & App Settings Section
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isBangla) "অ্যাপ সেটিংস ও ভাষা" else "App Settings & Language",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Emerald700
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = GlassCardWhite),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // Language Toggle
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = if (isBangla) "অ্যাপের ভাষা" else "App Language",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.5.sp,
                                            color = Slate900
                                        )
                                        Text(
                                            text = if (isBangla) "বাংলা এবং ইংরেজির মধ্যে পরিবর্তন করুন" else "Switch between Bengali and English",
                                            fontSize = 11.5.sp,
                                            color = Slate600
                                        )
                                    }

                                    Button(
                                        onClick = { viewModel.toggleLanguage() },
                                        colors = ButtonDefaults.buttonColors(containerColor = Emerald500),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(
                                            text = if (isBangla) "বাংলা ⇄ EN" else "EN ⇄ বাংলা",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Slate200)

                                // Asr Method Toggle
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = if (isBangla) "আসর নামাজের মাযহাব সময়" else "Asr Prayer Calculation",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.5.sp,
                                            color = Slate900
                                        )
                                        Text(
                                            text = if (uiState.isHanafiAsr)
                                                (if (isBangla) "হানাফী মাযহাব (ছায়া দ্বিগুণ)" else "Hanafi Method (2x shadow)")
                                            else
                                                (if (isBangla) "শাফেয়ী/মালিকী/হাম্বলী মাযহাব" else "Shafi'i/Maliki/Hanbali Method"),
                                            fontSize = 11.5.sp,
                                            color = Slate600
                                        )
                                    }

                                    Switch(
                                        checked = uiState.isHanafiAsr,
                                        onCheckedChange = { viewModel.toggleHanafiAsr() },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = Emerald500
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // 2. Knowledge & Guides
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (isBangla) "দ্বীনি শিক্ষা ও নিয়মাবলী" else "Knowledge & Guides",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Emerald700
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        ToolNavCard(
                            title = if (isBangla) "নামাজ ও ওজু শিক্ষা গাইড" else "Salah & Wudu Guide",
                            desc = if (isBangla) "নামাজের সঠিক নিয়ম, ওজুর ফরজ ও সুন্নাত, দোয়া ও নারী-পুরুষ পার্থক্য" else "Step-by-step Salah, Wudu, and essential Duas",
                            icon = Icons.AutoMirrored.Filled.MenuBook,
                            badge = if (isBangla) "পূর্ণ গাইড" else "Guide",
                            onClick = { onNavigateSub(MoreSubScreen.GUIDE) },
                            testTag = "nav_more_guide"
                        )
                    }

                    // 3. Tools & Analytics
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (isBangla) "আমল ও ট্র্যাকিং টুলস" else "Tools & Analytics",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Emerald700
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        ToolNavCard(
                            title = if (isBangla) "ক্যালেন্ডার ও স্ট্রিক রিপোর্ট" else "Calendar & Streak Analytics",
                            desc = if (isBangla) "মাসিক নামাযের ধারাবাহিকতা, স্ট্রিক ও জামাত আদায় পরিসংখ্যান" else "Consistency stats, daily streak, and completion rate",
                            icon = Icons.Default.BarChart,
                            badge = if (isBangla) "${DateUtil.toBanglaDigits(uiState.currentStreakDays.toString())} দিন স্ট্রিক" else "${uiState.currentStreakDays} Days Streak",
                            onClick = { onNavigateSub(MoreSubScreen.ANALYTICS) },
                            testTag = "nav_more_analytics"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        ToolNavCard(
                            title = if (isBangla) "ডিজিটাল তাসবীহ" else "Digital Tasbih",
                            desc = if (isBangla) "স্পর্শকাতর ভাইব্রেশন কাউন্টার, মাসনূন যিকির ও কাস্টম তাসবীহ" else "Tactile Dhikr counter with presets and custom dhikrs",
                            icon = Icons.Default.TouchApp,
                            badge = if (isBangla) "হ্যাপটিক" else "Haptic",
                            onClick = { onNavigateSub(MoreSubScreen.TASBIH) },
                            testTag = "nav_more_tasbih"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        ToolNavCard(
                            title = if (isBangla) "কিবলা কম্পাস" else "Qibla Direction",
                            desc = if (isBangla) "সেন্সরের সাহায্যে সঠিক কিবলামুখী কম্পাস নির্দেশক" else "Real-time compass pointing to Holy Kaaba",
                            icon = Icons.Default.Explore,
                            badge = "${uiState.qiblaBearing.toInt()}°",
                            onClick = { onNavigateSub(MoreSubScreen.QIBLA) },
                            testTag = "nav_more_qibla"
                        )
                    }

                    // App Branding Footer
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = GlassCardWhite),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.app_header),
                                    contentDescription = "Namaz Tracker",
                                    modifier = Modifier.height(38.dp),
                                    contentScale = ContentScale.Fit
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (isBangla) "সকল মুসলিম ভাই-বোনদের জন্য পূর্ণাঙ্গ ইসলামিক সহচর। আল কুরআন, সহিহ হাদীস, নামাজ ও আমল ট্র্যাকিং।" else "Complete Islamic companion for tracking daily prayers, Quran, Hadith, and Qaza.",
                                    fontSize = 11.5.sp,
                                    color = Slate600,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ToolNavCard(
    title: String,
    desc: String,
    icon: ImageVector,
    badge: String,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GlassCardWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
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
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Emerald50,
                    border = androidx.compose.foundation.BorderStroke(0.8.dp, Emerald100),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = icon, contentDescription = null, tint = Emerald700)
                    }
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = Slate900)
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Emerald50,
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, Emerald100)
                        ) {
                            Text(
                                text = badge,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Emerald700,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = desc, style = MaterialTheme.typography.bodySmall, color = Slate600, maxLines = 2)
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Slate400
            )
        }
    }
}
