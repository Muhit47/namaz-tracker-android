package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
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
        titleBn = "নামায",
        filledResId = R.drawable.ic_mosque_filled,
        outlinedResId = R.drawable.ic_mosque_outlined,
        testTag = "nav_tab_home"
    ),
    QURAN_HADITH(
        titleEn = "Qur'an & Hadith",
        titleBn = "কুরআন ও হাদীস",
        filledResId = R.drawable.ic_quran_crescent_filled,
        outlinedResId = R.drawable.ic_quran_crescent_outlined,
        testTag = "nav_tab_quran_hadith"
    ),
    IBADAH(
        titleEn = "Ibadah",
        titleBn = "ইবাদাত",
        filledIcon = Icons.Filled.SelfImprovement,
        outlinedIcon = Icons.Outlined.SelfImprovement,
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
                NamazAppRoot(viewModel = viewModel)
            }
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
