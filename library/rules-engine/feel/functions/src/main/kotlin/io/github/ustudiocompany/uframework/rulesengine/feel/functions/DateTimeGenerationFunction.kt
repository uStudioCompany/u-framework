package io.github.ustudiocompany.uframework.rulesengine.feel.functions

import io.github.ustudiocompany.uframework.rulesengine.feel.function.FeelFunction
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
public class DateTimeGenerationFunction : FeelFunction {
    override val name: String = "dateTime"
    override val body: JavaFunction = JavaFunction(listOf<String>("format")) { args ->
        val pattern: ValString = args[0] as ValString
        if (pattern.value().isBlank())
            generate(DEFAULT_FORMATTER)
        else
            generate(pattern.value())
    }

    public companion object {
        public const val DEFAULT_FORMAT: String = "uuuu-MM-dd'T'HH:mm:ss'Z'"
        private val DEFAULT_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(DEFAULT_FORMAT)
            .withResolverStyle(ResolverStyle.STRICT)

        private fun generate(format: String): Val =
            try {
                val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(format)
                    .withResolverStyle(ResolverStyle.STRICT)
                generate(formatter)
            } catch (_: Exception) {
                ValFatalError("The format is invalid: `$format`.")
            }

        private fun generate(formatter: DateTimeFormatter): Val =
            ValString(LocalDateTime.now(ZoneOffset.UTC).format(formatter))
    }
}
