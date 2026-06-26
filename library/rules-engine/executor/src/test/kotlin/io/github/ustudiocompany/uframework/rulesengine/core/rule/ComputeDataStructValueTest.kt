package io.github.ustudiocompany.uframework.rulesengine.core.rule

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldContainFailureInstance
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVarName
import io.github.ustudiocompany.uframework.rulesengine.core.env.envVarsOf
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.DataSchema
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class ComputeDataStructValueTest : UnitTest() {

    init {
        "Compute a value of the DataStruct type" - {

            "when success of building data struct" - {
                val value = Value.DataStruct(
                    scheme = DataSchema.Struct(
                        properties = listOf(
                            DataSchema.Property.Element(
                                name = KEY,
                                value = Value.Literal(fact = TEXT_VALUE)
                            )
                        )
                    )
                )

                "then the compute function should return a value" {
                    val result = value.compute(ENV_VARS, CONTEXT)
                    result shouldBeSuccess JsonElement.Struct(KEY to TEXT_VALUE)
                }
            }

            "when error of building data struct" - {
                val value = Value.DataStruct(
                    scheme = DataSchema.Struct(
                        properties = listOf(
                            DataSchema.Property.Element(
                                name = KEY,
                                value = Value.EnvVars(name = UNKNOWN_ENV_VAR)
                            )
                        )
                    )
                )

                "then the compute function should return a failure" {
                    val result = value.compute(ENV_VARS, CONTEXT)
                    result.shouldContainFailureInstance()
                        .shouldBeInstanceOf<ValueComputationErrors.DataStructBuild>()
                }

                "then the computeOrNull function should return a failure" {
                    val result = value.computeOrNull(ENV_VARS, CONTEXT)
                    result.shouldContainFailureInstance()
                        .shouldBeInstanceOf<OptionalValueComputationErrors.DataStructBuild>()
                }
            }
        }
    }

    companion object {
        val ENV_VARS = envVarsOf()
        val CONTEXT = Context.empty()

        private const val KEY = "id"
        private val TEXT_VALUE = JsonElement.Text("value")
        private val UNKNOWN_ENV_VAR = EnvVarName("unknown")
    }
}
