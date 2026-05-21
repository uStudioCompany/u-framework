package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.maybe.matcher.shouldBeNone
import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.env.envVarsOf
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.Condition
import io.github.ustudiocompany.uframework.rulesengine.executor.Merger
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe

@OptIn(AirfluxTypesExperimental::class)
internal class DataBuildStepExecutorTest : UnitTest() {

    init {

        "The data step executor" - {

            "then the executor should perform the step" - {
                val envVars = envVarsOf()
                val context = Context.empty()
                val step = createStep()
                val result = step.execute(envVars, context, TestMerger())

                "then the executor should return a success result" {
                    result.shouldBeNone()
                }

                "then the context should contain the generated data" {
                    val result = context.getOrNull(SOURCE)
                    result shouldBe EXPECTED_DATA
                }
            }
        }
    }

    private companion object {
        private val STEP_ID = StepId("step-1")
        private const val ID_DATA_KEY = "id"
        private const val ID_DATA_VALUE = "0000-0000-0000-0000"

        private val SOURCE = Source("output")

        private val EXPECTED_DATA = JsonElement.Struct(ID_DATA_KEY to JsonElement.Text(ID_DATA_VALUE))

        private fun createStep() =
            DataBuildStep(
                id = STEP_ID,
                condition = Condition.NONE,
                dataSchema = DataSchema.Struct(
                    properties = listOf(
                        DataSchema.Property.Element(
                            name = ID_DATA_KEY,
                            value = Value.Literal(fact = JsonElement.Text(ID_DATA_VALUE))
                        )
                    )
                ),
                result = StepResult(
                    source = SOURCE,
                    action = StepResult.Action.Put
                )
            )
    }

    private class TestMerger : Merger {
        override fun merge(
            strategyCode: StepResult.Action.Merge.StrategyCode,
            dst: JsonElement,
            src: JsonElement
        ): ResultK<JsonElement, Merger.Error> {
            error("Not implemented")
        }
    }
}
