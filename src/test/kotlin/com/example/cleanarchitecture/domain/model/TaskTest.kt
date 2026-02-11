package com.example.cleanarchitecture.domain.model

import com.example.cleanarchitecture.domain.exception.InvalidTaskStateException
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TaskTest {

    @Nested
    inner class Create {

        @Test
        fun `should create task with valid parameters`() {
            val task = Task.create("Buy groceries", "Milk, eggs, bread", TaskPriority.MEDIUM)

            assertEquals("Buy groceries", task.title)
            assertEquals("Milk, eggs, bread", task.description)
            assertEquals(TaskStatus.PENDING, task.status)
            assertEquals(TaskPriority.MEDIUM, task.priority)
            assertNotNull(task.createdAt)
            assertNotNull(task.updatedAt)
        }

        @Test
        fun `should trim title and description`() {
            val task = Task.create("  Buy groceries  ", "  Milk  ", TaskPriority.LOW)

            assertEquals("Buy groceries", task.title)
            assertEquals("Milk", task.description)
        }

        @Test
        fun `should throw when title is blank`() {
            assertThrows<IllegalArgumentException> {
                Task.create("", "description", TaskPriority.HIGH)
            }
        }

        @Test
        fun `should throw when title is only whitespace`() {
            assertThrows<IllegalArgumentException> {
                Task.create("   ", "description", TaskPriority.HIGH)
            }
        }

        @Test
        fun `should throw when description exceeds 1000 characters`() {
            val longDescription = "a".repeat(1001)
            assertThrows<IllegalArgumentException> {
                Task.create("Title", longDescription, TaskPriority.LOW)
            }
        }

        @Test
        fun `should allow description of exactly 1000 characters`() {
            val description = "a".repeat(1000)
            val task = Task.create("Title", description, TaskPriority.LOW)
            assertEquals(1000, task.description.length)
        }
    }

    @Nested
    inner class Start {

        @Test
        fun `should start a pending task`() {
            val task = Task.create("Task", "Desc", TaskPriority.MEDIUM)

            val started = task.start()

            assertEquals(TaskStatus.IN_PROGRESS, started.status)
        }

        @Test
        fun `should throw when starting an in-progress task`() {
            val task = Task.create("Task", "Desc", TaskPriority.MEDIUM).start()

            assertThrows<InvalidTaskStateException> {
                task.start()
            }
        }

        @Test
        fun `should throw when starting a completed task`() {
            val task = Task.create("Task", "Desc", TaskPriority.MEDIUM).complete()

            assertThrows<InvalidTaskStateException> {
                task.start()
            }
        }
    }

    @Nested
    inner class Complete {

        @Test
        fun `should complete a pending task`() {
            val task = Task.create("Task", "Desc", TaskPriority.MEDIUM)

            val completed = task.complete()

            assertEquals(TaskStatus.COMPLETED, completed.status)
        }

        @Test
        fun `should complete an in-progress task`() {
            val task = Task.create("Task", "Desc", TaskPriority.MEDIUM).start()

            val completed = task.complete()

            assertEquals(TaskStatus.COMPLETED, completed.status)
        }

        @Test
        fun `should throw when completing an already completed task`() {
            val task = Task.create("Task", "Desc", TaskPriority.MEDIUM).complete()

            assertThrows<InvalidTaskStateException> {
                task.complete()
            }
        }
    }
}
