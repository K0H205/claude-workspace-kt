package com.example.cleanarchitecture.domain.model

import com.example.cleanarchitecture.domain.exception.InvalidTaskStateException
import java.time.LocalDateTime

data class Task(
    val id: Long = 0,
    val title: String,
    val description: String,
    val status: TaskStatus,
    val priority: TaskPriority,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun create(title: String, description: String, priority: TaskPriority): Task {
            require(title.isNotBlank()) { "Title must not be blank" }
            require(description.length <= 1000) { "Description must be 1000 characters or less" }

            val now = LocalDateTime.now()
            return Task(
                title = title.trim(),
                description = description.trim(),
                status = TaskStatus.PENDING,
                priority = priority,
                createdAt = now,
                updatedAt = now
            )
        }
    }

    fun start(): Task {
        if (status != TaskStatus.PENDING) {
            throw InvalidTaskStateException(
                "Cannot start task: current status is $status, expected PENDING"
            )
        }
        return copy(status = TaskStatus.IN_PROGRESS, updatedAt = LocalDateTime.now())
    }

    fun complete(): Task {
        if (status == TaskStatus.COMPLETED) {
            throw InvalidTaskStateException("Task is already completed")
        }
        return copy(status = TaskStatus.COMPLETED, updatedAt = LocalDateTime.now())
    }
}
