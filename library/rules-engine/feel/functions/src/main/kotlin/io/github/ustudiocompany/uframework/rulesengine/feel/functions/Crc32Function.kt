package io.github.ustudiocompany.uframework.rulesengine.feel.functions

import io.github.airflux.commons.types.resultk.getOrForward
import io.github.ustudiocompany.uframework.rulesengine.feel.FeelFunction
import io.github.ustudiocompany.uframework.rulesengine.feel.functions.common.asType
import org.camunda.feel.context.JavaFunction
import org.camunda.feel.syntaxtree.ValString
import java.util.zip.CRC32

public class Crc32Function : FeelFunction {
    override val name: String = "crc32"
    override val body: JavaFunction = JavaFunction(parameters) { args ->
        val value = args[0].asType(PARAM_VALUE, ValString::class)
            .getOrForward { return@JavaFunction it.cause }
        val data = value.value().toByteArray(Charsets.UTF_8)
        calculate(data)
    }

    private companion object {
        private const val PARAM_VALUE = "value"
        private val parameters: List<String> = listOf(PARAM_VALUE)

        private fun calculate(value: ByteArray): ValString {
            val crc32 = CRC32()
            crc32.update(value)
            val result = crc32.value.toString()
            return ValString(result)
        }
    }
}
