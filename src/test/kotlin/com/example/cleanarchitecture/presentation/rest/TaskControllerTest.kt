package com.example.cleanarchitecture.presentation.rest

import com.example.cleanarchitecture.application.usecase.*
import com.example.cleanarchitecture.domain.exception.TaskNotFoundException
import com.example.cleanarchitecture.domain.model.Task
import com.example.cleanarchitecture.domain.model.TaskPriority
import com.example.cleanarchitecture.domain.model.TaskStatus
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.Runs
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*
import java.time.LocalDateTime

@WebMvcTest(TaskController::class)
class TaskControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var createTaskUseCase: CreateTaskUseCase

    @MockkBean
    private lateinit var getTaskUseCase: GetTaskUseCase

    @MockkBean
    private lateinit var listTasksUseCase: ListTasksUseCase

    @MockkBean
    private lateinit var completeTaskUseCase: CompleteTaskUseCase

    @MockkBean
    private lateinit var deleteTaskUseCase: DeleteTaskUseCase

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

    @Test
    fun `POST should create a task and return 201`() {
        every { createTaskUseCase.create(any()) } returns sampleTask

        mockMvc.post("/api/tasks") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(
                mapOf("title" to "Test Task", "description" to "Test Description", "priority" to "MEDIUM")
            )
        }.andExpect {
            status { isCreated() }
            jsonPath("$.id") { value(1) }
            jsonPath("$.title") { value("Test Task") }
            jsonPath("$.status") { value("PENDING") }
        }
    }

    @Test
    fun `POST should return 400 when title is blank`() {
        mockMvc.post("/api/tasks") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(
                mapOf("title" to "", "description" to "desc")
            )
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `GET by id should return task`() {
        every { getTaskUseCase.getById(1L) } returns sampleTask

        mockMvc.get("/api/tasks/1").andExpect {
            status { isOk() }
            jsonPath("$.id") { value(1) }
            jsonPath("$.title") { value("Test Task") }
        }
    }

    @Test
    fun `GET by id should return 404 when not found`() {
        every { getTaskUseCase.getById(99L) } throws TaskNotFoundException(99L)

        mockMvc.get("/api/tasks/99").andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `GET list should return all tasks`() {
        every { listTasksUseCase.list(null) } returns listOf(sampleTask)

        mockMvc.get("/api/tasks").andExpect {
            status { isOk() }
            jsonPath("$.length()") { value(1) }
        }
    }

    @Test
    fun `GET list should filter by status`() {
        every { listTasksUseCase.list(TaskStatus.PENDING) } returns listOf(sampleTask)

        mockMvc.get("/api/tasks?status=PENDING").andExpect {
            status { isOk() }
            jsonPath("$.length()") { value(1) }
        }
    }

    @Test
    fun `PATCH complete should return completed task`() {
        val completed = sampleTask.copy(status = TaskStatus.COMPLETED)
        every { completeTaskUseCase.complete(1L) } returns completed

        mockMvc.patch("/api/tasks/1/complete").andExpect {
            status { isOk() }
            jsonPath("$.status") { value("COMPLETED") }
        }
    }

    @Test
    fun `DELETE should return 204`() {
        every { deleteTaskUseCase.delete(1L) } just Runs

        mockMvc.delete("/api/tasks/1").andExpect {
            status { isNoContent() }
        }
    }
}
