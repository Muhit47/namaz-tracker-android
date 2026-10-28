package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.FinancyFloatingNavBar
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.NamazViewModel

enum class NamazNavTab(
    val titleEn: String,
    val titleBn: String,
    val filledIcon: ImageVector? = null,
    val outlinedIcon: ImageVector? = null,
    val filledResId: Int? = null,
    val outlinedResId: Int? = null,
    val testTag: String
) {
    HOME(
        titleEn = "Prayers",
        titleBn = "নামাজ",
        filledResId = R.drawable.ic_mosque_filled,
        outlinedResId = R.drawable.ic_mosque_outlined,
        testTag = "nav_tab_home"
    ),
    QURAN_HADITH(
        titleEn = "Quran",
        titleBn = "কুরআন",
        filledResId = R.drawable.ic_quran_crescent_filled,
        outlinedResId = R.drawable.ic_quran_crescent_outlined,
        testTag = "nav_tab_quran_hadith"
    ),
    IBADAH(
        titleEn = "Ibadah",
        titleBn = "ইবাদাত",
        filledResId = R.drawable.ic_ibadah_filled,
        outlinedResId = R.drawable.ic_ibadah_outlined,
        testTag = "nav_tab_ibadah"
    ),
    TRACKER(
        titleEn = "Tracker",
        titleBn = "ট্র্যাকার",
        filledResId = R.drawable.ic_tracker_chart_filled,
        outlinedResId = R.drawable.ic_tracker_chart_outlined,
        testTag = "nav_tab_tracker"
    ),
    SETTINGS(
        titleEn = "More",
        titleBn = "আরও",
        filledIcon = Icons.Filled.Menu,
        outlinedIcon = Icons.Outlined.Menu,
        testTag = "nav_tab_settings"
    );

    val icon: ImageVector get() = filledIcon ?: Icons.Default.MenuBook
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: NamazViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            NamazTrackerTheme(
                darkTheme = uiState.isDarkMode,
                isBangla = uiState.isBangla
            ) {
                MainAppEntry(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppEntry(viewModel: NamazViewModel) {
    var isSplashVisible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(1200)
        isSplashVisible = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NamazAppRoot(viewModel = viewModel)

        AnimatedVisibility(
            visible = isSplashVisible,
            exit = fadeOut(animationSpec = tween(450))
        ) {
            SplashScreenContent()
        }
    }
}

@Composable
fun SplashScreenContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B101B)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_app_logo),
                contentDescription = "App Icon",
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(24.dp))
            )
            Spacer(modifier = Modifier.height(20.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_app_brand_logo_dark),
                contentDescription = "Namaz Tracker Brand Logo",
                modifier = Modifier.height(42.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            CircularProgressIndicator(
                color = Color(0xFF10B981),
                strokeWidth = 2.5.dp,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun NamazAppRoot(
    viewModel: NamazViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var currentTab by remember { mutableStateOf(NamazNavTab.HOME) }
    var moreSubScreen by remember { mutableStateOf(MoreSubScreen.INDEX) }

    BackHandler(enabled = moreSubScreen != MoreSubScreen.INDEX) {
        moreSubScreen = MoreSubScreen.INDEX
    }

    BackHandler(enabled = currentTab != NamazNavTab.HOME && moreSubScreen == MoreSubScreen.INDEX && uiState.selectedSurah == null) {
        currentTab = NamazNavTab.HOME
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = FinancyTheme.colors.background,
        bottomBar = {
            FinancyFloatingNavBar(
                currentTab = currentTab,
                isBangla = uiState.isBangla,
                onTabSelected = { tab ->
                    currentTab = tab
                    if (tab == NamazNavTab.SETTINGS) {
                        moreSubScreen = MoreSubScreen.INDEX
                    }
                }
            )
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        when (currentTab) {
            NamazNavTab.HOME -> HomeScreen(
                uiState = uiState,
                viewModel = viewModel,
                onNavigateToTab = { currentTab = it },
                modifier = modifier
            )
            NamazNavTab.QURAN_HADITH -> QuranHadithScreen(
                uiState = uiState,
                viewModel = viewModel,
                modifier = modifier
            )
            NamazNavTab.IBADAH -> IbadahScreen(
                uiState = uiState,
                viewModel = viewModel,
                modifier = modifier
            )
            NamazNavTab.TRACKER -> TrackerScreen(
                uiState = uiState,
                viewModel = viewModel,
                modifier = modifier
            )
            NamazNavTab.SETTINGS -> MoreScreen(
                uiState = uiState,
                viewModel = viewModel,
                activeSubScreen = moreSubScreen,
                onNavigateSub = { moreSubScreen = it },
                modifier = modifier
            )
        }
    }
}
