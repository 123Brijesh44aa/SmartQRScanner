package com.brijesh.smartscanner.feature.scanner

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

private const val TAG = "SmartQRScanner"

@OptIn(ExperimentalGetImage::class)
@Composable
fun ScannerScreen(
    onQrDetected: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mainExecutor = remember(context) {
        ContextCompat.getMainExecutor(context)
    }

    val latestOnQrDetected by rememberUpdatedState(onQrDetected)

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var status by remember {
        mutableStateOf("Point your camera at a QR code")
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        status = if (granted) {
            "Looking for QR codes..."
        } else {
            "Camera permission is required."
        }
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val previewView = remember(context) {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            implementationMode = PreviewView.ImplementationMode.PERFORMANCE
        }
    }

    val cameraExecutor = remember {
        Executors.newSingleThreadExecutor()
    }

    val detected = remember {
        AtomicBoolean(false)
    }

    val scanner = remember {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()

        BarcodeScanning.getClient(options)
    }

    var cameraProvider by remember {
        mutableStateOf<ProcessCameraProvider?>(null)
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraProvider?.unbindAll()
            scanner.close()
            cameraExecutor.shutdown()
        }
    }

    LaunchedEffect(hasPermission, lifecycleOwner) {
        if (!hasPermission) return@LaunchedEffect

        status = "Looking for QR codes..."

        val providerFuture =
            ProcessCameraProvider.getInstance(context)

        providerFuture.addListener(
            {
                try {
                    val provider = providerFuture.get()
                    cameraProvider = provider

                    val preview = Preview.Builder()
                        .build()
                        .also {
                            it.surfaceProvider = previewView.surfaceProvider
                        }

                    val analysis = ImageAnalysis.Builder()
                        .setTargetResolution(Size(1280, 720))
                        .setBackpressureStrategy(
                            ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                        )
                        .build()

                    analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                        if (detected.get()) {
                            imageProxy.close()
                            return@setAnalyzer
                        }

                        val mediaImage = imageProxy.image

                        if (mediaImage == null) {
                            imageProxy.close()
                            return@setAnalyzer
                        }

                        try {
                            val inputImage = InputImage.fromMediaImage(
                                mediaImage,
                                imageProxy.imageInfo.rotationDegrees
                            )

                            scanner.process(inputImage)
                                .addOnSuccessListener(mainExecutor) { barcodes ->
                                    val qrValue = barcodes
                                        .firstOrNull {
                                            it.format == Barcode.FORMAT_QR_CODE &&
                                                    !it.rawValue.isNullOrBlank()
                                        }
                                        ?.rawValue

                                    if (
                                        qrValue != null &&
                                        detected.compareAndSet(false, true)
                                    ) {
                                        Log.d(TAG, "QR code detected")
                                        status = "QR code detected!"
                                        latestOnQrDetected(qrValue)
                                    }
                                }
                                .addOnFailureListener(mainExecutor) { error ->
                                    Log.e(TAG, "QR detection failed", error)
                                    status = "Detection error. Check Logcat."
                                }
                                .addOnCompleteListener {
                                    imageProxy.close()
                                }
                        } catch (error: Exception) {
                            Log.e(TAG, "Could not process camera frame", error)
                            imageProxy.close()
                        }
                    }

                    provider.unbindAll()

                    provider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        analysis
                    )

                    Log.d(TAG, "Camera preview and analyzer started")
                } catch (error: Exception) {
                    Log.e(TAG, "Camera initialization failed", error)
                    status = "Unable to start camera."
                }
            },
            mainExecutor
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(onClick = onBack) {
            Text("Back")
        }

        Text(
            text = "Scan QR Code",
            style = MaterialTheme.typography.headlineMedium
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            if (hasPermission) {
                AndroidView(
                    factory = { previewView },
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            MaterialTheme.colorScheme.surface,
                            RoundedCornerShape(24.dp)
                        )
                )

                // Visual guide only; ML Kit still analyzes the camera frame.
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.72f)
                        .height(220.dp)
                        .background(
                            MaterialTheme.colorScheme.surface.copy(
                                alpha = 0.04f
                            ),
                            RoundedCornerShape(24.dp)
                        )
                )
            } else {
                Button(
                    onClick = {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                ) {
                    Text("Grant Camera Permission")
                }
            }
        }

        Text(
            text = status,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Keep the QR code clear, in focus, and inside the camera view.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}