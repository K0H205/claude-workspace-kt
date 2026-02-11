package com.example.cleanarchitecture.presentation.rest

import com.example.cleanarchitecture.application.usecase.*
import com.example.cleanarchitecture.domain.model.TaskStatus
import com.example.cleanarchitecture.presentation.dto.CreateTaskRequest
import com.example.cleanarchitecture.presentation.dto.TaskResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/tasks")
class TaskController(
    private val createTaskUseCase: CreateTaskUseCase,
    private val getTaskUseCase: GetTaskUseCase,
    private val listTasksUseCase: ListTasksUseCase,
    private val completeTaskUseCase: CompleteTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
) {
    @PostMapping
    fun create(@Valid @RequestBody request: CreateTaskRequest): ResponseEntity<TaskResponse> {
        val command = CreateTaskUseCase.Command(
            title = request.title,
            description = request.description,
            priority = request.priority
        )
        val task = createTaskUseCase.create(command)
        return ResponseEntity.status(HttpStatus.CREATED).body(TaskResponse.from(task))
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<TaskResponse> {
        val task = getTaskUseCase.getById(id)
        return ResponseEntity.ok(TaskResponse.from(task))
    }

    @GetMapping
    fun list(@RequestParam(required = false) status: TaskStatus?): ResponseEntity<List<TaskResponse>> {
        val tasks = listTasksUseCase.list(status)
        return ResponseEntity.ok(tasks.map { TaskResponse.from(it) })
    }

    @PatchMapping("/{id}/complete")
    fun complete(@PathVariable id: Long): ResponseEntity<TaskResponse> {
        val task = completeTaskUseCase.complete(id)
        return ResponseEntity.ok(TaskResponse.from(task))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        deleteTaskUseCase.delete(id)
        return ResponseEntity.noContent().build()
    }
}
