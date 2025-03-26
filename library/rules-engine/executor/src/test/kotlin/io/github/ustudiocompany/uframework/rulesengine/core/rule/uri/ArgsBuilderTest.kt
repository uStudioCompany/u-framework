package io.github.ustudiocompany.uframework.rulesengine.core.rule.uri

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.orThrow
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.path.Path
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.rulesengine.executor.CallProvider
import io.github.ustudiocompany.uframework.rulesengine.executor.error.ContextError
import io.github.ustudiocompany.uframework.rulesengine.path.defaultPathEngine
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class ArgsBuilderTest : UnitTest() {

    init {

        "The args builder" - {

            "when compute value of arg is successful" - {
                val args = Args(
                    listOf(
                        Arg(
                            name = ARG_NAME_1,
                            value = Value.Literal(fact = DataElement.Text(ARG_VALUE_1))
                        )
                    )
                )
                val result = args.build(context = CONTEXT)

                "then the function should return the build args" {
                    result shouldBeSuccess listOf(
                        CallProvider.Request.Arg(
                            name = ARG_NAME_1,
                            value = ARG_VALUE_1
                        )
                    )
                }
            }

            "when compute value of arg is failed" - {
                val args = Args(
                    listOf(
                        Arg(
                            name = ARG_NAME_1,
                            value = Value.Reference(
                                source = SOURCE,
                                path = "$.id".parse()
                            )
                        )
                    )
                )
                val result = args.build(context = CONTEXT)

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
        private val PATH_ENGINE = defaultPathEngine(ObjectMapper())

        private const val ARG_NAME_1 = "name-1"
        private const val ARG_VALUE_1 = "value-1"

        private fun String.parse(): Path = PATH_ENGINE.parse(this).orThrow { error(it.description) }
    }
}
