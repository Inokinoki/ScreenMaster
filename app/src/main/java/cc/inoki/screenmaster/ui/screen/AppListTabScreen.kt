package cc.inoki.screenmaster.ui.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import cc.inoki.screenmaster.helper.AppHelper
import cc.inoki.screenmaster.model.AppInfo
import cc.inoki.screenmaster.model.DisplayInfo
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun AppListTabScreen(
    apps: List<AppInfo>,
    displays: List<DisplayInfo>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    appHelper: AppHelper
) {
    val context = LocalContext.current
    val showDisplayDialog = remember { mutableStateOf(false) }
    val selectedApp = remember { mutableStateOf<AppInfo?>(null) }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading)

    Box(modifier = Modifier.fillMaxSize()) {
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            if (apps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (isLoading) "Loading applications..." else "No launchable apps found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (!isLoading) {
                            Button(onClick = onRefresh) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Refresh")
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(apps) { app ->
                        AppListCard(
                            appInfo = app,
                            onClick = {
                                selectedApp.value = app
                                showDisplayDialog.value = true
                            }
                        )
                    }
                }
            }
        }

        if (showDisplayDialog.value) {
            DisplaySelectionDialog(
                displays = displays,
                onDismiss = { showDisplayDialog.value = false },
                onDisplaySelected = { display ->
                    selectedApp.value?.let { app ->
                        appHelper.launchApp(app.packageName, display.id)
                        Toast.makeText(
                            context,
                            "Launching ${app.appName} on ${display.name} (ID: ${display.id})",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    showDisplayDialog.value = false
                }
            )
        }
    }
}

@Composable
fun AppListCard(
    appInfo: AppInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                bitmap = appInfo.icon.toBitmap(width = 48, height = 48).asImageBitmap(),
                contentDescription = appInfo.appName,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = appInfo.appName,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = appInfo.packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun DisplaySelectionDialog(
    displays: List<DisplayInfo>,
    onDismiss: () -> Unit,
    onDisplaySelected: (DisplayInfo) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Display") },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(displays) { display ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDisplaySelected(display) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (display.isDefault)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = display.name,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    if (display.isDefault) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "Default",
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                                Text(
                                    text = "ID: ${display.id} | ${display.width}x${display.height}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
