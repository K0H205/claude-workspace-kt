package com.example.cleanarchitecture.application.service

import com.example.cleanarchitecture.application.usecase.CreateTaskUseCase
import com.example.cleanarchitecture.domain.exception.TaskNotFoundException
import com.example.cleanarchitecture.domain.model.Task
import com.example.cleanarchitecture.domain.model.TaskPriority
import com.example.cleanarchitecture.domain.model.TaskStatus
import com.example.cleanarchitecture.domain.repository.TaskRepository
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TaskServiceTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var taskService: TaskService

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
        taskService = TaskService(taskRepository)
    }

    @Nested
    inner class Create {

        @Test
        fun `should create and save a task`() {
            every { taskRepository.save(any()) } returns sampleTask

            val command = CreateTaskUseCase.Command("Test Task", "Test Description", TaskPriority.MEDIUM)
            val result = taskService.create(command)

            assertNotNull(result)
            assertEquals("Test Task", result.title)
            verify(exactly = 1) { taskRepository.save(any()) }
        }
    }

    @Nested
    inner class GetById {

        @Test
        fun `should return task when found`() {
            every { taskRepository.findById(1L) } returns sampleTask

            val result = taskService.getById(1L)

            assertEquals(sampleTask, result)
        }

        @Test
        fun `should throw when task not found`() {
            every { taskRepository.findById(99L) } returns null

            assertThrows<TaskNotFoundException> {
                taskService.getById(99L)
            }
        }
    }

    @Nested
    inner class List {

        @Test
        fun `should return all tasks when no status filter`() {
            every { taskRepository.findAll() } returns listOf(sampleTask)

            val result = taskService.list(null)

            assertEquals(1, result.size)
            verify(exactly = 1) { taskRepository.findAll() }
        }

        @Test
        fun `should filter tasks by status`() {
            every { taskRepository.findByStatus(TaskStatus.PENDING) } returns listOf(sampleTask)

            val result = taskService.list(TaskStatus.PENDING)

            assertEquals(1, result.size)
            verify(exactly = 1) { taskRepository.findByStatus(TaskStatus.PENDING) }
        }
    }

    @Nested
    inner class Complete {

        @Test
        fun `should complete a task`() {
            val completedTask = sampleTask.copy(status = TaskStatus.COMPLETED)
            every { taskRepository.findById(1L) } returns sampleTask
            every { taskRepository.save(any()) } returns completedTask

            val result = taskService.complete(1L)

            assertEquals(TaskStatus.COMPLETED, result.status)
        }

        @Test
        fun `should throw when task not found`() {
            every { taskRepository.findById(99L) } returns null

            assertThrows<TaskNotFoundException> {
                taskService.complete(99L)
            }
        }
    }

    @Nested
    inner class Delete {

        @Test
        fun `should delete an existing task`() {
            every { taskRepository.existsById(1L) } returns true
            every { taskRepository.deleteById(1L) } just Runs

            taskService.delete(1L)

            verify(exactly = 1) { taskRepository.deleteById(1L) }
        }

        @Test
        fun `should throw when task not found`() {
            every { taskRepository.existsById(99L) } returns false

            assertThrows<TaskNotFoundException> {
                taskService.delete(99L)
            }
        }
    }
}
