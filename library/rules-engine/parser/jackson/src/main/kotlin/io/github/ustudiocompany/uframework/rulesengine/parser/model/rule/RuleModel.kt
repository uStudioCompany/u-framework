package io.github.ustudiocompany.uframework.rulesengine.parser.model.rule

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.condition.ConditionModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.step.StepsModel

internal data class RuleModel(
    @JsonProperty("condition") val condition: ConditionModel? = null,
    @JsonProperty("steps") val steps: StepsModel
)
