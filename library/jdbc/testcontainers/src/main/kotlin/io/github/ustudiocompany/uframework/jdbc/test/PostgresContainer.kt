package io.github.ustudiocompany.uframework.jdbc.test

import io.kotest.extensions.testcontainers.ContainerLifecycleMode
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerExtension
import org.testcontainers.containers.PostgreSQLContainer

public fun postgresContainer(dockerImageName: String = "postgres:17.2"): JdbcDatabaseContainerExtension {
    val container = PostgreSQLContainer<Nothing>(dockerImageName)
        .apply {
            startupAttempts = 1
        }
    return JdbcDatabaseContainerExtension(container = container, mode = ContainerLifecycleMode.Spec)
}
