package at.christian.kassabuch.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import at.christian.kassabuch.R

data class DashboardExpenseItem(
    val title: String,
    val date: String,
    val amount: String
)

data class DashboardUiState(
    val monthTitle: String,
    val payoutAmount: String,
    val payoutDate: String,
    val monthlyBalance: String,
    val incomeSum: String,
    val expenseSum: String,
    val recentExpenses: List<DashboardExpenseItem>
)

@Composable
fun DashboardScreen(
    uiState: DashboardUiState,
    onAddIncome: () -> Unit,
    onAddExpense: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = uiState.monthTitle,
            style = MaterialTheme.typography.headlineMedium
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.dashboard_ams_payout),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.payoutAmount,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.dashboard_payout_for_prev_month),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(
                        R.string.dashboard_payout_date,
                        uiState.payoutDate
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.dashboard_monthly_balance),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.monthlyBalance,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.dashboard_income_sum),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = uiState.incomeSum,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Column {
                        Text(
                            text = stringResource(R.string.dashboard_expense_sum),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = uiState.expenseSum,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }

        if (uiState.recentExpenses.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.dashboard_recent_expenses),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    uiState.recentExpenses.forEachIndexed { index, expense ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = expense.title,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = expense.date,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Text(
                                text = expense.amount,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        if (index < uiState.recentExpenses.lastIndex) {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = onAddIncome
            ) {
                Text(text = stringResource(R.string.action_add_income))
            }
            Button(
                modifier = Modifier.weight(1f),
                onClick = onAddExpense
            ) {
                Text(text = stringResource(R.string.action_add_expense))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardScreenPreview() {
    MaterialTheme {
        Surface {
            DashboardScreen(
                uiState = DashboardUiState(
                    monthTitle = "März 2026",
                    payoutAmount = "1.234,56 €",
                    payoutDate = "03.04.2026",
                    monthlyBalance = "+ 320,00 €",
                    incomeSum = "1.500,00 €",
                    expenseSum = "1.180,00 €",
                    recentExpenses = listOf(
                        DashboardExpenseItem(
                            title = "Lebensmittel",
                            date = "12.03.2026",
                            amount = "45,90 €"
                        ),
                        DashboardExpenseItem(
                            title = "Internet/Telefon",
                            date = "10.03.2026",
                            amount = "29,90 €"
                        ),
                        DashboardExpenseItem(
                            title = "Mobilität",
                            date = "08.03.2026",
                            amount = "18,00 €"
                        )
                    )
                ),
                onAddIncome = { },
                onAddExpense = { }
            )
        }
    }
}
