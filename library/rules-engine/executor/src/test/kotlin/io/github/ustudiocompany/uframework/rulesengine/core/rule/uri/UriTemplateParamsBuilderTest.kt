package io.github.ustudiocompany.uframework.rulesengine.core.rule.uri

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.orThrow
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.path.Path
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.rulesengine.executor.context.Context
import io.github.ustudiocompany.uframework.rulesengine.executor.error.ContextError
import io.github.ustudiocompany.uframework.rulesengine.path.defaultPathEngine
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class UriTemplateParamsBuilderTest : UnitTest() {

    init {

        "The uri template params builder" - {

            "when compute value of param is successful" - {
                val params = UriTemplateParams(
                    listOf(
                        UriTemplateParam(
                            name = PARAM_NAME_1,
                            value = Value.Literal(fact = DataElement.Text(PARAM_VALUE_1))
                        )
                    )
                )
                val result = params.build(context = CONTEXT)

                "then the function should return the build args" {
                    result shouldBeSuccess mapOf(PARAM_NAME_1 to PARAM_VALUE_1)
                }
            }

            "when compute value of param is failed" - {
                val params = UriTemplateParams(
                    listOf(
                        UriTemplateParam(
                            name = PARAM_NAME_1,
                            value = Value.Reference(
                                source = SOURCE,
                                path = "$.id".compile()
                            )
                        )
                    )
                )

                "then the function should return the error" {
                    val result = params.build(context = CONTEXT)
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

        private const val PARAM_NAME_1 = "name-1"
        private const val PARAM_VALUE_1 = "value-1"

        private fun String.compile(): Path = PATH_ENGINE.compile(this).orThrow { error(it.description) }
    }
}
