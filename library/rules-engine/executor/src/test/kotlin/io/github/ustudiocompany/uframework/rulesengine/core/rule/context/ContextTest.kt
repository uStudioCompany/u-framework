package io.github.ustudiocompany.uframework.rulesengine.core.rule.context

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.Step.Result.Action.MERGE
import io.github.ustudiocompany.uframework.rulesengine.executor.context.Context
import io.github.ustudiocompany.uframework.rulesengine.executor.context.update
import io.github.ustudiocompany.uframework.rulesengine.executor.error.ContextError
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class ContextTest : UnitTest() {

    init {

        "The `Context` type" - {

            "when context does not contain the item" - {
                val context = Context.empty()

                "then the context should not contain the source" {
                    context.contains(SOURCE) shouldBe false
                }

                "then the context should return an error" {
                    val result = context[SOURCE]
                    result.shouldBeFailure()
                    result.cause.shouldBeInstanceOf<ContextError.SourceMissing>()
                }
            }

            "when adding a new item" - {
                val context = Context.empty()
                val value = DataElement.Text(ORIGIN_VALUE)
                val result = context.add(SOURCE, value)

                "then the operation should be successful" {
                    result.shouldBeSuccess()
                }

                "then the context should contain the source" {
                    context.contains(SOURCE) shouldBe true
                }

                "then the context should return the value" {
                    val result = context[SOURCE]
                    result.shouldBeSuccess()
                    result.value shouldBe value
                }
            }

            "when adding already existing item" - {
                val origin = DataElement.Text(ORIGIN_VALUE)
                val context = Context(mapOf(SOURCE to origin))
                val newValue = DataElement.Text(NEW_VALUE)
                val result = context.add(SOURCE, newValue)

                "then the operation should return an error" {
                    result.shouldBeFailure()
                    result.cause.shouldBeInstanceOf<ContextError.SourceAlreadyExists>()
                }
            }

            "when replacing an item" - {

                "when the item is missing" - {
                    val context = Context.empty()
                    val newValue = DataElement.Text(NEW_VALUE)
                    val result = context.replace(SOURCE, newValue)

                    "then the operation should return an error" {
                        result.shouldBeFailure()
                        result.cause.shouldBeInstanceOf<ContextError.SourceMissing>()
                    }
                }

                "when the item is present" - {
                    val origin = DataElement.Text(ORIGIN_VALUE)
                    val context = Context(mapOf(SOURCE to origin))
                    val newValue = DataElement.Text(NEW_VALUE)
                    val result = context.replace(SOURCE, newValue)

                    "then the operation should be successful" {
                        result.shouldBeSuccess()
                    }

                    "then the context should contain the source" {
                        context.contains(SOURCE) shouldBe true
                    }

                    "then the context should return the new value" {
                        val result = context[SOURCE]
                        result.shouldBeSuccess()
                        result.value shouldBe newValue
                    }
                }
            }

            "when merging an item" - {

                "when the item is missing" - {
                    val context = Context.empty()
                    val newValue = DataElement.Text(NEW_VALUE)
                    val result = context.merge(SOURCE, newValue, MERGER)

                    "then the operation should return an error" {
                        result.shouldBeFailure()
                        result.cause.shouldBeInstanceOf<ContextError.SourceMissing>()
                    }
                }

                "when the item is present" - {

                    "when the merge function is successful" - {
                        val context = Context(mapOf(SOURCE to DataElement.Text(ORIGIN_VALUE)))
                        val value = DataElement.Text(NEW_VALUE)

                        val result = context.update(
                            source = SOURCE,
                            action = MERGE,
                            value = value,
                            merge = MERGER
                        )

                        "then call the function should be successful" {
                            result.shouldBeSuccess()
                        }

                        "then the context should contain the source" {
                            context.contains(SOURCE) shouldBe true
                        }

                        "then the context should return the value" {
                            val result = context[SOURCE]
                            result.shouldBeSuccess()
                            result.value shouldBe DataElement.Text(ORIGIN_VALUE + NEW_VALUE)
                        }
                    }

                    "when the merge function is failed" - {
                        val context = Context(mapOf(SOURCE to DataElement.Text(ORIGIN_VALUE)))
                        val newValue = DataElement.Text(NEW_VALUE)

                        val result = context.update(source = SOURCE, action = MERGE, value = newValue) { _, _ ->
                            Errors.TestMergerError.asFailure()
                        }

                        "then call the function should be failed" {
                            result.shouldBeFailure()
                            result.cause.shouldBeInstanceOf<ContextError.Merge>()
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
