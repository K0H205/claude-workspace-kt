package com.example.cleanarchitecture.application.service

import com.example.cleanarchitecture.domain.exception.TaskNotFoundException
import com.example.cleanarchitecture.domain.model.Task
import com.example.cleanarchitecture.domain.model.TaskPriority
import com.example.cleanarchitecture.domain.model.TaskStatus
import com.example.cleanarchitecture.domain.repository.TaskRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import kotlin.test.assertEquals

class GetTaskServiceTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var getTaskService: GetTaskService

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
        getTaskService = GetTaskService(taskRepository)
    }

    @Test
    fun `should return task when found`() {
        every { taskRepository.findById(1L) } returns sampleTask

        val result = getTaskService.getById(1L)

        assertEquals(sampleTask, result)
    }

    @Test
    fun `should throw when task not found`() {
        every { taskRepository.findById(99L) } returns null

        assertThrows<TaskNotFoundException> {
            getTaskService.getById(99L)
        }
    }
}
