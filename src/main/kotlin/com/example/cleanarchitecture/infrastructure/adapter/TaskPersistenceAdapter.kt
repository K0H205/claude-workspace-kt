package com.example.cleanarchitecture.infrastructure.adapter

import com.example.cleanarchitecture.domain.model.Task
import com.example.cleanarchitecture.domain.model.TaskStatus
import com.example.cleanarchitecture.domain.repository.TaskRepository
import com.example.cleanarchitecture.infrastructure.entity.TaskJpaEntity
import com.example.cleanarchitecture.infrastructure.repository.SpringDataTaskRepository
import org.springframework.stereotype.Repository

@Repository
class TaskPersistenceAdapter(
    private val springDataTaskRepository: SpringDataTaskRepository
) : TaskRepository {

    override fun save(task: Task): Task {
        val entity = TaskJpaEntity.fromDomain(task)
        val saved = springDataTaskRepository.save(entity)
        return saved.toDomain()
    }

    override fun findById(id: Long): Task? {
        return springDataTaskRepository.findById(id)
            .map { it.toDomain() }
            .orElse(null)
    }

    override fun findAll(): List<Task> {
        return springDataTaskRepository.findAll().map { it.toDomain() }
    }

    override fun findByStatus(status: TaskStatus): List<Task> {
        return springDataTaskRepository.findByStatus(status).map { it.toDomain() }
    }

    override fun deleteById(id: Long) {
        springDataTaskRepository.deleteById(id)
    }

    override fun existsById(id: Long): Boolean {
        return springDataTaskRepository.existsById(id)
    }
}
