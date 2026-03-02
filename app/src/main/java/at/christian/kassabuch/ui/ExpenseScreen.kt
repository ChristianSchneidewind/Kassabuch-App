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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddExpenseDialog(
    categories: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (String, Double, LocalDate, ExpenseType, String?) -> Unit
) {
    val hasCategories = categories.isNotEmpty()
    var selectedCategory by rememberSaveable { mutableStateOf(categories.firstOrNull().orEmpty()) }
    var categoryInput by rememberSaveable { mutableStateOf(selectedCategory) }
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
                val category = if (hasCategories) selectedCategory else categoryInput.trim()
                if (amount != null && date != null && category.isNotBlank()) {
                    val note = noteInput.trim().ifBlank { null }
                    onConfirm(category, amount, date, type, note)
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
                if (hasCategories) {
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = !categoryExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text(text = stringResource(R.string.expense_category_label)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        androidx.compose.material3.DropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            categories.forEach { category ->
                                androidx.compose.material3.DropdownMenuItem(
                                    text = { Text(text = category) },
                                    onClick = {
                                        selectedCategory = category
                                        categoryExpanded = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = categoryInput,
                        onValueChange = { categoryInput = it },
                        label = { Text(text = stringResource(R.string.expense_category_label)) },
                        modifier = Modifier.fillMaxWidth()
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
                        FilterChip(
                            selected = type == ExpenseType.FIXED,
                            onClick = { type = ExpenseType.FIXED },
                            label = { Text(text = stringResource(R.string.expense_type_fixed)) }
                        )
                        FilterChip(
                            selected = type == ExpenseType.VARIABLE,
                            onClick = { type = ExpenseType.VARIABLE },
                            label = { Text(text = stringResource(R.string.expense_type_variable)) }
                        )
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
