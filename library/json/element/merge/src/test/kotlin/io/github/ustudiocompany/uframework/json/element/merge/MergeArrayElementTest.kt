package io.github.ustudiocompany.uframework.json.element.merge

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.StreamReadFeature
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.json.element.merge.strategy.MergeStrategy
import io.github.ustudiocompany.uframework.json.element.merge.strategy.path.AttributePath
import io.github.ustudiocompany.uframework.json.element.merge.strategy.role.MergeRule
import io.github.ustudiocompany.uframework.json.element.parser.jackson.module.JsonElementModule
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import java.math.BigDecimal

@OptIn(AirfluxTypesExperimental::class)
internal class MergeArrayElementTest : UnitTest() {

    init {

        "The function of the merge `Array` type of elements" - {
            val mapper: JsonMapper = JsonMapper.builder(JsonFactory())
                .enable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION)
                .configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, true)
                .build()
                .apply {
                    registerKotlinModule()
                    registerModules(JsonElementModule())
                }

            "when source is a Array type" - {
                testData()
                    .forEach { data ->

                        data.description - {
                            val dstValue = data.dst.parseToArray(mapper)
                            val srcValue = data.src.parseToArray(mapper)
                            val mergedValue = data.merged.parseToArray(mapper)
                            val result =
                                mergeArray(
                                    dst = dstValue,
                                    src = srcValue,
                                    strategy = data.strategy,
                                    currentPath = data.currentPath
                                )

                            "then it should return a merged value" {
                                result shouldBeSuccess mergedValue
                            }
                        }
                    }
            }

            "when source is a Null type" - {
                val src: JsonElement = NULL_VALUE

                val result = mergeArray(
                    dst = ARRAY_VALUE,
                    src = src,
                    strategy = EMPTY_MERGE_STRATEGY,
                    currentPath = AttributePath(PATH_ATTRIBUTE)
                )

                "then it should return the null value" {
                    result shouldBeSuccess null
                }
            }

            listOf(
                BOOL_VALUE,
                TEXT_VALUE,
                DECIMAL_VALUE,
                STRUCT_VALUE
            ).forEach { src ->

                "when source is a ${src::class.simpleName} type " - {
                    val result = mergeArray(
                        dst = ARRAY_VALUE,
                        src = src,
                        strategy = EMPTY_MERGE_STRATEGY,
                        currentPath = AttributePath(PATH_ATTRIBUTE)
                    )

                    "then it should return a type mismatch error" {
                        result.shouldBeFailure()

                        val expectedError = MergeError.Source.TypeMismatch(
                            path = AttributePath(PATH_ATTRIBUTE),
                            expected = JsonElement.Array::class,
                            actual = src,
                        )
                        result.cause failuresShouldBeEqual expectedError
                    }
                }
            }
        }
    }

    private fun String.parseToArray(mapper: JsonMapper): JsonElement.Array =
        mapper.readValue(this, JsonElement::class.java) as JsonElement.Array

    private fun testData(): List<TestData> = listOf(
        TestData(
            description = "when use the `WholeListMerge` strategy",
            dst = """[1,2]""",
            src = """[3,4,5]""",
            merged = """[3,4,5]""",
            strategy = mapOf(
                AttributePath() to MergeRule.WholeListMerge,
            ),
            currentPath = AttributePath()
        )
    )

    private companion object {
        private const val PATH_ATTRIBUTE = "id"

        private val NULL_VALUE = JsonElement.Null
        private val BOOL_VALUE = JsonElement.Bool(true)
        private val TEXT_VALUE = JsonElement.Text("text")
        private val DECIMAL_VALUE = JsonElement.Decimal(BigDecimal(123))
        private val ARRAY_VALUE = JsonElement.Array(NULL_VALUE, BOOL_VALUE)
        private val STRUCT_VALUE = JsonElement.Struct(PATH_ATTRIBUTE to DECIMAL_VALUE)
        private val EMPTY_MERGE_STRATEGY: MergeStrategy = emptyMap()
    }

    private data class TestData(
        val description: String,
        val dst: String,
        val src: String,
        val merged: String,
        val strategy: MergeStrategy,
        val currentPath: AttributePath
    )
}
