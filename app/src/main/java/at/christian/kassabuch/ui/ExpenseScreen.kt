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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import at.christian.kassabuch.R
import at.christian.kassabuch.data.ExpenseType
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val expenseDateInputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

@Composable
fun ExpenseScreen(
    uiState: ExpenseUiState,
    categories: List<String>,
    onAddExpense: (String, Double, LocalDate, ExpenseType, String?) -> Unit,
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
                text = stringResource(R.string.expense_title),
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
                Text(text = stringResource(R.string.action_add_expense))
            }
        }

        if (uiState.fixedItems.isEmpty() && uiState.variableItems.isEmpty()) {
            Text(
                text = stringResource(R.string.expense_empty_state),
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (uiState.fixedItems.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.expense_fixed_section),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(uiState.fixedItems, key = { it.id }) { expense ->
                        ExpenseCard(expense)
                    }
                }
                if (uiState.variableItems.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.expense_variable_section),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(uiState.variableItems, key = { it.id }) { expense ->
                        ExpenseCard(expense)
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddExpenseDialog(
            categories = categories,
            onDismiss = { showDialog = false },
            onConfirm = { category, amount, date, type, note ->
                onAddExpense(category, amount, date, type, note)
                showDialog = false
            }
        )
    }
}

@Composable
private fun ExpenseCard(expense: ExpenseListItem) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = expense.category,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${expense.amount} • ${expense.typeLabel}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = expense.date,
                style = MaterialTheme.typography.bodySmall
            )
            expense.note?.let { note ->
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = note,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun AddExpenseDialog(
    categories: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (String, Double, LocalDate, ExpenseType, String?) -> Unit
) {
    var selectedCategory by rememberSaveable { mutableStateOf(categories.firstOrNull().orEmpty()) }
    var categoryExpanded by rememberSaveable { mutableStateOf(false) }
    var amountInput by rememberSaveable { mutableStateOf("") }
    var dateInput by rememberSaveable { mutableStateOf(LocalDate.now().format(expenseDateInputFormatter)) }
    var type by rememberSaveable { mutableStateOf(ExpenseType.VARIABLE) }
    var noteInput by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.expense_add_title)) },
        confirmButton = {
            Button(onClick = {
                val amount = amountInput.replace(",", ".").toDoubleOrNull()
                val date = runCatching { LocalDate.parse(dateInput, expenseDateInputFormatter) }.getOrNull()
                if (amount != null && date != null && selectedCategory.isNotBlank()) {
                    val note = noteInput.trim().ifBlank { null }
                    onConfirm(selectedCategory, amount, date, type, note)
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
                    Text(text = stringResource(R.string.expense_category_label))
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = { },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { categoryExpanded = true }
                    )
                    ExpenseCategoryDropdown(
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
                    label = { Text(text = stringResource(R.string.expense_amount_label)) },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = dateInput,
                    onValueChange = { dateInput = it },
                    label = { Text(text = stringResource(R.string.expense_date_label)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Column {
                    Text(text = stringResource(R.string.expense_type_label))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { type = ExpenseType.FIXED }) {
                            Text(text = stringResource(R.string.expense_type_fixed))
                        }
                        Button(onClick = { type = ExpenseType.VARIABLE }) {
                            Text(text = stringResource(R.string.expense_type_variable))
                        }
                    }
                }

                OutlinedTextField(
                    value = noteInput,
                    onValueChange = { noteInput = it },
                    label = { Text(text = stringResource(R.string.expense_note_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}

@Composable
private fun ExpenseCategoryDropdown(
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
private fun ExpenseScreenPreview() {
    MaterialTheme {
        ExpenseScreen(
            uiState = ExpenseUiState(
                fixedItems = listOf(
                    ExpenseListItem(
                        id = 2,
                        category = "Internet/Telefon",
                        amount = "29,90 €",
                        date = "10.03.2026",
                        typeLabel = "Fix",
                        note = "Handyvertrag"
                    )
                ),
                variableItems = listOf(
                    ExpenseListItem(
                        id = 1,
                        category = "Lebensmittel",
                        amount = "45,90 €",
                        date = "12.03.2026",
                        typeLabel = "Variabel",
                        note = "Wocheneinkauf"
                    )
                )
            ),
            categories = listOf("Lebensmittel", "Internet/Telefon"),
            onAddExpense = { _, _, _, _, _ -> },
            onBack = { }
        )
    }
}
