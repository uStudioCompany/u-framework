package io.github.ustudiocompany.uframework.logging.diagnostic.context

import io.github.ustudiocompany.uframework.failure.Failure

private const val ERROR_CODE_DIAGNOSTIC_CONTEXT_KEY = "error-code"

public inline fun <T, E : Failure> DiagnosticContext.withDiagnosticContext(
    failure: E,
    block: DiagnosticContext.() -> T
): T = block(this + failure)

public inline fun <T, E : Failure> DiagnosticContext.withDiagnosticContext(
    failure: E,
    vararg elements: Pair<String, Any>,
    block: DiagnosticContext.() -> T
): T = withDiagnosticContext(failure, Iterable { elements.iterator() }, block)

public inline fun <T, E : Failure> DiagnosticContext.withDiagnosticContext(
    failure: E,
    elements: Iterable<Pair<String, Any>>,
    block: DiagnosticContext.() -> T
): T {
    val diagnosticContext = elements.fold(this + failure) { acc, element -> acc + element }
    return block(diagnosticContext)
}

public inline fun <T, E : Failure> DiagnosticContext.withDiagnosticContext(
    failure: E,
    vararg entries: DiagnosticContext.Entry,
    block: DiagnosticContext.() -> T
): T {
    val diagnosticContext = entries.fold(this + failure) { acc, element -> acc + element }
    return block(diagnosticContext)
}

public operator fun <E : Failure> DiagnosticContext.plus(error: E): DiagnosticContext =
    error.details
        .fold(this) { acc, detail -> acc + DiagnosticContext.Entry(key = detail.key, value = detail.value) }
        .plus(ERROR_CODE_DIAGNOSTIC_CONTEXT_KEY to error.code())
