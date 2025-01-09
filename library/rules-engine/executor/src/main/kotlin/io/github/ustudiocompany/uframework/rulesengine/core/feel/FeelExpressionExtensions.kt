package io.github.ustudiocompany.uframework.rulesengine.core.feel

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.context.Context
import io.github.ustudiocompany.uframework.rulesengine.executor.error.FeelExpressionError

internal fun FeelExpression.evaluateWithContext(context: Context): ResultK<DataElement, FeelExpressionError> =
    evaluate(context.immutable)
        .mapFailure { FeelExpressionError(it) }
