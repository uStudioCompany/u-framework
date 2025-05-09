package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldContainFailureInstance
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.feel.FeelExpression
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.rulesengine.executor.DataProvider
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class ArgsBuilderTest : UnitTest() {

    init {

        "The args builder" - {

            "when compute value of arg is successful" - {
                val args = Args(
                    listOf(
                        Arg(
                            name = ARG_NAME_1,
                            value = Value.Literal(fact = JsonElement.Text(ARG_VALUE_1))
                        )
                    )
                )
                val result = args.build(context = CONTEXT)

                "then the function should return the build args" {
                    result shouldBeSuccess listOf(
                        DataProvider.Arg(
                            name = ARG_NAME_1,
                            value = "\"" + ARG_VALUE_1 + "\""
                        )
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
                val result = args.build(context = CONTEXT)

                "then the function should return the error" {
                    result.shouldContainFailureInstance()
                        .shouldBeInstanceOf<DataProviderArgsErrors.ArgValueBuild>()
                }
            }
        }
    }

    private companion object {
        private val CONTEXT = Context.empty()

        private const val ARG_NAME_1 = "name-1"
        private const val ARG_VALUE_1 = "value-1"

        private val EXPRESSION = object : FeelExpression {

            override val text: String
                get() = "a/0"

            override fun evaluate(
                context: Context
            ): ResultK<JsonElement, FeelExpression.EvaluateError> =
                FeelExpression.EvaluateError(this).asFailure()
        }
    }
}
