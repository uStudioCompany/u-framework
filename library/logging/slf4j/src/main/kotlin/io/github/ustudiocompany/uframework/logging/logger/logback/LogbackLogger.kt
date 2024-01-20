package io.github.ustudiocompany.uframework.logging.logger.logback

import io.github.ustudiocompany.uframework.logging.api.Logger
import io.github.ustudiocompany.uframework.logging.diagnostic.context.DiagnosticContext
import org.slf4j.LoggerFactory

public class LogbackLogger(name: String, private val formatter: Logger.MessageFormatter) : Logger {

    private val logger: org.slf4j.Logger = LoggerFactory.getLogger(name)

    override fun isEnabled(level: Logger.Level): Boolean = when (level) {
        Logger.Level.TRACE -> logger.isTraceEnabled
        Logger.Level.DEBUG -> logger.isDebugEnabled
        Logger.Level.INFO -> logger.isInfoEnabled
        Logger.Level.WARN -> logger.isWarnEnabled
        Logger.Level.ERROR -> logger.isErrorEnabled
    }

    override fun trace(message: String, diagnosticContext: DiagnosticContext) {
        if (isEnabled(Logger.Level.TRACE)) logger.trace(formatter.format(message, diagnosticContext, null))
    }

    override fun debug(message: String, diagnosticContext: DiagnosticContext) {
        if (isEnabled(Logger.Level.DEBUG)) logger.debug(formatter.format(message, diagnosticContext, null))
    }

    override fun info(message: String, diagnosticContext: DiagnosticContext) {
        if (isEnabled(Logger.Level.INFO)) logger.info(formatter.format(message, diagnosticContext, null))
    }

    override fun warn(message: String, diagnosticContext: DiagnosticContext) {
        if (isEnabled(Logger.Level.WARN)) logger.warn(formatter.format(message, diagnosticContext, null))
    }

    override fun error(message: String, diagnosticContext: DiagnosticContext, exception: Throwable?) {
        if (isEnabled(Logger.Level.ERROR))
            logger.error(formatter.format(message, diagnosticContext, exception), exception)
    }
}
