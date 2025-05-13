package io.github.ustudiocompany.uframework.json.element.merge.strategy.parser.model

import com.fasterxml.jackson.annotation.JsonProperty

internal data class StrategyModel(
    @JsonProperty("version") val version: String,
    @JsonProperty("properties") val properties: Map<String, PropertyModel> = emptyMap()
)
