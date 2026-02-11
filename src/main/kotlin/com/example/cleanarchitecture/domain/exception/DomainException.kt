package com.example.cleanarchitecture.domain.exception

sealed class DomainException(message: String) : RuntimeException(message)

class TaskNotFoundException(id: Long) : DomainException("Task not found: id=$id")

class InvalidTaskStateException(message: String) : DomainException(message)
