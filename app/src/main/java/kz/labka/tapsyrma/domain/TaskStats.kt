package kz.labka.tapsyrma.domain

import kz.labka.tapsyrma.data.Priority
import kz.labka.tapsyrma.data.Task
import java.time.LocalDate

data class TaskStats(
    val total: Int = 0,
    val done: Int = 0,
    val active: Int = 0,
    val overdue: Int = 0,
    val dueToday: Int = 0,
    val highPriorityActive: Int = 0,
) {
    /** Орындалған тапсырмалардың үлесі, 0..1. */
    val progress: Float get() = if (total == 0) 0f else done.toFloat() / total
}

fun computeStats(tasks: List<Task>, today: LocalDate): TaskStats = TaskStats(
    total = tasks.size,
    done = tasks.count { it.isDone },
    active = tasks.count { !it.isDone },
    overdue = tasks.count { it.isOverdue(today) },
    dueToday = tasks.count { !it.isDone && it.dueDate == today },
    highPriorityActive = tasks.count { !it.isDone && it.priority == Priority.HIGH },
)
