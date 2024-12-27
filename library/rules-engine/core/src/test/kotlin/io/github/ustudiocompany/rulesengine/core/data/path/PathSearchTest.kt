package io.github.ustudiocompany.rulesengine.core.data.path

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.orThrow
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.data.path.Path
import io.github.ustudiocompany.uframework.rulesengine.core.data.path.Path.Errors
import io.github.ustudiocompany.uframework.rulesengine.core.data.path.defaultPathConfiguration
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

internal class PathSearchTest : UnitTest() {

    init {

        "The `search` function of the `Path` type" - {

            "when the data contains a value by path" - {
                val path = "$.id".compile()

                "then the function should return a value" {
                    val result = path.search(DATA)
                    result.shouldBeSuccess()
                    result.value shouldBe DataElement.Text(DATA_VALUE_1)
                }
            }

            "the data does not contain a value by path" - {
                val path = "$.scheme".compile()

                "then the function should return an error" {
                    val result = path.search(DATA)
                    result.shouldBeFailure()
                    result.cause.shouldBeInstanceOf<Errors.Search>()
                }
            }
        }
    }

    private companion object {
        private val PATH_COMPILER = Path.Compiler(defaultPathConfiguration(ObjectMapper()))
        private const val DATA_KEY_1 = "id"
        private const val DATA_VALUE_1 = "data-1"
        private val DATA = DataElement.Struct(
            mutableMapOf(DATA_KEY_1 to DataElement.Text(DATA_VALUE_1))
        )

        private fun String.compile(): Path = PATH_COMPILER.compile(this).orThrow { error(it.description) }
    }
}
