package com.example.cleanarchitecture.application.usecase

import com.example.cleanarchitecture.domain.model.Task
import com.example.cleanarchitecture.domain.model.TaskStatus

interface ListTasksUseCase {
    fun list(status: TaskStatus? = null): List<Task>
}
