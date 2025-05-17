package io.github.ustudiocompany.uframework.rulesengine.core.env

import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class EnvVarsTest : UnitTest() {

    init {

        "The `EnvVars` type" - {

            "when environment variables is empty" - {
                val envVars = EnvVars.EMPTY

                "then the function `contains` should return false" {
                    envVars.contains(ENV_VAR_NAME) shouldBe false
                }

                "then the function `get` should return null" {
                    val result = envVars.getOrNull(ENV_VAR_NAME)
                    result.shouldBeNull()
                }

                "then the variables property should be empty" {
                    envVars.shouldBeEmpty()
                }
            }

            "when environment variables is not empty" - {
                val origin = JsonElement.Text(ORIGIN_VALUE)
                val envVars = EnvVars(mapOf(ENV_VAR_NAME to origin))

                "then the function `contains` for existing environment variable should return true" {
                    envVars.contains(ENV_VAR_NAME) shouldBe true
                }

                "then the function `contains` for non-existing environment variable should return false" {
                    envVars.contains(UNKNOWN_ENV_VAR_NAME) shouldBe false
                }

                "then the function `get` for existing environment variable should return the value" {
                    val result = envVars.getOrNull(ENV_VAR_NAME)
                    result shouldBe origin
                }

                "then the function `get` for non-existing environment variable should return the null value" {
                    val result = envVars.getOrNull(UNKNOWN_ENV_VAR_NAME)
                    result.shouldBeNull()
                }

                "then the variables property should contain the environment variables" {
                    envVars shouldContainExactly listOf(ENV_VAR_NAME to origin)
                }
            }

            "when environment variables created from pairs" - {
                val origin = JsonElement.Text(ORIGIN_VALUE)
                val envVars = EnvVars(ENV_VAR_NAME to origin)

                "then the function `contains` for existing environment variable should return true" {
                    envVars.contains(ENV_VAR_NAME) shouldBe true
                }

                "then the function `contains` for non-existing environment variable should return false" {
                    envVars.contains(UNKNOWN_ENV_VAR_NAME) shouldBe false
                }

                "then the function `get` for existing environment variable should return the value" {
                    val result = envVars.getOrNull(ENV_VAR_NAME)
                    result shouldBe origin
                }

                "then the function `get` for non-existing environment variable should return the null value" {
                    val result = envVars.getOrNull(UNKNOWN_ENV_VAR_NAME)
                    result.shouldBeNull()
                }

                "then the variables property should contain the environment variables" {
                    envVars shouldContainExactly listOf(ENV_VAR_NAME to origin)
                }
            }

            "when environment variables created from a map" - {
                val origin = JsonElement.Text(ORIGIN_VALUE)
                val envVars = EnvVars(mapOf(ENV_VAR_NAME to origin))

                "then the function `contains` for existing environment variable should return true" {
                    envVars.contains(ENV_VAR_NAME) shouldBe true
                }

                "then the function `contains` for non-existing environment variable should return false" {
                    envVars.contains(UNKNOWN_ENV_VAR_NAME) shouldBe false
                }

                "then the function `get` for existing environment variable should return the value" {
                    val result = envVars.getOrNull(ENV_VAR_NAME)
                    result shouldBe origin
                }

                "then the function `get` for non-existing environment variable should return the null value" {
                    val result = envVars.getOrNull(UNKNOWN_ENV_VAR_NAME)
                    result.shouldBeNull()
                }

                "then the variables property should contain the environment variables" {
                    envVars shouldContainExactly listOf(ENV_VAR_NAME to origin)
                }
            }
        }
    }

    private companion object {
        private val ENV_VAR_NAME = EnvVarName("env_var_1")
        private val UNKNOWN_ENV_VAR_NAME = EnvVarName("env_var_2")
        private const val ORIGIN_VALUE = "value-1"
    }
}
