package kz.labka.tapsyrma.ui.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kz.labka.tapsyrma.data.Priority
import kz.labka.tapsyrma.data.Task
import kz.labka.tapsyrma.data.TaskRepository
import kz.labka.tapsyrma.domain.TaskValidator
import kz.labka.tapsyrma.domain.ValidationResult
import kz.labka.tapsyrma.ui.navigation.NEW_TASK_ID
import kz.labka.tapsyrma.ui.navigation.TASK_ID_ARG
import java.time.LocalDate

data class TaskEditUiState(
    val isNew: Boolean = true,
    val isLoading: Boolean = false,
    val title: String = "",
    val description: String = "",
    val priority: Priority = Priority.MEDIUM,
    val dueDate: LocalDate? = null,
    val errors: ValidationResult = ValidationResult(),
    val message: String? = null,
    val isSaved: Boolean = false,
)

class TaskEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: TaskRepository,
) : ViewModel() {

    private val taskId: Long = savedStateHandle.get<Long>(TASK_ID_ARG) ?: NEW_TASK_ID
    private var original: Task? = null

    var uiState by mutableStateOf(TaskEditUiState(isNew = taskId == NEW_TASK_ID, isLoading = taskId != NEW_TASK_ID))
        private set

    init {
        if (taskId != NEW_TASK_ID) {
            viewModelScope.launch {
                val task = repository.getTask(taskId)
                original = task
                uiState = if (task == null) {
                    uiState.copy(isLoading = false, message = "Тапсырма табылмады")
                } else {
                    uiState.copy(
                        isLoading = false,
                        title = task.title,
                        description = task.description,
                        priority = task.priority,
                        dueDate = task.dueDate,
                    )
                }
            }
        }
    }

    // Пайдаланушы өрісті түзете бастағанда, сол өрістің қатесі жасырылады.
    fun onTitleChange(value: String) {
        uiState = uiState.copy(title = value, errors = uiState.errors.copy(titleError = null))
    }

    fun onDescriptionChange(value: String) {
        uiState = uiState.copy(description = value, errors = uiState.errors.copy(descriptionError = null))
    }

    fun onPriorityChange(value: Priority) {
        uiState = uiState.copy(priority = value)
    }

    fun onDueDateChange(value: LocalDate?) {
        uiState = uiState.copy(dueDate = value, errors = uiState.errors.copy(dueDateError = null))
    }

    fun onMessageShown() {
        uiState = uiState.copy(message = null)
    }

    fun save() {
        val state = uiState
        val errors = TaskValidator.validate(
            title = state.title,
            description = state.description,
            dueDate = state.dueDate,
            today = LocalDate.now(),
            originalDueDate = original?.dueDate,
        )
        if (!errors.isValid) {
            uiState = state.copy(errors = errors, message = "Қателерді түзетіп, қайта сақтаңыз")
            return
        }
        viewModelScope.launch {
            try {
                val base = original
                if (base == null) {
                    repository.add(
                        Task(
                            title = state.title.trim(),
                            description = state.description.trim(),
                            priority = state.priority,
                            dueDate = state.dueDate,
                        ),
                    )
                } else {
                    repository.update(
                        base.copy(
                            title = state.title.trim(),
                            description = state.description.trim(),
                            priority = state.priority,
                            dueDate = state.dueDate,
                        ),
                    )
                }
                uiState = uiState.copy(isSaved = true)
            } catch (e: Exception) {
                uiState = uiState.copy(message = "Сақтау кезінде қате пайда болды: ${e.localizedMessage}")
            }
        }
    }
}
