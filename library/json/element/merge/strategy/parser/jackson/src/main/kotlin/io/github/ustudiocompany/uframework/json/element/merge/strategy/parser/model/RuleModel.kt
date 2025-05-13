package io.github.ustudiocompany.uframework.json.element.merge.strategy.parser.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "name"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = RuleModel.WholeListMerge::class, name = "wholeListMerge"),
    JsonSubTypes.Type(value = RuleModel.MergeByAttribute::class, name = "mergeByAttribute")
)
internal sealed interface RuleModel {
    data object WholeListMerge : RuleModel
    data class MergeByAttribute(
        @JsonProperty("attributes") val attributes: List<String>
    ) : RuleModel
}
