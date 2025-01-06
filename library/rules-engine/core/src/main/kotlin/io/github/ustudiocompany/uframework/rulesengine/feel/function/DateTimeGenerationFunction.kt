package io.github.ustudiocompany.uframework.rulesengine.feel.function

import org.camunda.feel.context.JavaFunction
import org.camunda.feel.syntaxtree.ValString
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

internal class DateTimeGenerationFunction : FeelFunction {
    override val name: String = "dateTime"
    override val body: JavaFunction = JavaFunction(listOf("format")) { args ->
        val pattern: ValString = args[0] as ValString
        ValString(generate(pattern.value()))
    }

    companion object {

        fun generate(format: String): String {
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(format)
                .withResolverStyle(ResolverStyle.STRICT)
            return LocalDateTime.now(ZoneOffset.UTC).format(formatter)
        }
    }
}
