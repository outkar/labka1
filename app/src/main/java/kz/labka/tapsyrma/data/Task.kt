package kz.labka.tapsyrma.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/** Тапсырманың маңыздылық деңгейі. */
enum class Priority(val label: String) {
    LOW("Төмен"),
    MEDIUM("Орташа"),
    HIGH("Жоғары"),
}

/** Деректер қорындағы бір тапсырма. */
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val dueDate: LocalDate? = null,
    val priority: Priority = Priority.MEDIUM,
    val isDone: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
) {
    fun isOverdue(today: LocalDate): Boolean =
        !isDone && dueDate != null && dueDate.isBefore(today)
}
