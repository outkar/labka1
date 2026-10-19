package kz.labka.tapsyrma.ui.common

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kz.labka.tapsyrma.data.Priority
import kz.labka.tapsyrma.ui.theme.PriorityColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

fun LocalDate.format(): String = format(dateFormatter)

/** Мерзімді адамға түсінікті түрде көрсету: «Бүгін», «Ертең», «3 күн өтті». */
fun LocalDate.relativeLabel(today: LocalDate): String {
    val days = ChronoUnit.DAYS.between(today, this)
    return when {
        days == 0L -> "Бүгін"
        days == 1L -> "Ертең"
        days == -1L -> "Кеше"
        days < 0 -> "${format()} · ${-days} күн өтті"
        days < 7 -> "${format()} · $days күннен кейін"
        else -> format()
    }
}

fun Priority.color(): Color = when (this) {
    Priority.HIGH -> PriorityColors.High
    Priority.MEDIUM -> PriorityColors.Medium
    Priority.LOW -> PriorityColors.Low
}

@Composable
fun PriorityBadge(priority: Priority, modifier: Modifier = Modifier) {
    val color = priority.color()
    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.14f),
        contentColor = color,
        shape = RoundedCornerShape(8.dp),
    ) {
        Text(
            text = priority.label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
        )
    }
}
