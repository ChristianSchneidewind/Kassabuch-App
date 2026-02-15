package at.christian.kassabuch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import at.christian.kassabuch.ui.DashboardScreen
import at.christian.kassabuch.ui.DashboardUiState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KassabuchApp()
        }
    }
}

@Composable
fun KassabuchApp() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            DashboardScreen(
                uiState = DashboardUiState(
                    monthTitle = "März 2026",
                    payoutAmount = "1.234,56 €",
                    payoutDate = "03.04.2026",
                    monthlyBalance = "+ 320,00 €",
                    incomeSum = "1.500,00 €",
                    expenseSum = "1.180,00 €"
                )
            )
        }
    }
}
