package at.christian.kassabuch.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.clickable
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
    categories: List<String>,
    onAddRule: (String, Double, LocalDate) -> Unit,
    onUpdateRule: (String, Double, LocalDate) -> Unit,
    onBack: () -> Unit
) {
    var editCategory by rememberSaveable { mutableStateOf<String?>(null) }
    var showAddDialog by rememberSaveable { mutableStateOf(false) }

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
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { showAddDialog = true }) {
                    Text(text = stringResource(R.string.fixed_expense_add_button))
                }
                Button(onClick = onBack) {
                    Text(text = stringResource(R.string.action_back))
                }
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

    if (showAddDialog) {
        AddFixedExpenseDialog(
            categories = categories,
            onDismiss = { showAddDialog = false },
            onConfirm = { category, amount, validFrom ->
                onAddRule(category, amount, validFrom)
                showAddDialog = false
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

@Composable
private fun AddFixedExpenseDialog(
    categories: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (String, Double, LocalDate) -> Unit
) {
    val hasCategories = categories.isNotEmpty()
    var selectedCategory by rememberSaveable { mutableStateOf(categories.firstOrNull().orEmpty()) }
    var categoryInput by rememberSaveable { mutableStateOf(selectedCategory) }
    var categoryExpanded by rememberSaveable { mutableStateOf(false) }
    var amountInput by rememberSaveable { mutableStateOf("") }
    var dateInput by rememberSaveable { mutableStateOf(LocalDate.now().format(fixedDateInputFormatter)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.fixed_expense_add_title)) },
        confirmButton = {
            Button(onClick = {
                val amount = amountInput.replace(",", ".").toDoubleOrNull()
                val date = runCatching { LocalDate.parse(dateInput, fixedDateInputFormatter) }.getOrNull()
                val category = if (hasCategories) selectedCategory else categoryInput.trim()
                if (amount != null && date != null && category.isNotBlank()) {
                    onConfirm(category, amount, date)
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
                    Column {
                        Text(text = stringResource(R.string.fixed_expense_category_label))
                        OutlinedTextField(
                            value = selectedCategory,
                            onValueChange = { },
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { categoryExpanded = true }
                        )
                        FixedExpenseCategoryDropdown(
                            expanded = categoryExpanded,
                            categories = categories,
                            onDismiss = { categoryExpanded = false },
                            onSelect = {
                                selectedCategory = it
                                categoryExpanded = false
                            }
                        )
                    }
                } else {
                    OutlinedTextField(
                        value = categoryInput,
                        onValueChange = { categoryInput = it },
                        label = { Text(text = stringResource(R.string.fixed_expense_category_label)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

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

@Composable
private fun FixedExpenseCategoryDropdown(
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
            categories = listOf("Internet/Telefon", "Wohnen/Miete"),
            onAddRule = { _, _, _ -> },
            onUpdateRule = { _, _, _ -> },
            onBack = { }
        )
    }
}
