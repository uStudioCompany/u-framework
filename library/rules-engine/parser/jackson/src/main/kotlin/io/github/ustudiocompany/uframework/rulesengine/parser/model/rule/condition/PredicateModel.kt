package io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.condition

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.ValueModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.operator.OperatorModel

internal data class PredicateModel(
    @JsonProperty("target") val target: ValueModel,
    @JsonProperty("operator") val operator: OperatorModel,
    @JsonProperty("value") val value: ValueModel?
)
