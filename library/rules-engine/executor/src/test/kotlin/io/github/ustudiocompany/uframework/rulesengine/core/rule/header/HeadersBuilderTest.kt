package io.github.ustudiocompany.uframework.rulesengine.core.rule.header

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
internal class HeadersBuilderTest : UnitTest() {

    init {

        "The headers builder" - {

            "when compute value of header is successful" - {
                val headers = Headers(
                    listOf(
                        Header(
                            name = HEADER_NAME_1,
                            value = Value.Literal(fact = DataElement.Text(HEADER_VALUE_1))
                        )
                    )
                )
                val result = headers.build(context = CONTEXT)

                "then the function should return the build args" {
                    result shouldBeSuccess listOf(
                        CallProvider.Request.Header(
                            name = HEADER_NAME_1,
                            value = HEADER_VALUE_1
                        )
                    )
                }
            }

            "when compute value of header is failed" - {
                val headers = Headers(
                    listOf(
                        Header(
                            name = HEADER_NAME_1,
                            value = Value.Reference(
                                source = SOURCE,
                                path = "$.id".parse()
                            )
                        )
                    )
                )

                "then the function should return the error" {
                    val result = headers.build(context = CONTEXT)
                    result.shouldBeFailure()
                    result.cause.shouldBeInstanceOf<ContextError.SourceMissing>()
                }
            }
        }
    }

    private companion object {
        private val SOURCE = Source("input")
        private val CONTEXT = Context.Companion.empty()
        private val PATH_ENGINE = defaultPathEngine(ObjectMapper())

        private const val HEADER_NAME_1 = "header-1"
        private const val HEADER_VALUE_1 = "value-1"

        private fun String.parse(): Path = PATH_ENGINE.parse(this).orThrow { error(it.description) }
    }
}
