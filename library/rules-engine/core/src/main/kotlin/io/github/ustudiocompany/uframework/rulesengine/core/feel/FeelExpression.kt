package io.github.ustudiocompany.uframework.rulesengine.core.feel

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.map
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.feel.FeelEngine
import org.camunda.feel.syntaxtree.ParsedExpression

public class FeelExpression private constructor(
    private val value: ParsedExpression,
    private val engine: FeelEngine
) {

    public fun evaluate(context: Map<Source, DataElement> = emptyMap()): ResultK<DataElement, FeelEngine.Errors> =
        engine.evaluate(value, context)

    public class Parser(private val engine: FeelEngine) {

        public fun parse(expression: String): ResultK<FeelExpression, FeelEngine.Errors> =
            engine.parse(expression)
                .map { FeelExpression(value = it, engine = engine) }
    }
}
