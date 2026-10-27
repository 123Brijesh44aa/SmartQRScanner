package com.brijesh.smartscanner.feature.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brijesh.smartscanner.data.local.ScanHistoryEntity
import com.brijesh.smartscanner.data.repository.ScanHistoryViewModel
import com.brijesh.smartscanner.feature.result.ScanContentType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: ScanHistoryViewModel,
    onBack: () -> Unit,
    onOpenScan: (String) -> Unit
) {
    val history by viewModel.history.collectAsStateWithLifecycle()
    var showClearDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onBack) {
                Text("Back")
            }

            Text(
                text = "Scan History",
                style = MaterialTheme.typography.titleLarge
            )

            TextButton(
                onClick = { showClearDialog = true },
                enabled = history.isNotEmpty()
            ) {
                Text("Clear")
            }
        }

        if (history.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 48.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "No scans yet",
                    style = MaterialTheme.typography.titleMedium
                )
                Text("Your scanned QR codes will appear here.")
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = history,
                    key = { it.id }
                ) { scan ->
                    HistoryItem(
                        scan = scan,
                        onClick = { onOpenScan(scan.content) },
                        onDelete = { viewModel.deleteScan(scan) }
                    )
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear scan history?") },
            text = { Text("This will permanently delete all saved scans.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearHistory()
                        showClearDialog = false
                    }
                ) {
                    Text("Clear all")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun HistoryItem(
    scan: ScanHistoryEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val typeTitle = runCatching {
        ScanContentType.valueOf(scan.contentType).title
    }.getOrDefault("Unknown")

    val date = remember(scan.scannedAt) {
        SimpleDateFormat(
            "dd MMM yyyy, hh:mm a",
            Locale.getDefault()
        ).format(Date(scan.scannedAt))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = typeTitle,
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = scan.content,
            maxLines = 2,
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = date,
            style = MaterialTheme.typography.bodySmall
        )

        OutlinedButton(onClick = onDelete) {
            Text("Delete")
        }
    }
}