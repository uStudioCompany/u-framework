package io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.step

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.SourceModel

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "action"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = ResultModel.Put::class, name = "put"),
    JsonSubTypes.Type(value = ResultModel.Replace::class, name = "replace"),
    JsonSubTypes.Type(value = ResultModel.Merge::class, name = "merge")
)
internal sealed interface ResultModel {

    data class Put(
        @JsonProperty("source") val source: SourceModel,
    ) : ResultModel

    data class Replace(
        @JsonProperty("source") val source: SourceModel,
    ) : ResultModel

    data class Merge(
        @JsonProperty("source") val source: SourceModel,
        @JsonProperty("mergeStrategyCode") val mergeStrategyCode: MergeStrategyCodeModel
    ) : ResultModel
}
