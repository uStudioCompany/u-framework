package io.github.ustudiocompany.uframework.rulesengine.parser.model.rule

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "kind"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = ValueModel.Literal::class, name = "fact"),
    JsonSubTypes.Type(value = ValueModel.Reference::class, name = "reference"),
    JsonSubTypes.Type(value = ValueModel.Expression::class, name = "expression")
)
internal sealed interface ValueModel {

    data class Literal(
        @JsonProperty("fact") public val fact: FactModel
    ) : ValueModel

    data class Reference(
        @JsonProperty("source") public val source: SourceModel,
        @JsonProperty("path") public val path: PathModel
    ) : ValueModel

    data class Expression(
        @JsonProperty("expression") public val expression: FeelExpressionModel
    ) : ValueModel
}
