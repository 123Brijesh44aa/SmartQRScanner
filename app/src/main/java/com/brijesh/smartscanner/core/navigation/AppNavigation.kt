package com.brijesh.smartscanner.core.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.brijesh.smartscanner.data.repository.ScanHistoryRepository
import com.brijesh.smartscanner.data.repository.ScanHistoryViewModel
import com.brijesh.smartscanner.data.repository.ScanHistoryViewModelFactory
import com.brijesh.smartscanner.feature.generator.GeneratorScreen
import com.brijesh.smartscanner.feature.history.HistoryScreen
import com.brijesh.smartscanner.feature.home.HomeScreen
import com.brijesh.smartscanner.feature.result.ScanContentType
import com.brijesh.smartscanner.feature.result.ScanResultScreen
import com.brijesh.smartscanner.feature.scanner.ScannerScreen

@Composable
fun AppNavigation(
    historyRepository: ScanHistoryRepository
) {
    val navController = rememberNavController()

    val historyViewModel: ScanHistoryViewModel = viewModel(
        factory = ScanHistoryViewModelFactory(historyRepository)
    )

    // Shared state for the scanned QR content.
    var scannedValue by remember {
        mutableStateOf("")
    }

    NavHost(
        navController = navController,
        startDestination = Screen.HOME
    ) {
        composable(Screen.HOME) {
            HomeScreen(
                onScanClick = {
                    navController.navigate(Screen.SCANNER)
                },
                onHistoryClick = {
                    navController.navigate(Screen.HISTORY)
                },
                onGeneratorClick = {
                    navController.navigate(Screen.GENERATOR)
                }
            )
        }

        composable(Screen.SCANNER) {
            ScannerScreen(
                onQrDetected = { value ->
                    if (value != scannedValue) {
                        scannedValue = value

                        historyViewModel.saveScan(
                            content = value,
                            contentType = ScanContentType.from(value).name
                        )

                        navController.navigate(Screen.SCAN_RESULT) {
                            launchSingleTop = true
                        }
                    }

                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.SCAN_RESULT) {
            ScanResultScreen(
                value = scannedValue,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.HISTORY) {
            HistoryScreen(
                viewModel = historyViewModel,
                onBack = {
                    navController.popBackStack()
                },
                onOpenScan = { value ->
                    scannedValue = value
                    navController.navigate(Screen.SCAN_RESULT)
                }
            )
        }

        composable(Screen.GENERATOR) {
            GeneratorScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}