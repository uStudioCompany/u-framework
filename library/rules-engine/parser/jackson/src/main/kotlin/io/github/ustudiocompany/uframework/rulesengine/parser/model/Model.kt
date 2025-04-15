package io.github.ustudiocompany.uframework.rulesengine.parser.model

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.RulesModel

internal data class Model(
    @JsonProperty("version") val version: String,
    @JsonProperty("rules") val rules: RulesModel
)
