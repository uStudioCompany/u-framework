package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldContainFailureInstance
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVars
import io.github.ustudiocompany.uframework.rulesengine.core.env.envVarsMapOf
import io.github.ustudiocompany.uframework.rulesengine.core.feel.FeelExpression
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.types.shouldBeInstanceOf
import java.math.BigDecimal

@OptIn(AirfluxTypesExperimental::class)
internal class ArgsBuilderTest : UnitTest() {

    init {

        "The args builder" - {

            "when compute value of arg is successful" - {

                "when the arg has a literal value" - {
                    val args = Args(
                        listOf(
                            Arg(
                                name = ARG_NAME_1,
                                value = Value.Literal(fact = JsonElement.Text(ARG_VALUE_TEXT))
                            ),
                            Arg(
                                name = ARG_NAME_2,
                                value = Value.Literal(fact = JsonElement.Bool(ARG_VALUE_BOOL))
                            ),
                            Arg(
                                name = ARG_NAME_3,
                                value = Value.Literal(fact = JsonElement.Decimal(BigDecimal(ARG_VALUE_DECIMAL)))
                            ),
                            Arg(
                                name = ARG_NAME_4,
                                value = Value.Literal(fact = JsonElement.Array(JsonElement.Text(ARG_VALUE_TEXT)))
                            ),
                            Arg(
                                name = ARG_NAME_5,
                                value = Value.Literal(
                                    fact = JsonElement.Struct(
                                        ARG_NAME_1 to JsonElement.Text(ARG_VALUE_TEXT)
                                    )
                                )
                            )
                        )
                    )
                    val result = args.build(envVars = ENV_VARS, context = CONTEXT) { name, value -> Pair(name, value) }

                    "then the function should return the build args" {
                        result shouldBeSuccess listOf(
                            Pair(ARG_NAME_1, ARG_VALUE_TEXT),
                            Pair(ARG_NAME_2, ARG_VALUE_BOOL.toString()),
                            Pair(ARG_NAME_3, ARG_VALUE_DECIMAL),
                            Pair(ARG_NAME_4, """["$ARG_VALUE_TEXT"]"""),
                            Pair(ARG_NAME_5, """{"$ARG_NAME_1": "$ARG_VALUE_TEXT"}""")
                        )
                    }
                }

                "when compute value of arg is failed" - {
                    val args = Args(
                        listOf(
                            Arg(
                                name = ARG_NAME_1,
                                value = Value.Expression(EXPRESSION)
                            )
                        )
                    )
                    val result = args.build(envVars = ENV_VARS, context = CONTEXT) { name, value -> Pair(name, value) }

                    "then the function should return the error" {
                        result.shouldContainFailureInstance()
                            .shouldBeInstanceOf<ArgsBuilderErrors.ArgValueBuilding>()
                    }
                }
            }
        }
    }

    private companion object {
        private val ENV_VARS = envVarsMapOf()
        private val CONTEXT = Context.empty()

        private const val ARG_NAME_1 = "name-1"
        private const val ARG_NAME_2 = "name-2"
        private const val ARG_NAME_3 = "name-3"
        private const val ARG_NAME_4 = "name-4"
        private const val ARG_NAME_5 = "name-5"
        private const val ARG_VALUE_TEXT = "value-1"
        private const val ARG_VALUE_BOOL = true
        private const val ARG_VALUE_DECIMAL = "10.5"

        private val EXPRESSION = object : FeelExpression {

            override val text: String
                get() = "a/0"

            override fun evaluate(
                envVars: EnvVars,
                context: Context
            ): ResultK<JsonElement, FeelExpression.EvaluateError> =
                FeelExpression.EvaluateError(this).asFailure()
        }
    }
}
