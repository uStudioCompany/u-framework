package io.github.ustudiocompany.uframework.rulesengine.core.env

import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class EnvVarsMapTest : UnitTest() {

    init {

        "The `EnvVarsMap` type" - {

            "when environment variables is empty" - {
                val envVars = envVarsOf()

                "then the function `isEmpty` source should return true" {
                    envVars.isEmpty() shouldBe true
                }

                "then the function `isNotEmpty` source should return false" {
                    envVars.isNotEmpty() shouldBe false
                }

                "then the function `contains` should return false" {
                    envVars.contains(FIRST_ENV_VAR_NAME) shouldBe false
                }

                "then the function `get` should return null" {
                    val result = envVars.getOrNull(FIRST_ENV_VAR_NAME)
                    result.shouldBeNull()
                }

                "then the variables property should be empty" {
                    envVars.shouldBeEmpty()
                }
            }

            "when environment variables created from variables" - {

                "when any variable is not passed" - {
                    val envVars = envVarsOf()

                    "then the function `isEmpty` source should return true" {
                        envVars.isEmpty() shouldBe true
                    }

                    "then the function `isNotEmpty` source should return false" {
                        envVars.isNotEmpty() shouldBe false
                    }

                    "then the function `contains` should return false" {
                        envVars.contains(FIRST_ENV_VAR_NAME) shouldBe false
                    }

                    "then the function `get` should return null" {
                        val result = envVars.getOrNull(FIRST_ENV_VAR_NAME)
                        result.shouldBeNull()
                    }

                    "then the variables property should be empty" {
                        envVars.shouldBeEmpty()
                    }
                }

                "when some variable is passed" - {

                    "when passing unique variables" - {
                        val first = JsonElement.Text(FIRST_ENV_VAR_VALUE)
                        val second = JsonElement.Text(SECOND_ENV_VAR_VALUE)
                        val envVars = envVarsOf(FIRST_ENV_VAR_NAME to first, SECOND_ENV_VAR_NAME to second)

                        "then the function `isEmpty` source should return false" {
                            envVars.isEmpty() shouldBe false
                        }

                        "then the function `isNotEmpty` source should return true" {
                            envVars.isNotEmpty() shouldBe true
                        }

                        "then the function `contains` for existing environment variable should return true" {
                            envVars.contains(FIRST_ENV_VAR_NAME) shouldBe true
                        }

                        "then the function `contains` for non-existing environment variable should return false" {
                            envVars.contains(UNKNOWN_ENV_VAR_NAME) shouldBe false
                        }

                        "then the function `get` for existing environment variable should return the value" {
                            val result = envVars.getOrNull(FIRST_ENV_VAR_NAME)
                            result shouldBe first
                        }

                        "then the function `get` for non-existing environment variable should return the null value" {
                            val result = envVars.getOrNull(UNKNOWN_ENV_VAR_NAME)
                            result.shouldBeNull()
                        }

                        "then the variables property should contain the unique environment variables" {
                            envVars shouldContainExactly listOf(
                                EnvVars.Variable(FIRST_ENV_VAR_NAME, first),
                                EnvVars.Variable(SECOND_ENV_VAR_NAME, second)
                            )
                        }
                    }

                    "when passing non unique variables" - {
                        val first = JsonElement.Text(FIRST_ENV_VAR_VALUE)
                        val second = JsonElement.Text(SECOND_ENV_VAR_VALUE)
                        val envVars = envVarsOf(FIRST_ENV_VAR_NAME to first, FIRST_ENV_VAR_NAME to second)

                        "then the function `isEmpty` source should return false" {
                            envVars.isEmpty() shouldBe false
                        }

                        "then the function `isNotEmpty` source should return true" {
                            envVars.isNotEmpty() shouldBe true
                        }

                        "then the function `contains` for existing environment variable should return true" {
                            envVars.contains(FIRST_ENV_VAR_NAME) shouldBe true
                        }

                        "then the function `contains` for non-existing environment variable should return false" {
                            envVars.contains(UNKNOWN_ENV_VAR_NAME) shouldBe false
                        }

                        "then the function `get` for existing environment variable should return the value" {
                            val result = envVars.getOrNull(FIRST_ENV_VAR_NAME)
                            result shouldBe second
                        }

                        "then the function `get` for non-existing environment variable should return the null value" {
                            val result = envVars.getOrNull(UNKNOWN_ENV_VAR_NAME)
                            result.shouldBeNull()
                        }

                        "then the variables property should contain the unique environment variables" {
                            envVars shouldContainExactly listOf(EnvVars.Variable(FIRST_ENV_VAR_NAME, second))
                        }
                    }
                }
            }
        }
    }

    private companion object {
        private val FIRST_ENV_VAR_NAME = EnvVarName("env_var_1")
        private val SECOND_ENV_VAR_NAME = EnvVarName("env_var_2")
        private val UNKNOWN_ENV_VAR_NAME = EnvVarName("env_var_3")
        private const val FIRST_ENV_VAR_VALUE = "value-1"
        private const val SECOND_ENV_VAR_VALUE = "value-2"
    }
}
