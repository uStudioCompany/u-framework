package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.orThrow
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.path.Path
import io.github.ustudiocompany.uframework.rulesengine.core.path.defaultPathCompiler
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.rulesengine.core.rule.comparator.Comparator.EQ
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.Condition
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.Predicate
import io.github.ustudiocompany.uframework.rulesengine.core.rule.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.rule.header.Headers
import io.github.ustudiocompany.uframework.rulesengine.core.rule.uri.UriTemplate
import io.github.ustudiocompany.uframework.rulesengine.core.rule.uri.UriTemplateParams
import io.github.ustudiocompany.uframework.rulesengine.executor.DataProvider
import io.github.ustudiocompany.uframework.rulesengine.executor.Merger
import io.github.ustudiocompany.uframework.rulesengine.executor.error.CallStepError
import io.github.ustudiocompany.uframework.rulesengine.executor.error.ContextError
import io.github.ustudiocompany.uframework.rulesengine.executor.error.UriBuilderError
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

internal class CallStepExecutorTest : UnitTest() {

    init {

        "The call step executor" - {

            "when condition is missing" - {
                val condition: Condition? = null

                "when execution of the step is fail" - {

                    "when uri template is invalid" - {
                        val step = CallStep(
                            condition = condition,
                            uri = INVALID_URI_TEMPLATE,
                            params = UriTemplateParams(
                                ID_PARAM_NAME to Value.Literal(
                                    fact = DataElement.Text(
                                        ID_PARAM_VALUE
                                    )
                                )
                            ),
                            headers = Headers(emptyList()),
                            result = Step.Result(
                                source = RESULT_SOURCE,
                                action = Step.Result.Action.PUT
                            )
                        )

                        "then the executor should return an error result" {
                            val result = step.execute(
                                context = CONTEXT,
                                provider = DataProvider { CALL_RESULT.asSuccess() },
                                merger = Merger { origin, _ -> origin.asSuccess() }
                            )
                            result.shouldBeFailure()
                            result.cause.shouldBeInstanceOf<UriBuilderError.InvalidUriTemplate>()
                        }
                    }

                    "when an error computing some parameter for uri-template" - {
                        val step = CallStep(
                            condition = condition,
                            uri = URI_TEMPLATE,
                            params = UriTemplateParams(
                                ID_PARAM_NAME to Value.Reference(source = PARAM_SOURCE, path = "$.id".compile())
                            ),
                            headers = Headers(emptyList()),
                            result = Step.Result(
                                source = RESULT_SOURCE,
                                action = Step.Result.Action.PUT
                            )
                        )

                        "then the executor should return an error result" {
                            val result = step.execute(
                                context = CONTEXT,
                                provider = DataProvider { CALL_RESULT.asSuccess() },
                                merger = Merger { origin, _ -> origin.asSuccess() }
                            )
                            result.shouldBeFailure()
                            result.cause.shouldBeInstanceOf<ContextError.SourceMissing>()
                        }
                    }

                    "when some param is missing for building a URI by a template" - {
                        val step = CallStep(
                            condition = condition,
                            uri = URI_TEMPLATE,
                            params = UriTemplateParams(emptyList()),
                            headers = Headers(emptyList()),
                            result = Step.Result(
                                source = RESULT_SOURCE,
                                action = Step.Result.Action.PUT
                            )
                        )

                        "then the executor should return an error result" {
                            val result = step.execute(
                                context = CONTEXT,
                                provider = DataProvider { CALL_RESULT.asSuccess() },
                                merger = Merger { origin, _ -> origin.asSuccess() }
                            )
                            result.shouldBeFailure()
                            result.cause.shouldBeInstanceOf<UriBuilderError.ParamMissing>()
                        }
                    }

                    "when an error computing some header" - {
                        val step = CallStep(
                            condition = condition,
                            uri = URI_TEMPLATE,
                            params = UriTemplateParams(
                                ID_PARAM_NAME to Value.Literal(
                                    fact = DataElement.Text(
                                        ID_PARAM_VALUE
                                    )
                                )
                            ),
                            headers = Headers(
                                ID_PARAM_NAME to Value.Reference(source = PARAM_SOURCE, path = "$.id".compile())
                            ),
                            result = Step.Result(
                                source = RESULT_SOURCE,
                                action = Step.Result.Action.PUT
                            )
                        )

                        "then the executor should return an error result" {
                            val result = step.execute(
                                context = CONTEXT,
                                provider = DataProvider { CALL_RESULT.asSuccess() },
                                merger = Merger { origin, _ -> origin.asSuccess() }
                            )
                            result.shouldBeFailure()
                            result.cause.shouldBeInstanceOf<ContextError.SourceMissing>()
                        }
                    }

                    "when an external call error" - {
                        val step = CallStep(
                            condition = condition,
                            uri = URI_TEMPLATE,
                            params = UriTemplateParams(
                                ID_PARAM_NAME to Value.Literal(
                                    fact = DataElement.Text(
                                        ID_PARAM_VALUE
                                    )
                                )
                            ),
                            headers = Headers(emptyList()),
                            result = Step.Result(
                                source = RESULT_SOURCE,
                                action = Step.Result.Action.PUT
                            )
                        )

                        "then the executor should return an error result" {
                            val result = step.execute(
                                context = CONTEXT,
                                provider = DataProvider { Errors.TestDataProviderError.asFailure() },
                                merger = Merger { origin, _ -> origin.asSuccess() }
                            )
                            result.shouldBeFailure()
                            result.cause.shouldBeInstanceOf<CallStepError>()
                        }
                    }

                    "when an error of merging" - {
                        val context = Context(mapOf(RESULT_SOURCE to DataElement.Text(ORIGIN_VALUE)))
                        val step = CallStep(
                            condition = condition,
                            uri = URI_TEMPLATE,
                            params = UriTemplateParams(
                                ID_PARAM_NAME to Value.Literal(
                                    fact = DataElement.Text(
                                        ID_PARAM_VALUE
                                    )
                                )
                            ),
                            headers = Headers(emptyList()),
                            result = Step.Result(
                                source = RESULT_SOURCE,
                                action = Step.Result.Action.MERGE
                            )
                        )

                        val result = step.execute(
                            context = context,
                            provider = DataProvider { CALL_RESULT.asSuccess() },
                            merger = Merger { _, _ -> Errors.TestMergerError.asFailure() }
                        )

                        "then the executor should return an error result" {
                            result.shouldBeFailure()
                            result.cause.shouldBeInstanceOf<ContextError.Merge>()
                        }
                    }
                }

                "when execution of the step is successful" - {
                    val context = Context.empty()
                    val step = CallStep(
                        condition = condition,
                        uri = URI_TEMPLATE,
                        params = UriTemplateParams(ID_PARAM_NAME to Value.Literal(fact = DataElement.Text(ID_PARAM_VALUE))),
                        headers = Headers(emptyList()),
                        result = Step.Result(
                            source = RESULT_SOURCE,
                            action = Step.Result.Action.PUT
                        )
                    )

                    val result = step.execute(
                        context = context,
                        provider = DataProvider { CALL_RESULT.asSuccess() },
                        merger = Merger { origin, _ -> origin.asSuccess() }
                    )

                    "then the executor should return a success result" {
                        result.shouldBeSuccess()
                        result.value.shouldBeNull()
                    }

                    "then the context should be updated" {
                        val result = context[RESULT_SOURCE]
                        result.shouldBeSuccess()
                        result.value shouldBe CALL_RESULT
                    }
                }
            }

            "when condition is present" - {

                "when condition is satisfied" - {
                    val condition: Condition = satisfiedCondition()

                    "when execution of the step is successful" - {
                        val context = Context.empty()
                        val step = CallStep(
                            condition = condition,
                            uri = URI_TEMPLATE,
                            params = UriTemplateParams(
                                ID_PARAM_NAME to Value.Literal(
                                    fact = DataElement.Text(
                                        ID_PARAM_VALUE
                                    )
                                )
                            ),
                            headers = Headers(emptyList()),
                            result = Step.Result(
                                source = RESULT_SOURCE,
                                action = Step.Result.Action.PUT
                            )
                        )

                        val result = step.execute(
                            context = context,
                            provider = DataProvider { CALL_RESULT.asSuccess() },
                            merger = Merger { origin, _ -> origin.asSuccess() }
                        )

                        "then the executor should return a success result" {
                            result.shouldBeSuccess()
                            result.value.shouldBeNull()
                        }

                        "then the context should be updated" {
                            val result = context[RESULT_SOURCE]
                            result.shouldBeSuccess()
                            result.value shouldBe CALL_RESULT
                        }
                    }
                }

                "when condition is not satisfied" - {
                    val condition: Condition = notSatisfiedCondition()

                    "then the step is not performed" {
                        val step = CallStep(
                            condition = condition,
                            uri = INVALID_URI_TEMPLATE,
                            params = UriTemplateParams(),
                            headers = Headers(emptyList()),
                            result = Step.Result(
                                source = RESULT_SOURCE,
                                action = Step.Result.Action.PUT
                            )
                        )

                        val result = step.execute(
                            context = CONTEXT,
                            provider = DataProvider { Errors.TestDataProviderError.asFailure() },
                            merger = Merger { _, _ -> Errors.TestMergerError.asFailure() }
                        )
                        result.shouldBeSuccess()
                        result.value.shouldBeNull()
                    }
                }
            }
        }
    }

    private companion object {
        private val CONTEXT = Context.empty()
        private const val ORIGIN_VALUE = "origin"

        private val TEXT_VALUE_1 = DataElement.Text("value-1")
        private val TEXT_VALUE_2 = DataElement.Text("value-2")

        private val URI_TEMPLATE = UriTemplate("http://example.com/users/{id}")
        private val INVALID_URI_TEMPLATE = UriTemplate("http[:]//example.com/users/{id}")

        private const val ID_PARAM_NAME = "id"
        private const val ID_PARAM_VALUE = "1"

        private val PARAM_SOURCE = Source("input")
        private val RESULT_SOURCE = Source("output")

        private val CALL_RESULT = DataElement.Text("data")

        private val PATH_COMPILER = defaultPathCompiler(ObjectMapper())
        private fun String.compile(): Path = PATH_COMPILER.compile(this).orThrow { error(it.description) }

        private fun satisfiedCondition() = Condition(
            listOf(
                Predicate(
                    target = Value.Literal(fact = TEXT_VALUE_1),
                    compareWith = Value.Literal(fact = TEXT_VALUE_1),
                    comparator = EQ
                )
            )
        )

        private fun notSatisfiedCondition() = Condition(
            listOf(
                Predicate(
                    target = Value.Literal(fact = TEXT_VALUE_1),
                    compareWith = Value.Literal(fact = TEXT_VALUE_2),
                    comparator = EQ
                )
            )
        )
    }

    private sealed interface Errors : Failure {

        data object TestDataProviderError : Errors {
            override val code: String = "DATA_PROVIDER_ERROR"
        }

        data object TestMergerError : Errors {
            override val code: String = "MERGER_ERROR"
        }
    }
}
