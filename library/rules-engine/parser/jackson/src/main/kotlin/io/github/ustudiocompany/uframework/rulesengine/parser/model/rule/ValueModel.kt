package io.github.ustudiocompany.uframework.rulesengine.parser.model.rule

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "kind"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = ValueModel.Literal::class, name = "fact"),
    JsonSubTypes.Type(value = ValueModel.Reference::class, name = "reference"),
    JsonSubTypes.Type(value = ValueModel.Expression::class, name = "expression")
)
internal sealed interface ValueModel {

    data class Literal(
        @JsonProperty("fact") val fact: FactModel
    ) : ValueModel

    data class Reference(
        @JsonProperty("source") val source: SourceModel,
        @JsonProperty("path") val path: PathModel
    ) : ValueModel

    data class Expression(
        @JsonProperty("expression") val expression: FeelExpressionModel
    ) : ValueModel
}
