package io.github.ustudiocompany.rulesengine.core.data.path

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.rulesengine.core.data.path.Path
import io.github.ustudiocompany.uframework.rulesengine.core.data.path.Path.Errors
import io.github.ustudiocompany.uframework.rulesengine.core.data.path.defaultPathConfiguration
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.types.shouldBeInstanceOf

internal class PathCompilerTest : UnitTest() {

    init {

        "The path compiler" - {

            "when path is valid" - {
                val path = PATH_COMPILER.compile(VALID_PATH)

                "then the compiling should be a success" {
                    path.shouldBeSuccess()
                }
            }

            "when path is invalid" - {
                val path = PATH_COMPILER.compile(INVALID_PATH)

                "then the compiling should be a failure" {
                    path.shouldBeFailure()
                    path.cause.shouldBeInstanceOf<Errors.Compiling>()
                }
            }
        }
    }

    private companion object {
        private val PATH_COMPILER = Path.Compiler(defaultPathConfiguration(ObjectMapper()))
        private const val VALID_PATH = "$.id"
        private const val INVALID_PATH = "[[]"
    }
}
