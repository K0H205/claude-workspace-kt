package com.example.cleanarchitecture.application.service

import com.example.cleanarchitecture.application.usecase.DeleteTaskUseCase
import com.example.cleanarchitecture.domain.exception.TaskNotFoundException
import com.example.cleanarchitecture.domain.repository.TaskRepository

class DeleteTaskService(
    private val taskRepository: TaskRepository
) : DeleteTaskUseCase {

    override fun delete(id: Long) {
        if (!taskRepository.existsById(id)) {
            throw TaskNotFoundException(id)
        }
        taskRepository.deleteById(id)
    }
}
