package com.example.cleanarchitecture.application.usecase

import com.example.cleanarchitecture.domain.model.Task

interface GetTaskUseCase {
    fun getById(id: Long): Task
}
