package io.github.ustudiocompany.uframework.rulesengine.path

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldContainFailureInstance
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class DefaultPathParserTest : UnitTest() {

    init {

        "The `DefaultPathParser` type" - {

            "the `parse` function" - {

                "when path is valid" - {
                    val path = PATH_PARSER.parse(VALID_PATH)

                    "then the parsing should be a success" {
                        path.shouldBeSuccess()
                    }
                }

                "when path is invalid" - {
                    val path = PATH_PARSER.parse(INVALID_PATH)

                    "then the parsing should be a failure" {
                        path.shouldContainFailureInstance()
                            .shouldBeInstanceOf<PathParser.Errors.Parsing>()
                    }
                }
            }
        }
    }

    private companion object {
        private val PATH_PARSER = defaultPathParser(ObjectMapper())
        private const val VALID_PATH = "$.id"
        private const val INVALID_PATH = "[[]"
    }
}
