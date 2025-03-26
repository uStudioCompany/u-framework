package io.github.ustudiocompany.uframework.rulesengine.core.rule.context

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.maybe.matcher.shouldBeNone
import io.github.airflux.commons.types.maybe.matcher.shouldBeSome
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.Step
import io.github.ustudiocompany.uframework.rulesengine.executor.context.update
import io.github.ustudiocompany.uframework.rulesengine.executor.error.ContextError
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class ContextUpdateTest : UnitTest() {

    init {

        "The extension function `update` for `Context` type" - {

            "when the action is PUT" - {
                val action = Step.Result.Action.PUT

                "when the context is not contain the source" - {
                    val context = Context.empty()
                    val value = DataElement.Text(ORIGIN_VALUE)

                    val result = context.update(
                        source = SOURCE,
                        action = action,
                        value = value,
                        merge = { _, _ -> Errors.TestMergerError.asFailure() }
                    )

                    "then call the function should be successful" {
                        result.shouldBeNone()
                    }
                }

                "when the context is contain the source" - {
                    val context = Context(mapOf(SOURCE to DataElement.Text(ORIGIN_VALUE)))
                    val value = DataElement.Text(NEW_VALUE)

                    val result = context.update(
                        source = SOURCE,
                        action = action,
                        value = value,
                        merge = { _, _ -> Errors.TestMergerError.asFailure() }
                    )

                    "then call the function should be failed" {
                        result.shouldBeSome()
                        result.value.shouldBeInstanceOf<ContextError.SourceAlreadyExists>()
                    }
                }
            }

            "when the action is REPLACE" - {
                val action = Step.Result.Action.REPLACE

                "when the context is not contain the source" - {
                    val context = Context.empty()
                    val value = DataElement.Text(ORIGIN_VALUE)

                    val result = context.update(source = SOURCE, action = action, value = value, merge = MERGER)

                    "then call the function should be failed" {
                        result.shouldBeSome()
                        result.value.shouldBeInstanceOf<ContextError.SourceMissing>()
                    }
                }

                "when the context is contain the source" - {
                    val context = Context(mapOf(SOURCE to DataElement.Text(ORIGIN_VALUE)))
                    val value = DataElement.Text(NEW_VALUE)

                    val result = context.update(source = SOURCE, action = action, value = value, merge = MERGER)

                    "then call the function should be successful" {
                        result.shouldBeNone()
                    }

                    "then the context should contain the source" {
                        context.contains(SOURCE) shouldBe true
                    }

                    "then the context should return the new value" {
                        val result = context[SOURCE]
                        result shouldBe DataElement.Text(NEW_VALUE)
                    }
                }
            }

            "when the action is MERGE" - {
                val action = Step.Result.Action.MERGE

                "when the context is not contain the source" - {
                    val context = Context.empty()

                    val newValue = DataElement.Text(ORIGIN_VALUE)
                    val result = context.update(source = SOURCE, action = action, value = newValue, merge = MERGER)

                    "then call the function should be failed" {
                        result.shouldBeSome()
                        result.value.shouldBeInstanceOf<ContextError.SourceMissing>()
                    }
                }

                "when the context is contain the source" - {

                    "when the merge function is successful" - {
                        val context = Context(mapOf(SOURCE to DataElement.Text(ORIGIN_VALUE)))

                        val newValue = DataElement.Text(NEW_VALUE)
                        val result = context.update(source = SOURCE, action = action, value = newValue, merge = MERGER)

                        "then call the function should be successful" {
                            result.shouldBeNone()
                        }

                        "then the context should contain the source" {
                            context.contains(SOURCE) shouldBe true
                        }

                        "then the context should return the merged value" {
                            val result = context[SOURCE]
                            result shouldBe DataElement.Text(ORIGIN_VALUE + NEW_VALUE)
                        }
                    }

                    "when the merge function is failed" - {
                        val context = Context(mapOf(SOURCE to DataElement.Text(ORIGIN_VALUE)))

                        val newValue = DataElement.Text(NEW_VALUE)
                        val result = context.update(source = SOURCE, action = action, value = newValue) { _, _ ->
                            Errors.TestMergerError.asFailure()
                        }

                        "then call the function should be failed" {
                            result.shouldBeSome()
                            result.value.shouldBeInstanceOf<ContextError.Merge>()
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
        private val MERGER = { o: DataElement, n: DataElement ->
            DataElement.Text((o as DataElement.Text).get + (n as DataElement.Text).get)
                .asSuccess()
        }
    }

    private sealed interface Errors : Failure {

        data object TestMergerError : Errors {
            override val code: String = "MERGER_ERROR"
        }
    }
}
