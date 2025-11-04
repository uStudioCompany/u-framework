package io.github.ustudiocompany.uframework.rulesengine.feel.functions

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldContainSuccessInstance
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVarName
import io.github.ustudiocompany.uframework.rulesengine.core.env.envVarsOf
import io.github.ustudiocompany.uframework.rulesengine.core.feel.FeelExpression
import io.github.ustudiocompany.uframework.rulesengine.feel.FeelExpressionParserConfiguration
import io.github.ustudiocompany.uframework.rulesengine.feel.feelExpressionParser
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.collections.shouldBeUnique
import io.kotest.matchers.collections.shouldMatchEach
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import org.camunda.feel.syntaxtree.ValString
import java.util.UUID

@OptIn(AirfluxTypesExperimental::class)
internal class UuidGenerationFunctionTest : UnitTest() {

    init {
        "The `uuid` function" - {
            val context = Context.empty()

            "when some parameter is not specified" - {
                val expression = shouldBeSuccess { parser.parse("uuid()") }
                val envVars = envVarsOf()

                "when a function is called multiple times" - {
                    val results: List<ResultK<JsonElement, FeelExpression.EvaluateError>> = listOf(
                        expression.evaluate(envVars, context),
                        expression.evaluate(envVars, context),
                    )

                    "then each function call should" - {

                        "return a value in UUID format" {
                            results.forEach { result ->
                                val value = result.shouldContainSuccessInstance()
                                    .shouldBeInstanceOf<JsonElement.Text>()
                                value.get shouldContain UUID_PATTERN
                            }
                        }

                        "return a different value" {
                            val values = results.map {
                                it.shouldContainSuccessInstance()
                                    .shouldBeInstanceOf<JsonElement.Text>()
                                    .get
                            }

                            values.shouldBeUnique()
                        }
                    }
                }
            }

            "when some parameters are specified as environment variables" - {
                val envVars = envVarsOf(
                    EnvVarName(ENV_VAR_FIRST_NAME) to JsonElement.Text(ENV_VAR_FIRST_VALUE),
                    EnvVarName(ENV_VAR_SECOND_NAME) to JsonElement.Text(ENV_VAR_SECOND_VALUE),
                    EnvVarName(ENV_VAR_THIRD_NAME) to JsonElement.Text(ENV_VAR_THIRD_VALUE),
                )

                "when a function is called multiple times with same parameters" - {
                    val expression = shouldBeSuccess { parser.parse("uuid($ENV_VAR_FIRST_NAME, $ENV_VAR_SECOND_NAME)") }
                    val results: List<ResultK<JsonElement, FeelExpression.EvaluateError>> = listOf(
                        expression.evaluate(envVars, context),
                        expression.evaluate(envVars, context),
                    )

                    "then each function call should" - {

                        "return a value in UUID format" {
                            results.forEach { result ->
                                val value = result.shouldContainSuccessInstance()
                                    .shouldBeInstanceOf<JsonElement.Text>()
                                    .get

                                value shouldContain UUID_PATTERN
                            }
                        }

                        "return same values" {

                            results.forEach {
                                val value = it.shouldContainSuccessInstance()
                                    .shouldBeInstanceOf<JsonElement.Text>()
                                    .get

                                value shouldBe genUUID(ENV_VAR_FIRST_VALUE, ENV_VAR_SECOND_VALUE)
                            }
                        }
                    }
                }

                "when a function is called multiple times with different parameters" - {
                    val firstExpression =
                        shouldBeSuccess { parser.parse("uuid($ENV_VAR_FIRST_NAME, $ENV_VAR_SECOND_NAME)") }
                    val secondExpression =
                        shouldBeSuccess { parser.parse("uuid($ENV_VAR_SECOND_NAME, $ENV_VAR_THIRD_NAME)") }
                    val results: List<ResultK<JsonElement, FeelExpression.EvaluateError>> = listOf(
                        firstExpression.evaluate(envVars, context),
                        secondExpression.evaluate(envVars, context),
                    )

                    "then each function call should" - {

                        "return a value in UUID format" {
                            results.forEach { result ->
                                val value = result.shouldContainSuccessInstance()
                                    .shouldBeInstanceOf<JsonElement.Text>()
                                    .get

                                value shouldContain UUID_PATTERN
                            }
                        }

                        "return different values" {
                            results.shouldMatchEach(
                                { result ->
                                    val value = result.shouldContainSuccessInstance()
                                        .shouldBeInstanceOf<JsonElement.Text>()
                                        .get
                                    value shouldBe genUUID(ENV_VAR_FIRST_VALUE, ENV_VAR_SECOND_VALUE)
                                },
                                { result ->
                                    val value = result.shouldContainSuccessInstance()
                                        .shouldBeInstanceOf<JsonElement.Text>()
                                        .get
                                    value shouldBe genUUID(ENV_VAR_SECOND_VALUE, ENV_VAR_THIRD_VALUE)
                                }
                            )
                        }
                    }
                }
            }

            "when some parameters are specified as values" - {
                val envVars = envVarsOf()

                "when a function is called multiple times with same parameters" - {
                    val expression =
                        shouldBeSuccess { parser.parse("""uuid("$ENV_VAR_FIRST_VALUE", "$ENV_VAR_SECOND_VALUE")""") }
                    val results: List<ResultK<JsonElement, FeelExpression.EvaluateError>> = listOf(
                        expression.evaluate(envVars, context),
                        expression.evaluate(envVars, context),
                    )

                    "then each function call should" - {

                        "return a value in UUID format" {
                            results.forEach { result ->
                                val value = result.shouldContainSuccessInstance()
                                    .shouldBeInstanceOf<JsonElement.Text>()
                                    .get

                                value shouldContain UUID_PATTERN
                            }
                        }

                        "return same values" {
                            results.forEach {
                                val value = it.shouldContainSuccessInstance()
                                    .shouldBeInstanceOf<JsonElement.Text>()
                                    .get

                                value shouldBe genUUID(ENV_VAR_FIRST_VALUE, ENV_VAR_SECOND_VALUE)
                            }
                        }
                    }
                }

                "when a function is called multiple times with different parameters" - {
                    val firstExpression =
                        shouldBeSuccess { parser.parse("""uuid("$ENV_VAR_FIRST_VALUE", "$ENV_VAR_SECOND_VALUE")""") }
                    val secondExpression =
                        shouldBeSuccess { parser.parse("""uuid("$ENV_VAR_SECOND_VALUE", "$ENV_VAR_THIRD_VALUE")""") }
                    val results: List<ResultK<JsonElement, FeelExpression.EvaluateError>> = listOf(
                        firstExpression.evaluate(envVars, context),
                        secondExpression.evaluate(envVars, context),
                    )

                    "then each function call should" - {

                        "return a value in UUID format" {
                            results.forEach { result ->
                                val value = result.shouldContainSuccessInstance()
                                    .shouldBeInstanceOf<JsonElement.Text>()
                                value.get shouldContain UUID_PATTERN
                            }
                        }

                        "return different values" {
                            results.shouldMatchEach(
                                { result ->
                                    val value = result.shouldContainSuccessInstance()
                                        .shouldBeInstanceOf<JsonElement.Text>()
                                        .get
                                    value shouldBe genUUID(ENV_VAR_FIRST_VALUE, ENV_VAR_SECOND_VALUE)
                                },
                                { result ->
                                    val value = result.shouldContainSuccessInstance()
                                        .shouldBeInstanceOf<JsonElement.Text>()
                                        .get
                                    value shouldBe genUUID(ENV_VAR_SECOND_VALUE, ENV_VAR_THIRD_VALUE)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private companion object {
        private val UUID_PATTERN = Regex(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
        )
        private const val ENV_VAR_FIRST_NAME = "ARG_1"
        private const val ENV_VAR_FIRST_VALUE = "arg-1-value"
        private const val ENV_VAR_SECOND_NAME = "ARG_2"
        private const val ENV_VAR_SECOND_VALUE = "arg-2-value"
        private const val ENV_VAR_THIRD_NAME = "ARG_3"
        private const val ENV_VAR_THIRD_VALUE = "arg-3-value"

        private val parser = feelExpressionParser(
            FeelExpressionParserConfiguration(
                listOf(UuidGenerationFunction())
            )
        )

        private fun genUUID(vararg args: String): String {
            val base = genBase(args).toByteArray()
            return UUID.nameUUIDFromBytes(base).toString()
        }

        private fun genBase(args: Array<out String>): String = args.joinToString(":") { ValString(it).toString() }
    }
}
