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

private val payoutInputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

@Composable
fun PayoutScheduleScreen(
    uiState: PayoutScheduleUiState,
    onUpdateSchedule: (Long, java.time.YearMonth, LocalDate) -> Unit,
    onBack: () -> Unit
) {
    var editItem by rememberSaveable { mutableStateOf<PayoutScheduleItem?>(null) }

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
                text = stringResource(R.string.payout_schedule_title),
                style = MaterialTheme.typography.headlineMedium
            )
            Button(onClick = onBack) {
                Text(text = stringResource(R.string.action_back))
            }
        }

        Text(
            text = stringResource(R.string.payout_schedule_hint),
            style = MaterialTheme.typography.bodySmall
        )

        if (uiState.items.isEmpty()) {
            Text(
                text = stringResource(R.string.payout_schedule_empty),
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(uiState.items, key = { it.id }) { item ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = item.month,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.payoutDate,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(onClick = { editItem = item }) {
                                    Text(text = stringResource(R.string.action_edit))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    editItem?.let { item ->
        EditPayoutDialog(
            item = item,
            onDismiss = { editItem = null },
            onConfirm = { payoutDate ->
                onUpdateSchedule(item.id, item.rawMonth, payoutDate)
                editItem = null
            }
        )
    }
}

@Composable
private fun EditPayoutDialog(
    item: PayoutScheduleItem,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate) -> Unit
) {
    var dateInput by rememberSaveable { mutableStateOf(item.payoutDate) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.payout_schedule_edit_title)) },
        confirmButton = {
            Button(onClick = {
                val date = runCatching { LocalDate.parse(dateInput, payoutInputFormatter) }.getOrNull()
                if (date != null) {
                    onConfirm(date)
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
                    value = item.month,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text(text = stringResource(R.string.payout_schedule_month_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dateInput,
                    onValueChange = { dateInput = it },
                    label = { Text(text = stringResource(R.string.payout_schedule_date_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun PayoutScheduleScreenPreview() {
    MaterialTheme {
        PayoutScheduleScreen(
            uiState = PayoutScheduleUiState(
                items = listOf(
                    PayoutScheduleItem(
                        id = 1,
                        month = "Februar 2026",
                        rawMonth = java.time.YearMonth.of(2026, 2),
                        payoutDate = "03.03.2026"
                    ),
                    PayoutScheduleItem(
                        id = 2,
                        month = "März 2026",
                        rawMonth = java.time.YearMonth.of(2026, 3),
                        payoutDate = "03.04.2026"
                    )
                )
            ),
            onUpdateSchedule = { _, _, _ -> },
            onBack = { }
        )
    }
}
