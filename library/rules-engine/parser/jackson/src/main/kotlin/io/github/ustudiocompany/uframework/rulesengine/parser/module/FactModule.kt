package io.github.ustudiocompany.uframework.rulesengine.parser.module

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
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
                JsonToken.VALUE_STRING -> FactModel(DataElement.Text(jsonParser.text))
                JsonToken.VALUE_FALSE -> FactModel(DataElement.Bool(false))
                JsonToken.VALUE_TRUE -> FactModel(DataElement.Bool(true))
                JsonToken.VALUE_NUMBER_INT -> FactModel(DataElement.Decimal(BigDecimal(jsonParser.text)))
                JsonToken.VALUE_NUMBER_FLOAT -> FactModel(DataElement.Decimal(BigDecimal(jsonParser.text)))
                JsonToken.START_ARRAY -> deserializeArray(jsonParser, deserializationContext)
                else -> throw ParsingException("Incorrect value of the fact: '${jsonParser.text}'.")
            }
        }

        @Throws(IOException::class, JsonProcessingException::class)
        fun deserializeArray(
            jsonParser: JsonParser,
            deserializationContext: DeserializationContext,
            builder: DataElement.Array.Builder = DataElement.Array.Builder(),
        ): FactModel {
            jsonParser.nextToken()
            val token = jsonParser.currentToken
            if (token == JsonToken.END_ARRAY) return FactModel(builder.build())
            val item = when (token) {
                JsonToken.VALUE_NULL -> DataElement.Null
                JsonToken.VALUE_STRING -> DataElement.Text(jsonParser.text)
                JsonToken.VALUE_FALSE -> DataElement.Bool(false)
                JsonToken.VALUE_TRUE -> DataElement.Bool(true)
                JsonToken.VALUE_NUMBER_INT -> DataElement.Decimal(BigDecimal(jsonParser.text))
                JsonToken.VALUE_NUMBER_FLOAT -> DataElement.Decimal(BigDecimal(jsonParser.text))
                else -> throw ParsingException("Incorrect value of the fact: '${jsonParser.text}'.")
            }

            return deserializeArray(jsonParser, deserializationContext, builder.apply { add(item) })
        }

        override fun getNullValue(ctxt: DeserializationContext?): FactModel = FactModel(DataElement.Null)
    }
}
