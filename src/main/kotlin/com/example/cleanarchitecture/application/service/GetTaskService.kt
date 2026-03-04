package com.example.cleanarchitecture.application.service

import com.example.cleanarchitecture.application.usecase.GetTaskUseCase
import com.example.cleanarchitecture.domain.exception.TaskNotFoundException
import com.example.cleanarchitecture.domain.model.Task
import com.example.cleanarchitecture.domain.repository.TaskRepository

class GetTaskService(
    private val taskRepository: TaskRepository
) : GetTaskUseCase {

    override fun getById(id: Long): Task {
        return taskRepository.findById(id) ?: throw TaskNotFoundException(id)
    }
}
