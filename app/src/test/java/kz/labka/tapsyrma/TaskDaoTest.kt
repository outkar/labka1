package kz.labka.tapsyrma

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kz.labka.tapsyrma.data.AppDatabase
import kz.labka.tapsyrma.data.Priority
import kz.labka.tapsyrma.data.Task
import kz.labka.tapsyrma.data.TaskDao
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

/** Room деректер қорымен CRUD амалдарын тексереді. */
@RunWith(AndroidJUnit4::class)
class TaskDaoTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: TaskDao

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.taskDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertUpdateToggleAndDelete() = runBlocking {
        val date = LocalDate.of(2026, 11, 1)
        val id = dao.insert(Task(title = "Есеп жазу", priority = Priority.HIGH, dueDate = date))

        val saved = dao.getById(id)!!
        assertEquals("Есеп жазу", saved.title)
        assertEquals(Priority.HIGH, saved.priority)
        assertEquals(date, saved.dueDate)

        dao.update(saved.copy(title = "Есепті тапсыру"))
        dao.setDone(id, true)
        val updated = dao.observeById(id).first()!!
        assertEquals("Есепті тапсыру", updated.title)
        assertTrue(updated.isDone)

        dao.delete(updated)
        assertNull(dao.getById(id))
        assertTrue(dao.observeAll().first().isEmpty())
    }
}
