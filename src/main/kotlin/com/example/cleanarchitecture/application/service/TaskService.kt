package com.example.cleanarchitecture.application.service

import com.example.cleanarchitecture.application.usecase.*
import com.example.cleanarchitecture.domain.exception.TaskNotFoundException
import com.example.cleanarchitecture.domain.model.Task
import com.example.cleanarchitecture.domain.model.TaskStatus
import com.example.cleanarchitecture.domain.repository.TaskRepository

class TaskService(
    private val taskRepository: TaskRepository
) : CreateTaskUseCase, GetTaskUseCase, ListTasksUseCase, CompleteTaskUseCase, DeleteTaskUseCase {

    override fun create(command: CreateTaskUseCase.Command): Task {
        val task = Task.create(
            title = command.title,
            description = command.description,
            priority = command.priority
        )
        return taskRepository.save(task)
    }

    override fun getById(id: Long): Task {
        return taskRepository.findById(id) ?: throw TaskNotFoundException(id)
    }

    override fun list(status: TaskStatus?): List<Task> {
        return if (status != null) {
            taskRepository.findByStatus(status)
        } else {
            taskRepository.findAll()
        }
    }

    override fun complete(id: Long): Task {
        val task = taskRepository.findById(id) ?: throw TaskNotFoundException(id)
        val completedTask = task.complete()
        return taskRepository.save(completedTask)
    }

    override fun delete(id: Long) {
        if (!taskRepository.existsById(id)) {
            throw TaskNotFoundException(id)
        }
        taskRepository.deleteById(id)
    }
}
