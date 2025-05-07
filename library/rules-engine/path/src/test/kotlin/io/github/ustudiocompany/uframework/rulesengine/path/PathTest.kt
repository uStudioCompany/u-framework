package io.github.ustudiocompany.uframework.rulesengine.path

import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.Option
import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldContainFailureInstance
import io.github.airflux.commons.types.resultk.orThrow
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.path.Path
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class PathTest : UnitTest() {

    init {

        "The `Path` type" - {

            "the `searchIn` function" - {

                "when option `SUPPRESS_EXCEPTIONS` is enabled" - {
                    val pathEngine = defaultPathParser(
                        defaultPathParserConfiguration(ObjectMapper(), Option.SUPPRESS_EXCEPTIONS)
                    )

                    "when the data contains a value by path" - {
                        val path = "$.id".parse(pathEngine)

                        "then the function should return a value" {
                            val result = path.searchIn(DATA)
                            result.shouldBeSuccess()
                            result.value shouldBe DataElement.Text(DATA_VALUE_1)
                        }
                    }

                    "the data does not contain a value by path" - {
                        val path = "$.scheme".parse(pathEngine)

                        "then the function should return the null value" {
                            val result = path.searchIn(DATA)
                            result.shouldBeSuccess()
                            result.value.shouldBeNull()
                        }
                    }
                }

                "when option `SUPPRESS_EXCEPTIONS` is not enabled" - {
                    val pathEngine = defaultPathParser(ObjectMapper())

                    "when the data contains a value by path" - {
                        val path = "$.id".parse(pathEngine)

                        "then the function should return a value" {
                            val result = path.searchIn(DATA)
                            result.shouldBeSuccess()
                            result.value shouldBe DataElement.Text(DATA_VALUE_1)
                        }
                    }

                    "the data does not contain a value by path" - {
                        val path = "$.scheme".parse(pathEngine)

                        "then the function should return the null value" {
                            val result = path.searchIn(DATA)
                            result.shouldBeSuccess()
                            result.value.shouldBeNull()
                        }
                    }
                }

                "when occurs an error during the search" - {
                    val pathEngine = defaultPathParser(ObjectMapper(), Option.AS_PATH_LIST)
                    val path = "$.id.sum()".parse(pathEngine)

                    "then the function should return an error" {
                        val result = path.searchIn(DATA)
                        result.shouldContainFailureInstance()
                            .shouldBeInstanceOf<Path.SearchError>()
                    }
                }
            }
        }
    }

    private companion object {
        private const val DATA_KEY_1 = "id"
        private const val DATA_VALUE_1 = "data-1"
        private val DATA = DataElement.Struct(
            DATA_KEY_1 to DataElement.Text(DATA_VALUE_1)
        )

        private fun String.parse(parser: PathParser): Path =
            parser.parse(this).orThrow { error(it.description) }
    }
}
