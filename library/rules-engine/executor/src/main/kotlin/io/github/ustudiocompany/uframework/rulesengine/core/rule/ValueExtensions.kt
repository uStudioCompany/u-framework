package io.github.ustudiocompany.uframework.rulesengine.core.rule

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.andThen
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.filterNotNull
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.data.search
import io.github.ustudiocompany.uframework.rulesengine.core.rule.context.Context
import io.github.ustudiocompany.uframework.rulesengine.executor.error.DataErrors
import io.github.ustudiocompany.uframework.rulesengine.executor.error.RuleEngineError
import io.github.ustudiocompany.uframework.rulesengine.feel.evaluateWithContext

internal fun Value.compute(context: Context): ResultK<DataElement, RuleEngineError> =
    when (this) {
        is Value.Literal -> fact.asSuccess()

        is Value.Reference -> context[source]
            .andThen { element ->
                element.search(path)
                    .filterNotNull { DataErrors.Missing(source, path) }
            }

        is Value.Expression -> expression.evaluateWithContext(context)
    }

internal fun Value.computeOrNull(context: Context): ResultK<DataElement?, RuleEngineError> =
    when (this) {
        is Value.Literal -> fact.asSuccess()
        is Value.Reference -> context[source].andThen { element -> element.search(path) }
        is Value.Expression -> expression.evaluateWithContext(context)
    }
