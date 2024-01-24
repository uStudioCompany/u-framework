package io.github.ustudiocompany.uframework.telemetry.logging.logger.formatter.json

import io.github.ustudiocompany.uframework.telemetry.logging.api.Logger
import io.github.ustudiocompany.uframework.telemetry.logging.diagnostic.context.DiagnosticContext

public object JsonFormatter : Logger.MessageFormatter {
    private const val SEPARATOR = ", "

    override fun format(message: String, diagnosticContext: DiagnosticContext, exception: Throwable?): String =
        buildString {
            append("""{"message": """")
            append(message)
            append("\"")
            appendDetails(diagnosticContext)
            appendException(exception)
            append("}")
        }

    private fun StringBuilder.appendDetails(diagnosticContext: DiagnosticContext) {
        if (diagnosticContext.isNotEmpty) {
            append(SEPARATOR)
            append(""""details": {""")
            appendDiagnosticContext(diagnosticContext)
            append("}")
        }
    }

    private fun StringBuilder.appendDiagnosticContext(diagnosticContext: DiagnosticContext) {
        var addSeparator = false
        diagnosticContext.forEach { entry ->
            if (addSeparator) append(SEPARATOR) else addSeparator = true
            appendAttribute(entry)
        }
    }

    private fun StringBuilder.appendAttribute(entry: DiagnosticContext.Entry) {
        appendKey(entry.key)
        appendValue(entry.value)
    }

    private fun StringBuilder.appendKey(key: String) {
        append("\"")
        append(key)
        append("\": ")
    }

    private fun StringBuilder.appendValue(value: Any) {
        if (value is Iterable<*>)
            appendItems(value)
        else {
            append("\"")
            append(value)
            append("\"")
        }
    }

    private fun StringBuilder.appendItems(value: Iterable<*>) {
        append("[")
        var addSeparator = false
        value.forEach { item ->
            if (addSeparator) append(SEPARATOR) else addSeparator = true
            append("\"")
            append(item)
            append("\"")
        }
        append("]")
    }

    private fun StringBuilder.appendException(exception: Throwable?) {
        if (exception != null) {
            append(SEPARATOR)
            append(""""exception": {""")
            append(""""message": """")
            append(exception.message)
            append("\"")
            append("}")
        }
    }
}
