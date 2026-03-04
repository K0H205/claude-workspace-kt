package com.example.cleanarchitecture.infrastructure.configuration

import com.example.cleanarchitecture.presentation.grpc.TaskGrpcService
import io.grpc.Server
import io.grpc.ServerBuilder
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener

@Configuration
class GrpcServerConfiguration(
    private val taskGrpcService: TaskGrpcService
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private var server: Server? = null

    @Value("\${grpc.server.port:9090}")
    private var port: Int = 9090

    @EventListener(ApplicationReadyEvent::class)
    fun startGrpcServer() {
        server = ServerBuilder.forPort(port)
            .addService(taskGrpcService)
            .build()
            .start()
        logger.info("gRPC server started on port $port")
    }

    @PreDestroy
    fun stopGrpcServer() {
        server?.shutdown()
        logger.info("gRPC server stopped")
    }
}
