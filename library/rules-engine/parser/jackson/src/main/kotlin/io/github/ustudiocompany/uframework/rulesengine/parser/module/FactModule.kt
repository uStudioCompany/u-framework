package io.github.ustudiocompany.uframework.rulesengine.parser.module

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.parser.ParsingException
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.FactModel
import java.io.IOException
import java.math.BigDecimal

public class FactModule : SimpleModule() {

    init {
        addDeserializer(FactModel::class.java, Deserializer())
    }

    internal class Deserializer : JsonDeserializer<FactModel>() {

        @Throws(IOException::class, JsonProcessingException::class)
        override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): FactModel {
            val token = jsonParser.currentToken
            return when (token) {
                JsonToken.VALUE_STRING -> FactModel(JsonElement.Text(jsonParser.text))
                JsonToken.VALUE_FALSE -> FactModel(JsonElement.Bool(false))
                JsonToken.VALUE_TRUE -> FactModel(JsonElement.Bool(true))
                JsonToken.VALUE_NUMBER_INT -> FactModel(JsonElement.Decimal(BigDecimal(jsonParser.text)))
                JsonToken.VALUE_NUMBER_FLOAT -> FactModel(JsonElement.Decimal(BigDecimal(jsonParser.text)))
                JsonToken.START_ARRAY -> deserializeArray(jsonParser, deserializationContext)
                else -> throw ParsingException("Incorrect value of the fact: '${jsonParser.text}'.")
            }
        }

        @Throws(IOException::class, JsonProcessingException::class)
        fun deserializeArray(
            jsonParser: JsonParser,
            deserializationContext: DeserializationContext,
            builder: JsonElement.Array.Builder = JsonElement.Array.Builder(),
        ): FactModel {
            jsonParser.nextToken()
            val token = jsonParser.currentToken
            if (token == JsonToken.END_ARRAY) return FactModel(builder.build())
            val item = when (token) {
                JsonToken.VALUE_NULL -> JsonElement.Null
                JsonToken.VALUE_STRING -> JsonElement.Text(jsonParser.text)
                JsonToken.VALUE_FALSE -> JsonElement.Bool(false)
                JsonToken.VALUE_TRUE -> JsonElement.Bool(true)
                JsonToken.VALUE_NUMBER_INT -> JsonElement.Decimal(BigDecimal(jsonParser.text))
                JsonToken.VALUE_NUMBER_FLOAT -> JsonElement.Decimal(BigDecimal(jsonParser.text))
                else -> throw ParsingException("Incorrect value of the fact: '${jsonParser.text}'.")
            }

            return deserializeArray(jsonParser, deserializationContext, builder.apply { add(item) })
        }

        override fun getNullValue(ctxt: DeserializationContext?): FactModel = FactModel(JsonElement.Null)
    }
}
