package io.github.ustudiocompany.uframework.rulesengine.feel.functions

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.fold
import io.github.airflux.commons.types.resultk.getOrForward
import io.github.ustudiocompany.uframework.rulesengine.feel.FeelFunction
import io.github.ustudiocompany.uframework.rulesengine.feel.functions.common.asType
import org.camunda.feel.context.JavaFunction
import org.camunda.feel.syntaxtree.Val
import org.camunda.feel.syntaxtree.ValFatalError
import org.camunda.feel.syntaxtree.ValString
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

public class YearFunction(private val default: DateTimeFormatter) : FeelFunction {
    override val name: String = "year"
    override val body: JavaFunction = JavaFunction(parameters) { args ->
        val value = args[0].asType(PARAM_VALUE, ValString::class)
            .getOrForward { return@JavaFunction it.cause }
        val pattern = args[1].asType(PARAM_FORMAT, ValString::class)
            .getOrForward { return@JavaFunction it.cause }
        if (pattern.value().isBlank())
            year(value.value(), default)
        else
            year(value.value(), pattern.value())
    }

    private fun year(value: String, format: String): Val = try {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(format)
            .withResolverStyle(ResolverStyle.STRICT)
        year(value, formatter)
    } catch (_: Exception) {
        ValFatalError("The format is invalid: '$format'.")
    }

    private fun year(value: String, formatter: DateTimeFormatter): Val =
        value.toLocalDateTime(formatter)
            .fold(
                onSuccess = { dateTime -> ValString(dateTime.year.toString()) },
                onFailure = { it }
            )

    private fun String.toLocalDateTime(formatter: DateTimeFormatter): ResultK<LocalDateTime, ValFatalError> = try {
        LocalDateTime.parse(this, formatter).asSuccess()
    } catch (_: Exception) {
        ValFatalError("Invalid value '$this' of parameter for extracting year.").asFailure()
    }

    private companion object {
        private const val PARAM_VALUE = "value"
        private const val PARAM_FORMAT = "format"
        private val parameters: List<String> = listOf(PARAM_VALUE, PARAM_FORMAT)
    }
}
