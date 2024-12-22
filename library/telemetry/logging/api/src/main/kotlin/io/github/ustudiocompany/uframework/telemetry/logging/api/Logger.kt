package io.github.ustudiocompany.uframework.telemetry.logging.api

import io.github.ustudiocompany.uframework.telemetry.logging.diagnostic.context.DiagnosticContext
import io.github.ustudiocompany.uframework.telemetry.logging.diagnostic.context.DiagnosticContext.Empty

public interface Logger {
    public fun trace(message: String, diagnosticContext: DiagnosticContext = Empty)
    public fun debug(message: String, diagnosticContext: DiagnosticContext = Empty)
    public fun info(message: String, diagnosticContext: DiagnosticContext = Empty)
    public fun warn(message: String, diagnosticContext: DiagnosticContext = Empty)
    public fun error(message: String, diagnosticContext: DiagnosticContext = Empty, exception: Throwable? = null)

    public fun isEnabled(level: Level): Boolean

    public enum class Level {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR
    }

    public fun interface MessageFormatter {
        public fun format(message: String, diagnosticContext: DiagnosticContext, exception: Throwable?): String
    }
}

context (DiagnosticContext)
public inline fun Logger.trace(block: () -> String): Unit =
    trace(diagnosticContext = this@DiagnosticContext, block = block)

public inline fun Logger.trace(diagnosticContext: DiagnosticContext = Empty, block: () -> String) {
    if (isEnabled(Logger.Level.TRACE))
        trace(message = block(), diagnosticContext = diagnosticContext)
}

context (DiagnosticContext)
public inline fun Logger.debug(block: () -> String): Unit =
    debug(diagnosticContext = this@DiagnosticContext, block = block)

public inline fun Logger.debug(diagnosticContext: DiagnosticContext = Empty, block: () -> String) {
    if (isEnabled(Logger.Level.DEBUG))
        debug(message = block(), diagnosticContext = diagnosticContext)
}

context (DiagnosticContext)
public inline fun Logger.info(block: () -> String): Unit =
    info(diagnosticContext = this@DiagnosticContext, block = block)

public inline fun Logger.info(diagnosticContext: DiagnosticContext = Empty, block: () -> String) {
    if (isEnabled(Logger.Level.INFO))
        info(message = block(), diagnosticContext = diagnosticContext)
}

context (DiagnosticContext)
public inline fun Logger.warn(block: () -> String): Unit =
    warn(diagnosticContext = this@DiagnosticContext, block = block)

public inline fun Logger.warn(diagnosticContext: DiagnosticContext = Empty, block: () -> String) {
    if (isEnabled(Logger.Level.WARN)) warn(message = block(), diagnosticContext = diagnosticContext)
}

context (DiagnosticContext)
public inline fun Logger.error(exception: Throwable? = null, block: () -> String): Unit =
    error(diagnosticContext = this@DiagnosticContext, exception = exception, block)

public inline fun Logger.error(
    diagnosticContext: DiagnosticContext = Empty,
    exception: Throwable? = null,
    block: () -> String
) {
    if (isEnabled(Logger.Level.ERROR))
        error(message = block(), diagnosticContext = diagnosticContext, exception = exception)
}
