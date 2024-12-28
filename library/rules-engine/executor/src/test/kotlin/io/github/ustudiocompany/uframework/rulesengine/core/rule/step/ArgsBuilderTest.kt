package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.orThrow
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.path.Path
import io.github.ustudiocompany.uframework.rulesengine.core.path.defaultPathCompiler
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Arg
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Args
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.rulesengine.core.rule.context.Context
import io.github.ustudiocompany.uframework.rulesengine.executor.error.ContextError
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.types.shouldBeInstanceOf

internal class ArgsBuilderTest : UnitTest() {

    init {

        "The arg builder" - {

            "when building the args is successful" - {
                val result = ARGS.build(context = CONTEXT, argType = Arg.Type.PATH_VARIABLE)

                "then the function should return the build args" {
                    result shouldBeSuccess mapOf(ARG_NAME_1 to DataElement.Text(ARG_VALUE_1))
                }
            }

            "when building the args is failed" - {
                val result = ARGS.build(context = CONTEXT, argType = Arg.Type.REQUEST_PARAMETER)

                "then the function should return the error" {
                    result.shouldBeFailure()
                    result.cause.shouldBeInstanceOf<ContextError.SourceMissing>()
                }
            }
        }
    }

    private companion object {
        private val SOURCE = Source("input")
        private val CONTEXT = Context.empty()
        private val PATH_COMPILER = defaultPathCompiler(ObjectMapper())

        private const val ARG_NAME_1 = "path"
        private const val ARG_VALUE_1 = "value-1"

        private const val ARG_NAME_2 = "query"

        private const val ARG_NAME_3 = "header"
        private const val ARG_VALUE_3 = "value-3"

        private val ARGS = Args(
            listOf(
                Arg(
                    name = ARG_NAME_1,
                    type = Arg.Type.PATH_VARIABLE,
                    value = Value.Literal(fact = DataElement.Text(ARG_VALUE_1))
                ),
                Arg(
                    name = ARG_NAME_2,
                    type = Arg.Type.REQUEST_PARAMETER,
                    value = Value.Reference(
                        source = SOURCE,
                        path = "$.id".compile()
                    )
                ),
                Arg(
                    name = ARG_NAME_3,
                    type = Arg.Type.HEADER,
                    value = Value.Literal(fact = DataElement.Text(ARG_VALUE_3))
                )
            )
        )

        private fun String.compile(): Path = PATH_COMPILER.compile(this).orThrow { error(it.description) }
    }
}
