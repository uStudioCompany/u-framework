package io.github.ustudiocompany.uframework.json.path

import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.Option
import com.jayway.jsonpath.PathNotFoundException
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.json.element.JsonElement

public fun defaultPathParser(mapper: ObjectMapper, vararg options: Option): PathParser =
    defaultPathParser(defaultPathParserConfiguration(mapper, options.toSet()))

public fun defaultPathParser(config: Configuration): PathParser = DefaultPathParser(config)

private class DefaultPathParser(private val config: Configuration) : PathParser {

    override fun parse(path: String): ResultK<Path, PathParser.Errors.Parsing> = try {
        PathInstance(text = path, parsedPath = JsonPath.compile(path)).asSuccess()
    } catch (expected: Exception) {
        PathParser.Errors.Parsing(path, expected).asFailure()
    }

    private inner class PathInstance(
        override val text: String,
        private val parsedPath: JsonPath
    ) : Path {

        override fun searchIn(data: JsonElement): ResultK<JsonElement?, Path.SearchError> = try {
            parsedPath.read<JsonElement>(data, config)
                ?.asSuccess()
                ?: ResultK.Success.asNull
        } catch (_: PathNotFoundException) {
            ResultK.Success.asNull
        } catch (expected: Exception) {
            Path.SearchError(path = this, exception = expected).asFailure()
        }
    }
}
