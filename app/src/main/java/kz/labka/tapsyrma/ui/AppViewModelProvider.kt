package kz.labka.tapsyrma.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kz.labka.tapsyrma.TasksApp
import kz.labka.tapsyrma.ui.detail.TaskDetailViewModel
import kz.labka.tapsyrma.ui.edit.TaskEditViewModel
import kz.labka.tapsyrma.ui.list.TaskListViewModel
import kz.labka.tapsyrma.ui.stats.StatsViewModel

/** Барлық ViewModel-дерді AppContainer-дегі тәуелділіктермен жасайды. */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            TaskListViewModel(tasksApp().container.repository, tasksApp().container.settings)
        }
        initializer {
            TaskDetailViewModel(createSavedStateHandle(), tasksApp().container.repository)
        }
        initializer {
            TaskEditViewModel(createSavedStateHandle(), tasksApp().container.repository)
        }
        initializer {
            StatsViewModel(tasksApp().container.repository)
        }
    }
}

private fun CreationExtras.tasksApp(): TasksApp =
    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TasksApp
