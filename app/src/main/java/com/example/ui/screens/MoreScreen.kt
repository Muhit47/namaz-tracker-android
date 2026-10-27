package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import com.example.util.BackupRestoreHelper
import com.example.util.DateUtil
import com.example.ui.viewmodel.NamazUiState
import com.example.ui.viewmodel.NamazViewModel
import com.example.widget.PrayerWidgetProvider

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
    val colors = FinancyTheme.colors
    val isBangla = uiState.isBangla
    val context = LocalContext.current
    var showRestoreDialog by remember { mutableStateOf(false) }
    var restoreInputText by remember { mutableStateOf("") }

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
                                        text = if (isBangla) "অ্যাপ সেটিংস ও পছন্দসমূহ" else "Settings & Preferences",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary
                                    )
                                    Text(
                                        text = if (isBangla) "থিম, ভাষা, নোটিফিকেশন ও ব্যাকআপ" else "Theme, language, notifications & backup",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 11.sp,
                                        color = colors.textSecondary
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(999.dp),
                                    color = colors.emerald.copy(alpha = 0.12f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.emerald.copy(alpha = 0.3f)),
                                    modifier = Modifier.padding(end = 12.dp)
                                ) {
                                    Text(
                                        text = if (isBangla) "ভার্সন ২.০.০" else "v2.0.0",
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
                    contentPadding = PaddingValues(top = 10.dp, bottom = 120.dp)
                ) {
                    // 1. Language, Dark Mode & App Settings Section
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isBangla) "অ্যাপ সেটিংস ও থিম" else "App Preferences & Theme",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.emerald
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                            border = androidx.compose.foundation.BorderStroke(1.dp, colors.borderSubtle)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // Dark Mode Toggle
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
                                            color = colors.textPrimary
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
                                            uncheckedThumbColor = colors.textSecondary,
                                            uncheckedTrackColor = colors.surfaceSubtle
                                        )
                                    )
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = colors.borderSubtle)

                                // Language Segmented Capsule Toggle
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
                                            color = colors.textPrimary
                                        )
                                        Text(
                                            text = if (isBangla) "বাংলা এবং ইংরেজির মধ্যে পরিবর্তন" else "Switch between Bengali & English",
                                            fontSize = 11.5.sp,
                                            color = colors.textSecondary
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    // Capsule Container matching reference design
                                    Surface(
                                        shape = RoundedCornerShape(999.dp),
                                        color = colors.surfaceSubtle,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.borderSubtle),
                                        modifier = Modifier.clip(RoundedCornerShape(999.dp))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(3.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // English Option
                                            val isEnglish = !isBangla
                                            val enBg = if (isEnglish) colors.emerald else Color.Transparent
                                            val enTextColor = if (isEnglish) {
                                                if (colors.isDark) Color(0xFF022C22) else Color.White
                                            } else colors.textSecondary

                                            Surface(
                                                shape = RoundedCornerShape(999.dp),
                                                color = enBg,
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(999.dp))
                                                    .clickable { viewModel.setLanguage(false) }
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    if (isEnglish) {
                                                        Icon(
                                                            imageVector = Icons.Default.Check,
                                                            contentDescription = null,
                                                            tint = enTextColor,
                                                            modifier = Modifier.size(13.dp)
                                                        )
                                                    }
                                                    Text(
                                                        text = "English",
                                                        fontSize = 12.sp,
                                                        fontWeight = if (isEnglish) FontWeight.Bold else FontWeight.Medium,
                                                        color = enTextColor
                                                    )
                                                }
                                            }

                                            // Bangla Option
                                            val bnBg = if (isBangla) colors.emerald else Color.Transparent
                                            val bnTextColor = if (isBangla) {
                                                if (colors.isDark) Color(0xFF022C22) else Color.White
                                            } else colors.textSecondary

                                            Surface(
                                                shape = RoundedCornerShape(999.dp),
                                                color = bnBg,
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(999.dp))
                                                    .clickable { viewModel.setLanguage(true) }
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    if (isBangla) {
                                                        Icon(
                                                            imageVector = Icons.Default.Check,
                                                            contentDescription = null,
                                                            tint = bnTextColor,
                                                            modifier = Modifier.size(13.dp)
                                                        )
                                                    }
                                                    Text(
                                                        text = "বাংলা",
                                                        fontSize = 12.sp,
                                                        fontWeight = if (isBangla) FontWeight.Bold else FontWeight.Medium,
                                                        color = bnTextColor
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = colors.borderSubtle)

                                // Asr Method Toggle
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
                                            color = colors.textPrimary
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
                                            uncheckedThumbColor = colors.textSecondary,
                                            uncheckedTrackColor = colors.surfaceSubtle
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // 2. Prayer Notifications Section
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (isBangla) "ওয়াক্ত পুশ নোটিফিকেশন" else "Prayer Push Notifications",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.emerald
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                            border = androidx.compose.foundation.BorderStroke(1.dp, colors.borderSubtle)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isBangla) "৫ ওয়াক্ত নামাজের নোটিফিকেশন" else "5 Daily Prayer Alerts",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.5.sp,
                                            color = colors.textPrimary
                                        )
                                        Text(
                                            text = if (isBangla) "প্রতিটি ওয়াক্ত শুরু হওয়ার সাথে সাথে অ্যালার্ট ও হাদিস বাণী পান" else "Get notified on exact time when each Waqt begins",
                                            fontSize = 11.5.sp,
                                            color = colors.textSecondary
                                        )
                                    }

                                    Switch(
                                        checked = uiState.prayerNotificationsEnabled,
                                        onCheckedChange = {
                                            if (!uiState.prayerNotificationsEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                            } else {
                                                viewModel.togglePrayerNotifications(context)
                                            }
                                        },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = colors.emerald,
                                            uncheckedThumbColor = colors.textSecondary,
                                            uncheckedTrackColor = colors.surfaceSubtle
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedButton(
                                    onClick = {
                                        viewModel.sendTestNotification(context)
                                        Toast.makeText(
                                            context,
                                            if (isBangla) "টেস্ট নোটিফিকেশন পাঠানো হয়েছে" else "Test notification sent",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.emerald),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.emerald.copy(alpha = 0.5f))
                                ) {
                                    Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isBangla) "টেস্ট নোটিফিকেশন পরীক্ষা করুন" else "Send Test Notification",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    // 3. Home Screen Widget Section
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (isBangla) "হোম স্ক্রিন উইজেট (App Widget)" else "Home Screen Widget",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.emerald
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                            border = androidx.compose.foundation.BorderStroke(1.dp, colors.borderSubtle)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = colors.emerald.copy(alpha = 0.12f),
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(imageVector = Icons.Default.Widgets, contentDescription = null, tint = colors.emerald)
                                        }
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isBangla) "হোম স্ক্রিন থেকে নামাজের সময় দেখুন" else "Prayer times on your home screen",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.5.sp,
                                            color = colors.textPrimary
                                        )
                                        Text(
                                            text = if (isBangla) "হোম স্ক্রিনের খালি জায়গায় চাপ দিয়ে ধরে 'Widgets' থেকে 'Namaz Tracker' যোগ করুন।" else "Touch & hold empty space on home screen, select 'Widgets' and add 'Namaz Tracker'.",
                                            fontSize = 11.5.sp,
                                            color = colors.textSecondary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedButton(
                                    onClick = {
                                        PrayerWidgetProvider.updateAllWidgets(context)
                                        Toast.makeText(
                                            context,
                                            if (isBangla) "উইজেট ডাটা আপডেট করা হয়েছে" else "Widget data refreshed",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.emerald),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.emerald.copy(alpha = 0.5f))
                                ) {
                                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isBangla) "উইজেট ডাটা রিফ্রেশ করুন" else "Refresh Widget Now",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    // 4. Data Backup & Export Section
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (isBangla) "ডাটা ব্যাকআপ ও রিস্টোর" else "Data Backup & Restore",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.emerald
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                            border = androidx.compose.foundation.BorderStroke(1.dp, colors.borderSubtle)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = if (isBangla) "আপনার নামাযের হিসাব ও কাযা রেকর্ড সুরক্ষিত রাখতে ব্যাকআপ ফাইল তৈরি বা পূর্বের ডাটা রিস্টোর করুন।" else "Backup your prayer logs and Qaza records or restore previous records safely.",
                                    fontSize = 11.5.sp,
                                    color = colors.textSecondary
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            viewModel.exportBackup { json ->
                                                BackupRestoreHelper.shareBackup(context, json)
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = colors.emeraldPrimary)
                                    ) {
                                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = if (isBangla) "শেয়ার ব্যাকআপ" else "Share Backup", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            viewModel.exportBackup { json ->
                                                BackupRestoreHelper.copyToClipboard(context, json, isBangla)
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.emerald),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.emerald.copy(alpha = 0.5f))
                                    ) {
                                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = if (isBangla) "কপি JSON" else "Copy JSON", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }

                                OutlinedButton(
                                    onClick = {
                                        restoreInputText = ""
                                        showRestoreDialog = true
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.textPrimary),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.borderSubtle)
                                ) {
                                    Icon(imageVector = Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp), tint = colors.emerald)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isBangla) "ডাটা রিস্টোর করুন (Restore Data)" else "Restore Data from JSON",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    // 5. Knowledge & Guides
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (isBangla) "দ্বীনি শিক্ষা ও নিয়মাবলী" else "Knowledge & Guides",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.emerald
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

                    // 6. Tools & Analytics
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (isBangla) "আমল ও ট্র্যাকিং টুলস" else "Tools & Analytics",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.emerald
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

                        Spacer(modifier = Modifier.height(10.dp))

                        ToolNavCard(
                            title = if (isBangla) "ডিজিটাল তাসবীহ" else "Digital Tasbih",
                            desc = if (isBangla) "স্পর্শকাতর ভাইব্রেশন কাউন্টার, মাসনূন যিকির ও কাস্টম তাসবীহ" else "Tactile Dhikr counter with presets and custom dhikrs",
                            icon = Icons.Default.TouchApp,
                            badge = if (isBangla) "হ্যাপটিক" else "Haptic",
                            onClick = { onNavigateSub(MoreSubScreen.TASBIH) },
                            testTag = "nav_more_tasbih"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        ToolNavCard(
                            title = if (isBangla) "কিবলা কম্পাস" else "Qibla Direction",
                            desc = if (isBangla) "সেন্সরের সাহায্যে সঠিক কিবলামুখী কম্পাস নির্দেশক" else "Real-time compass pointing to Holy Kaaba",
                            icon = Icons.Default.Explore,
                            badge = "${uiState.qiblaBearing.toInt()}°",
                            onClick = { onNavigateSub(MoreSubScreen.QIBLA) },
                            testTag = "nav_more_qibla"
                        )
                    }

                    // 7. About App & Developer Section
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = if (isBangla) "অ্যাপ ও ডেভেলপার পরিচিতি" else "About App & Developer",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.emerald
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                            border = androidx.compose.foundation.BorderStroke(1.dp, colors.borderSubtle)
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp)
                            ) {
                                // App Header Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.app_header),
                                        contentDescription = "Namaz Tracker",
                                        modifier = Modifier.height(34.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Surface(
                                        shape = RoundedCornerShape(999.dp),
                                        color = colors.emerald.copy(alpha = 0.12f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.emerald.copy(alpha = 0.35f))
                                    ) {
                                        Text(
                                            text = if (isBangla) "ভার্সন ২.০.০" else "v2.0.0",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = colors.emerald,
                                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = if (isBangla)
                                        "মুসলিম উম্মাহর দৈনন্দিন নামায, আল-কুরআন, সহিহ হাদীস, কিবলা, ডিজিটাল তাসবীহ ও স্বয়ংক্রিয় আমল ট্র্যাকিংয়ের পূর্ণাঙ্গ সহচর।"
                                    else
                                        "All-in-one Islamic companion for automated prayer tracking, Holy Qur'an, Sahih Hadith, Qibla compass, and daily Dhikr.",
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                    color = colors.textSecondary
                                )

                                HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = colors.borderSubtle)

                                // Developer Profile Section
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Avatar
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(Color(0xFF059669), Color(0xFF10B981))
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "M",
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = if (isBangla) "মুহিত (Muhit)" else "Muhit (Muhit47)",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = colors.textPrimary
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = colors.emerald.copy(alpha = 0.15f)
                                            ) {
                                                Text(
                                                    text = if (isBangla) "অথর ও ডিজাইনার" else "Author & Dev",
                                                    fontSize = 9.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = colors.emerald,
                                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = if (isBangla)
                                                "মোবাইল অ্যাপ ডিজাইনার ও ইঞ্জিনিয়ার"
                                            else
                                                "Mobile App Designer & Software Engineer",
                                            fontSize = 11.5.sp,
                                            color = colors.textSecondary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Interactive Links (GitHub, Repo, Email)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // GitHub Profile
                                    OutlinedButton(
                                        onClick = {
                                            try {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Muhit47"))
                                                context.startActivity(intent)
                                            } catch (e: Exception) {}
                                        },
                                        modifier = Modifier.weight(1f).height(36.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp),
                                        shape = RoundedCornerShape(9.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.borderSubtle),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.textPrimary)
                                    ) {
                                        Text(
                                            text = "🐙 GitHub",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    // Source Code Repo
                                    OutlinedButton(
                                        onClick = {
                                            try {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Muhit47/namaz-tracker"))
                                                context.startActivity(intent)
                                            } catch (e: Exception) {}
                                        },
                                        modifier = Modifier.weight(1f).height(36.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp),
                                        shape = RoundedCornerShape(9.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.borderSubtle),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.textPrimary)
                                    ) {
                                        Text(
                                            text = "⭐ Repo",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    // Email / Contact
                                    OutlinedButton(
                                        onClick = {
                                            try {
                                                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:muhitpias@gmail.com")).apply {
                                                    putExtra(Intent.EXTRA_SUBJECT, "Namaz Tracker v2.0 Feedback")
                                                }
                                                context.startActivity(intent)
                                            } catch (e: Exception) {}
                                        },
                                        modifier = Modifier.weight(1f).height(36.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp),
                                        shape = RoundedCornerShape(9.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.borderSubtle),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.emerald)
                                    ) {
                                        Text(
                                            text = if (isBangla) "✉️ ইমেইল" else "✉️ Email",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Copyright & Ummah Footer
                                Text(
                                    text = "Made with ❤️ for the Ummah • © 2026 Muhit",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = colors.textSecondary.copy(alpha = 0.8f),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            containerColor = colors.surfaceCard,
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
                        textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = colors.textPrimary),
                        maxLines = 10
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (restoreInputText.isBlank()) {
                            Toast.makeText(
                                context,
                                if (isBangla) "দয়া করে ব্যাকআপ টেক্সট পেস্ট করুন" else "Please paste backup text",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }
                        viewModel.restoreBackup(restoreInputText) { success, msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                            if (success) {
                                showRestoreDialog = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.emeraldPrimary)
                ) {
                    Text(text = if (isBangla) "রিস্টোর নিশ্চিত করুন" else "Confirm Restore", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreDialog = false }) {
                    Text(text = if (isBangla) "বাতিল" else "Cancel", color = colors.textSecondary)
                }
            }
        )
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
    val colors = FinancyTheme.colors
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.borderSubtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
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
                    color = colors.emerald.copy(alpha = 0.12f),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = icon, contentDescription = null, tint = colors.emerald, modifier = Modifier.size(22.dp))
                    }
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = colors.emerald.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = badge,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.emerald,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = desc, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary, maxLines = 2)
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = colors.textSecondary
            )
        }
    }
}
