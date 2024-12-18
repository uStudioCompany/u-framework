package io.github.ustudiocompany.uframework.rulesengine.core.data.path

import com.jayway.jsonpath.JsonPath
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess

@JvmInline
public value class Path private constructor(public val get: JsonPath) {

    override fun toString(): String = get.path.toString()

    public companion object {

        @JvmStatic
        public fun compile(path: String): ResultK<Path, Errors.PathParsing> = try {
            Path(JsonPath.compile(path)).asSuccess()
        } catch (expected: Exception) {
            Errors.PathParsing(path, expected).asFailure()
        }
    }

    public sealed interface Errors {
        public val message: String

        public class PathParsing(path: String, public val cause: Exception) : Errors {
            override val message: String = "Error of parsing json path: '$path'."
        }
    }
}
