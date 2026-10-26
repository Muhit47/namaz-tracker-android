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
    val icon: ImageVector,
    val testTag: String
) {
    HOME("Prayers", "নামায", Icons.Default.AccessTime, "nav_tab_home"),
    QURAN("Qur'an", "কুরআন", Icons.AutoMirrored.Filled.MenuBook, "nav_tab_quran"),
    HADITH("Hadith", "হাদীস", Icons.AutoMirrored.Filled.LibraryBooks, "nav_tab_hadith"),
    QAZA("Qaza", "কাযা", Icons.Default.History, "nav_tab_qaza"),
    MORE("Settings", "সেটিংস", Icons.Default.Settings, "nav_tab_more")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: NamazViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            NamazTrackerTheme(darkTheme = uiState.isDarkMode) {
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
                    if (tab == NamazNavTab.MORE) {
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
                modifier = modifier
            )
            NamazNavTab.QURAN -> QuranScreen(
                uiState = uiState,
                viewModel = viewModel,
                modifier = modifier
            )
            NamazNavTab.HADITH -> HadithScreen(
                uiState = uiState,
                viewModel = viewModel,
                modifier = modifier
            )
            NamazNavTab.QAZA -> QazaScreen(
                uiState = uiState,
                viewModel = viewModel,
                modifier = modifier
            )
            NamazNavTab.MORE -> MoreScreen(
                uiState = uiState,
                viewModel = viewModel,
                activeSubScreen = moreSubScreen,
                onNavigateSub = { moreSubScreen = it },
                modifier = modifier
            )
        }
    }
}
