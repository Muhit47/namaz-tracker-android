package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    val context = LocalContext.current
    var showCityDialog by remember { mutableStateOf(false) }
    var showJumuahDialog by remember { mutableStateOf(false) }
    var showRamadanDuaDialog by remember { mutableStateOf(false) }

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
        containerColor = Slate50,
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
                            color = Emerald50,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Emerald100),
                            modifier = Modifier.clickable { showCityDialog = true }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = Emerald600
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = if (isBangla) uiState.selectedCity.nameBn else uiState.selectedCity.nameEn.split(" ")[0],
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = Emerald600,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = Emerald600
                                )
                            }
                        }
                    }
                },
                actions = {
                    if (isFriday) {
                        FilledTonalButton(
                            onClick = { showJumuahDialog = true },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Cyan50,
                                contentColor = Cyan700
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier
                                .height(34.dp)
                                .padding(end = 8.dp)
                        ) {
                            Text(if (isBangla) "🕌 জুমু'আ" else "🕌 Jumu'ah", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GlassCardWhite
                )
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
            // 1. Date Navigation bar
            item {
                Spacer(modifier = Modifier.height(8.dp))
                DateNavigationHeader(
                    selectedDate = uiState.selectedDate,
                    isBangla = isBangla,
                    isToday = isToday,
                    onPrevClick = { viewModel.changeDateOffset(-1) },
                    onNextClick = { viewModel.changeDateOffset(1) },
                    onTodayClick = { viewModel.resetToToday() }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 2. Ramadan / Roza Bento Card
            item {
                RamadanBentoCard(
                    uiState = uiState,
                    isBangla = isBangla,
                    onToggleRoza = { viewModel.toggleRoza() },
                    onOpenDua = { showRamadanDuaDialog = true }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 3. Hero Overview Progress Card (Framer Style)
            item {
                HeroSummaryCard(
                    uiState = uiState,
                    isBangla = isBangla
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            // 3.5 Active Waqt Live Banner
            if (isToday && activeWaqtInfo != null) {
                val activeLog = activeWaqt?.let { p ->
                    uiState.currentDayLogs.find { it.prayerName.equals(p.keyName, ignoreCase = true) }
                }
                item {
                    ActiveWaqtBanner(
                        info = activeWaqtInfo,
                        currentLog = activeLog,
                        isBangla = isBangla
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }

            // 4. Section Title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBangla) "আজকের ৫ ওয়াক্ত নামায" else "Today's 5 Daily Prayers",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Surface(
                        color = Emerald50,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(0.8.dp, Emerald100)
                    ) {
                        Text(
                            text = if (isBangla) "${DateUtil.toBanglaDigits(uiState.totalPrayedToday.toString())}/৫ সম্পন্ন"
                            else "${uiState.totalPrayedToday}/5 Done",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Emerald700,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
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
private fun ActiveWaqtBanner(
    info: ActiveWaqtInfo,
    currentLog: PrayerLogEntity?,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    val prayer = info.currentPrayer
    val isDone = currentLog?.status == "jamaah" || currentLog?.status == "on_time"

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (prayer != null) Emerald50 else Slate100
        ),
        border = BorderStroke(
            1.2.dp,
            if (prayer != null) Emerald300 else Slate300
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (prayer != null) Emerald500 else Amber500)
                )
                Column {
                    Text(
                        text = if (prayer != null) {
                            if (isBangla) "চলতি ওয়াক্ত: ${prayer.banglaName}" else "Active Waqt: ${prayer.englishName}"
                        } else {
                            if (isBangla) "পরবর্তী ওয়াক্ত: ${info.nextPrayer.banglaName}" else "Next Waqt: ${info.nextPrayer.englishName}"
                        },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (prayer != null) Emerald900 else Slate900
                    )
                    Text(
                        text = if (prayer != null) {
                            if (isBangla) "ওয়াক্ত শেষ হতে বাকি: ${PrayerTimeCalculator.formatRemainingTime(info.minutesRemaining, true)}"
                            else "Ends in: ${PrayerTimeCalculator.formatRemainingTime(info.minutesRemaining, false)}"
                        } else {
                            if (isBangla) "শুরু হতে বাকি: ${PrayerTimeCalculator.formatRemainingTime(info.minutesRemaining, true)}"
                            else "Starts in: ${PrayerTimeCalculator.formatRemainingTime(info.minutesRemaining, false)}"
                        },
                        fontSize = 11.5.sp,
                        color = if (prayer != null) Emerald700 else Slate600,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (prayer != null) {
                Surface(
                    color = if (isDone) Emerald100 else Color.White,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(0.8.dp, if (isDone) Emerald300 else Slate200)
                ) {
                    Text(
                        text = if (isDone) {
                            if (isBangla) "✓ আদায় সম্পন্ন" else "✓ Completed"
                        } else {
                            if (isBangla) "বাকি আছে" else "Pending"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDone) Emerald800 else Slate700,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun RamadanBentoCard(
    uiState: NamazUiState,
    isBangla: Boolean,
    onToggleRoza: () -> Unit,
    onOpenDua: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = GlassCardWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFFFEF3C7), Color(0xFFEDE9FE))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🌙", fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isBangla) "মাহে রমজান ও রোজা" else "Ramadan & Fasting",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = Slate900
                    )
                }

                Surface(
                    color = Emerald50,
                    shape = RoundedCornerShape(9999.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Emerald100)
                ) {
                    Text(
                        text = if (isBangla) "ইফতার: ${uiState.prayerTimes.maghrib}" else "Iftar: ${uiState.prayerTimes.maghrib}",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Emerald700,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Sehri Box
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Slate50)
                        .border(1.dp, Slate200, RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = if (isBangla) "🌌 সেহরি শেষ" else "Sehri Ends",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = Slate600
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = uiState.prayerTimes.fajr,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                    }
                }

                // Iftar Box
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Amber50)
                        .border(1.dp, Amber200, RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = if (isBangla) "🌅 ইফতারের সময়" else "Iftar Time",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = Amber700
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = uiState.prayerTimes.maghrib,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Amber700
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onToggleRoza,
                    modifier = Modifier.weight(1.3f).height(38.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.isRozaObservedToday) Emerald500 else Slate100,
                        contentColor = if (uiState.isRozaObservedToday) Color.White else Slate900
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = if (uiState.isRozaObservedToday) Icons.Default.Check else Icons.Default.Circle,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (uiState.isRozaObservedToday) (if (isBangla) "রোজা রাখা হয়েছে ✓" else "Fasting Done ✓")
                        else (if (isBangla) "রোজা ট্র্যাকার" else "Mark Fasting"),
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onOpenDua,
                    modifier = Modifier.weight(1f).height(38.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text(
                        text = if (isBangla) "🤲 দোয়া ও নিয়ত" else "🤲 Duas",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Slate900
                    )
                }
            }
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
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GlassCardWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
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
                    tint = Slate600
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
                    color = Slate900,
                    textAlign = TextAlign.Center
                )
                if (isToday) {
                    Surface(
                        color = Emerald50,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Text(
                            text = if (isBangla) "আজ" else "Today",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Emerald700,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                        )
                    }
                } else {
                    Text(
                        text = if (isBangla) "আজকে ফিরে যান" else "Return to Today",
                        fontSize = 11.sp,
                        color = Emerald600,
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
                    tint = Slate600
                )
            }
        }
    }
}

@Composable
private fun HeroSummaryCard(
    uiState: NamazUiState,
    isBangla: Boolean
) {
    val progress = (uiState.totalPrayedToday / 5f).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "progress")

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = GlassCardWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header: Hijri Date badge + Streak Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hijri Date
                Surface(
                    color = Cyan50,
                    shape = RoundedCornerShape(9999.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Cyan200)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("🌙 ", fontSize = 12.sp)
                        Text(
                            text = DateUtil.getApproximateHijriDate(
                                DateUtil.getCalendarFromIso(uiState.selectedDate),
                                isBangla
                            ),
                            color = Cyan700,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Streak Badge matching user's Framer streak-badge
                Surface(
                    color = Amber50,
                    shape = RoundedCornerShape(9999.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Amber200)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("🔥 ", fontSize = 12.sp)
                        Text(
                            text = if (isBangla) "${DateUtil.toBanglaDigits(uiState.currentStreakDays.toString())} দিন স্ট্রিক"
                            else "${uiState.currentStreakDays} Days Streak",
                            color = Amber700,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Center row: Circle progress + Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(76.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.fillMaxSize(),
                        color = Emerald500,
                        trackColor = Slate200,
                        strokeWidth = 7.dp,
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isBangla) "${DateUtil.toBanglaDigits(uiState.totalPrayedToday.toString())}/৫"
                            else "${uiState.totalPrayedToday}/5",
                            color = Slate900,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = if (isBangla) "আদায়" else "Done",
                            color = Slate600,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(18.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (uiState.totalPrayedToday == 5) {
                            if (isBangla) "মাশাআল্লাহ! আজকের সকল ফরয পূর্ণ!" else "MashaAllah! All 5 completed today!"
                        } else {
                            if (isBangla) "দৈনিক ৫ ওয়াক্ত নামায পূর্ণ করুন" else "Track your daily prayers"
                        },
                        color = Slate900,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isBangla) "সপ্তাহের সমাপ্তির হার: ${DateUtil.toBanglaDigits(uiState.weeklyCompletionRate.toString())}%"
                        else "Weekly consistency: ${uiState.weeklyCompletionRate}%",
                        color = Slate600,
                        fontSize = 12.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Motivational Ayah Quote box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Slate100)
                    .padding(10.dp)
            ) {
                Text(
                    text = if (isBangla) "“নিশ্চয় নামায মানুষকে অশ্লীল ও মন্দ কাজ থেকে বিরত রাখে।” — [সূরা আন-আনকাবুত: ৪৫]"
                    else "“Indeed, prayer prohibits immorality and wrongdoing.” — [Surah Al-Ankabut: 45]",
                    color = Slate600,
                    fontSize = 11.5.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun JumuahDeedsDialog(isBangla: Boolean, onDismiss: () -> Unit) {
    var check1 by remember { mutableStateOf(false) }
    var check2 by remember { mutableStateOf(false) }
    var check3 by remember { mutableStateOf(false) }
    var check4 by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Text(
                text = if (isBangla) "🕌 জুমু'আর বিশেষ আমলসমূহ" else "🕌 Special Virtues & Deeds of Jumu'ah",
                fontWeight = FontWeight.Bold,
                color = Slate900
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
                colors = ButtonDefaults.buttonColors(containerColor = Emerald500, contentColor = Color.White)
            ) { Text(if (isBangla) "ঠিক আছে" else "Close", fontWeight = FontWeight.Bold) }
        }
    )
}

@Composable
private fun DeedCheckboxItem(title: String, desc: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
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
            colors = CheckboxDefaults.colors(checkedColor = Emerald500),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = Slate900)
            Text(desc, fontSize = 11.sp, color = Slate600, lineHeight = 16.sp)
        }
    }
}

@Composable
private fun RamadanDuaDialog(isBangla: Boolean, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Text(
                text = if (isBangla) "🤲 রমজানের দোয়া ও নিয়ত" else "🤲 Ramadan Duas & Intentions",
                fontWeight = FontWeight.Bold,
                color = Slate900
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Emerald50),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Emerald100),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = if (isBangla) "🌅 ইফতারের বিশুদ্ধ সুন্নাহ দোয়া (ইফতারের পর)" else "🌅 Authentic Sunnah Dua for Iftar (After Breaking Fast)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Emerald700
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "ذَهَبَ الظَّمَأُ وَابْتَلَّتِ الْعُرُوقُ وَثَبَتَ الأَجْرُ إِنْ شَاءَ اللَّهُ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth(),
                            color = Slate900
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isBangla) "উচ্চারণ: যাহাবায যমা'উ, ওয়াবতাল্লাতিল ‘উরূক্ব, ওয়া ছাবাতাল আজরু ইনশাআল্লাহ।"
                            else "Transliteration: Dhahaba adh-dhama'u wabtallatil-'urooqu wa thabatal-ajru in sha Allah.",
                            fontSize = 11.5.sp,
                            color = Slate900
                        )
                        Text(
                            text = if (isBangla) "অর্থ: পিপাসা দূর হলো, শিরা-উপশিরা সিক্ত হলো এবং ইনশাআল্লাহ প্রতিদান নির্ধারিত হলো। [আবু দাউদ: ২৩৫৭ • সহিহ]"
                            else "Meaning: The thirst is gone, the veins are moistened, and the reward is confirmed, if Allah wills. [Abu Dawud: 2357 • Sahih]",
                            fontSize = 11.sp,
                            color = Slate600
                        )
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Slate100),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = if (isBangla) "🌌 রোজার নিয়ত" else "🌌 Intention for Fasting (Niyyah)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Slate900
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isBangla) "সুন্নাহসম্মত নিয়ম: নিয়তের মূল স্থান হলো অন্তর। মনে মনে এই সংকল্প করাই যথেষ্ট যে— 'আমি মহান আল্লাহর সন্তুষ্টির উদ্দেশ্যে আজকের রোজা রাখছি।'"
                            else "Sunnah principle: The true place of intention is the heart. Having a conscious intention to fast today for the pleasure of Allah is sufficient.",
                            fontSize = 11.5.sp,
                            color = Slate600
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Emerald500, contentColor = Color.White)
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
            detectStatusMessage = if (isBangla) "লোকেশন পারমিশন দেওয়া হয়নি।" else "Location permission denied."
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
        containerColor = Color.White,
        title = {
            Text(
                text = if (isBangla) "নামাযের স্থান ও সেটিংস" else "Location & Prayer Settings",
                fontWeight = FontWeight.Bold,
                color = Slate900
            )
        },
        text = {
            Column {
                // Auto detect button card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(enabled = !isDetectingLocation) { triggerAutoDetect() },
                    color = Emerald50,
                    border = BorderStroke(1.dp, Emerald100),
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
                                tint = Emerald600,
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
                                color = Emerald700
                            )
                        }
                        if (isDetectingLocation) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Emerald600,
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
                        color = Color(0xFFDC2626),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isBangla) "অথবা শহর নির্বাচন করুন:" else "Or select city from list:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Slate800
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
                                colors = RadioButtonDefaults.colors(selectedColor = Emerald500)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBangla) city.nameBn else city.nameEn,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Emerald700 else Slate800
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Slate200)

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
                            color = Slate900
                        )
                        Text(
                            text = if (isHanafiAsr) (if (isBangla) "হানাফী (দ্বিগুণ ছায়া)" else "Hanafi (Standard in BD)")
                            else (if (isBangla) "শাফেয়ী / হাম্বলী (এক গুণ ছায়া)" else "Shafi'i / General"),
                            fontSize = 11.5.sp,
                            color = Slate800
                        )
                    }
                    Switch(
                        checked = isHanafiAsr,
                        onCheckedChange = { onToggleHanafi() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Emerald500)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Emerald500, contentColor = Color.White)
            ) {
                Text(if (isBangla) "ঠিক আছে" else "Close", fontWeight = FontWeight.Bold)
            }
        }
    )
}
