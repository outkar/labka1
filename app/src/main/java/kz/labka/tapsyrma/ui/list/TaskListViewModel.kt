package kz.labka.tapsyrma.ui.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kz.labka.tapsyrma.data.SettingsStore
import kz.labka.tapsyrma.data.Task
import kz.labka.tapsyrma.data.TaskRepository
import kz.labka.tapsyrma.domain.SortOrder
import kz.labka.tapsyrma.domain.StatusFilter
import kz.labka.tapsyrma.domain.TaskQuery

data class TaskListUiState(
    val tasks: List<Task> = emptyList(),
    val filter: StatusFilter = StatusFilter.ALL,
    val sort: SortOrder = SortOrder.DUE_DATE,
    val totalCount: Int = 0,
    val isLoading: Boolean = true,
)

class TaskListViewModel(
    private val repository: TaskRepository,
    private val settings: SettingsStore,
) : ViewModel() {

    /** Іздеу жолы Compose күйінде сақталады, сондықтан курсор «секірмейді». */
    var query by mutableStateOf("")
        private set

    val uiState: StateFlow<TaskListUiState> = combine(
        repository.observeTasks(),
        snapshotFlow { query },
        settings.statusFilter,
        settings.sortOrder,
    ) { tasks, text, filter, sort ->
        TaskListUiState(
            tasks = TaskQuery.apply(tasks, text, filter, sort),
            filter = filter,
            sort = sort,
            totalCount = tasks.size,
            isLoading = false,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TaskListUiState())

    fun onQueryChange(value: String) {
        query = value
    }

    fun onFilterChange(filter: StatusFilter) {
        viewModelScope.launch { settings.setStatusFilter(filter) }
    }

    fun onSortChange(sort: SortOrder) {
        viewModelScope.launch { settings.setSortOrder(sort) }
    }

    fun onToggleDone(task: Task) {
        viewModelScope.launch { repository.setDone(task.id, !task.isDone) }
    }
}
