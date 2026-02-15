package at.christian.kassabuch.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val fixedDateInputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

@Composable
fun FixedExpenseScreen(
    uiState: FixedExpenseUiState,
    onUpdateRule: (String, Double, LocalDate) -> Unit,
    onBack: () -> Unit
) {
    var editCategory by rememberSaveable { mutableStateOf<String?>(null) }

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
                text = stringResource(R.string.fixed_expense_title),
                style = MaterialTheme.typography.headlineMedium
            )
            Button(onClick = onBack) {
                Text(text = stringResource(R.string.action_back))
            }
        }

        if (uiState.items.isEmpty()) {
            Text(
                text = stringResource(R.string.fixed_expense_empty),
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(uiState.items, key = { it.id }) { item ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = item.category,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.amount,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(
                                    R.string.fixed_expense_valid_from,
                                    item.validFrom
                                ),
                                style = MaterialTheme.typography.bodySmall
                            )
                            item.validTo?.let { validTo ->
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = stringResource(
                                        R.string.fixed_expense_valid_to,
                                        validTo
                                    ),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(onClick = { editCategory = item.category }) {
                                    Text(text = stringResource(R.string.action_edit))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    editCategory?.let { category ->
        EditFixedExpenseDialog(
            category = category,
            onDismiss = { editCategory = null },
            onConfirm = { amount, validFrom ->
                onUpdateRule(category, amount, validFrom)
                editCategory = null
            }
        )
    }
}

@Composable
private fun EditFixedExpenseDialog(
    category: String,
    onDismiss: () -> Unit,
    onConfirm: (Double, LocalDate) -> Unit
) {
    var amountInput by rememberSaveable { mutableStateOf("") }
    var dateInput by rememberSaveable { mutableStateOf(LocalDate.now().format(fixedDateInputFormatter)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.fixed_expense_edit_title, category)) },
        confirmButton = {
            Button(onClick = {
                val amount = amountInput.replace(",", ".").toDoubleOrNull()
                val date = runCatching { LocalDate.parse(dateInput, fixedDateInputFormatter) }.getOrNull()
                if (amount != null && date != null) {
                    onConfirm(amount, date)
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
                    value = category,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text(text = stringResource(R.string.fixed_expense_category_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it },
                    label = { Text(text = stringResource(R.string.fixed_expense_amount_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dateInput,
                    onValueChange = { dateInput = it },
                    label = { Text(text = stringResource(R.string.fixed_expense_date_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun FixedExpenseScreenPreview() {
    MaterialTheme {
        FixedExpenseScreen(
            uiState = FixedExpenseUiState(
                items = listOf(
                    FixedExpenseItem(
                        id = 1,
                        category = "Internet/Telefon",
                        amount = "29,90 €",
                        validFrom = "01.01.2026",
                        validTo = null
                    ),
                    FixedExpenseItem(
                        id = 2,
                        category = "Wohnen/Miete",
                        amount = "620,00 €",
                        validFrom = "01.02.2026",
                        validTo = "30.06.2026"
                    )
                )
            ),
            onUpdateRule = { _, _, _ -> },
            onBack = { }
        )
    }
}
