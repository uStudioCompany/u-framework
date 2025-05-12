package io.github.ustudiocompany.uframework.rulesengine.parser

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.andThen
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Rules
import io.github.ustudiocompany.uframework.rulesengine.feel.ExpressionParser
import io.github.ustudiocompany.uframework.rulesengine.path.PathParser

public fun rulesParser(
    mapper: ObjectMapper,
    expressionParser: ExpressionParser,
    pathParser: PathParser
): RulesParser =
    JacksonRulesParser(mapper, expressionParser, pathParser)

private class JacksonRulesParser(
    mapper: ObjectMapper,
    expressionParser: ExpressionParser,
    pathParser: PathParser
) : RulesParser {
    private val deserializer: RulesDeserializer = RulesDeserializer(mapper)
    private val converter = Converter(expressionParser, pathParser)

    override fun parse(input: String): ResultK<Rules, RulesParser.Errors> =
        deserializer.deserialize(input)
            .andThen { model -> converter.convert(model.rules) }
            .mapFailure { error -> RulesParser.Errors.Parsing(error) }
}
