package io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.operator

import io.github.ustudiocompany.uframework.json.element.JsonElement

public fun interface Operator<T> {
    public fun compute(target: JsonElement?, value: JsonElement?): T
}
