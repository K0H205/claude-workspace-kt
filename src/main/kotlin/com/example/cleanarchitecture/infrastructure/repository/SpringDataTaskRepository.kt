package com.example.cleanarchitecture.infrastructure.repository

import com.example.cleanarchitecture.domain.model.TaskStatus
import com.example.cleanarchitecture.infrastructure.entity.TaskJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface SpringDataTaskRepository : JpaRepository<TaskJpaEntity, Long> {
    fun findByStatus(status: TaskStatus): List<TaskJpaEntity>
}
