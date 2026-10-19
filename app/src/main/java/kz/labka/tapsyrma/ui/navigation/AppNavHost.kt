package kz.labka.tapsyrma.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kz.labka.tapsyrma.ui.detail.TaskDetailScreen
import kz.labka.tapsyrma.ui.edit.TaskEditScreen
import kz.labka.tapsyrma.ui.list.TaskListScreen
import kz.labka.tapsyrma.ui.stats.StatsScreen

const val TASK_ID_ARG = "taskId"
const val NEW_TASK_ID = -1L

private object Routes {
    const val LIST = "tasks"
    const val DETAIL = "tasks/{$TASK_ID_ARG}"
    const val EDIT = "edit?$TASK_ID_ARG={$TASK_ID_ARG}"
    const val STATS = "stats"

    fun detail(id: Long) = "tasks/$id"
    fun edit(id: Long = NEW_TASK_ID) = "edit?$TASK_ID_ARG=$id"
}

/** Экрандар арасындағы навигация: Тізім → Толық ақпарат → Өзгерту, Тізім → Статистика. */
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.LIST) {
        composable(Routes.LIST) {
            TaskListScreen(
                onAddTask = { navController.navigate(Routes.edit()) },
                onOpenTask = { id -> navController.navigate(Routes.detail(id)) },
                onOpenStats = { navController.navigate(Routes.STATS) },
            )
        }
        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument(TASK_ID_ARG) { type = NavType.LongType }),
        ) {
            TaskDetailScreen(
                onBack = { navController.navigateUp() },
                onEdit = { id -> navController.navigate(Routes.edit(id)) },
            )
        }
        composable(
            route = Routes.EDIT,
            arguments = listOf(
                navArgument(TASK_ID_ARG) {
                    type = NavType.LongType
                    defaultValue = NEW_TASK_ID
                },
            ),
        ) {
            TaskEditScreen(
                onBack = { navController.navigateUp() },
            )
        }
        composable(Routes.STATS) {
            StatsScreen(onBack = { navController.navigateUp() })
        }
    }
}
