package io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.step

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.ValueModel

internal typealias StructPropertiesModel = List<DataSchemaModel.StructProperty>
internal typealias ArrayItemsModel = List<DataSchemaModel.ArrayItem>

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
    visible = false
)
@JsonSubTypes(
    JsonSubTypes.Type(value = DataSchemaModel.Struct::class, name = "struct"),
    JsonSubTypes.Type(value = DataSchemaModel.Array::class, name = "array")
)
internal sealed interface DataSchemaModel {

    data class Struct(
        @JsonProperty("properties") val properties: StructPropertiesModel
    ) : DataSchemaModel

    data class Array(
        @JsonProperty("items") val items: ArrayItemsModel
    ) : DataSchemaModel

    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        visible = false
    )
    @JsonSubTypes(
        JsonSubTypes.Type(value = StructProperty.Struct::class, name = "struct"),
        JsonSubTypes.Type(value = StructProperty.Array::class, name = "array"),
        JsonSubTypes.Type(value = StructProperty.Element::class, name = "value")
    )
    sealed interface StructProperty {

        data class Struct(
            @JsonProperty("name") val name: String,
            @JsonProperty("properties") val properties: StructPropertiesModel
        ) : StructProperty

        data class Array(
            @JsonProperty("name") val name: String,
            @JsonProperty("items") val items: ArrayItemsModel
        ) : StructProperty

        data class Element(
            @JsonProperty("name") val name: String,
            @JsonProperty("value") val value: ValueModel
        ) : StructProperty
    }

    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        visible = false
    )
    @JsonSubTypes(
        JsonSubTypes.Type(value = ArrayItem.Struct::class, name = "struct"),
        JsonSubTypes.Type(value = ArrayItem.Array::class, name = "array"),
        JsonSubTypes.Type(value = ArrayItem.Element::class, name = "value")
    )
    sealed interface ArrayItem {

        data class Struct(
            @JsonProperty("properties") val properties: StructPropertiesModel
        ) : ArrayItem

        data class Array(
            @JsonProperty("items") val items: ArrayItemsModel
        ) : ArrayItem

        data class Element(
            @JsonProperty("value") val value: ValueModel
        ) : ArrayItem
    }
}
