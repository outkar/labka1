package kz.labka.tapsyrma.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kz.labka.tapsyrma.data.Task
import kz.labka.tapsyrma.data.TaskRepository
import kz.labka.tapsyrma.ui.navigation.TASK_ID_ARG

data class TaskDetailUiState(
    val task: Task? = null,
    val isLoading: Boolean = true,
)

class TaskDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: TaskRepository,
) : ViewModel() {

    private val taskId: Long = checkNotNull(savedStateHandle.get<Long>(TASK_ID_ARG))

    val uiState: StateFlow<TaskDetailUiState> = repository.observeTask(taskId)
        .map { TaskDetailUiState(task = it, isLoading = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TaskDetailUiState())

    fun toggleDone() {
        val task = uiState.value.task ?: return
        viewModelScope.launch { repository.setDone(task.id, !task.isDone) }
    }

    fun delete(onDeleted: () -> Unit) {
        val task = uiState.value.task ?: return
        viewModelScope.launch {
            repository.delete(task)
            onDeleted()
        }
    }
}
