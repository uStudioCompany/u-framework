package io.github.ustudiocompany.uframework.telemetry.logging.api

public inline fun <T> withLogging(logger: Logger, block: Logging.() -> T): T {
    val logging: Logging = object : Logging {
        override val logger: Logger = logger
    }
    return with(logging, block)
}

public interface Logging {
    public val logger: Logger
}
