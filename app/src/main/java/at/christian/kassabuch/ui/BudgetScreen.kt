package at.christian.kassabuch.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import at.christian.kassabuch.R
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.GERMANY)

@Composable
fun BudgetScreen(
    uiState: BudgetUiState,
    onSetBudget: (Double) -> Unit,
    onMonthChange: (java.time.YearMonth) -> Unit,
    onBack: () -> Unit
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.budget_title),
                style = MaterialTheme.typography.headlineMedium
            )
            Button(onClick = onBack) {
                Text(text = stringResource(R.string.action_back))
            }
        }

        MonthPicker(
            month = uiState.selectedMonth,
            onPrevious = { onMonthChange(uiState.selectedMonth.minusMonths(1)) },
            onNext = { onMonthChange(uiState.selectedMonth.plusMonths(1)) }
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.budget_current_label),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (uiState.budgetAmount.isBlank()) "—" else uiState.budgetAmount,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = { showDialog = true }) {
                    Text(text = stringResource(R.string.budget_set_button))
                }
            }
        }

        if (uiState.isOverBudget) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.budget_warning),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(
                            R.string.budget_spend_label,
                            java.text.NumberFormat.getCurrencyInstance(Locale.GERMANY)
                                .format(uiState.currentSpend)
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

    if (showDialog) {
        SetBudgetDialog(
            onDismiss = { showDialog = false },
            onConfirm = { amount ->
                onSetBudget(amount)
                showDialog = false
            }
        )
    }
}

@Composable
private fun SetBudgetDialog(
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var amountInput by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.budget_set_title)) },
        confirmButton = {
            Button(onClick = {
                val amount = amountInput.replace(",", ".").toDoubleOrNull()
                if (amount != null) {
                    onConfirm(amount)
                }
            }) {
                Text(text = stringResource(R.string.action_save))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = stringResource(R.string.action_cancel))
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it },
                    label = { Text(text = stringResource(R.string.budget_amount_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun BudgetScreenPreview() {
    MaterialTheme {
        BudgetScreen(
            uiState = BudgetUiState(
                selectedMonth = YearMonth.of(2026, 3),
                budgetAmount = "500,00 €",
                rawBudgetAmount = 500.0,
                currentSpend = 620.0
            ),
            onSetBudget = { },
            onMonthChange = { },
            onBack = { }
        )
    }
}
