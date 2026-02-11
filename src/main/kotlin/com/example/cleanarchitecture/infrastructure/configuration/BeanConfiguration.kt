package com.example.cleanarchitecture.infrastructure.configuration

import com.example.cleanarchitecture.application.service.TaskService
import com.example.cleanarchitecture.domain.repository.TaskRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BeanConfiguration {

    @Bean
    fun taskService(taskRepository: TaskRepository): TaskService {
        return TaskService(taskRepository)
    }
}
