package com.example.cleanarchitecture.infrastructure.entity

import com.example.cleanarchitecture.domain.model.Task
import com.example.cleanarchitecture.domain.model.TaskPriority
import com.example.cleanarchitecture.domain.model.TaskStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "tasks")
class TaskJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val title: String = "",

    @Column(nullable = false, length = 1000)
    val description: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: TaskStatus = TaskStatus.PENDING,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val priority: TaskPriority = TaskPriority.MEDIUM,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun toDomain(): Task = Task(
        id = id,
        title = title,
        description = description,
        status = status,
        priority = priority,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    companion object {
        fun fromDomain(task: Task): TaskJpaEntity = TaskJpaEntity(
            id = task.id,
            title = task.title,
            description = task.description,
            status = task.status,
            priority = task.priority,
            createdAt = task.createdAt,
            updatedAt = task.updatedAt
        )
    }
}
