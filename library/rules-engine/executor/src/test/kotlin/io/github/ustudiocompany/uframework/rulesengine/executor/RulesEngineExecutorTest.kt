package io.github.ustudiocompany.uframework.rulesengine.executor

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Comparator.EQ
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Rule
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Rules
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.rulesengine.core.rule.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.Step
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.Steps
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.ValidationStep
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.net.URI

internal class RulesEngineExecutorTest : UnitTest() {

    init {
        "the RulesEngineExecutor" - {

            "test1" {
                val executor = RulesEngineExecutor(TestDataProvider(), TestMerger())
                val context = Context.empty()

                val rules = Rules(
                    listOf(
                        Rule(
                            predicate = null,
                            steps = Steps(
                                listOf(
                                    ValidationStep(
                                        predicate = null,
                                        target = Value.Literal(fact = DataElement.Text("test")),
                                        compareWith = Value.Literal(fact = DataElement.Text("test")),
                                        comparator = EQ,
                                        errorCode = Step.ErrorCode("err-1")
                                    )
                                )
                            )
                        )
                    )
                )

                val result = executor.execute(context, rules)
                result.shouldBeSuccess()
                result.value shouldBe null
            }

            "test2" {
                val executor = RulesEngineExecutor(TestDataProvider(), TestMerger())
                val context = Context(mapOf(Source("test") to DataElement.Text("test")))
                val rules = Rules(
                    listOf(
                        Rule(
                            predicate = null,
                            steps = Steps(
                                listOf(
                                    ValidationStep(
                                        predicate = null,
                                        target = Value.Literal(fact = DataElement.Text("test")),
                                        compareWith = Value.Literal(fact = DataElement.Text("test2")),
                                        comparator = EQ,
                                        errorCode = Step.ErrorCode("err-1")
                                    )
                                )
                            )
                        )
                    )
                )

                val result = executor.execute(context, rules)
                result.shouldBeSuccess()
                result.value.shouldBeInstanceOf<Step.ErrorCode>().get shouldBe "err-1"
            }
        }
    }

    private class TestMerger : Merger {
        override fun merge(origin: DataElement, target: DataElement): ResultK<DataElement, Failure> {
            val result = (origin as DataElement.Text).get + (target as DataElement.Text).get
            return DataElement.Text(result).asSuccess()
        }
    }

    private class TestDataProvider : DataProvider {
        override fun call(uri: URI, headers: Map<String, DataElement>): ResultK<DataElement, Failure> =
            DataElement.Text("test").asSuccess()
    }
}
