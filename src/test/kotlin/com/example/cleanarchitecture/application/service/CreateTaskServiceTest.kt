package com.example.cleanarchitecture.application.service

import com.example.cleanarchitecture.application.usecase.CreateTaskUseCase
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
import kotlin.test.assertNotNull

class CreateTaskServiceTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var createTaskService: CreateTaskService

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
        createTaskService = CreateTaskService(taskRepository)
    }

    @Test
    fun `should create and save a task`() {
        every { taskRepository.save(any()) } returns sampleTask

        val command = CreateTaskUseCase.Command("Test Task", "Test Description", TaskPriority.MEDIUM)
        val result = createTaskService.create(command)

        assertNotNull(result)
        assertEquals("Test Task", result.title)
        verify(exactly = 1) { taskRepository.save(any()) }
    }
}
