package com.example.cleanarchitecture.infrastructure.adapter

import com.example.cleanarchitecture.domain.model.Task
import com.example.cleanarchitecture.domain.model.TaskPriority
import com.example.cleanarchitecture.domain.model.TaskStatus
import com.example.cleanarchitecture.infrastructure.repository.SpringDataTaskRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@DataJpaTest
@Import(TaskPersistenceAdapter::class)
class TaskPersistenceAdapterTest {

    @Autowired
    private lateinit var adapter: TaskPersistenceAdapter

    @Autowired
    private lateinit var springDataTaskRepository: SpringDataTaskRepository

    private val now = LocalDateTime.of(2024, 1, 1, 12, 0)

    private fun createTask(
        title: String = "Test Task",
        status: TaskStatus = TaskStatus.PENDING,
        priority: TaskPriority = TaskPriority.MEDIUM
    ): Task = Task(
        id = 0,
        title = title,
        description = "Description",
        status = status,
        priority = priority,
        createdAt = now,
        updatedAt = now
    )

    @Test
    fun `should save and retrieve a task`() {
        val task = createTask()
        val saved = adapter.save(task)

        assertNotNull(saved.id)
        assertTrue(saved.id > 0)
        assertEquals("Test Task", saved.title)

        val found = adapter.findById(saved.id)
        assertNotNull(found)
        assertEquals(saved.id, found.id)
    }

    @Test
    fun `should return null when task not found`() {
        val found = adapter.findById(999L)
        assertNull(found)
    }

    @Test
    fun `should find all tasks`() {
        adapter.save(createTask("Task 1"))
        adapter.save(createTask("Task 2"))

        val all = adapter.findAll()
        assertEquals(2, all.size)
    }

    @Test
    fun `should find tasks by status`() {
        adapter.save(createTask("Pending", status = TaskStatus.PENDING))
        adapter.save(createTask("Completed", status = TaskStatus.COMPLETED))

        val pending = adapter.findByStatus(TaskStatus.PENDING)
        assertEquals(1, pending.size)
        assertEquals("Pending", pending[0].title)
    }

    @Test
    fun `should delete a task`() {
        val saved = adapter.save(createTask())
        assertTrue(adapter.existsById(saved.id))

        adapter.deleteById(saved.id)
        assertNull(adapter.findById(saved.id))
    }
}
