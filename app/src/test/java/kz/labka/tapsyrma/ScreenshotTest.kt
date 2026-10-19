package kz.labka.tapsyrma

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import kz.labka.tapsyrma.data.Priority
import kz.labka.tapsyrma.data.Task
import kz.labka.tapsyrma.domain.SortOrder
import kz.labka.tapsyrma.domain.StatusFilter
import kz.labka.tapsyrma.domain.TaskQuery
import kz.labka.tapsyrma.domain.ValidationResult
import kz.labka.tapsyrma.domain.computeStats
import kz.labka.tapsyrma.ui.detail.TaskDetailContent
import kz.labka.tapsyrma.ui.detail.TaskDetailUiState
import kz.labka.tapsyrma.ui.edit.TaskEditContent
import kz.labka.tapsyrma.ui.edit.TaskEditUiState
import kz.labka.tapsyrma.ui.list.TaskListContent
import kz.labka.tapsyrma.ui.list.TaskListUiState
import kz.labka.tapsyrma.ui.stats.StatsContent
import kz.labka.tapsyrma.ui.theme.TapsyrmaTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.time.LocalDate

/**
 * Есепке арналған экран суреттерін жасайды: app/build/screenshots/*.png.
 * Экрандар ViewModel-сіз, дайын күймен көрсетіледі.
 */
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w393dp-h851dp-xhdpi")
class ScreenshotTest {

    @get:Rule
    val rule = createComposeRule()

    private val today = LocalDate.now()

    private val sampleTasks = listOf(
        Task(
            id = 1, title = "Зертханалық жұмысты тапсыру",
            description = "Android қосымшасы, есеп және APK файл",
            priority = Priority.HIGH, dueDate = today.plusDays(2), createdAt = 5,
        ),
        Task(
            id = 2, title = "Физикадан емтиханға дайындалу",
            description = "Электр тогы, Ом заңы, Кирхгоф ережелері",
            priority = Priority.MEDIUM, dueDate = today, createdAt = 4,
        ),
        Task(
            id = 3, title = "Кітапханаға кітап қайтару",
            priority = Priority.LOW, dueDate = today.minusDays(1), createdAt = 3,
        ),
        Task(
            id = 4, title = "Спорт залына бару",
            description = "Кешкі 19:00", priority = Priority.LOW, dueDate = today.plusDays(10), createdAt = 2,
        ),
        Task(id = 5, title = "Анаға қоңырау шалу", priority = Priority.MEDIUM, isDone = true, createdAt = 1),
    )

    private fun capture(name: String, content: @Composable () -> Unit) {
        rule.setContent { TapsyrmaTheme(darkTheme = false) { content() } }
        rule.waitForIdle()
        val bitmap = rule.onRoot().captureToImage().asAndroidBitmap()
        val dir = File(System.getProperty("user.dir"), "build/screenshots").apply { mkdirs() }
        File(dir, "$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }

    @Test
    fun listScreen() = capture("01_tizim") {
        TaskListContent(
            state = TaskListUiState(
                tasks = TaskQuery.apply(sampleTasks, "", StatusFilter.ALL, SortOrder.DUE_DATE),
                totalCount = sampleTasks.size,
                isLoading = false,
            ),
            query = "",
            onQueryChange = {}, onFilterChange = {}, onSortChange = {}, onToggleDone = {},
            onOpenTask = {}, onAddTask = {}, onOpenStats = {},
        )
    }

    @Test
    fun searchScreen() = capture("02_izdeu") {
        TaskListContent(
            state = TaskListUiState(
                tasks = TaskQuery.apply(sampleTasks, "жұмыс", StatusFilter.ACTIVE, SortOrder.PRIORITY),
                filter = StatusFilter.ACTIVE,
                sort = SortOrder.PRIORITY,
                totalCount = sampleTasks.size,
                isLoading = false,
            ),
            query = "жұмыс",
            onQueryChange = {}, onFilterChange = {}, onSortChange = {}, onToggleDone = {},
            onOpenTask = {}, onAddTask = {}, onOpenStats = {},
        )
    }

    @Test
    fun detailScreen() = capture("03_tolyq_aqparat") {
        TaskDetailContent(
            state = TaskDetailUiState(task = sampleTasks.first(), isLoading = false),
            onBack = {}, onEdit = {}, onToggleDone = {}, onDelete = {},
        )
    }

    @Test
    fun editScreen() = capture("04_qosu") {
        TaskEditContent(
            state = TaskEditUiState(
                title = "Курстық жобаның презентациясы",
                description = "Слайдтар мен баяндаманы дайындау",
                priority = Priority.HIGH,
                dueDate = today.plusDays(7),
            ),
            onBack = {}, onTitleChange = {}, onDescriptionChange = {}, onPriorityChange = {},
            onDueDateChange = {}, onSave = {}, onMessageShown = {},
        )
    }

    @Test
    fun editScreenWithErrors() = capture("05_qateler") {
        TaskEditContent(
            state = TaskEditUiState(
                title = "",
                description = "",
                dueDate = today.minusDays(3),
                errors = ValidationResult(
                    titleError = "Тапсырма атауын енгізіңіз",
                    dueDateError = "Мерзімі ретінде өткен күнді таңдауға болмайды",
                ),
            ),
            onBack = {}, onTitleChange = {}, onDescriptionChange = {}, onPriorityChange = {},
            onDueDateChange = {}, onSave = {}, onMessageShown = {},
        )
    }

    @Test
    fun statsScreen() = capture("06_statistika") {
        StatsContent(stats = computeStats(sampleTasks, today), onBack = {})
    }
}
