package io.github.ustudiocompany.uframework.rulesengine.core.data.path

import com.jayway.jsonpath.JsonPath
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.failure.TypeFailure
import io.github.ustudiocompany.uframework.failure.TypeOf
import io.github.ustudiocompany.uframework.failure.typeOf

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

    public sealed interface Errors : TypeFailure<Path> {

        override val type: TypeOf<Path>
            get() = typeOf<Path>()

        public class PathParsing(path: String, cause: Exception) : Errors {
            override val code: String = type.name + "-1"
            override val description: String = "Error of parsing json path: `$path`."
            override val cause: Failure.Cause = Failure.Cause.Exception(cause)
            override val details: Failure.Details = Failure.Details.of(
                DETAILS_KEY_PATH to path
            )
        }

        private companion object {
            private const val DETAILS_KEY_PATH = "json-path"
        }
    }
}

internal fun main() {
    val type = typeOf<Path>()
    println(type.name + "-1")
}
