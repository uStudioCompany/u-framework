package io.github.ustudiocompany.uframework.json.element.merge

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.engine.merge.path.AttributePath
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import java.math.BigDecimal

@OptIn(AirfluxTypesExperimental::class)
internal class MergeTextElementTest : UnitTest() {

    init {

        "The function of the merge `Text` type of elements" - {

            "when source is a Text type" - {
                val src: JsonElement = TEXT_VALUE

                val result = mergeText(src = src, currentPath = AttributePath(PATH_ATTRIBUTE))

                "then it should return the source element" {
                    result shouldBeSuccess src
                }
            }

            "when source is a Null type" - {
                val src: JsonElement = NULL_VALUE

                val result = mergeText(src = src, currentPath = AttributePath(PATH_ATTRIBUTE))

                "then it should return the null value" {
                    result shouldBeSuccess null
                }
            }

            listOf(
                BOOL_VALUE,
                DECIMAL_VALUE,
                ARRAY_VALUE,
                STRUCT_VALUE
            ).forEach { src ->

                "when source is a ${src::class.simpleName} type " - {
                    val result = mergeText(src = src, currentPath = AttributePath(PATH_ATTRIBUTE))

                    "then it should return a type mismatch error" {
                        result.shouldBeFailure()

                        val expectedError = MergeError.Source.TypeMismatch(
                            path = AttributePath(PATH_ATTRIBUTE),
                            expected = JsonElement.Text::class,
                            actual = src,
                        )
                        result.cause failuresShouldBeEqual expectedError
                    }
                }
            }
        }
    }

    private companion object {
        private const val PATH_ATTRIBUTE = "id"
        private val NULL_VALUE = JsonElement.Null
        private val BOOL_VALUE = JsonElement.Bool(true)
        private val TEXT_VALUE = JsonElement.Text("text")
        private val DECIMAL_VALUE = JsonElement.Decimal(BigDecimal(123))
        private val ARRAY_VALUE = JsonElement.Array(NULL_VALUE, BOOL_VALUE)
        private val STRUCT_VALUE = JsonElement.Struct(PATH_ATTRIBUTE to DECIMAL_VALUE)
    }
}
