package io.github.ustudiocompany.uframework.rulesengine.core.rule

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldContainFailureInstance
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.json.path.Path
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVars
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class ComputeReferenceValueTest : UnitTest() {

    init {
        "when the value is the Reference type" - {

            "when the source is not in the context" - {
                val envVars = EnvVars.EMPTY
                val context = Context.empty()

                "then the compute function should return a failure" {
                    val value = Value.Reference(
                        source = SOURCE,
                        path = path(result = null)
                    )
                    val result = value.compute(envVars, context)
                    result.shouldContainFailureInstance()
                        .shouldBeInstanceOf<ValueComputeErrors.GettingDataFromContext>()
                }
            }

            "when the source is in the context" - {
                val envVars = EnvVars.EMPTY
                val context = Context(sources = mapOf(SOURCE to DATA))

                "when the data does not contain values by path" - {

                    "then the compute function should return a failure" {
                        val value = Value.Reference(
                            source = SOURCE,
                            path = path(result = null)
                        )
                        val result = value.compute(envVars, context)
                        result.shouldContainFailureInstance()
                            .shouldBeInstanceOf<ValueComputeErrors.DataByPathIsNotFound>()
                    }
                }

                "when the data contains values by path" - {

                    "then the compute function should return a value" {
                        val value = Value.Reference(
                            source = SOURCE,
                            path = path(result = TEXT_VALUE)
                        )
                        val result = value.compute(envVars, context)
                        result shouldBeSuccess JsonElement.Text(VALUE)
                    }
                }
            }
        }
    }

    companion object {
        private const val SOURCE_NAME = "input.body"
        private val SOURCE = Source(SOURCE_NAME)

        private const val KEY = "id"
        private const val VALUE = "value"

        private val TEXT_VALUE = JsonElement.Text(VALUE)
        private val DATA = JsonElement.Struct(KEY to TEXT_VALUE)

        private const val PATH_VALUE = "$.id"
        private fun path(result: JsonElement?) =
            object : Path {
                override val text: String = PATH_VALUE

                override fun searchIn(data: JsonElement): ResultK<JsonElement?, Path.SearchError> =
                    result.asSuccess()
            }
    }
}
