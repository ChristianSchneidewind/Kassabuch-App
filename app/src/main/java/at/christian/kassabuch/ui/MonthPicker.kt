package at.christian.kassabuch.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private val monthPickerFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.GERMANY)

@Composable
fun MonthPicker(
    month: YearMonth,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(onClick = onPrevious) {
            Text(text = "◀")
        }
        Text(text = month.format(monthPickerFormatter))
        Button(onClick = onNext) {
            Text(text = "▶")
        }
    }
}
