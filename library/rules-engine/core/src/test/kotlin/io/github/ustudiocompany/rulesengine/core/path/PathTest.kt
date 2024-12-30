package io.github.ustudiocompany.rulesengine.core.path

import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.Option
import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.orThrow
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.path.Path
import io.github.ustudiocompany.uframework.rulesengine.core.path.defaultPathCompiler
import io.github.ustudiocompany.uframework.rulesengine.core.path.defaultPathCompilerConfiguration
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

internal class PathTest : UnitTest() {

    init {

        "The Path type" - {

            "the `search` function" - {

                "when option `SUPPRESS_EXCEPTIONS` is enabled" - {
                    val compiler = Path.Compiler(
                        defaultPathCompilerConfiguration(ObjectMapper(), Option.SUPPRESS_EXCEPTIONS)
                    )

                    "when the data contains a value by path" - {
                        val path = "$.id".compile(compiler)

                        "then the function should return a value" {
                            val result = path.search(DATA)
                            result.shouldBeSuccess()
                            result.value shouldBe DataElement.Text(DATA_VALUE_1)
                        }
                    }

                    "the data does not contain a value by path" - {
                        val path = "$.scheme".compile(compiler)

                        "then the function should return the null value" {
                            val result = path.search(DATA)
                            result.shouldBeSuccess()
                            result.value.shouldBeNull()
                        }
                    }
                }

                "when option `SUPPRESS_EXCEPTIONS` is not enabled" - {
                    val compiler = defaultPathCompiler(ObjectMapper())

                    "when the data contains a value by path" - {
                        val path = "$.id".compile(compiler)

                        "then the function should return a value" {
                            val result = path.search(DATA)
                            result.shouldBeSuccess()
                            result.value shouldBe DataElement.Text(DATA_VALUE_1)
                        }
                    }

                    "the data does not contain a value by path" - {
                        val path = "$.scheme".compile(compiler)

                        "then the function should return the null value" {
                            val result = path.search(DATA)
                            result.shouldBeSuccess()
                            result.value.shouldBeNull()
                        }
                    }
                }

                "when occurs an error during the search" - {
                    val compiler = defaultPathCompiler(ObjectMapper(), Option.AS_PATH_LIST)
                    val path = "$.id.sum()".compile(compiler)

                    "then the function should return an error" {
                        val result = path.search(DATA)
                        result.shouldBeFailure()
                        result.cause.shouldBeInstanceOf<Path.Errors.Search>()
                    }
                }
            }
        }
    }

    private companion object {
        private const val DATA_KEY_1 = "id"
        private const val DATA_VALUE_1 = "data-1"
        private val DATA = DataElement.Struct(
            mutableMapOf(DATA_KEY_1 to DataElement.Text(DATA_VALUE_1))
        )

        private fun String.compile(compiler: Path.Compiler): Path =
            compiler.compile(this).orThrow { error(it.description) }
    }
}
