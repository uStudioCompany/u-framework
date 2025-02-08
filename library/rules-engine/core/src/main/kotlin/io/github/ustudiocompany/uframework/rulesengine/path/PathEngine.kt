package io.github.ustudiocompany.uframework.rulesengine.path

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.PathNotFoundException
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.path.Path

public class PathEngine(private val config: Configuration) {

    public fun compile(path: String): ResultK<Path, Errors.Compiling> = try {
        PathInstance(JsonPath.compile(path)).asSuccess()
    } catch (expected: Exception) {
        Errors.Compiling(path, expected).asFailure()
    }

    private inner class PathInstance(
        private val compiledPath: JsonPath
    ) : Path {

        override fun searchIn(data: DataElement): ResultK<DataElement?, Errors.Search> = try {
            compiledPath.read<DataElement>(data, config)
                ?.asSuccess()
                ?: ResultK.Success.asNull
        } catch (_: PathNotFoundException) {
            ResultK.Success.asNull
        } catch (expected: Exception) {
            Errors.Search(compiledPath.path, expected).asFailure()
        }
    }

    public sealed interface Errors : Failure {

        public class Compiling(path: String, cause: Exception) : Errors {
            override val code: String = PREFIX + "COMPILE"
            override val description: String = "The error of parsing json-path: `$path`."
            override val cause: Failure.Cause = Failure.Cause.Exception(cause)
            override val details: Failure.Details = Failure.Details.of(
                DETAILS_KEY_PATH to path
            )
        }

        public class Search(path: String, exception: Throwable) : Errors {
            override val code: String = PREFIX + "SEARCH"
            override val description: String = "The error of searching by json-path: `$path`."
            override val cause: Failure.Cause = Failure.Cause.Exception(exception)
            override val details: Failure.Details = Failure.Details.of(
                DETAILS_KEY_PATH to path.toString()
            )
        }

        private companion object {
            private const val PREFIX = "JSON-PATH-"
            private const val DETAILS_KEY_PATH = "json-path"
        }
    }
}
