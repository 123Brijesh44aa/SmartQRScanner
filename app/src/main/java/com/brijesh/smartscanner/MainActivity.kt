package com.brijesh.smartscanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.brijesh.smartscanner.core.navigation.AppNavigation
import com.brijesh.smartscanner.core.ui.theme.SmartScannerTheme
import com.brijesh.smartscanner.data.local.ScannerDatabase
import com.brijesh.smartscanner.data.repository.ScanHistoryRepository

class MainActivity : ComponentActivity() {

    private lateinit var historyRepository: ScanHistoryRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = ScannerDatabase.getInstance(applicationContext)
        historyRepository = ScanHistoryRepository(database.scanHistoryDao())

        setContent {
            SmartScannerTheme {
                AppNavigation(
                    historyRepository = historyRepository
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SmartScannerTheme {
        Greeting("Android")
    }
}