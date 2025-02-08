package io.github.ustudiocompany.uframework.rulesengine.core.feel

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.feel.FeelEngine

public interface FeelExpression {

    public fun evaluate(
        context: Map<Source, DataElement> = emptyMap()
    ): ResultK<DataElement, FeelEngine.Errors.Evaluate>
}
