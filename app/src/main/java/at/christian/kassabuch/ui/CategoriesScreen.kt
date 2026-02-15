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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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

private enum class CategoryTab(val labelRes: Int) {
    INCOME(R.string.categories_income_tab),
    EXPENSE(R.string.categories_expense_tab)
}

@Composable
fun CategoriesScreen(
    uiState: CategoriesUiState,
    onAddIncome: (String) -> Unit,
    onAddExpense: (String) -> Unit,
    onRenameIncome: (Long, String) -> Unit,
    onRenameExpense: (Long, String) -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by rememberSaveable { mutableStateOf(CategoryTab.INCOME) }
    var editCategory by rememberSaveable { mutableStateOf<CategoryItem?>(null) }
    var isIncomeEdit by rememberSaveable { mutableStateOf(true) }
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
                text = stringResource(R.string.categories_title),
                style = MaterialTheme.typography.headlineMedium
            )
            Button(onClick = onBack) {
                Text(text = stringResource(R.string.action_back))
            }
        }

        TabRow(selectedTabIndex = selectedTab.ordinal) {
            CategoryTab.values().forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTab.ordinal == index,
                    onClick = { selectedTab = tab },
                    text = { Text(text = stringResource(tab.labelRes)) }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(onClick = { showAddDialog = true }) {
                Text(text = stringResource(R.string.categories_add_button))
            }
        }

        val items = if (selectedTab == CategoryTab.INCOME) uiState.income else uiState.expenses
        if (items.isEmpty()) {
            Text(
                text = stringResource(R.string.categories_empty_state),
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(items, key = { it.id }) { item ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(onClick = {
                                    editCategory = item
                                    isIncomeEdit = selectedTab == CategoryTab.INCOME
                                }) {
                                    Text(text = stringResource(R.string.action_rename))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddCategoryDialog(
            title = stringResource(R.string.categories_add_title),
            onDismiss = { showAddDialog = false },
            onConfirm = { name ->
                if (selectedTab == CategoryTab.INCOME) {
                    onAddIncome(name)
                } else {
                    onAddExpense(name)
                }
                showAddDialog = false
            }
        )
    }

    editCategory?.let { category ->
        EditCategoryDialog(
            title = stringResource(R.string.categories_rename_title),
            initialValue = category.name,
            onDismiss = { editCategory = null },
            onConfirm = { name ->
                if (isIncomeEdit) {
                    onRenameIncome(category.id, name)
                } else {
                    onRenameExpense(category.id, name)
                }
                editCategory = null
            }
        )
    }
}

@Composable
private fun AddCategoryDialog(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var nameInput by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        confirmButton = {
            Button(onClick = {
                val name = nameInput.trim()
                if (name.isNotBlank()) {
                    onConfirm(name)
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
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text(text = stringResource(R.string.categories_name_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}

@Composable
private fun EditCategoryDialog(
    title: String,
    initialValue: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var nameInput by rememberSaveable { mutableStateOf(initialValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        confirmButton = {
            Button(onClick = {
                val name = nameInput.trim()
                if (name.isNotBlank()) {
                    onConfirm(name)
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
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text(text = stringResource(R.string.categories_name_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun CategoriesScreenPreview() {
    MaterialTheme {
        CategoriesScreen(
            uiState = CategoriesUiState(
                income = listOf(
                    CategoryItem(id = 1, name = "Lohn"),
                    CategoryItem(id = 2, name = "Sozialleistungen")
                ),
                expenses = listOf(
                    CategoryItem(id = 3, name = "Lebensmittel"),
                    CategoryItem(id = 4, name = "Wohnen/Miete")
                )
            ),
            onAddIncome = { },
            onAddExpense = { },
            onRenameIncome = { _, _ -> },
            onRenameExpense = { _, _ -> },
            onBack = { }
        )
    }
}
