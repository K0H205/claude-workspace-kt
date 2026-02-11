package com.example.cleanarchitecture.presentation.dto

import com.example.cleanarchitecture.domain.model.TaskPriority
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateTaskRequest(
    @field:NotBlank(message = "Title is required")
    val title: String,

    @field:Size(max = 1000, message = "Description must be 1000 characters or less")
    val description: String = "",

    val priority: TaskPriority = TaskPriority.MEDIUM
)
