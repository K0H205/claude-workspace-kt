package com.example.cleanarchitecture.application.service

import com.example.cleanarchitecture.domain.model.Task
import com.example.cleanarchitecture.domain.model.TaskPriority
import com.example.cleanarchitecture.domain.model.TaskStatus
import com.example.cleanarchitecture.domain.repository.TaskRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals

class ListTasksServiceTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var listTasksService: ListTasksService

    private val now = LocalDateTime.of(2024, 1, 1, 12, 0)
    private val sampleTask = Task(
        id = 1L,
        title = "Test Task",
        description = "Test Description",
        status = TaskStatus.PENDING,
        priority = TaskPriority.MEDIUM,
        createdAt = now,
        updatedAt = now
    )

    @BeforeEach
    fun setUp() {
        taskRepository = mockk()
        listTasksService = ListTasksService(taskRepository)
    }

    @Test
    fun `should return all tasks when no status filter`() {
        every { taskRepository.findAll() } returns listOf(sampleTask)

        val result = listTasksService.list(null)

        assertEquals(1, result.size)
        verify(exactly = 1) { taskRepository.findAll() }
    }

    @Test
    fun `should filter tasks by status`() {
        every { taskRepository.findByStatus(TaskStatus.PENDING) } returns listOf(sampleTask)

        val result = listTasksService.list(TaskStatus.PENDING)

        assertEquals(1, result.size)
        verify(exactly = 1) { taskRepository.findByStatus(TaskStatus.PENDING) }
    }
}
