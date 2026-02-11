package com.example.cleanarchitecture.application.usecase

import com.example.cleanarchitecture.domain.model.Task

interface CompleteTaskUseCase {
    fun complete(id: Long): Task
}
