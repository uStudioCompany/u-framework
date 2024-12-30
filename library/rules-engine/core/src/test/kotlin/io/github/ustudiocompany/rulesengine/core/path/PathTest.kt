package io.github.ustudiocompany.rulesengine.core.path

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.orThrow
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.path.Path
import io.github.ustudiocompany.uframework.rulesengine.core.path.Path.Errors
import io.github.ustudiocompany.uframework.rulesengine.core.path.defaultPathCompiler
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

internal class PathTest : UnitTest() {

    init {

        "The Path type" - {

            "the `search` function" - {

                "when the data contains a value by path" - {
                    val path = VALID_PATH

                    "then the function should return a value" {
                        val result = path.search(DATA)
                        result.shouldBeSuccess()
                        result.value shouldBe DataElement.Text(DATA_VALUE_1)
                    }
                }

                "the data does not contain a value by path" - {
                    val path = INVALID_PATH

                    "then the function should return an error" {
                        val result = path.search(DATA)
                        result.shouldBeFailure()
                        result.cause.shouldBeInstanceOf<Errors.Search>()
                    }
                }
            }

            "the `searchOrNull` function" - {

                "when the data contains a value by path" - {
                    val path = VALID_PATH

                    "then the function should return a value" {
                        val result = path.searchOrNull(DATA)
                        result.shouldBeSuccess()
                        result.value shouldBe DataElement.Text(DATA_VALUE_1)
                    }
                }

                "the data does not contain a value by path" - {
                    val path = INVALID_PATH

                    "then the function should return the null value" {
                        val result = path.searchOrNull(DATA)
                        result.shouldBeSuccess()
                        result.value.shouldBeNull()
                    }
                }
            }
        }
    }

    private companion object {
        private val PATH_COMPILER = defaultPathCompiler(ObjectMapper())
        private const val DATA_KEY_1 = "id"
        private const val DATA_VALUE_1 = "data-1"
        private val DATA = DataElement.Struct(
            mutableMapOf(DATA_KEY_1 to DataElement.Text(DATA_VALUE_1))
        )

        private val VALID_PATH = "$.id".compile()
        private val INVALID_PATH = "$.scheme".compile()

        private fun String.compile(): Path = PATH_COMPILER.compile(this).orThrow { error(it.description) }
    }
}
