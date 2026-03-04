package com.example.cleanarchitecture.application.service

import com.example.cleanarchitecture.application.usecase.ListTasksUseCase
import com.example.cleanarchitecture.domain.model.Task
import com.example.cleanarchitecture.domain.model.TaskStatus
import com.example.cleanarchitecture.domain.repository.TaskRepository

class ListTasksService(
    private val taskRepository: TaskRepository
) : ListTasksUseCase {

    override fun list(status: TaskStatus?): List<Task> {
        return if (status != null) {
            taskRepository.findByStatus(status)
        } else {
            taskRepository.findAll()
        }
    }
}
