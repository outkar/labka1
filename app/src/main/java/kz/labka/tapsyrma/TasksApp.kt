package kz.labka.tapsyrma

import android.app.Application
import kz.labka.tapsyrma.data.AppDatabase
import kz.labka.tapsyrma.data.SettingsStore
import kz.labka.tapsyrma.data.TaskRepository

/** Қолданба бойы бір данада болатын тәуелділіктер. */
class AppContainer(app: Application) {
    val repository: TaskRepository by lazy { TaskRepository(AppDatabase.build(app).taskDao()) }
    val settings: SettingsStore by lazy { SettingsStore(app) }
}

class TasksApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
