package io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.step

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.ValueModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.condition.ConditionModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.operator.OperatorModel

internal typealias ErrorCode = String

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = StepModel.Validation::class, name = "validation"),
    JsonSubTypes.Type(value = StepModel.DataRetrieve::class, name = "dataRetrieve"),
    JsonSubTypes.Type(value = StepModel.DataBuild::class, name = "dataBuild")
)
internal sealed interface StepModel {

    data class Validation(
        @JsonProperty("condition") val condition: ConditionModel = emptyList(),
        @JsonProperty("target") val target: ValueModel,
        @JsonProperty("operator") val operator: OperatorModel,
        @JsonProperty("value") val value: ValueModel?,
        @JsonProperty("errorCode") val errorCode: ErrorCode
    ) : StepModel

    data class DataRetrieve(
        @JsonProperty("condition") val condition: ConditionModel = emptyList(),
        @JsonProperty("uri") val uri: UriModel,
        @JsonProperty("args") val args: ArgsModel,
        @JsonProperty("result") val result: ResultModel
    ) : StepModel

    data class DataBuild(
        @JsonProperty("condition") val condition: ConditionModel = emptyList(),
        @JsonProperty("dataSchema") val dataSchema: DataSchemaModel,
        @JsonProperty("result") val result: ResultModel
    ) : StepModel
}
