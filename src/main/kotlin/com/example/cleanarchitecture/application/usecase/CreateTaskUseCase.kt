package com.example.cleanarchitecture.application.usecase

import com.example.cleanarchitecture.domain.model.Task
import com.example.cleanarchitecture.domain.model.TaskPriority

interface CreateTaskUseCase {
    fun create(command: Command): Task

    data class Command(
        val title: String,
        val description: String,
        val priority: TaskPriority
    )
}
