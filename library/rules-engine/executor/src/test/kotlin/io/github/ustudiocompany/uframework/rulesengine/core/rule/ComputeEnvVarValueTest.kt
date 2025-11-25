package io.github.ustudiocompany.uframework.rulesengine.core.rule

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldContainFailureInstance
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVarName
import io.github.ustudiocompany.uframework.rulesengine.core.env.envVarsOf
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class ComputeEnvVarValueTest : UnitTest() {

    init {

        "when the value is the EnvVars type" - {

            "when the variable is missing from the environment variables" - {
                val envVars = envVarsOf()

                "then the compute function should return a failure" {
                    val value = Value.EnvVars(name = ENV_VAR_NAME)
                    val result = value.compute(envVars, CONTEXT)
                    result.shouldContainFailureInstance()
                        .shouldBeInstanceOf<ValueComputeErrors.GettingValueFromEnvVars>()
                }
            }

            "when the variable is present from the environment variables" - {
                val envVars = envVarsOf(ENV_VAR_NAME to DATA)

                "then the compute function should return a value" {
                    val value = Value.EnvVars(name = ENV_VAR_NAME)
                    val result = value.compute(envVars, CONTEXT)
                    result shouldBeSuccess DATA
                }
            }
        }
    }

    companion object {
        private val ENV_VAR_NAME = EnvVarName("env_var_1")
        private val CONTEXT = Context.empty()

        private const val VALUE = "value"
        private val DATA = JsonElement.Text(VALUE)
    }
}
