package io.github.ustudiocompany.uframework.rulesengine.core.env

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldContainFailureInstance
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class GetValueFromEnvVarsTest : UnitTest() {

    init {

        "The extension function `get` for `EnvVars` type" - {

            "when environment variables is empty" - {
                val envVars = EnvVars.EMPTY

                "then function should return an error" {
                    val result = envVars[ENV_VAR]
                    result.shouldContainFailureInstance()
                        .shouldBeInstanceOf<GetValueFromEnvVarsErrors.EnvVarMissing>()
                }
            }

            "when environment variables is not empty" - {
                val value = JsonElement.Text(ORIGIN_VALUE)
                val envVars = EnvVars(envVars = mapOf(ENV_VAR to value))

                "when the variable is missing from the environment variables" - {

                    "then function should return an error" {
                        val result = envVars[UNKNOWN_ENV_VAR]
                        result.shouldContainFailureInstance()
                            .shouldBeInstanceOf<GetValueFromEnvVarsErrors.EnvVarMissing>()
                    }
                }

                "when the variable is present from the environment variables" - {

                    "then function should return the value" {
                        val result = envVars[ENV_VAR]
                        result shouldBeSuccess value
                    }
                }
            }
        }
    }

    private companion object {
        private val ENV_VAR = EnvVarName("env_var")
        private val UNKNOWN_ENV_VAR = EnvVarName("unknown_env_var")
        private const val ORIGIN_VALUE = "value-1"
    }

    private sealed interface Errors : Failure {

        data object TestMergerError : Errors {
            override val code: String = "MERGER_ERROR"
        }
    }
}
