package com.example.cleanarchitecture.application.service

import com.example.cleanarchitecture.domain.exception.TaskNotFoundException
import com.example.cleanarchitecture.domain.repository.TaskRepository
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DeleteTaskServiceTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var deleteTaskService: DeleteTaskService

    @BeforeEach
    fun setUp() {
        taskRepository = mockk()
        deleteTaskService = DeleteTaskService(taskRepository)
    }

    @Test
    fun `should delete an existing task`() {
        every { taskRepository.existsById(1L) } returns true
        every { taskRepository.deleteById(1L) } just Runs

        deleteTaskService.delete(1L)

        verify(exactly = 1) { taskRepository.deleteById(1L) }
    }

    @Test
    fun `should throw when task not found`() {
        every { taskRepository.existsById(99L) } returns false

        assertThrows<TaskNotFoundException> {
            deleteTaskService.delete(99L)
        }
    }
}
