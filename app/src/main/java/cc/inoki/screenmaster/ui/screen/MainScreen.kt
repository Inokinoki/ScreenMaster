package cc.inoki.screenmaster.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import cc.inoki.screenmaster.helper.AppHelper
import cc.inoki.screenmaster.helper.DisplayHelper
import cc.inoki.screenmaster.model.AppInfo
import cc.inoki.screenmaster.model.DisplayInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val appHelper = remember { AppHelper(context) }
    val displayHelper = remember { DisplayHelper(context) }
    val coroutineScope = rememberCoroutineScope()

    var selectedTab by remember { mutableStateOf(0) }
    val apps = remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    val displays = remember { mutableStateOf<List<DisplayInfo>>(emptyList()) }
    val isLoadingApps = remember { mutableStateOf(false) }

    val loadApps: () -> Unit = {
        coroutineScope.launch {
            isLoadingApps.value = true
            try {
                val loadedApps = withContext(Dispatchers.IO) {
                    appHelper.getLaunchableApps()
                }
                apps.value = loadedApps
            } finally {
                isLoadingApps.value = false
            }
        }
    }

    val loadDisplays: () -> Unit = {
        displays.value = displayHelper.getAllDisplays()
    }

    LaunchedEffect(Unit) {
        loadApps()
        loadDisplays()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Screen Master") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Apps") },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Apps") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Displays") },
                    icon = { Icon(Icons.Default.Build, contentDescription = "Displays") }
                )
            }

            when (selectedTab) {
                0 -> AppListTabScreen(
                    apps = apps.value,
                    displays = displays.value,
                    isLoading = isLoadingApps.value,
                    onRefresh = loadApps,
                    appHelper = appHelper
                )
                1 -> DisplayInfoTabScreen(
                    displays = displays.value,
                    displayHelper = displayHelper,
                    onRefresh = loadDisplays
                )
            }
        }
    }
}
