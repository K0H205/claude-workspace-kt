package com.example.cleanarchitecture.integration

import com.example.cleanarchitecture.domain.model.TaskPriority
import com.example.cleanarchitecture.presentation.dto.CreateTaskRequest
import com.example.cleanarchitecture.presentation.dto.TaskResponse
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class TaskIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    @Order(1)
    fun `should create a task`() {
        val request = CreateTaskRequest(
            title = "Integration Test Task",
            description = "End to end test",
            priority = TaskPriority.HIGH
        )

        mockMvc.post("/api/tasks") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isCreated() }
            jsonPath("$.title") { value("Integration Test Task") }
            jsonPath("$.status") { value("PENDING") }
            jsonPath("$.priority") { value("HIGH") }
        }
    }

    @Test
    @Order(2)
    fun `should get a task by id`() {
        val createResponse = createTask("Get Test")

        mockMvc.get("/api/tasks/${createResponse.id}").andExpect {
            status { isOk() }
            jsonPath("$.title") { value("Get Test") }
        }
    }

    @Test
    @Order(3)
    fun `should list all tasks`() {
        createTask("List Test 1")
        createTask("List Test 2")

        mockMvc.get("/api/tasks").andExpect {
            status { isOk() }
            jsonPath("$.length()") { exists() }
        }
    }

    @Test
    @Order(4)
    fun `should complete a task`() {
        val created = createTask("Complete Test")

        mockMvc.patch("/api/tasks/${created.id}/complete").andExpect {
            status { isOk() }
            jsonPath("$.status") { value("COMPLETED") }
        }
    }

    @Test
    @Order(5)
    fun `should delete a task`() {
        val created = createTask("Delete Test")

        mockMvc.delete("/api/tasks/${created.id}").andExpect {
            status { isNoContent() }
        }

        mockMvc.get("/api/tasks/${created.id}").andExpect {
            status { isNotFound() }
        }
    }

    @Test
    @Order(6)
    fun `should return 404 for non-existent task`() {
        mockMvc.get("/api/tasks/99999").andExpect {
            status { isNotFound() }
        }
    }

    @Test
    @Order(7)
    fun `should return 409 when completing already completed task`() {
        val created = createTask("Double Complete Test")

        mockMvc.patch("/api/tasks/${created.id}/complete").andExpect {
            status { isOk() }
        }

        mockMvc.patch("/api/tasks/${created.id}/complete").andExpect {
            status { isConflict() }
        }
    }

    private fun createTask(title: String): TaskResponse {
        val request = CreateTaskRequest(title = title, description = "desc", priority = TaskPriority.MEDIUM)
        val result = mockMvc.post("/api/tasks") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andReturn()

        return objectMapper.readValue(result.response.contentAsString, TaskResponse::class.java)
    }
}
