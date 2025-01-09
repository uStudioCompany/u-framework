package io.github.ustudiocompany.uframework.rulesengine.core.rule.operation

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.orThrow
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.operation.calculate
import io.github.ustudiocompany.uframework.rulesengine.core.path.Path
import io.github.ustudiocompany.uframework.rulesengine.core.path.defaultPathCompiler
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.operator.BooleanOperators.EQ
import io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.operator.Operator
import io.github.ustudiocompany.uframework.rulesengine.executor.context.Context
import io.github.ustudiocompany.uframework.rulesengine.executor.error.ContextError
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

internal class OperationCalculatorTest : UnitTest() {

    init {

        "The extension function `calculate` for the `Operation` type" - {

            "when computing the operation is successful" - {
                val operation = TestOperation(
                    target = Value.Literal(fact = DataElement.Text(VALUE_1)),
                    value = Value.Literal(fact = DataElement.Text(VALUE_1)),
                    operator = EQ
                )

                "then the function should return the result" {
                    val result = operation.calculate(CONTEXT)
                    result shouldBeSuccess true
                }
            }

            "when computing the operation returns an error" - {
                val operation = TestOperation(
                    target = Value.Reference(source = SOURCE, path = PATH),
                    value = Value.Literal(fact = DataElement.Text(VALUE_1)),
                    operator = EQ
                )

                "then the function should return an error" {
                    val result = operation.calculate(CONTEXT)
                    result.shouldBeFailure()
                    val error = result.cause.shouldBeInstanceOf<ContextError.SourceMissing>()
                    error.source shouldBe SOURCE
                }
            }
        }
    }

    companion object {
        private val CONTEXT = Context.empty()
        private val PATH_COMPILER = defaultPathCompiler(ObjectMapper())

        private const val SOURCE_NAME = "input.body"
        private val SOURCE = Source(SOURCE_NAME)

        private const val VALUE_1 = "value-1"

        private val PATH = "$.id".compile()

        private fun String.compile(): Path = PATH_COMPILER.compile(this).orThrow { error(it.description) }
    }

    private data class TestOperation(
        override val target: Value,
        override val operator: Operator<Boolean>,
        override val value: Value
    ) : Operation<Boolean>
}
