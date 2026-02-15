package at.christian.kassabuch.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import at.christian.kassabuch.R

@Composable
fun MoreScreen(
    onShowCategories: () -> Unit,
    onShowPayouts: () -> Unit,
    onShowBudget: () -> Unit,
    onShowFixedExpenses: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.more_title),
            style = MaterialTheme.typography.headlineMedium
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(modifier = Modifier.weight(1f), onClick = onShowCategories) {
                Text(text = stringResource(R.string.categories_button))
            }
            Button(modifier = Modifier.weight(1f), onClick = onShowPayouts) {
                Text(text = stringResource(R.string.payout_schedule_button))
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(modifier = Modifier.weight(1f), onClick = onShowBudget) {
                Text(text = stringResource(R.string.budget_button))
            }
            Button(modifier = Modifier.weight(1f), onClick = onShowFixedExpenses) {
                Text(text = stringResource(R.string.fixed_expense_button))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MoreScreenPreview() {
    MaterialTheme {
        MoreScreen(
            onShowCategories = { },
            onShowPayouts = { },
            onShowBudget = { },
            onShowFixedExpenses = { }
        )
    }
}
