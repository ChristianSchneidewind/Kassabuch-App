package at.christian.kassabuch.ui

import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import at.christian.kassabuch.R
import at.christian.kassabuch.data.IncomeType
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val dateInputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

@Composable
fun IncomeScreen(
    uiState: IncomeUiState,
    categories: List<String>,
    onAddIncome: (String, Double, LocalDate, IncomeType, String?) -> Unit,
    onEditDailyRate: (Double, LocalDate) -> Unit,
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
                text = stringResource(R.string.income_title),
                style = MaterialTheme.typography.headlineMedium
            )
            Button(onClick = onBack) {
                Text(text = stringResource(R.string.action_back))
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(onClick = { showDialog = true }) {
                Text(text = stringResource(R.string.action_add_income))
            }
        }

        if (uiState.items.isEmpty()) {
            Text(
                text = stringResource(R.string.income_empty_state),
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(uiState.items, key = { it.id }) { income ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = income.category,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${income.amount} • ${income.typeLabel}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = income.date,
                                style = MaterialTheme.typography.bodySmall
                            )
                            income.note?.let { note ->
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = note,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddIncomeDialog(
            categories = categories,
            onDismiss = { showDialog = false },
            onConfirm = { category, amount, date, type, note, dailyRate ->
                onAddIncome(category, amount, date, type, note)
                dailyRate?.let { onEditDailyRate(it, date) }
                showDialog = false
            }
        )
    }
}

@Composable
private fun AddIncomeDialog(
    categories: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (String, Double, LocalDate, IncomeType, String?, Double?) -> Unit
) {
    var selectedCategory by rememberSaveable { mutableStateOf(categories.firstOrNull().orEmpty()) }
    var categoryExpanded by rememberSaveable { mutableStateOf(false) }
    var amountInput by rememberSaveable { mutableStateOf("") }
    var dateInput by rememberSaveable { mutableStateOf(LocalDate.now().format(dateInputFormatter)) }
    var type by rememberSaveable { mutableStateOf(IncomeType.ONE_TIME) }
    var noteInput by rememberSaveable { mutableStateOf("") }
    var dailyRateInput by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.income_add_title)) },
        confirmButton = {
            Button(onClick = {
                val amount = amountInput.replace(",", ".").toDoubleOrNull()
                val date = runCatching { LocalDate.parse(dateInput, dateInputFormatter) }.getOrNull()
                if (amount != null && date != null && selectedCategory.isNotBlank()) {
                    val note = noteInput.trim().ifBlank { null }
                    val dailyRate = dailyRateInput.replace(",", ".").toDoubleOrNull()
                    onConfirm(selectedCategory, amount, date, type, note, dailyRate)
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
                Column {
                    Text(text = stringResource(R.string.income_category_label))
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = { },
                        readOnly = true,
                        enabled = categories.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { if (categories.isNotEmpty()) categoryExpanded = true }
                    )
                    CategoryDropdown(
                        expanded = categoryExpanded,
                        categories = categories,
                        onDismiss = { categoryExpanded = false },
                        onSelect = {
                            selectedCategory = it
                            categoryExpanded = false
                        }
                    )
                }

                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it },
                    label = { Text(text = stringResource(R.string.income_amount_label)) },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = dateInput,
                    onValueChange = { dateInput = it },
                    label = { Text(text = stringResource(R.string.income_date_label)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Column {
                    Text(text = stringResource(R.string.income_type_label))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { type = IncomeType.ONE_TIME }) {
                            Text(text = stringResource(R.string.income_type_one_time))
                        }
                        Button(onClick = { type = IncomeType.RECURRING }) {
                            Text(text = stringResource(R.string.income_type_recurring))
                        }
                    }
                }

                OutlinedTextField(
                    value = noteInput,
                    onValueChange = { noteInput = it },
                    label = { Text(text = stringResource(R.string.income_note_label)) },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = dailyRateInput,
                    onValueChange = { dailyRateInput = it },
                    label = { Text(text = stringResource(R.string.income_daily_rate_label)) },
                    supportingText = {
                        Text(text = stringResource(R.string.income_daily_rate_hint))
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}

@Composable
private fun CategoryDropdown(
    expanded: Boolean,
    categories: List<String>,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    androidx.compose.material3.DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        categories.forEach { category ->
            androidx.compose.material3.DropdownMenuItem(
                text = { Text(text = category) },
                onClick = { onSelect(category) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun IncomeScreenPreview() {
    MaterialTheme {
        IncomeScreen(
            uiState = IncomeUiState(
                items = listOf(
                    IncomeListItem(
                        id = 1,
                        category = "Lohn",
                        amount = "1.500,00 €",
                        date = "01.03.2026",
                        typeLabel = "Einmalig",
                        note = "Bonus"
                    ),
                    IncomeListItem(
                        id = 2,
                        category = "Sozialleistungen",
                        amount = "800,00 €",
                        date = "02.03.2026",
                        typeLabel = "Wiederkehrend",
                        note = "Notstandshilfe"
                    )
                )
            ),
            categories = listOf("Lohn", "Sozialleistungen"),
            onAddIncome = { _, _, _, _, _ -> },
            onEditDailyRate = { _, _ -> },
            onBack = { }
        )
    }
}
