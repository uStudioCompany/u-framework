package io.github.ustudiocompany.uframework.rulesengine.parser

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.andThen
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Rules
import io.github.ustudiocompany.uframework.rulesengine.feel.ExpressionParser
import io.github.ustudiocompany.uframework.rulesengine.parser.model.Model
import io.github.ustudiocompany.uframework.rulesengine.path.PathParser

public fun jacksonRulesParser(expressionParser: ExpressionParser, pathParser: PathParser): RulesParser =
    JacksonRulesParser(expressionParser, pathParser)

private class JacksonRulesParser(
    expressionParser: ExpressionParser,
    pathParser: PathParser
) : RulesParser {
    private val deserializer: JacksonDeserializer = JacksonDeserializer()
    private val converter = Converter(expressionParser, pathParser)

    override fun parse(input: String): ResultK<Rules, RulesParser.Errors> =
        deserializer.deserialize(input, Model::class.java)
            .andThen { model -> converter.convert(model.rules) }
            .mapFailure { error -> RulesParser.Errors.Parsing(error) }
}
