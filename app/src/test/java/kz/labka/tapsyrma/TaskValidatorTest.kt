package kz.labka.tapsyrma

import kz.labka.tapsyrma.domain.TaskValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class TaskValidatorTest {
    private val today = LocalDate.of(2026, 10, 19)

    @Test
    fun validTaskPasses() {
        val result = TaskValidator.validate("Курстық жұмыс", "", today.plusDays(3), today)
        assertTrue(result.isValid)
    }

    @Test
    fun emptyTitleIsRejected() {
        val result = TaskValidator.validate("   ", "", null, today)
        assertFalse(result.isValid)
        assertEquals("Тапсырма атауын енгізіңіз", result.titleError)
    }

    @Test
    fun tooShortAndTooLongTitlesAreRejected() {
        assertNotNull(TaskValidator.validate("ab", "", null, today).titleError)
        assertNotNull(TaskValidator.validate("а".repeat(TaskValidator.TITLE_MAX + 1), "", null, today).titleError)
    }

    @Test
    fun longDescriptionIsRejected() {
        val result = TaskValidator.validate("Есеп", "х".repeat(TaskValidator.DESCRIPTION_MAX + 1), null, today)
        assertNotNull(result.descriptionError)
    }

    @Test
    fun pastDateIsRejectedForNewTask() {
        val result = TaskValidator.validate("Есеп", "", today.minusDays(1), today)
        assertNotNull(result.dueDateError)
    }

    @Test
    fun unchangedPastDateIsAllowedWhenEditing() {
        val past = today.minusDays(5)
        val result = TaskValidator.validate("Есеп", "", past, today, originalDueDate = past)
        assertNull(result.dueDateError)
    }
}
