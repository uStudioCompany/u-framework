package io.github.ustudiocompany.uframework.rulesengine.core.env

import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class ExtendedEnvVarsTest : UnitTest() {

    init {

        "The `ExtendedEnvVars` type" - {

            "when the original and additional environment variable collections are empty" - {
                val origin = envVarsListOf()
                val extend = origin.append()

                "then the function `isEmpty` source should return true" {
                    extend.isEmpty() shouldBe true
                }

                "then the function `isNotEmpty` source should return false" {
                    extend.isNotEmpty() shouldBe false
                }

                "then the function `contains` should return false" {
                    extend.contains(ORIGIN_ENV_VAR_NAME) shouldBe false
                }

                "then the function `get` should return null" {
                    val result = extend.getOrNull(ORIGIN_ENV_VAR_NAME)
                    result.shouldBeNull()
                }

                "then the variables property should be empty" {
                    extend.shouldBeEmpty()
                }
            }

            "when the origin collection of environment variables is empty" - {
                val appendingValue = JsonElement.Text(APPEND_ENV_VAR_VALUE)
                val origin = envVarsListOf()
                val extend = origin.append(APPEND_ENV_VAR_NAME to appendingValue)

                "then the function `isEmpty` source should return false" {
                    extend.isEmpty() shouldBe false
                }

                "then the function `isNotEmpty` source should return true" {
                    extend.isNotEmpty() shouldBe true
                }

                "then the function `contains` for existing environment variable should return true" {
                    extend.contains(APPEND_ENV_VAR_NAME) shouldBe true
                }

                "then the function `contains` for non-existing environment variable should return false" {
                    extend.contains(UNKNOWN_ENV_VAR_NAME) shouldBe false
                }

                "then the function `get` for existing environment variable should return the value" {
                    val result = extend.getOrNull(APPEND_ENV_VAR_NAME)
                    result shouldBe appendingValue
                }

                "then the function `get` for non-existing environment variable should return the null value" {
                    val result = extend.getOrNull(UNKNOWN_ENV_VAR_NAME)
                    result.shouldBeNull()
                }

                "then the variables property should contain the unique environment variables" {
                    extend shouldContainExactly listOf(APPEND_ENV_VAR_NAME to appendingValue)
                }
            }

            "when the added collection of environment variables is empty" - {
                val originValue = JsonElement.Text(ORIGIN_ENV_VAR_VALUE)
                val origin = envVarsListOf(ORIGIN_ENV_VAR_NAME to originValue)
                val extend = origin.append()

                "then the function `isEmpty` source should return false" {
                    extend.isEmpty() shouldBe false
                }

                "then the function `isNotEmpty` source should return true" {
                    extend.isNotEmpty() shouldBe true
                }

                "then the function `contains` for existing environment variable should return true" {
                    extend.contains(ORIGIN_ENV_VAR_NAME) shouldBe true
                }

                "then the function `contains` for non-existing environment variable should return false" {
                    extend.contains(UNKNOWN_ENV_VAR_NAME) shouldBe false
                }

                "then the function `get` for existing environment variable should return the value" {
                    val result = extend.getOrNull(ORIGIN_ENV_VAR_NAME)
                    result shouldBe originValue
                }

                "then the function `get` for non-existing environment variable should return the null value" {
                    val result = extend.getOrNull(UNKNOWN_ENV_VAR_NAME)
                    result.shouldBeNull()
                }

                "then the variables property should contain the unique environment variables" {
                    extend shouldContainExactly listOf(ORIGIN_ENV_VAR_NAME to originValue)
                }
            }

            "the original and additional environment variable collections are not empty" - {
                val originValue = JsonElement.Text(ORIGIN_ENV_VAR_VALUE)
                val appendingValue = JsonElement.Text(APPEND_ENV_VAR_VALUE)
                val origin = envVarsListOf(ORIGIN_ENV_VAR_NAME to originValue)
                val extend = origin.append(APPEND_ENV_VAR_NAME to appendingValue)

                "then the function `isEmpty` source should return false" {
                    extend.isEmpty() shouldBe false
                }

                "then the function `isNotEmpty` source should return true" {
                    extend.isNotEmpty() shouldBe true
                }

                "then the function `contains` for existing environment variable should return true" {
                    extend.contains(ORIGIN_ENV_VAR_NAME) shouldBe true
                }

                "then the function `contains` for non-existing environment variable should return false" {
                    extend.contains(UNKNOWN_ENV_VAR_NAME) shouldBe false
                }

                "then the function `get` for existing environment variable should return the value" {
                    val result = extend.getOrNull(ORIGIN_ENV_VAR_NAME)
                    result shouldBe originValue
                }

                "then the function `get` for non-existing environment variable should return the null value" {
                    val result = extend.getOrNull(UNKNOWN_ENV_VAR_NAME)
                    result.shouldBeNull()
                }

                "then the variables property should contain the unique environment variables" {
                    extend.toList() shouldContainExactlyInAnyOrder listOf(
                        ORIGIN_ENV_VAR_NAME to originValue,
                        APPEND_ENV_VAR_NAME to appendingValue
                    )
                }
            }
        }
    }

    private companion object {
        private val ORIGIN_ENV_VAR_NAME = EnvVarName("env_var_1")
        private val APPEND_ENV_VAR_NAME = EnvVarName("env_var_2")
        private val UNKNOWN_ENV_VAR_NAME = EnvVarName("env_var_3")
        private const val ORIGIN_ENV_VAR_VALUE = "value-1"
        private const val APPEND_ENV_VAR_VALUE = "value-2"
    }
}
