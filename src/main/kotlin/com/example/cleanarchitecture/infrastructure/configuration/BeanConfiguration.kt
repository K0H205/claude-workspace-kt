package com.example.cleanarchitecture.infrastructure.configuration

import com.example.cleanarchitecture.application.service.*
import com.example.cleanarchitecture.domain.repository.TaskRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BeanConfiguration {

    @Bean
    fun createTaskService(taskRepository: TaskRepository): CreateTaskService {
        return CreateTaskService(taskRepository)
    }

    @Bean
    fun getTaskService(taskRepository: TaskRepository): GetTaskService {
        return GetTaskService(taskRepository)
    }

    @Bean
    fun listTasksService(taskRepository: TaskRepository): ListTasksService {
        return ListTasksService(taskRepository)
    }

    @Bean
    fun completeTaskService(taskRepository: TaskRepository): CompleteTaskService {
        return CompleteTaskService(taskRepository)
    }

    @Bean
    fun deleteTaskService(taskRepository: TaskRepository): DeleteTaskService {
        return DeleteTaskService(taskRepository)
    }
}
