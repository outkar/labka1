package kz.labka.tapsyrma

import kz.labka.tapsyrma.data.Priority
import kz.labka.tapsyrma.data.Task
import kz.labka.tapsyrma.domain.SortOrder
import kz.labka.tapsyrma.domain.StatusFilter
import kz.labka.tapsyrma.domain.TaskQuery
import kz.labka.tapsyrma.domain.computeStats
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class TaskQueryTest {
    private val today = LocalDate.of(2026, 10, 19)

    private val tasks = listOf(
        Task(id = 1, title = "Нан сатып алу", priority = Priority.LOW, dueDate = today, createdAt = 1),
        Task(id = 2, title = "Емтиханға дайындық", description = "Физика", priority = Priority.HIGH, dueDate = today.plusDays(5), createdAt = 2),
        Task(id = 3, title = "Анаға қоңырау шалу", priority = Priority.MEDIUM, isDone = true, createdAt = 3),
        Task(id = 4, title = "Зертханалық жұмыс", priority = Priority.HIGH, dueDate = today.minusDays(2), createdAt = 4),
    )

    private fun ids(list: List<Task>) = list.map { it.id }

    @Test
    fun searchMatchesTitleAndDescriptionIgnoringCase() {
        assertEquals(listOf(2L), ids(TaskQuery.apply(tasks, "физика", StatusFilter.ALL, SortOrder.DUE_DATE)))
        assertEquals(listOf(1L), ids(TaskQuery.apply(tasks, "  НАН ", StatusFilter.ALL, SortOrder.DUE_DATE)))
    }

    @Test
    fun statusFilterWorks() {
        assertEquals(listOf(3L), ids(TaskQuery.apply(tasks, "", StatusFilter.DONE, SortOrder.DUE_DATE)))
        assertEquals(3, TaskQuery.apply(tasks, "", StatusFilter.ACTIVE, SortOrder.DUE_DATE).size)
    }

    @Test
    fun sortByDueDatePutsTasksWithoutDateAndDoneTasksLast() {
        assertEquals(listOf(4L, 1L, 2L, 3L), ids(TaskQuery.apply(tasks, "", StatusFilter.ALL, SortOrder.DUE_DATE)))
    }

    @Test
    fun sortByPriority() {
        assertEquals(listOf(4L, 2L, 1L, 3L), ids(TaskQuery.apply(tasks, "", StatusFilter.ALL, SortOrder.PRIORITY)))
    }

    @Test
    fun sortByCreatedNewestFirst() {
        assertEquals(listOf(4L, 2L, 1L, 3L), ids(TaskQuery.apply(tasks, "", StatusFilter.ALL, SortOrder.CREATED)))
    }

    @Test
    fun statsAreCounted() {
        val stats = computeStats(tasks, today)
        assertEquals(4, stats.total)
        assertEquals(1, stats.done)
        assertEquals(3, stats.active)
        assertEquals(1, stats.overdue)
        assertEquals(1, stats.dueToday)
        assertEquals(2, stats.highPriorityActive)
        assertEquals(0.25f, stats.progress, 0.0001f)
    }
}
