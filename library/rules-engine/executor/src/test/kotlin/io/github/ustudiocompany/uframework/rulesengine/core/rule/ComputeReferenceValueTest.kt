package io.github.ustudiocompany.uframework.rulesengine.core.rule

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.ResultK.Success
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldContainFailureInstance
import io.github.airflux.commons.types.resultk.matcher.shouldContainSuccessInstance
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.json.path.Path
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.env.envVarsOf
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class ComputeReferenceValueTest : UnitTest() {

    init {

        "Compute a value of the Reference type" - {

            "when the source is not in the context" - {
                val envVars = envVarsOf()
                val context = Context.empty()

                "then the compute function should return a failure" {
                    val value = Value.Reference(
                        source = SOURCE,
                        path = path(result = Success.asNull)
                    )
                    val result = value.compute(envVars, context)
                    result.shouldContainFailureInstance()
                        .shouldBeInstanceOf<ValueComputationErrors.ContextDataRetrieval>()
                }

                "then the computeOrNull function should return a failure" {
                    val value = Value.Reference(
                        source = SOURCE,
                        path = path(result = Success.asNull)
                    )
                    val result = value.computeOrNull(envVars, context)
                    result.shouldContainFailureInstance()
                        .shouldBeInstanceOf<OptionalValueComputationErrors.ContextDataRetrieval>()
                }
            }

            "when the source is in the context" - {
                val envVars = envVarsOf()
                val context = Context(sources = mapOf(SOURCE to DATA))

                "when the data does not contain values by path" - {

                    "then the compute function should return an error" {
                        val value = Value.Reference(
                            source = SOURCE,
                            path = path(result = Success.asNull)
                        )
                        val result = value.compute(envVars, context)
                        result.shouldContainFailureInstance()
                            .shouldBeInstanceOf<ValueComputationErrors.DataNotFoundAtPath>()
                    }

                    "then the computeOrNull function should return an error" {
                        val value = Value.Reference(
                            source = SOURCE,
                            path = path(result = Success.asNull)
                        )
                        val result = value.computeOrNull(envVars, context)
                        result.shouldContainSuccessInstance()
                            .shouldBeNull()
                    }
                }

                "when the data contains values by path" - {

                    "then the compute function should return a value" {
                        val value = Value.Reference(
                            source = SOURCE,
                            path = path(result = TEXT_VALUE.asSuccess())
                        )
                        val result = value.compute(envVars, context)
                        result shouldBeSuccess JsonElement.Text(VALUE)
                    }

                    "then the computeOrNull function should return a value" {
                        val value = Value.Reference(
                            source = SOURCE,
                            path = path(result = TEXT_VALUE.asSuccess())
                        )
                        val result = value.computeOrNull(envVars, context)
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
        private fun path(result: ResultK<JsonElement?, Path.SearchError>) =
            object : Path {
                override val text: String = PATH_VALUE
                override fun searchIn(data: JsonElement): ResultK<JsonElement?, Path.SearchError> = result
            }
    }
}
