package com.example.cleanarchitecture.application.service

import com.example.cleanarchitecture.application.usecase.CreateTaskUseCase
import com.example.cleanarchitecture.domain.model.Task
import com.example.cleanarchitecture.domain.repository.TaskRepository

class CreateTaskService(
    private val taskRepository: TaskRepository
) : CreateTaskUseCase {

    override fun create(command: CreateTaskUseCase.Command): Task {
        val task = Task.create(
            title = command.title,
            description = command.description,
            priority = command.priority
        )
        return taskRepository.save(task)
    }
}
