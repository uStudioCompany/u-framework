package io.github.ustudiocompany.uframework.rulesengine.executor

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jayway.jsonpath.Configuration
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Comparator.EQ
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Rule
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Rules
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Step
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.rulesengine.executor.error.RequirementError
import io.github.ustudiocompany.uframework.rulesengine.executor.path.DataElementMappingProvider
import io.github.ustudiocompany.uframework.rulesengine.executor.path.DataElementProvider
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.net.URI

internal class RulesEngineExecutorTest : UnitTest() {

    init {
        "the RulesEngineExecutor" - {

            "test1" {
                val executor = RulesEngineExecutor(CONFIG, TestDataProvider(), TestMerger())
                val context = Context(mapOf(Source("test") to DataElement.Text("test")))

                val rules = Rules(
                    listOf(
                        Rule(
                            predicate = null,
                            steps = listOf(
                                Step.Requirement(
                                    predicate = null,
                                    target = Value.Literal(fact = DataElement.Text("test")),
                                    compareWith = Value.Literal(fact = DataElement.Text("test")),
                                    comparator = EQ,
                                    errorCode = "err-1"
                                )
                            )
                        )
                    )
                )

                val result = executor.execute(context, rules)
                result.shouldBeSuccess()
            }

            "test2" {
                val executor = RulesEngineExecutor(CONFIG, TestDataProvider(), TestMerger())
                val context = Context(mapOf(Source("test") to DataElement.Text("test")))
                val rules = Rules(
                    listOf(
                        Rule(
                            predicate = null,
                            steps = listOf(
                                Step.Requirement(
                                    predicate = null,
                                    target = Value.Literal(fact = DataElement.Text("test")),
                                    compareWith = Value.Literal(fact = DataElement.Text("test2")),
                                    comparator = EQ,
                                    errorCode = "err-1"
                                )
                            )
                        )
                    )
                )

                val result = executor.execute(context, rules)
                result.shouldBeFailure()
                val p = result.cause.shouldBeInstanceOf<RequirementError>()
                p.code shouldBe "err-1"
            }
        }
    }

    companion object {
        private val MAPPER = jacksonObjectMapper()
        private val CONFIG: Configuration = Configuration.builder()
            .jsonProvider(DataElementProvider(MAPPER, MAPPER.reader().forType(DataElement::class.java)))
            .mappingProvider(DataElementMappingProvider(MAPPER))
            .build()
    }

    private class TestMerger : Merger {
        override fun merge(origin: DataElement, target: DataElement): ResultK<DataElement, Merger.Error> {
            val result = (origin as DataElement.Text).get + (target as DataElement.Text).get
            return DataElement.Text(result).asSuccess()
        }
    }

    private class TestDataProvider : DataProvider {
        override fun call(uri: URI, headers: Map<String, DataElement>): ResultK<DataElement, DataProvider.Error> =
            DataElement.Text("test").asSuccess()
    }
}
