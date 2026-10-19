package kz.labka.tapsyrma.data

import kotlinx.coroutines.flow.Flow

/** ViewModel-дер деректер қорымен тек осы класс арқылы жұмыс істейді. */
class TaskRepository(private val dao: TaskDao) {
    fun observeTasks(): Flow<List<Task>> = dao.observeAll()

    fun observeTask(id: Long): Flow<Task?> = dao.observeById(id)

    suspend fun getTask(id: Long): Task? = dao.getById(id)

    suspend fun add(task: Task): Long = dao.insert(task)

    suspend fun update(task: Task) = dao.update(task)

    suspend fun delete(task: Task) = dao.delete(task)

    suspend fun setDone(id: Long, done: Boolean) = dao.setDone(id, done)
}
