package io.github.ustudiocompany.uframework.rulesengine.path

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.PathNotFoundException
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.path.Path
import io.github.ustudiocompany.uframework.rulesengine.core.path.Path.SearchError

public class PathEngine(private val config: Configuration) {

    public fun parse(path: String): ResultK<Path, Errors.Parsing> = try {
        PathInstance(text = path, parsedPath = JsonPath.compile(path)).asSuccess()
    } catch (expected: Exception) {
        Errors.Parsing(path, expected).asFailure()
    }

    private inner class PathInstance(
        override val text: String,
        private val parsedPath: JsonPath
    ) : Path {

        override fun searchIn(data: DataElement): ResultK<DataElement?, SearchError> = try {
            parsedPath.read<DataElement>(data, config)
                ?.asSuccess()
                ?: ResultK.Success.asNull
        } catch (_: PathNotFoundException) {
            ResultK.Success.asNull
        } catch (expected: Exception) {
            SearchError(path = this, exception = expected).asFailure()
        }
    }

    public sealed interface Errors : BasicRulesEngineError {

        public class Parsing(path: String, cause: Exception) : Errors {
            override val code: String = PREFIX + "1"
            override val description: String = "The error of parsing json-path: '$path'."
            override val cause: Failure.Cause = Failure.Cause.Exception(cause)
            override val details: Failure.Details = Failure.Details.of(
                DETAILS_KEY_PATH to path
            )
        }

        private companion object {
            private const val PREFIX = "JSON-PATH-ENGINE-"
            private const val DETAILS_KEY_PATH = "json-path"
        }
    }
}
