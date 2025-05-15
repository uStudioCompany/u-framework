package io.github.ustudiocompany.uframework.rulesengine.core.context

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.maybe.matcher.shouldBeNone
import io.github.airflux.commons.types.maybe.matcher.shouldContainSomeInstance
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.StepResult
import io.github.ustudiocompany.uframework.rulesengine.executor.Merger
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class ContextUpdateTest : UnitTest() {

    init {

        "The extension function `update` for `Context` type" - {

            "when the action is PUT" - {
                val action = StepResult.Action.Put

                "when the context is not contain the source" - {
                    val context = Context.empty()
                    val value = JsonElement.Text(ORIGIN_VALUE)

                    val result = context.update(
                        source = SOURCE,
                        action = action,
                        value = value,
                        merge = { _, _, _ -> Merger.Error().asFailure() }
                    )

                    "then call the function should be successful" {
                        result.shouldBeNone()
                    }
                }

                "when the context is contain the source" - {
                    val context = Context(mapOf(SOURCE to JsonElement.Text(ORIGIN_VALUE)))
                    val value = JsonElement.Text(NEW_VALUE)

                    val result = context.update(
                        source = SOURCE,
                        action = action,
                        value = value,
                        merge = { _, _, _ -> Merger.Error().asFailure() }
                    )

                    "then call the function should be failed" {
                        result.shouldContainSomeInstance()
                            .shouldBeInstanceOf<UpdateContextErrors.AddingData>()
                    }
                }
            }

            "when the action is REPLACE" - {
                val action = StepResult.Action.Replace

                "when the context is not contain the source" - {
                    val context = Context.empty()
                    val value = JsonElement.Text(ORIGIN_VALUE)

                    val result = context.update(source = SOURCE, action = action, value = value, merge = MERGER)

                    "then call the function should be failed" {
                        result.shouldContainSomeInstance()
                            .shouldBeInstanceOf<UpdateContextErrors.ReplacingData>()
                    }
                }

                "when the context is contain the source" - {
                    val context = Context(mapOf(SOURCE to JsonElement.Text(ORIGIN_VALUE)))
                    val value = JsonElement.Text(NEW_VALUE)

                    val result = context.update(source = SOURCE, action = action, value = value, merge = MERGER)

                    "then call the function should be successful" {
                        result.shouldBeNone()
                    }

                    "then the context should contain the source" {
                        context.contains(SOURCE) shouldBe true
                    }

                    "then the context should return the new value" {
                        val result = context[SOURCE]
                        result shouldBe JsonElement.Text(NEW_VALUE)
                    }
                }
            }

            "when the action is MERGE" - {
                val action = StepResult.Action.Merge(strategyCode = MERGE_STRATEGY_CODE)

                "when the context is not contain the source" - {
                    val context = Context.empty()

                    val newValue = JsonElement.Text(ORIGIN_VALUE)
                    val result = context.update(source = SOURCE, action = action, value = newValue, merge = MERGER)

                    "then call the function should be failed" {
                        result.shouldContainSomeInstance()
                            .shouldBeInstanceOf<UpdateContextErrors.MergingData>()
                    }
                }

                "when the context is contain the source" - {

                    "when the merge function is successful" - {
                        val context = Context(mapOf(SOURCE to JsonElement.Text(ORIGIN_VALUE)))

                        val newValue = JsonElement.Text(NEW_VALUE)
                        val result = context.update(source = SOURCE, action = action, value = newValue, merge = MERGER)

                        "then call the function should be successful" {
                            result.shouldBeNone()
                        }

                        "then the context should contain the source" {
                            context.contains(SOURCE) shouldBe true
                        }

                        "then the context should return the merged value" {
                            val result = context[SOURCE]
                            result shouldBe JsonElement.Text(ORIGIN_VALUE + NEW_VALUE)
                        }
                    }

                    "when the merge function is failed" - {
                        val context = Context(mapOf(SOURCE to JsonElement.Text(ORIGIN_VALUE)))

                        val newValue = JsonElement.Text(NEW_VALUE)
                        val result = context.update(source = SOURCE, action = action, value = newValue) { _, _, _ ->
                            Merger.Error().asFailure()
                        }

                        "then call the function should be failed" {
                            result.shouldContainSomeInstance()
                                .shouldBeInstanceOf<UpdateContextErrors.MergingData>()
                        }
                    }
                }
            }
        }
    }

    private companion object {
        private val SOURCE = Source("source")
        private const val ORIGIN_VALUE = "value-1"
        private const val NEW_VALUE = "value-2"
        private val MERGE_STRATEGY_CODE = StepResult.Action.Merge.StrategyCode("merge-strategy-code")
        private val MERGER = { c: StepResult.Action.Merge.StrategyCode, o: JsonElement, n: JsonElement ->
            JsonElement.Text((o as JsonElement.Text).get + (n as JsonElement.Text).get)
                .asSuccess()
        }
    }
}
