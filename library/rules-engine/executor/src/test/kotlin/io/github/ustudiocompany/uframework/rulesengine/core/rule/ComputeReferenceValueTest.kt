package io.github.ustudiocompany.uframework.rulesengine.core.rule

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.orThrow
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.path.Path
import io.github.ustudiocompany.uframework.rulesengine.executor.error.ContextError
import io.github.ustudiocompany.uframework.rulesengine.executor.error.DataErrors
import io.github.ustudiocompany.uframework.rulesengine.path.defaultPathEngine
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class ComputeReferenceValueTest : UnitTest() {

    init {
        "when value is reference type" - {

            "when the source is not in the context" - {
                val context = Context.empty()

                "then the compute function should return a failure" {
                    val value = Value.Reference(
                        source = SOURCE,
                        path = VALID_PATH
                    )
                    val result = value.compute(context)
                    result.shouldBeFailure()
                    result.cause.shouldBeInstanceOf<ContextError.SourceMissing>()
                }
            }

            "when the source is in the context" - {
                val context = Context(mapOf(SOURCE to DATA))

                "when the data does not contain values by path" - {

                    "then the compute function should return a failure" {
                        val value = Value.Reference(
                            source = SOURCE,
                            path = INVALID_PATH
                        )
                        val result = value.compute(context)
                        result.shouldBeFailure()
                        result.cause.shouldBeInstanceOf<DataErrors.Missing>()
                    }
                }

                "when the data contains values by path" - {

                    "then the compute function should return a value" {
                        val value = Value.Reference(
                            source = SOURCE,
                            path = VALID_PATH
                        )
                        val result = value.compute(context)
                        result shouldBeSuccess DataElement.Text(VALUE)
                    }
                }
            }
        }
    }

    companion object {
        private val PATH_ENGINE = defaultPathEngine(ObjectMapper())

        private const val SOURCE_NAME = "input.body"
        private val SOURCE = Source(SOURCE_NAME)

        private const val VALUE = "value"
        private val DATA = DataElement.Struct(
            mutableMapOf(VALUE to DataElement.Text(VALUE))
        )

        private val VALID_PATH = "$.$VALUE".parse()
        private val INVALID_PATH = "$.id".parse()

        private fun String.parse(): Path = PATH_ENGINE.parse(this).orThrow { error(it.description) }
    }
}
