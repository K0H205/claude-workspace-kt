package com.example.cleanarchitecture.application.service

import com.example.cleanarchitecture.application.usecase.CompleteTaskUseCase
import com.example.cleanarchitecture.domain.exception.TaskNotFoundException
import com.example.cleanarchitecture.domain.model.Task
import com.example.cleanarchitecture.domain.repository.TaskRepository

class CompleteTaskService(
    private val taskRepository: TaskRepository
) : CompleteTaskUseCase {

    override fun complete(id: Long): Task {
        val task = taskRepository.findById(id) ?: throw TaskNotFoundException(id)
        val completedTask = task.complete()
        return taskRepository.save(completedTask)
    }
}
