package io.github.ustudiocompany.uframework.rulesengine.core.context

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldContainFailureInstance
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class TryGetFromContextTest : UnitTest() {

    init {

        "The extension function `tryGet` for `Context` type" - {

            "when context is empty" - {
                val context = Context.empty()

                "then function should return an error" {
                    val result = context.tryGet(SOURCE)
                    result.shouldContainFailureInstance()
                        .shouldBeInstanceOf<GetDataFromContextErrors.SourceMissing>()
                }
            }

            "when context is not empty" - {
                val value = DataElement.Text(ORIGIN_VALUE)
                val context = Context(mapOf(SOURCE to value))

                "when source is missing" - {

                    "then function should return an error" {
                        val result = context.tryGet(UNKNOWN_SOURCE)
                        result.shouldContainFailureInstance()
                            .shouldBeInstanceOf<GetDataFromContextErrors.SourceMissing>()
                    }
                }

                "when source is present" - {

                    "then function should return the value" {
                        val result = context.tryGet(SOURCE)
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
