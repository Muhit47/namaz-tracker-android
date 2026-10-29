package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.IosGlassPillButton
import com.example.ui.theme.*
import com.example.util.AppVersionHelper
import com.example.util.BackupRestoreHelper
import com.example.ui.viewmodel.NamazUiState
import com.example.ui.viewmodel.NamazViewModel
import com.example.widget.WidgetHelper

enum class MoreSubScreen {
    INDEX,
    SETTINGS,
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
    val colors = FinancyTheme.colors
    val isBangla = uiState.isBangla
    val isDark = colors.isDark
    val context = LocalContext.current
    var showRestoreDialog by remember { mutableStateOf(false) }
    var restoreInputText by remember { mutableStateOf("") }
    var showPerWaqtDialog by remember { mutableStateOf(false) }
    var selectedOverrideWaqtKey by remember { mutableStateOf<String?>(null) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.togglePrayerNotifications(context)
            Toast.makeText(
                context,
                if (isBangla) "ওয়াক্ত নোটিফিকেশন চালু করা হয়েছে" else "Prayer notifications enabled",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                context,
                if (isBangla) "নোটিফিকেশন পারমিশন প্রয়োজন" else "Notification permission is required",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    BackHandler(enabled = activeSubScreen != MoreSubScreen.INDEX) {
        onNavigateSub(MoreSubScreen.INDEX)
    }

    // Per-Waqt Sound Customization Dialog
    if (showPerWaqtDialog) {
        val waqts = listOf(
            Triple("fajr", if (isBangla) "ফজর" else "Fajr", "Fajr"),
            Triple("dhuhr", if (isBangla) "যোহর" else "Dhuhr", "Dhuhr"),
            Triple("asr", if (isBangla) "আসর" else "Asr", "Asr"),
            Triple("maghrib", if (isBangla) "মাগরিব" else "Maghrib", "Maghrib"),
            Triple("isha", if (isBangla) "ইশা" else "Isha", "Isha")
        )

        AlertDialog(
            onDismissRequest = {
                showPerWaqtDialog = false
                selectedOverrideWaqtKey = null
            },
            containerColor = colors.surfaceCard,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = if (isBangla) "ওয়াক্তভিত্তিক সাউন্ড কাস্টমাইজেশন" else "Per-Waqt Sound Customization",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = colors.textPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isBangla) "যেকোনো নির্দিষ্ট ওয়াক্তের জন্য আলাদা সাউন্ড বেছে নিন:" else "Select specific alert tone for each prayer:",
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )

                    waqts.forEach { (key, nameBn, nameEn) ->
                        val currentOverride = uiState.prayerOverrides[key]
                        val displaySound = when (currentOverride) {
                            "full_azan" -> if (isBangla) "🔊 পূর্ণ আজান" else "🔊 Full Azan"
                            "short_takbeer" -> if (isBangla) "🔔 সংক্ষিপ্ত তাকবীর" else "🔔 Takbeer"
                            "system_tone" -> if (isBangla) "🎵 সাধারণ টোন" else "🎵 System Tone"
                            "silent" -> if (isBangla) "🔕 নীরব" else "🔕 Silent"
                            else -> if (isBangla) "🌐 গ্লোবাল ডিফল্ট" else "🌐 Global Default"
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedOverrideWaqtKey = key },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = colors.surfaceSubtle),
                            border = BorderStroke(1.dp, if (selectedOverrideWaqtKey == key) colors.emerald else colors.borderSubtle)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isBangla) nameBn else nameEn,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp,
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = displaySound,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.emerald
                                )
                            }
                        }
                    }

                    if (selectedOverrideWaqtKey != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isBangla) "সাউন্ড নির্বাচন করুন:" else "Choose sound:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = colors.textPrimary
                        )

                        val soundOptions = listOf(
                            null to (if (isBangla) "ডিফল্ট মোড ব্যবহার করুন" else "Use Global Default"),
                            "full_azan" to (if (isBangla) "🔊 পূর্ণ আজান" else "🔊 Full Azan"),
                            "short_takbeer" to (if (isBangla) "🔔 সংক্ষিপ্ত তাকবীর" else "🔔 Short Takbeer"),
                            "system_tone" to (if (isBangla) "🎵 সাধারণ টোন" else "🎵 System Tone"),
                            "silent" to (if (isBangla) "🔕 নীরব" else "🔕 Silent")
                        )

                        soundOptions.forEach { (mode, label) ->
                            TextButton(
                                onClick = {
                                    viewModel.setPrayerOverride(selectedOverrideWaqtKey!!, mode)
                                    selectedOverrideWaqtKey = null
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.emerald,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Start
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showPerWaqtDialog = false
                    selectedOverrideWaqtKey = null
                }) {
                    Text(
                        if (isBangla) "সম্পন্ন" else "Done",
                        fontWeight = FontWeight.Bold,
                        color = colors.emerald
                    )
                }
            }
        )
    }

    // Restore Backup JSON Dialog
    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            containerColor = colors.surfaceCard,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = if (isBangla) "ডাটা রিস্টোর করুন" else "Restore Data",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = colors.textPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isBangla) "পূর্বে ব্যাকআপ করা JSON টেক্সটটি নিচে পেস্ট করুন:" else "Paste your exported JSON backup text below:",
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )
                    OutlinedTextField(
                        value = restoreInputText,
                        onValueChange = { restoreInputText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        placeholder = {
                            Text(
                                text = "{\"appName\": \"NamazTracker\", ...}",
                                fontSize = 11.sp,
                                color = colors.textSecondary.copy(alpha = 0.5f)
                            )
                        },
                        textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.emerald,
                            unfocusedBorderColor = colors.borderSubtle
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.restoreBackup(restoreInputText) { success, _ ->
                            if (success) {
                                showRestoreDialog = false
                                restoreInputText = ""
                                Toast.makeText(
                                    context,
                                    if (isBangla) "ডাটা সফলভাবে রিস্টোর হয়েছে!" else "Data restored successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    if (isBangla) "ভুল ফরম্যাট! সঠিক JSON ব্যাকআপ দিন।" else "Invalid backup JSON format!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.emerald)
                ) {
                    Text(if (isBangla) "রিস্টোর নিশ্চিত করুন" else "Confirm Restore", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreDialog = false }) {
                    Text(if (isBangla) "বাতিল" else "Cancel", color = colors.textSecondary)
                }
            }
        )
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
        MoreSubScreen.SETTINGS -> SettingsScreen(
            uiState = uiState,
            viewModel = viewModel,
            isBangla = isBangla,
            isDark = isDark,
            colors = colors,
            context = context,
            onBack = { onNavigateSub(MoreSubScreen.INDEX) },
            onOpenPerWaqt = { showPerWaqtDialog = true },
            onOpenRestore = { showRestoreDialog = true },
            onRequestNotificationPermission = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    viewModel.togglePrayerNotifications(context)
                }
            },
            modifier = modifier
        )
        MoreSubScreen.INDEX -> MoreHubScreen(
            uiState = uiState,
            isBangla = isBangla,
            isDark = isDark,
            colors = colors,
            onNavigateSub = onNavigateSub,
            modifier = modifier
        )
    }
}

// =======================================================================
// SUB-SCREEN 1: MORE HUB (Clean, Modern, Colourful iOS 18 Heavy Glass)
// =======================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreHubScreen(
    uiState: NamazUiState,
    isBangla: Boolean,
    isDark: Boolean,
    colors: FinancyColorTokens,
    onNavigateSub: (MoreSubScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    var showHijriDialog by remember { mutableStateOf(false) }

    if (showHijriDialog) {
        HijriCalendarDialog(
            isBangla = isBangla,
            isDark = isDark,
            colors = colors,
            onDismiss = { showHijriDialog = false }
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
                                text = if (isBangla) "দ্বীনি আমল ও সেটিংস হাব" else "Islamic Hub & Settings",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = colors.textPrimary
                            )
                            Text(
                                text = if (isBangla) "জ্ঞান, আমল ও কাস্টমাইজেশন" else "Knowledge, tools & preferences",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = colors.textSecondary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = colors.emerald.copy(alpha = if (isDark) 0.22f else 0.12f),
                            border = BorderStroke(1.dp, colors.emerald.copy(alpha = 0.35f)),
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Text(
                                text = AppVersionHelper.getDisplayVersion(isBangla),
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.emerald,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
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
            contentPadding = PaddingValues(top = 10.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. ISLAMIC TRACKER TOOLS (2x2 Bento Quick Grid)
            item {
                val todayHijri = remember { DateUtil.getHijriDate(Calendar.getInstance()) }

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isBangla) "আমল ও ট্র্যাকিং টুলস" else "Islamic Tracker Tools",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = colors.textPrimary
                    )

                    // Row 1: Analytics & Digital Tasbih
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Analytics
                        HubToolCard(
                            icon = "📊",
                            title = if (isBangla) "অ্যানালিটিক্স" else "Analytics",
                            badge = if (isBangla) "${uiState.currentStreakDays} দিন স্ট্রিক" else "${uiState.currentStreakDays}d streak",
                            accentColor = Color(0xFFF97316),
                            onClick = { onNavigateSub(MoreSubScreen.ANALYTICS) },
                            modifier = Modifier.weight(1f),
                            isDark = isDark
                        )

                        // Tasbih
                        HubToolCard(
                            icon = "📿",
                            title = if (isBangla) "ডিজিটাল তাসবীহ" else "Digital Tasbih",
                            badge = if (isBangla) "হ্যাপটিক" else "Haptic",
                            accentColor = Color(0xFF0284C7),
                            onClick = { onNavigateSub(MoreSubScreen.TASBIH) },
                            modifier = Modifier.weight(1f),
                            isDark = isDark
                        )
                    }

                    // Row 2: Qibla Compass & Hijri Calendar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Qibla
                        HubToolCard(
                            icon = "🧭",
                            title = if (isBangla) "কিবলা কম্পাস" else "Qibla Compass",
                            badge = "${uiState.qiblaBearing.toInt()}°",
                            accentColor = Color(0xFF6366F1),
                            onClick = { onNavigateSub(MoreSubScreen.QIBLA) },
                            modifier = Modifier.weight(1f),
                            isDark = isDark
                        )

                        // Hijri Calendar
                        HubToolCard(
                            icon = "🌙",
                            title = if (isBangla) "হিজরি ক্যালেন্ডার" else "Hijri Calendar",
                            badge = if (isBangla) "${DateUtil.toBanglaDigits(todayHijri.day.toString())} ${todayHijri.monthNameBn}"
                            else "${todayHijri.day} ${todayHijri.monthNameEn}",
                            accentColor = Color(0xFF10B981),
                            onClick = { showHijriDialog = true },
                            modifier = Modifier.weight(1f),
                            isDark = isDark
                        )
                    }
                }
            }

            // 3. DEDICATED APP SETTINGS CARD
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateSub(MoreSubScreen.SETTINGS) },
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                    border = BorderStroke(1.2.dp, colors.emerald.copy(alpha = 0.35f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = colors.emerald.copy(alpha = if (isDark) 0.22f else 0.14f),
                                    modifier = Modifier.size(46.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("⚙️", fontSize = 22.sp)
                                    }
                                }

                                Column {
                                    Text(
                                        text = if (isBangla) "অ্যাপ সেটিংস ও পছন্দসমূহ" else "App Settings & Preferences",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp,
                                        color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                                    )
                                    Text(
                                        text = if (isBangla) "থিম, আজান নোটিফিকেশন, উইজেট ও ব্যাকআপ" else "Theme, azan alerts, widgets & backup",
                                        fontSize = 11.5.sp,
                                        color = colors.textSecondary
                                    )
                                }
                            }

                            Text("➔", fontWeight = FontWeight.Bold, color = colors.emerald, fontSize = 16.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(thickness = 0.8.dp, color = colors.borderSubtle)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (uiState.prayerNotificationsEnabled)
                                    (if (isBangla) "✓ ৫ ওয়াক্ত আজান অ্যালার্ট সক্রিয়" else "✓ 5 Daily Azan alerts active")
                                else
                                    (if (isBangla) "✕ আজান অ্যালার্ট নিষ্ক্রিয়" else "✕ Azan alerts off"),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.prayerNotificationsEnabled) colors.emerald else colors.textSecondary
                            )

                            Text(
                                text = if (isBangla) "কাস্টমাইজ করুন ➔" else "Customize ➔",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.emerald
                            )
                        }
                    }
                }
            }


        }
    }
}

// =======================================================================
// SUB-SCREEN 2: DEDICATED SETTINGS SCREEN (With iOS Toggle Switches)
// =======================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: NamazUiState,
    viewModel: NamazViewModel,
    isBangla: Boolean,
    isDark: Boolean,
    colors: FinancyColorTokens,
    context: android.content.Context,
    onBack: () -> Unit,
    onOpenPerWaqt: () -> Unit,
    onOpenRestore: () -> Unit,
    onRequestNotificationPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isBangla) "অ্যাপ সেটিংস ও পছন্দসমূহ" else "Settings & Preferences",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = if (isBangla) "থিম, ভাষা, নোটিফিকেশন ও ব্যাকআপ" else "Theme, language, notifications & backup",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = colors.textSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = colors.textPrimary
                        )
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
            contentPadding = PaddingValues(top = 10.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // GROUP 1: APPEARANCE & MADHAB
            item {
                Text(
                    text = if (isBangla) "🎨 ডিসপ্লে ও মাযহাব" else "🎨 Display & Madhab",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = AmberAccentDark
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                    border = BorderStroke(1.dp, if (isDark) colors.border else Color(0xFFCBD5E1))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // 1. Dark Mode Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isBangla) "ডার্ক থিম" else "Dark Theme",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp,
                                    color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                                )
                                Text(
                                    text = if (uiState.isDarkMode)
                                        (if (isBangla) "গাঢ় থিম সক্রিয়" else "Dark theme active")
                                    else
                                        (if (isBangla) "উজ্জ্বল হালকা থিম সক্রিয়" else "Light theme active"),
                                    fontSize = 11.5.sp,
                                    color = colors.textSecondary
                                )
                            }

                            Switch(
                                checked = uiState.isDarkMode,
                                onCheckedChange = { viewModel.toggleDarkMode() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = colors.emerald,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1)
                                )
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = colors.borderSubtle)

                        // 2. Language Capsule Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isBangla) "অ্যাপের ভাষা" else "Language",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp,
                                    color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                                )
                                Text(
                                    text = if (isBangla) "বাংলা এবং ইংরেজির মধ্যে পরিবর্তন" else "Switch between Bengali & English",
                                    fontSize = 11.5.sp,
                                    color = colors.textSecondary
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = if (isDark) Color(0xFF131D18) else Color(0xFFF1F5F9),
                                border = BorderStroke(1.dp, if (isDark) Color(0xFF22352C) else Color(0xFFCBD5E1)),
                                modifier = Modifier.clip(RoundedCornerShape(999.dp))
                            ) {
                                Row(
                                    modifier = Modifier.padding(3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // English
                                    val isEn = !isBangla
                                    Surface(
                                        shape = RoundedCornerShape(999.dp),
                                        color = if (isEn) colors.emerald else Color.Transparent,
                                        modifier = Modifier.clickable { viewModel.setLanguage(false) }
                                    ) {
                                        Text(
                                            text = "English",
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                            fontSize = 11.5.sp,
                                            fontWeight = if (isEn) FontWeight.ExtraBold else FontWeight.Bold,
                                            color = if (isEn) Color.White else if (isDark) Color(0xFF94A3B8) else Color(0xFF334155)
                                        )
                                    }

                                    // Bangla
                                    Surface(
                                        shape = RoundedCornerShape(999.dp),
                                        color = if (isBangla) colors.emerald else Color.Transparent,
                                        modifier = Modifier.clickable { viewModel.setLanguage(true) }
                                    ) {
                                        Text(
                                            text = "বাংলা",
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                            fontSize = 11.5.sp,
                                            fontWeight = if (isBangla) FontWeight.ExtraBold else FontWeight.Bold,
                                            color = if (isBangla) Color.White else if (isDark) Color(0xFF94A3B8) else Color(0xFF334155)
                                        )
                                    }
                                }
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = colors.borderSubtle)

                        // 3. Asr Method Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isBangla) "আসর নামাজের মাযহাব সময়" else "Asr Prayer Calculation",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp,
                                    color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                                )
                                Text(
                                    text = if (uiState.isHanafiAsr)
                                        (if (isBangla) "হানাফী মাযহাব (ছায়া দ্বিগুণ)" else "Hanafi Method (2x shadow)")
                                    else
                                        (if (isBangla) "শাফেয়ী/মালিকী/হাম্বলী মাযহাব" else "Shafi'i/Maliki/Hanbali Method"),
                                    fontSize = 11.5.sp,
                                    color = colors.textSecondary
                                )
                            }

                            Switch(
                                checked = uiState.isHanafiAsr,
                                onCheckedChange = { viewModel.toggleHanafiAsr() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = colors.emerald,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1)
                                )
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = colors.borderSubtle)

                        // 4. Lockscreen Tracker Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isBangla) "লকস্ক্রিন লাইভ প্রেয়ার ট্র্যাকার" else "Lockscreen Live Prayer Tracker",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp,
                                    color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                                )
                                Text(
                                    text = if (isBangla) "লকস্ক্রিন ও স্ট্যাটাসবারে বর্তমান নামাজের লাইভ কাউন্টডাউন"
                                    else "Live remaining time on lockscreen & status bar",
                                    fontSize = 11.5.sp,
                                    color = colors.textSecondary
                                )
                            }

                            Switch(
                                checked = uiState.isLockscreenTrackerEnabled,
                                onCheckedChange = { viewModel.toggleLockscreenTracker(context) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = colors.emerald,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1)
                                )
                            )
                        }
                    }
                }
            }

            // GROUP 2: AZAN & NOTIFICATIONS
            item {
                Text(
                    text = if (isBangla) "🔔 আজান ও নোটিফিকেশন" else "🔔 Azan & Notifications",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = CyanAccent
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                    border = BorderStroke(1.dp, if (isDark) colors.border else Color(0xFFCBD5E1))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Master Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isBangla) "৫ ওয়াক্ত আজান অ্যালার্ট" else "5 Daily Azan Alerts",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp,
                                    color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                                )
                                Text(
                                    text = if (isBangla) "ওয়াক্ত শুরু হওয়া মাত্রই নোটিফিকেশন" else "Instant notifications when waqt starts",
                                    fontSize = 11.5.sp,
                                    color = colors.textSecondary
                                )
                            }

                            Switch(
                                checked = uiState.prayerNotificationsEnabled,
                                onCheckedChange = { onRequestNotificationPermission() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = colors.emerald,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1)
                                )
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = colors.borderSubtle)

                        // Sound Mode Selector
                        Text(
                            text = if (isBangla) "ডিফল্ট অ্যালার্ট সাউন্ড:" else "Default Alert Sound:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val soundModes = listOf(
                            "full_azan" to (if (isBangla) "🔊 আজান" else "🔊 Azan"),
                            "short_takbeer" to (if (isBangla) "🔔 তাকবীর" else "🔔 Takbeer"),
                            "system_tone" to (if (isBangla) "🎵 টোন" else "🎵 Tone"),
                            "silent" to (if (isBangla) "🔕 নীরব" else "🔕 Silent")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            soundModes.forEach { (mode, label) ->
                                val selected = uiState.notificationSoundMode == mode
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { viewModel.setNotificationSoundMode(mode) },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (selected) colors.emerald else if (isDark) Color(0xFF131D18) else Color(0xFFF1F5F9),
                                    border = BorderStroke(1.dp, if (selected) colors.emerald else if (isDark) Color(0xFF22352C) else Color(0xFFCBD5E1))
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Bold,
                                            fontSize = 10.5.sp,
                                            color = if (selected) Color.White else if (isDark) Color(0xFFCBD5E1) else Color(0xFF0F172A)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Muezzin Choice
                        Text(
                            text = if (isBangla) "মুয়াজ্জিন নির্বাচন:" else "Muezzin Selection:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val muezzins = listOf(
                                "makkah" to (if (isBangla) "🕌 মক্কা শরীফ" else "🕌 Makkah"),
                                "madinah" to (if (isBangla) "🕌 মদীনা শরীফ" else "🕌 Madinah")
                            )

                            muezzins.forEach { (key, label) ->
                                val selected = uiState.selectedMuezzin == key
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { viewModel.setSelectedMuezzin(key) },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (selected) colors.emerald.copy(alpha = if (isDark) 0.22f else 0.15f) else if (isDark) Color(0xFF131D18) else Color(0xFFF1F5F9),
                                    border = BorderStroke(1.dp, if (selected) colors.emerald else if (isDark) Color(0xFF22352C) else Color(0xFFCBD5E1))
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 9.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (selected) colors.emerald else if (isDark) Color(0xFFCBD5E1) else Color(0xFF334155)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Per-Waqt Customize Button
                        OutlinedButton(
                            onClick = onOpenPerWaqt,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, colors.emerald.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = if (isBangla) "⚙️ ওয়াক্তভিত্তিক আলাদা সাউন্ড কাস্টমাইজ" else "⚙️ Per-Waqt Sound Customization",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.emerald
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Test Azan Button
                        Button(
                            onClick = {
                                viewModel.sendTestNotification(context)
                                Toast.makeText(
                                    context,
                                    if (isBangla) "টেস্ট আজান নোটিফিকেশন পাঠানো হয়েছে" else "Test Azan notification sent",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9)
                            ),
                            border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1))
                        ) {
                            Text(
                                text = if (isBangla) "🧪 আজান ও নোটিফিকেশন পরীক্ষা করুন" else "🧪 Test Azan Notification",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                            )
                        }
                    }
                }
            }

            // GROUP 3: WIDGETS & BACKUP
            item {
                Text(
                    text = if (isBangla) "📱 উইজেট ও ডাটা ব্যাকআপ" else "📱 Widgets & Data Backup",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = VioletAccent
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                    border = BorderStroke(1.dp, if (isDark) colors.border else Color(0xFFCBD5E1))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isBangla) "হোম স্ক্রিন ৪টি উইজেট স্যুট" else "Home Screen 4 Widgets Suite",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp,
                                    color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                                )
                                Text(
                                    text = if (isBangla) "ওয়াক্ত ক্যাপসুল, ৫-ওয়াক্ত বোর্ড, রমজান ও হাদিস" else "Waqt capsule, 5-waqt board, Ramadan & hadith",
                                    fontSize = 11.5.sp,
                                    color = colors.textSecondary
                                )
                            }

                            Button(
                                onClick = {
                                    WidgetHelper.updateAllWidgets(context)
                                    Toast.makeText(
                                        context,
                                        if (isBangla) "হোম স্ক্রিনের ৪টি উইজেটই আপডেট করা হয়েছে" else "All 4 widgets refreshed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = VioletAccent.copy(alpha = 0.20f))
                            ) {
                                Text(
                                    text = if (isBangla) "রিফ্রেশ ↻" else "Refresh ↻",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color(0xFFD8B4FE) else Color(0xFF6B21A8)
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = colors.borderSubtle)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Backup
                            Button(
                                onClick = {
                                    viewModel.exportBackup { json ->
                                        BackupRestoreHelper.shareBackup(context, json)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colors.emerald.copy(alpha = if (isDark) 0.25f else 0.15f)
                                ),
                                border = BorderStroke(1.dp, colors.emerald.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = if (isBangla) "📤 ব্যাকআপ JSON" else "📤 Backup JSON",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = colors.emerald
                                )
                            }

                            // Restore
                            Button(
                                onClick = onOpenRestore,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9)
                                ),
                                border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1))
                            ) {
                                Text(
                                    text = if (isBangla) "📥 ডাটা রিস্টোর" else "📥 Restore Data",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                                )
                            }
                        }
                    }
                }
            }

            // GROUP 4: DEVELOPER PROFILE CARD
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                    border = BorderStroke(1.dp, if (isDark) colors.border else Color(0xFFCBD5E1))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = colors.emerald,
                            modifier = Modifier.size(52.dp),
                            shadowElevation = 4.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("M", fontWeight = FontWeight.Black, fontSize = 22.sp, color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (isBangla) "মুহিত (Muhit)" else "Muhit",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                        )
                        Text(
                            text = "Mobile App Designer & Software Engineer",
                            fontSize = 11.5.sp,
                            color = colors.textSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DeveloperLinkPill(
                                icon = "🐙",
                                label = "GitHub",
                                url = "https://github.com/Muhit47",
                                context = context,
                                isDark = isDark
                            )

                            DeveloperLinkPill(
                                icon = "⭐",
                                label = "Repo",
                                url = "https://github.com/Muhit47",
                                context = context,
                                isDark = isDark
                            )

                            DeveloperLinkPill(
                                icon = "✉️",
                                label = "Email",
                                url = "mailto:muhit.dev@gmail.com",
                                context = context,
                                isDark = isDark
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- Helper Components ---
@Composable
fun HubToolCard(
    icon: String,
    title: String,
    badge: String,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDark: Boolean
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF131D18) else Color(0xFFFFFFFF)
        ),
        border = BorderStroke(1.dp, if (isDark) Color(0xFF22352C) else Color(0xFFCBD5E1))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = accentColor.copy(alpha = if (isDark) 0.22f else 0.14f),
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(icon, fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A),
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = badge,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
        }
    }
}

@Composable
fun DeveloperLinkPill(
    icon: String,
    label: String,
    url: String,
    context: android.content.Context,
    isDark: Boolean
) {
    Surface(
        modifier = Modifier.clickable {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "Could not open link", Toast.LENGTH_SHORT).show()
            }
        },
        shape = RoundedCornerShape(999.dp),
        color = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
        border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1)),
        shadowElevation = if (!isDark) 1.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(icon, fontSize = 12.sp)
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
            )
        }
    }
}

/**
 * Elegant Interactive Hijri Calendar Dialog with Current Month Grid & Sunnah Fasting Information
 */
@Composable
private fun HijriCalendarDialog(
    isBangla: Boolean,
    isDark: Boolean,
    colors: FinancyColorTokens,
    onDismiss: () -> Unit
) {
    val calendar = remember { Calendar.getInstance() }
    val todayHijri = remember { DateUtil.getHijriDate(calendar) }
    val todayGregorianStr = remember(isBangla) { DateUtil.formatDisplayDate(DateUtil.getTodayIso(), isBangla) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.surfaceCard,
        shape = RoundedCornerShape(26.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = colors.emerald.copy(alpha = if (isDark) 0.22f else 0.14f),
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🌙", fontSize = 18.sp)
                    }
                }
                Column {
                    Text(
                        text = if (isBangla) "হিজরি ক্যালেন্ডার" else "Hijri Calendar",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                        color = colors.textPrimary
                    )
                    Text(
                        text = if (isBangla) "ইসলামিক চান্দ্র বর্ষপঞ্জি" else "Islamic Lunar Calendar",
                        fontSize = 11.sp,
                        color = colors.textSecondary
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Today's Highlighted Hijri Hero Card
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = if (isDark) Color(0xFF064E3B).copy(alpha = 0.35f) else Color(0xFFECFDF5),
                    border = BorderStroke(1.2.dp, colors.emerald.copy(alpha = 0.45f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isBangla) "আজকের হিজরি তারিখ" else "Today's Hijri Date",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.emerald
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isBangla)
                                "${DateUtil.toBanglaDigits(todayHijri.day.toString())} ${todayHijri.monthNameBn}, ${DateUtil.toBanglaDigits(todayHijri.year.toString())} হিজরি"
                            else
                                "${todayHijri.day} ${todayHijri.monthNameEn}, ${todayHijri.year} AH",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isDark) Color(0xFF6EE7B7) else Color(0xFF065F46),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = todayGregorianStr,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.textSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Month Grid (1 to 29/30) with Ayyam al-Beed marked
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBangla) "${todayHijri.monthNameBn} মাসের দিনসমূহ" else "Days of ${todayHijri.monthNameEn}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = colors.emeraldSoft
                        ) {
                            Text(
                                text = if (isBangla) "২৯ / ৩০ দিন" else "29 / 30 Days",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.emerald,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 6 columns x 5 rows grid (1..30 days)
                    val daysList = (1..30).toList()
                    val rows = daysList.chunked(6)

                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        rows.forEach { rowDays ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                rowDays.forEach { d ->
                                    val isCurrentDay = d == todayHijri.day
                                    val isAyyamAlBeed = d in 13..15

                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = when {
                                            isCurrentDay -> colors.emerald
                                            isAyyamAlBeed -> if (isDark) Color(0xFF0E7490).copy(alpha = 0.35f) else Color(0xFFE0F2FE)
                                            else -> if (isDark) Color.White.copy(alpha = 0.05f) else Color(0xFFF1F5F9)
                                        },
                                        border = BorderStroke(
                                            if (isCurrentDay || isAyyamAlBeed) 1.dp else 0.5.dp,
                                            when {
                                                isCurrentDay -> colors.emerald
                                                isAyyamAlBeed -> Color(0xFF38BDF8)
                                                else -> if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE2E8F0)
                                            }
                                        ),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier.padding(vertical = 6.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = if (isBangla) DateUtil.toBanglaDigits(d.toString()) else d.toString(),
                                                fontSize = 11.sp,
                                                fontWeight = if (isCurrentDay) FontWeight.Black else FontWeight.Bold,
                                                color = when {
                                                    isCurrentDay -> Color.White
                                                    isAyyamAlBeed -> if (isDark) Color(0xFF7DD3FC) else Color(0xFF0369A1)
                                                    else -> colors.textPrimary
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Sunnah Fasting Information Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isDark) Color(0xFF1E293B).copy(alpha = 0.6f) else Color(0xFFF8FAFC),
                    border = BorderStroke(0.8.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text("✨", fontSize = 11.sp)
                            Text(
                                text = if (isBangla) "আইয়ামে বীজের সুন্নাত রোজা (১৩, ১৪, ১৫)" else "Sunnah Fasting: Ayyam al-Beed (13, 14, 15)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7)
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = if (isBangla)
                                "প্রতি হিজরি মাসের ১৩, ১৪ ও ১৫ তারিখ রোজা রাখা সুন্নাত ও সারা বছর রোজা রাখার সমতুল্য সওয়াব। (সহিহ বুখারি: ১৯৮১)"
                            else
                                "Fasting on the 13th, 14th & 15th of each lunar month is Sunnah and equals a lifetime of fasting. (Bukhari: 1981)",
                            fontSize = 10.5.sp,
                            color = colors.textSecondary,
                            lineHeight = 15.sp
                        )
                    }
                }

                // Moon Sighting Note
                Text(
                    text = if (isBangla)
                        "⚠️ চাঁদ দেখার ওপর নির্ভর করে হিজরি তারিখ ১ দিন পরিবর্তন হতে পারে।"
                    else
                        "⚠️ Hijri dates may vary by 1 day depending on local moon sighting.",
                    fontSize = 10.sp,
                    color = colors.textMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.emerald,
                    contentColor = Color.White
                )
            ) {
                Text(if (isBangla) "ঠিক আছে" else "Close", fontWeight = FontWeight.Bold)
            }
        }
    )
}
