package com.example.cleanarchitecture.presentation.grpc

import com.example.cleanarchitecture.application.usecase.*
import com.example.cleanarchitecture.domain.exception.InvalidTaskStateException
import com.example.cleanarchitecture.domain.exception.TaskNotFoundException
import com.example.cleanarchitecture.domain.model.Task
import com.example.cleanarchitecture.domain.model.TaskPriority
import com.example.cleanarchitecture.domain.model.TaskStatus
import com.example.cleanarchitecture.grpc.generated.*
import com.example.cleanarchitecture.grpc.generated.TaskServiceGrpcKt.TaskServiceCoroutineImplBase
import com.google.protobuf.Empty
import com.google.protobuf.Timestamp
import io.grpc.Status
import io.grpc.StatusException
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneOffset

@Component
class TaskGrpcService(
    private val createTaskUseCase: CreateTaskUseCase,
    private val getTaskUseCase: GetTaskUseCase,
    private val listTasksUseCase: ListTasksUseCase,
    private val completeTaskUseCase: CompleteTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
) : TaskServiceCoroutineImplBase() {

    override suspend fun createTask(request: CreateTaskRequest): TaskResponse {
        return handleExceptions {
            val command = CreateTaskUseCase.Command(
                title = request.title,
                description = request.description,
                priority = request.priority.toDomain()
            )
            createTaskUseCase.create(command).toGrpcResponse()
        }
    }

    override suspend fun getTask(request: GetTaskRequest): TaskResponse {
        return handleExceptions {
            getTaskUseCase.getById(request.id).toGrpcResponse()
        }
    }

    override suspend fun listTasks(request: ListTasksRequest): ListTasksResponse {
        return handleExceptions {
            val status = if (request.hasStatus()) request.status.toDomain() else null
            val tasks = listTasksUseCase.list(status)
            ListTasksResponse.newBuilder()
                .addAllTasks(tasks.map { it.toGrpcResponse() })
                .build()
        }
    }

    override suspend fun completeTask(request: CompleteTaskRequest): TaskResponse {
        return handleExceptions {
            completeTaskUseCase.complete(request.id).toGrpcResponse()
        }
    }

    override suspend fun deleteTask(request: DeleteTaskRequest): Empty {
        return handleExceptions {
            deleteTaskUseCase.delete(request.id)
            Empty.getDefaultInstance()
        }
    }

    private inline fun <T> handleExceptions(block: () -> T): T {
        return try {
            block()
        } catch (e: TaskNotFoundException) {
            throw StatusException(Status.NOT_FOUND.withDescription(e.message))
        } catch (e: InvalidTaskStateException) {
            throw StatusException(Status.FAILED_PRECONDITION.withDescription(e.message))
        } catch (e: IllegalArgumentException) {
            throw StatusException(Status.INVALID_ARGUMENT.withDescription(e.message))
        }
    }
}

// --- マッピング関数 ---

private fun Priority.toDomain(): TaskPriority = when (this) {
    Priority.PRIORITY_LOW -> TaskPriority.LOW
    Priority.PRIORITY_MEDIUM -> TaskPriority.MEDIUM
    Priority.PRIORITY_HIGH -> TaskPriority.HIGH
    Priority.PRIORITY_UNSPECIFIED, Priority.UNRECOGNIZED -> TaskPriority.MEDIUM
}

private fun com.example.cleanarchitecture.grpc.generated.Status.toDomain(): TaskStatus = when (this) {
    com.example.cleanarchitecture.grpc.generated.Status.STATUS_PENDING -> TaskStatus.PENDING
    com.example.cleanarchitecture.grpc.generated.Status.STATUS_IN_PROGRESS -> TaskStatus.IN_PROGRESS
    com.example.cleanarchitecture.grpc.generated.Status.STATUS_COMPLETED -> TaskStatus.COMPLETED
    com.example.cleanarchitecture.grpc.generated.Status.STATUS_UNSPECIFIED,
    com.example.cleanarchitecture.grpc.generated.Status.UNRECOGNIZED ->
        throw StatusException(io.grpc.Status.INVALID_ARGUMENT.withDescription("Invalid status"))
}

private fun TaskStatus.toGrpc(): com.example.cleanarchitecture.grpc.generated.Status = when (this) {
    TaskStatus.PENDING -> com.example.cleanarchitecture.grpc.generated.Status.STATUS_PENDING
    TaskStatus.IN_PROGRESS -> com.example.cleanarchitecture.grpc.generated.Status.STATUS_IN_PROGRESS
    TaskStatus.COMPLETED -> com.example.cleanarchitecture.grpc.generated.Status.STATUS_COMPLETED
}

private fun TaskPriority.toGrpc(): Priority = when (this) {
    TaskPriority.LOW -> Priority.PRIORITY_LOW
    TaskPriority.MEDIUM -> Priority.PRIORITY_MEDIUM
    TaskPriority.HIGH -> Priority.PRIORITY_HIGH
}

private fun LocalDateTime.toTimestamp(): Timestamp {
    val instant = this.toInstant(ZoneOffset.UTC)
    return Timestamp.newBuilder()
        .setSeconds(instant.epochSecond)
        .setNanos(instant.nano)
        .build()
}

private fun Task.toGrpcResponse(): TaskResponse =
    TaskResponse.newBuilder()
        .setId(id)
        .setTitle(title)
        .setDescription(description)
        .setStatus(status.toGrpc())
        .setPriority(priority.toGrpc())
        .setCreatedAt(createdAt.toTimestamp())
        .setUpdatedAt(updatedAt.toTimestamp())
        .build()
