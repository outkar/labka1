package kz.labka.tapsyrma.domain

import kz.labka.tapsyrma.data.Task
import java.text.Collator
import java.util.Locale

enum class SortOrder(val label: String) {
    DUE_DATE("Мерзімі бойынша"),
    PRIORITY("Маңыздылығы бойынша"),
    CREATED("Жаңалары алдымен"),
    TITLE("Атауы бойынша (А–Я)"),
}

enum class StatusFilter(val label: String) {
    ALL("Барлығы"),
    ACTIVE("Орындалмаған"),
    DONE("Орындалған"),
}

/** Тізімді іздеу, сүзу және сұрыптау логикасы. Android-қа тәуелсіз, сондықтан тесттеу оңай. */
object TaskQuery {

    fun apply(
        tasks: List<Task>,
        query: String,
        filter: StatusFilter,
        sort: SortOrder,
    ): List<Task> {
        val text = query.trim()
        return tasks
            .filter { text.isEmpty() || it.title.contains(text, true) || it.description.contains(text, true) }
            .filter {
                when (filter) {
                    StatusFilter.ALL -> true
                    StatusFilter.ACTIVE -> !it.isDone
                    StatusFilter.DONE -> it.isDone
                }
            }
            // Орындалмаған тапсырмалар әрқашан жоғарыда тұрады.
            .sortedWith(compareBy<Task> { it.isDone }.then(comparator(sort)))
    }

    private val byDueDate: Comparator<Task> =
        compareBy<Task, Long?>(nullsLast(naturalOrder<Long>())) { it.dueDate?.toEpochDay() }

    private fun comparator(sort: SortOrder): Comparator<Task> = when (sort) {
        SortOrder.DUE_DATE -> byDueDate.thenByDescending { it.priority.ordinal }
        SortOrder.PRIORITY -> compareByDescending<Task> { it.priority.ordinal }.then(byDueDate)
        SortOrder.CREATED -> compareByDescending<Task> { it.createdAt }
        SortOrder.TITLE -> compareBy<Task, String>(Collator.getInstance(Locale("kk", "KZ"))) { it.title }
    }
}
