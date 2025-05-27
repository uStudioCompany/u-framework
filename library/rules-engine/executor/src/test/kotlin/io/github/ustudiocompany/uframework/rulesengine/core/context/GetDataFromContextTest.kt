package io.github.ustudiocompany.uframework.rulesengine.core.context

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldContainFailureInstance
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class GetDataFromContextTest : UnitTest() {

    init {

        "The extension function `get` for the `Context` type" - {

            "when the context is empty" - {
                val context = Context.empty()

                "then function should return an error" {
                    val result = context[SOURCE]
                    result.shouldContainFailureInstance()
                        .shouldBeInstanceOf<GetDataFromContextErrors.SourceMissing>()
                }
            }

            "when the context is not empty" - {
                val value = JsonElement.Text(ORIGIN_VALUE)
                val context = Context(sources = mapOf(SOURCE to value))

                "when the source is missing" - {

                    "then function should return an error" {
                        val result = context[UNKNOWN_SOURCE]
                        result.shouldContainFailureInstance()
                            .shouldBeInstanceOf<GetDataFromContextErrors.SourceMissing>()
                    }
                }

                "when the source is present" - {

                    "then function should return the value" {
                        val result = context[SOURCE]
                        result shouldBeSuccess value
                    }
                }
            }
        }
    }

    private companion object {
        private val SOURCE = Source("source-1")
        private val UNKNOWN_SOURCE = Source("source-2")
        private const val ORIGIN_VALUE = "value-1"
    }

    private sealed interface Errors : Failure {

        data object TestMergerError : Errors {
            override val code: String = "MERGER_ERROR"
        }
    }
}
