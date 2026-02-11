package com.example.cleanarchitecture.domain.repository

import com.example.cleanarchitecture.domain.model.Task
import com.example.cleanarchitecture.domain.model.TaskStatus

interface TaskRepository {
    fun save(task: Task): Task
    fun findById(id: Long): Task?
    fun findAll(): List<Task>
    fun findByStatus(status: TaskStatus): List<Task>
    fun deleteById(id: Long)
    fun existsById(id: Long): Boolean
}
