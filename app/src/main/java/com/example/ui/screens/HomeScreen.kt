package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.PrayerLogEntity
import com.example.data.model.PrayerType
import com.example.ui.components.PrayerCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.NamazUiState
import com.example.ui.viewmodel.NamazViewModel
import com.example.util.ActiveWaqtInfo
import com.example.util.DateUtil
import com.example.util.PrayerTimeCalculator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: NamazUiState,
    viewModel: NamazViewModel,
    modifier: Modifier = Modifier
) {
    val colors = FinancyTheme.colors
    val context = LocalContext.current
    var showCityDialog by remember { mutableStateOf(false) }
    var showJumuahDialog by remember { mutableStateOf(false) }
    var showRamadanDuaDialog by remember { mutableStateOf(false) }
    var showAdditionalPrayers by remember { mutableStateOf(false) }

    val isToday = uiState.selectedDate == DateUtil.getTodayIso()
    val isBangla = uiState.isBangla
    val calendar = remember(uiState.selectedDate) { DateUtil.getCalendarFromIso(uiState.selectedDate) }
    val isFriday = calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY

    var currentClockMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(30000)
            currentClockMillis = System.currentTimeMillis()
        }
    }

    val activeWaqtInfo = if (isToday) {
        remember(uiState.prayerTimes, currentClockMillis) {
            PrayerTimeCalculator.getActiveWaqtInfo(Calendar.getInstance(), uiState.prayerTimes)
        }
    } else null
    val activeWaqt = activeWaqtInfo?.currentPrayer

    val fardhPrayers = listOf(
        PrayerType.FAJR,
        PrayerType.DHUHR,
        PrayerType.ASR,
        PrayerType.MAGHRIB,
        PrayerType.ISHA
    )

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.app_header),
                            contentDescription = if (isBangla) "নামায ট্র্যাকার" else "Namaz Tracker",
                            modifier = Modifier
                                .height(38.dp)
                                .padding(vertical = 2.dp),
                            contentScale = ContentScale.Fit
                        )
                        Surface(
                            color = colors.emeraldSoft,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, colors.emerald.copy(alpha = 0.3f)),
                            modifier = Modifier.clickable { showCityDialog = true }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = colors.emerald
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = if (isBangla) uiState.selectedCity.nameBn else uiState.selectedCity.nameEn.split(" ")[0],
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.5.sp,
                                    color = colors.emerald,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = colors.emerald
                                )
                            }
                        }
                    }
                },
                actions = {
                    // Quick Dark / Light Mode Toggle Pill
                    IconButton(
                        onClick = { viewModel.toggleDarkMode() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (uiState.isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme",
                            tint = if (uiState.isDarkMode) Color(0xFFFBBF24) else colors.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    if (isFriday) {
                        FilledTonalButton(
                            onClick = { showJumuahDialog = true },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = colors.cyanSoft,
                                contentColor = colors.cyan
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .padding(end = 8.dp)
                        ) {
                            Text(if (isBangla) "🕌 জুমু'আ" else "🕌 Jumu'ah", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.card
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // 1. Date Navigation bar (Financy Pill Style)
            item {
                Spacer(modifier = Modifier.height(10.dp))
                DateNavigationHeader(
                    selectedDate = uiState.selectedDate,
                    isBangla = isBangla,
                    isToday = isToday,
                    onPrevClick = { viewModel.changeDateOffset(-1) },
                    onNextClick = { viewModel.changeDateOffset(1) },
                    onTodayClick = { viewModel.resetToToday() }
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            // 2. Active Waqt Hero Card (Financy Balance Style)
            if (isToday && activeWaqtInfo != null) {
                val activeLog = activeWaqt?.let { p ->
                    uiState.currentDayLogs.find { it.prayerName.equals(p.keyName, ignoreCase = true) }
                }
                item {
                    FinancyHeroWaqtCard(
                        info = activeWaqtInfo,
                        currentLog = activeLog,
                        isBangla = isBangla
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }

            // 3. 2x2 Bento Quick Actions Grid
            item {
                FinancyBentoGrid(
                    uiState = uiState,
                    isBangla = isBangla,
                    onToggleRoza = { viewModel.toggleRoza() },
                    onOpenRamadanDua = { showRamadanDuaDialog = true }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 4. Section Title (Activity Feed Style)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isBangla) "দৈনিক ৫ ওয়াক্ত নামায" else "5 Daily Prayers",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = if (isBangla) "সঠিক সময়ে জামাতে নামায আদায় করুন" else "Track your daily Salah in Jama'at",
                            fontSize = 11.sp,
                            color = colors.textMuted
                        )
                    }
                    Surface(
                        color = colors.emeraldSoft,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(0.8.dp, colors.emerald.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = if (isBangla) "${DateUtil.toBanglaDigits(uiState.totalPrayedToday.toString())}/৫ সম্পন্ন"
                            else "${uiState.totalPrayedToday}/5 Done",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.emerald,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // 5. Prayers list
            items(fardhPrayers) { prayer ->
                val log = uiState.currentDayLogs.find { it.prayerName.equals(prayer.keyName, ignoreCase = true) }
                val timeStr = when (prayer) {
                    PrayerType.FAJR -> uiState.prayerTimes.fajr
                    PrayerType.DHUHR -> uiState.prayerTimes.dhuhr
                    PrayerType.ASR -> uiState.prayerTimes.asr
                    PrayerType.MAGHRIB -> uiState.prayerTimes.maghrib
                    PrayerType.ISHA -> uiState.prayerTimes.isha
                    else -> ""
                }
                PrayerCard(
                    prayerType = prayer,
                    timeString = timeStr,
                    log = log,
                    isBangla = isBangla,
                    isActiveWaqt = isToday && prayer == activeWaqt,
                    remainingMinutes = if (isToday && prayer == activeWaqt) activeWaqtInfo?.minutesRemaining else null,
                    onStatusSelected = { status ->
                        viewModel.updatePrayerStatus(prayer.keyName, status, context)
                    },
                    onToggleSunnah = {
                        viewModel.toggleSunnah(prayer.keyName, context)
                    },
                    onSaveNotes = { notes ->
                        viewModel.updateNotes(prayer.keyName, notes)
                    }
                )
            }

            // 6. Additional Nafal Prayers (Tahajjud, Ishraq, Duha, Awwabin)
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAdditionalPrayers = !showAdditionalPrayers },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.card),
                    border = BorderStroke(1.dp, colors.border)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = colors.emeraldSoft,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Nightlight,
                                            contentDescription = null,
                                            tint = colors.emerald,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = if (isBangla) "নফল ও তাহাজ্জুদ নামাযের সময়" else "Nafl & Voluntary Prayers",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = colors.textPrimary
                                )
                            }
                            Icon(
                                imageVector = if (showAdditionalPrayers) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = colors.textMuted
                            )
                        }

                        AnimatedVisibility(visible = showAdditionalPrayers) {
                            Column(
                                modifier = Modifier.padding(top = 10.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AdditionalPrayerRow(
                                    title = if (isBangla) "তাহাজ্জুদ (Tahajjud)" else "Tahajjud (Night Prayer)",
                                    time = if (isBangla) "মধ্যরাত থেকে ফজরের পূর্ব পর্যন্ত" else "Midnight until before Fajr",
                                    rakah = if (isBangla) "২-১২ রাকাত" else "2-12 Rakahs",
                                    isBangla = isBangla
                                )
                                AdditionalPrayerRow(
                                    title = if (isBangla) "ইশরাক (Ishraq)" else "Ishraq (Post-Sunrise)",
                                    time = if (isBangla) "সূর্যোদয়ের ১৫-২০ মিনিট পর" else "15-20 mins after sunrise",
                                    rakah = if (isBangla) "২-৪ রাকাত" else "2-4 Rakahs",
                                    isBangla = isBangla
                                )
                                AdditionalPrayerRow(
                                    title = if (isBangla) "চাশত / দুহা (Duha)" else "Duha / Chasht (Forenoon)",
                                    time = if (isBangla) "সূর্য দ্বিপ্রহরে উঠার পূর্বে" else "Mid-morning before midday",
                                    rakah = if (isBangla) "২-৮ রাকাত" else "2-8 Rakahs",
                                    isBangla = isBangla
                                )
                                AdditionalPrayerRow(
                                    title = if (isBangla) "আউওয়াবিন (Awwabin)" else "Awwabin (Post-Maghrib)",
                                    time = if (isBangla) "মাগরিবের পর থেকে ইশার পূর্বে" else "Between Maghrib and Isha",
                                    rakah = if (isBangla) "৬ রাকাত" else "6 Rakahs",
                                    isBangla = isBangla
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCityDialog) {
        CitySelectionDialog(
            currentCity = uiState.selectedCity,
            isBangla = isBangla,
            isHanafiAsr = uiState.isHanafiAsr,
            onDismiss = { showCityDialog = false },
            onCitySelected = { city ->
                viewModel.selectCity(city)
                showCityDialog = false
            },
            onToggleHanafi = { viewModel.toggleHanafiAsr() }
        )
    }

    if (showJumuahDialog) {
        JumuahDeedsDialog(isBangla = isBangla, onDismiss = { showJumuahDialog = false })
    }

    if (showRamadanDuaDialog) {
        RamadanDuaDialog(isBangla = isBangla, onDismiss = { showRamadanDuaDialog = false })
    }
}

@Composable
private fun AdditionalPrayerRow(title: String, time: String, rakah: String, isBangla: Boolean) {
    val colors = FinancyTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(colors.cardSubtle)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary)
            Text(text = time, fontSize = 10.5.sp, color = colors.textMuted)
        }
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = colors.emeraldSoft
        ) {
            Text(
                text = rakah,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = colors.emerald,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
private fun FinancyHeroWaqtCard(
    info: ActiveWaqtInfo,
    currentLog: PrayerLogEntity?,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = FinancyTheme.colors
    val prayer = info.currentPrayer
    val isDone = currentLog?.status == "jamaah" || currentLog?.status == "on_time"

    // Calculate approximate progress
    val totalMins = (info.startTimeMinutes?.let { start ->
        info.endTimeMinutes?.let { end ->
            if (end > start) end - start else (end + 1440 - start)
        }
    } ?: 240).toFloat()

    val elapsedMins = (totalMins - info.minutesRemaining).coerceIn(0f, totalMins)
    val progress = (elapsedMins / totalMins).coerceIn(0.05f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "waqt_progress")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (colors.isDark) 12.dp else 4.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = if (colors.isDark) Color(0x60000000) else Color(0x15000000)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.card
        ),
        border = BorderStroke(1.2.dp, if (prayer != null) colors.emerald.copy(alpha = 0.6f) else colors.border)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header Row: Active Badge & Status Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(colors.emerald)
                    )
                    Text(
                        text = if (prayer != null) {
                            if (isBangla) "চলতি ওয়াক্ত • ${prayer.banglaName}" else "Active Waqt • ${prayer.englishName}"
                        } else {
                            if (isBangla) "পরবর্তী ওয়াক্ত • ${info.nextPrayer.banglaName}" else "Next Waqt • ${info.nextPrayer.englishName}"
                        },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isDone) colors.emeraldSoft else colors.cardSubtle,
                    border = BorderStroke(0.8.dp, if (isDone) colors.emerald else colors.border)
                ) {
                    Text(
                        text = if (isDone) {
                            if (isBangla) "✓ আদায় সম্পন্ন" else "✓ Done"
                        } else {
                            if (isBangla) "বাকি আছে" else "Pending"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDone) colors.emerald else colors.textMuted,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Large Remaining Countdown (Financy Balance typography)
            Text(
                text = if (prayer != null) {
                    if (isBangla) "${PrayerTimeCalculator.formatRemainingTime(info.minutesRemaining, true)} বাকি"
                    else "${PrayerTimeCalculator.formatRemainingTime(info.minutesRemaining, false)} left"
                } else {
                    if (isBangla) "${PrayerTimeCalculator.formatRemainingTime(info.minutesRemaining, true)} পর শুরু"
                    else "Starts in ${PrayerTimeCalculator.formatRemainingTime(info.minutesRemaining, false)}"
                },
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colors.emerald
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Smooth Linear Progress Bar
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = colors.emerald,
                trackColor = colors.cardSubtle
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Bottom Info: Waqt Range & Next Waqt
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (prayer != null) "🕒 ${info.startTime} — ${info.endTime}" else "🕒 ${info.endTime}",
                    fontSize = 11.5.sp,
                    color = colors.textMuted,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = if (isBangla) "পরবর্তী: ${info.nextPrayer.banglaName}" else "Next: ${info.nextPrayer.englishName}",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textSecondary
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        modifier = Modifier.size(10.dp),
                        tint = colors.textMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun FinancyBentoGrid(
    uiState: NamazUiState,
    isBangla: Boolean,
    onToggleRoza: () -> Unit,
    onOpenRamadanDua: () -> Unit
) {
    val colors = FinancyTheme.colors

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Tile 1: Qibla Compass
            BentoTile(
                modifier = Modifier.weight(1f),
                icon = "🧭",
                title = if (isBangla) "কিবলা দিক" else "Qibla Direction",
                value = "${uiState.qiblaBearing.toInt()}°",
                subtitle = if (isBangla) "মক্কাভিমুখী" else "Facing Kaaba",
                badge = if (uiState.isAlignedWithQibla) "✓ সঠিক" else "কম্পাস",
                badgeColor = colors.emerald
            )

            // Tile 2: Digital Tasbih
            BentoTile(
                modifier = Modifier.weight(1f),
                icon = "📿",
                title = if (isBangla) "তাসবীহ কাউন্টার" else "Digital Tasbih",
                value = if (isBangla) "সুবহানাল্লাহ" else "SubhanAllah",
                subtitle = if (isBangla) "দৈনিক যিকির" else "Daily Dhikr",
                badge = "হ্যাপটিক",
                badgeColor = colors.cyan
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Tile 3: Streak & Consistency
            BentoTile(
                modifier = Modifier.weight(1f),
                icon = "🔥",
                title = if (isBangla) "নামাযের স্ট্রিক" else "Daily Streak",
                value = if (isBangla) "${DateUtil.toBanglaDigits(uiState.currentStreakDays.toString())} দিন"
                else "${uiState.currentStreakDays} Days",
                subtitle = if (isBangla) "${DateUtil.toBanglaDigits(uiState.weeklyCompletionRate.toString())}% সাপ্তাহিক হার"
                else "${uiState.weeklyCompletionRate}% weekly rate",
                badge = "ধারাবাহিক",
                badgeColor = colors.amber
            )

            // Tile 4: Ramadan Sehri / Iftar
            BentoTile(
                modifier = Modifier.weight(1f),
                icon = "🌙",
                title = if (isBangla) "মাহে রমজান" else "Ramadan & Roza",
                value = "${uiState.prayerTimes.maghrib}",
                subtitle = if (isBangla) "ইফতারের সময়" else "Iftar time",
                badge = if (uiState.isRozaObservedToday) "রোজা সম্পন্ন ✓" else "দোয়া ও নিয়ত",
                badgeColor = colors.emerald,
                onClick = onOpenRamadanDua
            )
        }
    }
}

@Composable
private fun BentoTile(
    icon: String,
    title: String,
    value: String,
    subtitle: String,
    badge: String,
    badgeColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val colors = FinancyTheme.colors

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        border = BorderStroke(1.dp, colors.border),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                Text(text = icon, fontSize = 20.sp)
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = colors.cardSubtle,
                    border = BorderStroke(0.6.dp, colors.border)
                ) {
                    Text(
                        text = badge,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.5.sp,
                color = colors.textMuted,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun DateNavigationHeader(
    selectedDate: String,
    isBangla: Boolean,
    isToday: Boolean,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit,
    onTodayClick: () -> Unit
) {
    val colors = FinancyTheme.colors

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        border = BorderStroke(1.dp, colors.border),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onPrevClick,
                modifier = Modifier.testTag("btn_prev_day")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous Day",
                    tint = colors.textSecondary
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = DateUtil.formatDisplayDate(selectedDate, isBangla),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center
                )
                if (isToday) {
                    Surface(
                        color = colors.emeraldSoft,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Text(
                            text = if (isBangla) "আজ" else "Today",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.emerald,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                        )
                    }
                } else {
                    Text(
                        text = if (isBangla) "আজকে ফিরে যান" else "Return to Today",
                        fontSize = 11.sp,
                        color = colors.emerald,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable { onTodayClick() }
                            .padding(top = 2.dp)
                    )
                }
            }

            IconButton(
                onClick = onNextClick,
                modifier = Modifier.testTag("btn_next_day")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next Day",
                    tint = colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun JumuahDeedsDialog(isBangla: Boolean, onDismiss: () -> Unit) {
    val colors = FinancyTheme.colors
    var check1 by remember { mutableStateOf(false) }
    var check2 by remember { mutableStateOf(false) }
    var check3 by remember { mutableStateOf(false) }
    var check4 by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.card,
        title = {
            Text(
                text = if (isBangla) "🕌 জুমু'আর বিশেষ আমলসমূহ" else "🕌 Special Virtues & Deeds of Jumu'ah",
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DeedCheckboxItem(
                    title = if (isBangla) "১. সূরা আল-কাহাফ তিলাওয়াত" else "1. Reciting Surah Al-Kahf",
                    desc = if (isBangla) "জুমু'আর দিনে সূরা কাহাফ পাঠ করলে পরবর্তী জুমু'আ পর্যন্ত তার জন্য বিশেষ নূর চমকাতে থাকবে। (সহিহ আত-তারগিব: ৭৩৬)"
                    else "Whoever reads Surah Al-Kahf on Friday, light will shine for him from one Friday to the next. (Sahih Al-Targhib: 736)",
                    isChecked = check1
                ) { check1 = it }
                DeedCheckboxItem(
                    title = if (isBangla) "২. নবীজি (সা.) এর ওপর দরূদ পাঠ" else "2. Sending Durood upon the Prophet (pbuh)",
                    desc = if (isBangla) "তোমরা জুমু'আর দিনে আমার ওপর বেশি বেশি দরুদ পাঠ করো, কেননা তা আমার কাছে পেশ করা হয়। (আবু দাউদ: ১০৪৭)"
                    else "Send abundant blessings upon me on Friday, for your blessings are presented to me. (Abu Dawud: 1047)",
                    isChecked = check2
                ) { check2 = it }
                DeedCheckboxItem(
                    title = if (isBangla) "৩. গোসল, মেসওয়াক ও আতর" else "3. Ghusl, Miswak and Attar",
                    desc = if (isBangla) "সুন্নাহ অনুযায়ী উত্তমরূপে গোসল করা, মেসওয়াক করা, পরিচ্ছন্ন পোশাক ও সুগন্ধি ব্যবহার করা। (বুখারি: ৮৮০)"
                    else "Bathing, cleaning teeth with Miswak, wearing clean clothes, and applying perfume. (Bukhari: 880)",
                    isChecked = check3
                ) { check3 = it }
                DeedCheckboxItem(
                    title = if (isBangla) "৪. আসর পরবর্তী দোয়া কবুলের মুহূর্ত" else "4. Special Hour of Acceptance after Asr",
                    desc = if (isBangla) "জুমু'আর দিনে একটি বিশেষ মুহূর্ত রয়েছে যাতে বান্দা যে দোয়াই করে আল্লাহ তা কবুল করেন। (আবু দাউদ: ১০৪৮)"
                    else "On Friday there is an hour in which no Muslim asks Allah for anything good but He grants it to him. (Abu Dawud: 1048)",
                    isChecked = check4
                ) { check4 = it }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = colors.emerald, contentColor = Color.White)
            ) { Text(if (isBangla) "ঠিক আছে" else "Close", fontWeight = FontWeight.Bold) }
        }
    )
}

@Composable
private fun DeedCheckboxItem(title: String, desc: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val colors = FinancyTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = colors.emerald),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = colors.textPrimary)
            Text(desc, fontSize = 11.sp, color = colors.textSecondary, lineHeight = 16.sp)
        }
    }
}

@Composable
private fun RamadanDuaDialog(isBangla: Boolean, onDismiss: () -> Unit) {
    val colors = FinancyTheme.colors
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.card,
        title = {
            Text(
                text = if (isBangla) "🤲 রমজানের দোয়া ও নিয়ত" else "🤲 Ramadan Duas & Intentions",
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = colors.emeraldSoft),
                    border = BorderStroke(1.dp, colors.emerald.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = if (isBangla) "🌅 ইফতারের বিশুদ্ধ সুন্নাহ দোয়া (ইফতারের পর)" else "🌅 Authentic Sunnah Dua for Iftar (After Breaking Fast)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = colors.emerald
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "ذَهَبَ الظَّمَأُ وَابْتَلَّتِ الْعُرُوقُ وَثَبَتَ الأَجْرُ إِنْ شَاءَ اللَّهُ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth(),
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isBangla) "উচ্চারণ: যাহাবায যমা'উ, ওয়াবতাল্লাতিল ‘উরূক্ব, ওয়া ছাবাতাল আজরু ইনশাআল্লাহ।"
                            else "Transliteration: Dhahaba adh-dhama'u wabtallatil-'urooqu wa thabatal-ajru in sha Allah.",
                            fontSize = 11.5.sp,
                            color = colors.textPrimary
                        )
                        Text(
                            text = if (isBangla) "অর্থ: পিপাসা দূর হলো, শিরা-উপশিরা সিক্ত হলো এবং ইনশাআল্লাহ প্রতিদান নির্ধারিত হলো। [আবু দাউদ: ২৩৫৭ • সহিহ]"
                            else "Meaning: The thirst is gone, the veins are moistened, and the reward is confirmed, if Allah wills. [Abu Dawud: 2357 • Sahih]",
                            fontSize = 11.sp,
                            color = colors.textSecondary
                        )
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = colors.cardSubtle),
                    border = BorderStroke(1.dp, colors.border),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = if (isBangla) "🌌 রোজার নিয়ত" else "🌌 Intention for Fasting (Niyyah)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isBangla) "সুন্নাহসম্মত নিয়ম: নিয়তের মূল স্থান হলো অন্তর। মনে মনে এই সংকল্প করাই যথেষ্ট যে— 'আমি মহান আল্লাহর সন্তুষ্টির উদ্দেশ্যে আজকের রোজা রাখছি।'"
                            else "Sunnah principle: The true place of intention is the heart. Having a conscious intention to fast today for the pleasure of Allah is sufficient.",
                            fontSize = 11.5.sp,
                            color = colors.textSecondary
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = colors.emerald, contentColor = Color.White)
            ) { Text(if (isBangla) "ঠিক আছে" else "Close", fontWeight = FontWeight.Bold) }
        }
    )
}

@Composable
private fun CitySelectionDialog(
    currentCity: com.example.util.CityLocation,
    isBangla: Boolean,
    isHanafiAsr: Boolean,
    onDismiss: () -> Unit,
    onCitySelected: (com.example.util.CityLocation) -> Unit,
    onToggleHanafi: () -> Unit
) {
    val colors = FinancyTheme.colors
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isDetectingLocation by remember { mutableStateOf(false) }
    var detectStatusMessage by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            isDetectingLocation = true
            coroutineScope.launch {
                val city = com.example.util.LocationHelper.detectCurrentCity(context)
                isDetectingLocation = false
                if (city != null) {
                    onCitySelected(city)
                    android.widget.Toast.makeText(
                        context,
                        if (isBangla) "লোকেশন সনাক্ত হয়েছে: ${city.nameBn}" else "Location detected: ${city.nameEn}",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                } else {
                    detectStatusMessage = if (isBangla) "জিপিএস লোকেশন সনাক্ত করা যায়নি। তালিকা থেকে নির্বাচন করুন।" else "Could not detect GPS location. Select from list."
                }
            }
        } else {
            detectStatusMessage = if (isBangla) "লোকেশন পারমিশন দেওয়া হয়নি।" else "Location permission denied."
        }
    }

    fun triggerAutoDetect() {
        detectStatusMessage = null
        if (!com.example.util.LocationHelper.hasLocationPermission(context)) {
            permissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else if (!com.example.util.LocationHelper.isLocationEnabled(context)) {
            detectStatusMessage = if (isBangla) "দয়া করে ফোনের জিপিএস/লোকেশন চালু করুন।" else "Please enable device GPS/Location services."
        } else {
            isDetectingLocation = true
            coroutineScope.launch {
                val city = com.example.util.LocationHelper.detectCurrentCity(context)
                isDetectingLocation = false
                if (city != null) {
                    onCitySelected(city)
                    android.widget.Toast.makeText(
                        context,
                        if (isBangla) "লোকেশন সনাক্ত হয়েছে: ${city.nameBn}" else "Location detected: ${city.nameEn}",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                } else {
                    detectStatusMessage = if (isBangla) "জিপিএস লোকেশন সনাক্ত করা যায়নি। তালিকা থেকে নির্বাচন করুন।" else "Could not detect GPS location. Select from list."
                }
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.card,
        title = {
            Text(
                text = if (isBangla) "নামাযের স্থান ও সেটিংস" else "Location & Prayer Settings",
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
        },
        text = {
            Column {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(enabled = !isDetectingLocation) { triggerAutoDetect() },
                    color = colors.emeraldSoft,
                    border = BorderStroke(1.dp, colors.emerald.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 11.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = null,
                                tint = colors.emerald,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isDetectingLocation) {
                                    if (isBangla) "লোকেশন শনাক্ত করা হচ্ছে..." else "Detecting location..."
                                } else {
                                    if (isBangla) "স্বয়ংক্রিয় লোকেশন সনাক্তকরণ (Auto)" else "Auto Detect Location (GPS)"
                                },
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.emerald
                            )
                        }
                        if (isDetectingLocation) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = colors.emerald,
                                strokeWidth = 2.dp
                            )
                        }
                    }
                }

                if (detectStatusMessage != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = detectStatusMessage!!,
                        fontSize = 11.sp,
                        color = colors.rose,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isBangla) "অথবা শহর নির্বাচন করুন:" else "Or select city from list:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyColumn(modifier = Modifier.heightIn(max = 220.dp)) {
                    items(PrayerTimeCalculator.predefinedCities) { city ->
                        val isSelected = city.nameEn == currentCity.nameEn
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onCitySelected(city) }
                                .padding(vertical = 9.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { onCitySelected(city) },
                                colors = RadioButtonDefaults.colors(selectedColor = colors.emerald)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBangla) city.nameBn else city.nameEn,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) colors.emerald else colors.textPrimary
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = colors.border)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleHanafi() }
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isBangla) "আসর পদ্ধতি" else "Asr Calculation Method",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = colors.textPrimary
                        )
                        Text(
                            text = if (isHanafiAsr) (if (isBangla) "হানাফী (দ্বিগুণ ছায়া)" else "Hanafi (Standard in BD)")
                            else (if (isBangla) "শাফেয়ী / হাম্বলী (এক গুণ ছায়া)" else "Shafi'i / General"),
                            fontSize = 11.5.sp,
                            color = colors.textSecondary
                        )
                    }
                    Switch(
                        checked = isHanafiAsr,
                        onCheckedChange = { onToggleHanafi() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = colors.emerald)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = colors.emerald, contentColor = Color.White)
            ) {
                Text(if (isBangla) "ঠিক আছে" else "Close", fontWeight = FontWeight.Bold)
            }
        }
    )
}
