package io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.step

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.ValueModel

internal typealias PropertiesModel = List<DataSchemeModel.Property>
internal typealias ItemsModel = List<DataSchemeModel.Item>

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = DataSchemeModel.Struct::class, name = "struct"),
    JsonSubTypes.Type(value = DataSchemeModel.Array::class, name = "array")
)
internal sealed interface DataSchemeModel {

    data class Struct(
        @JsonProperty("properties") val properties: PropertiesModel
    ) : DataSchemeModel

    data class Array(
        @JsonProperty("items") val items: ItemsModel
    ) : DataSchemeModel

    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
    )
    @JsonSubTypes(
        JsonSubTypes.Type(value = Property.Struct::class, name = "struct"),
        JsonSubTypes.Type(value = Property.Array::class, name = "array"),
        JsonSubTypes.Type(value = Property.Element::class, name = "value")
    )
    sealed interface Property {
        data class Struct(
            @JsonProperty("name") val name: String,
            @JsonProperty("properties") val properties: PropertiesModel
        ) : Property

        data class Array(
            @JsonProperty("name") val name: String,
            @JsonProperty("items") val items: ItemsModel
        ) : Property

        data class Element(
            @JsonProperty("name") val name: String,
            @JsonProperty("value") val value: ValueModel
        ) : Property
    }

    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
    )
    @JsonSubTypes(
        JsonSubTypes.Type(value = Item.Struct::class, name = "struct"),
        JsonSubTypes.Type(value = Item.Array::class, name = "array"),
        JsonSubTypes.Type(value = Item.Element::class, name = "value")
    )
    sealed interface Item {
        data class Struct(
            @JsonProperty("properties") val properties: PropertiesModel
        ) : Item

        data class Array(
            @JsonProperty("items") val items: ItemsModel
        ) : Item

        data class Element(
            @JsonProperty("value") val value: ValueModel
        ) : Item
    }
}
