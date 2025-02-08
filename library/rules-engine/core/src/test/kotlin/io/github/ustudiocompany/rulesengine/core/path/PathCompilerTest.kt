package io.github.ustudiocompany.rulesengine.core.path

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.rulesengine.path.PathEngine
import io.github.ustudiocompany.uframework.rulesengine.path.defaultPathEngine
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class PathCompilerTest : UnitTest() {

    init {

        "The path compiler" - {

            "when path is valid" - {
                val path = PATH_ENGINE.compile(VALID_PATH)

                "then the compiling should be a success" {
                    path.shouldBeSuccess()
                }
            }

            "when path is invalid" - {
                val path = PATH_ENGINE.compile(INVALID_PATH)

                "then the compiling should be a failure" {
                    path.shouldBeFailure()
                    path.cause.shouldBeInstanceOf<PathEngine.Errors.Compiling>()
                }
            }
        }
    }

    private companion object {
        private val PATH_ENGINE = defaultPathEngine(ObjectMapper())
        private const val VALID_PATH = "$.id"
        private const val INVALID_PATH = "[[]"
    }
}
