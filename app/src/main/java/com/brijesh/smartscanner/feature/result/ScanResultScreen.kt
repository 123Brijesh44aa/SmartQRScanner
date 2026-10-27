package com.brijesh.smartscanner.feature.result

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ScanResultScreen(
    value: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val contentType = ScanContentType.from(value)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Scan Result",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = contentType.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Detected content",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Button(
                onClick = {
                    clipboard.setText(AnnotatedString(value))
                    Toast.makeText(
                        context,
                        "Copied to clipboard",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Copy")
            }

            OutlinedButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, value)
                    }

                    context.startActivity(
                        Intent.createChooser(intent, "Share QR content")
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Share")
            }

            if (contentType == ScanContentType.WEB_URL) {
                Button(
                    onClick = {
                        val candidate = if (
                            value.trim().startsWith("www.", true)
                        ) {
                            "https://${value.trim()}"
                        } else {
                            value.trim()
                        }

                        val uri = Uri.parse(candidate)

                        if (
                            uri.scheme in listOf("http", "https") &&
                            !uri.host.isNullOrBlank()
                        ) {
                            try {
                                context.startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        uri
                                    )
                                )
                            } catch (_: ActivityNotFoundException) {
                                Toast.makeText(
                                    context,
                                    "No app can open this link",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text("Open Link")
                }
            }

            if (contentType == ScanContentType.UPI_PAYMENT) {
                Text(
                    text = "This QR contains UPI payment information. " +
                            "Verify the recipient and amount in your payment app " +
                            "before approving any transaction.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Back")
                }
            }
        }
    }
}