package io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.step

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.ValueModel

internal data class MessageHeaderModel(
    @JsonProperty("name") val name: String,
    @JsonProperty("value") val value: ValueModel
)
