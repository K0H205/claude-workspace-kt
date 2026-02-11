package com.example.cleanarchitecture.presentation.dto

import com.example.cleanarchitecture.domain.model.Task
import com.example.cleanarchitecture.domain.model.TaskPriority
import com.example.cleanarchitecture.domain.model.TaskStatus
import java.time.LocalDateTime

data class TaskResponse(
    val id: Long,
    val title: String,
    val description: String,
    val status: TaskStatus,
    val priority: TaskPriority,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(task: Task): TaskResponse = TaskResponse(
            id = task.id,
            title = task.title,
            description = task.description,
            status = task.status,
            priority = task.priority,
            createdAt = task.createdAt,
            updatedAt = task.updatedAt
        )
    }
}
