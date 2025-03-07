package io.github.ustudiocompany.uframework.rulesengine.path

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.PathNotFoundException
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.failure.FailureDetails
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.path.Path

public class PathEngine(private val config: Configuration) {

    public fun parse(path: String): ResultK<Path, Errors.Parsing> = try {
        PathInstance(JsonPath.compile(path)).asSuccess()
    } catch (expected: Exception) {
        Errors.Parsing(path, expected).asFailure()
    }

    private inner class PathInstance(
        private val parsedPath: JsonPath
    ) : Path {

        override fun searchIn(data: DataElement): ResultK<DataElement?, Errors.Search> = try {
            parsedPath.read<DataElement>(data, config)
                ?.asSuccess()
                ?: ResultK.Success.asNull
        } catch (_: PathNotFoundException) {
            ResultK.Success.asNull
        } catch (expected: Exception) {
            Errors.Search(parsedPath.path, expected).asFailure()
        }
    }

    public sealed interface Errors : Failure {

        public class Parsing(public val path: String, cause: Exception) : Errors {
            override val code: String = PREFIX + "1"
            override val description: String = "The error of parsing json-path: '$path'."
            override val cause: Failure.Cause = Failure.Cause.Exception(cause)
            override val details: FailureDetails = FailureDetails.of(
                DETAILS_KEY_PATH to path
            )
        }

        public class Search(public val path: String, exception: Exception) : Errors {
            override val code: String = PREFIX + "2"
            override val description: String = "The error of searching by json-path: '$path'."
            override val cause: Failure.Cause = Failure.Cause.Exception(exception)
            override val details: FailureDetails = FailureDetails.of(
                DETAILS_KEY_PATH to path
            )
        }

        private companion object {
            private const val PREFIX = "JSON-PATH-"
            private const val DETAILS_KEY_PATH = "json-path"
        }
    }
}
