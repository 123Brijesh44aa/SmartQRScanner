package com.brijesh.smartscanner.feature.generator

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun GeneratorScreen(
    onBack: () -> Unit
) {
    var input by remember { mutableStateOf("") }
    var pendingContent by remember { mutableStateOf<String?>(null) }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isGenerating by remember { mutableStateOf(false) }

    var selectedType by remember { mutableStateOf("Text / URL") }
    var wifiSsid by remember { mutableStateOf("") }
    var wifiPassword by remember { mutableStateOf("") }
    var wifiSecurity by remember { mutableStateOf(WifiSecurity.WPA) }

    var contactName by remember { mutableStateOf("") }
    var contactPhone by remember { mutableStateOf("") }
    var contactEmail by remember { mutableStateOf("") }

    var upiId by remember { mutableStateOf("") }
    var upiPayeeName by remember { mutableStateOf("") }
    var upiAmount by remember { mutableStateOf("") }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        androidx.compose.material3.TextButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.Start)
        ) {
            Text("← Back")
        }

        Text(
            text = "QR Code Generator",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Create a QR code from text or a website link.",
            style = MaterialTheme.typography.bodyMedium
        )


        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedType == "Text / URL",
                        onClick = { selectedType = "Text / URL" }
                    )
                    Text(
                        text = "Text / URL",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedType == "Wi-Fi",
                        onClick = { selectedType = "Wi-Fi" }
                    )
                    Text(
                        text = "Wi-Fi",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedType == "Contact",
                        onClick = { selectedType = "Contact" }
                    )
                    Text(
                        text = "Contact",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedType == "UPI",
                        onClick = { selectedType = "UPI" }
                    )
                    Text(
                        text = "UPI Payment",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }


        if (selectedType == "Text / URL") {
            OutlinedTextField(
                value = input,
                onValueChange = {
                    input = it
                    errorMessage = null
                },
                label = { Text("Text or URL") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )
        }
        else if (selectedType == "Contact") {
            OutlinedTextField(
                value = contactName,
                onValueChange = { contactName = it },
                label = { Text("Full name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = contactPhone,
                onValueChange = { contactPhone = it },
                label = { Text("Phone number (optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = contactEmail,
                onValueChange = { contactEmail = it },
                label = { Text("Email address (optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
        else if (selectedType == "UPI") {
            OutlinedTextField(
                value = upiId,
                onValueChange = { upiId = it },
                label = { Text("UPI ID") },
                placeholder = { Text("name@bank") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = upiPayeeName,
                onValueChange = { upiPayeeName = it },
                label = { Text("Payee name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = upiAmount,
                onValueChange = { upiAmount = it },
                label = { Text("Amount in INR (optional)") },
                placeholder = { Text("100.00") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text(
                text = "Verify the recipient and amount in your UPI app before paying.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
        else {
            OutlinedTextField(
                value = wifiSsid,
                onValueChange = { wifiSsid = it },
                label = { Text("Wi-Fi network name (SSID)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = wifiPassword,
                onValueChange = { wifiPassword = it },
                label = { Text("Wi-Fi password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text("Network security")

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = wifiSecurity == WifiSecurity.WPA,
                    onClick = { wifiSecurity = WifiSecurity.WPA }
                )
                Text("WPA")
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = wifiSecurity == WifiSecurity.WEP,
                    onClick = { wifiSecurity = WifiSecurity.WEP }
                )
                Text("WEP")
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = wifiSecurity == WifiSecurity.NONE,
                    onClick = { wifiSecurity = WifiSecurity.NONE }
                )
                Text("Open network")
            }
        }

        Button(
            onClick = {
                errorMessage = null
                qrBitmap = null

                val content = try {
                    when (selectedType) {
                        "Wi-Fi" -> QrContentBuilder.wifi(
                            ssid = wifiSsid,
                            password = wifiPassword,
                            security = wifiSecurity
                        )

                        "UPI" -> QrContentBuilder.upi(
                            upiId = upiId,
                            payeeName = upiPayeeName,
                            amount = upiAmount
                        )

                        "Contact" -> QrContentBuilder.contact(
                            name = contactName,
                            phone = contactPhone,
                            email = contactEmail
                        )

                        else -> input.trim().also {
                            require(it.isNotBlank()) {
                                "Please enter some text or a URL."
                            }
                        }
                    }
                } catch (e: IllegalArgumentException) {
                    errorMessage = e.message ?: "Invalid input."
                    null
                }

                if (content != null) {
                    pendingContent = content
                    isGenerating = true
                }
            },
            enabled = !isGenerating,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isGenerating) "Generating..." else "Generate QR Code")
        }

        LaunchedEffect(pendingContent) {
            val content = pendingContent ?: return@LaunchedEffect

            val bitmap = withContext(Dispatchers.Default) {
                QrCodeGenerator.generate(content)
            }

            qrBitmap = bitmap
            errorMessage = if (bitmap == null) {
                "Couldn't generate this QR code. Try shorter text."
            } else {
                null
            }

            isGenerating = false
            pendingContent = null
        }

        errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
        }

        qrBitmap?.let { bitmap ->
            Spacer(modifier = Modifier.height(8.dp))

            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Generated QR code",
                modifier = Modifier.size(280.dp)
            )

            OutlinedButton(
                onClick = {
                    QrCodeImageActions.saveToGallery(context, bitmap)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save to Gallery")
            }

            OutlinedButton(
                onClick = {
                    QrCodeImageActions.shareImage(context, bitmap)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Share QR Code")
            }

            Text(
                text = "Scan this code with a QR scanner.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}