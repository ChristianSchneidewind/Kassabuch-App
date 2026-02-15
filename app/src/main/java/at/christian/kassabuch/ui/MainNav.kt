package at.christian.kassabuch.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import at.christian.kassabuch.R

private enum class MainTab(val labelRes: Int, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Dashboard(R.string.nav_dashboard, Icons.Filled.Home),
    Income(R.string.nav_income, Icons.Filled.AttachMoney),
    Expense(R.string.nav_expense, Icons.Filled.Receipt),
    More(R.string.nav_more, Icons.Filled.AccountCircle)
}

@Composable
fun MainNav(
    dashboardContent: @Composable () -> Unit,
    incomeContent: @Composable () -> Unit,
    expenseContent: @Composable () -> Unit,
    moreContent: @Composable () -> Unit
) {
    var selectedTab by remember { mutableStateOf(MainTab.Dashboard) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                MainTab.values().forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = { Icon(tab.icon, contentDescription = null) },
                        label = { Text(text = stringResource(tab.labelRes)) }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                MainTab.Dashboard -> dashboardContent()
                MainTab.Income -> incomeContent()
                MainTab.Expense -> expenseContent()
                MainTab.More -> moreContent()
            }
        }
    }
}
