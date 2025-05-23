package io.github.ustudiocompany.uframework.rulesengine.feel.functions

import io.github.airflux.commons.types.resultk.getOrForward
import io.github.ustudiocompany.uframework.rulesengine.feel.FeelFunction
import io.github.ustudiocompany.uframework.rulesengine.feel.functions.common.asType
import org.camunda.feel.context.JavaFunction
import org.camunda.feel.syntaxtree.Val
import org.camunda.feel.syntaxtree.ValFatalError
import org.camunda.feel.syntaxtree.ValString
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

/**
 * The function generates the current date and time in the UTC time zone.
 * @param format The parameter specifies the format of the date and time.
 * @return Returns the date and time in the specified format.
 * If the parameter is not specified, the function returns the date and time in the format "uuuu-MM-dd'T'HH:mm:ss'Z'".
 */
public class DateTimeGenerationFunction(private val default: DateTimeFormatter) : FeelFunction {
    override val name: String = "dateTime"
    override val body: JavaFunction = JavaFunction(parameters) { args ->
        val pattern = args[0].asType(PARAM_FORMAT, ValString::class)
            .getOrForward { return@JavaFunction it.cause }
        if (pattern.value().isBlank())
            generate(default)
        else
            generate(pattern.value())
    }

    private fun generate(format: String): Val =
        try {
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(format)
                .withResolverStyle(ResolverStyle.STRICT)
            generate(formatter)
        } catch (_: Exception) {
            ValFatalError("The format is invalid: '$format'.")
        }

    private fun generate(formatter: DateTimeFormatter): Val =
        ValString(LocalDateTime.now(ZoneOffset.UTC).format(formatter))

    private companion object {
        private const val PARAM_FORMAT = "format"
        private val parameters: List<String> = listOf(PARAM_FORMAT)
    }
}
