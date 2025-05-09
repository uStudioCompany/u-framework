package io.github.ustudiocompany.uframework.rulesengine.parser.module

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.parser.ParsingException
import java.util.*

public class JsonElementModule : SimpleModule() {

    init {
        addDeserializer(JsonElement::class.java, Deserializer())
    }

    private class Deserializer : JsonDeserializer<JsonElement>() {

        override fun deserialize(jp: JsonParser, ctxt: DeserializationContext): JsonElement =
            deserialize(jp, ctxt, Stack())

        @Deprecated("This method is deprecated in Jackson")
        override fun getNullValue(): JsonElement.Null = JsonElement.Null

        @Suppress("LongMethod", "CyclomaticComplexMethod", "CognitiveComplexMethod", "ThrowsCount")
        private tailrec fun deserialize(
            jp: JsonParser,
            context: DeserializationContext,
            parserContext: Stack<DeserializerContext>
        ): JsonElement {
            if (jp.currentToken == null) jp.nextToken()
            val tokenId: JsonToken = jp.currentToken

            val maybeValue: JsonElement?
            val nextContext: Stack<DeserializerContext>
            when (tokenId) {
                JsonToken.VALUE_TRUE -> {
                    maybeValue = JsonElement.Bool.asTrue
                    nextContext = parserContext
                }

                JsonToken.VALUE_FALSE -> {
                    maybeValue = JsonElement.Bool.asFalse
                    nextContext = parserContext
                }

                JsonToken.VALUE_STRING -> {
                    maybeValue = JsonElement.Text(jp.text)
                    nextContext = parserContext
                }

                JsonToken.VALUE_NUMBER_INT, JsonToken.VALUE_NUMBER_FLOAT -> {
                    maybeValue = jp.text.toDecimal()
                    nextContext = parserContext
                }

                JsonToken.VALUE_NULL -> {
                    maybeValue = JsonElement.Null
                    nextContext = parserContext
                }

                JsonToken.START_ARRAY -> {
                    maybeValue = null
                    nextContext = parserContext.apply {
                        push(DeserializerContext.ReadingList())
                    }
                }

                JsonToken.END_ARRAY -> {
                    val head = parserContext.pop()
                    if (head is DeserializerContext.ReadingList) {
                        maybeValue = head.toDataElement()
                        nextContext = parserContext
                    } else
                        throw ParsingException("We should have been reading list, something got wrong")
                }

                JsonToken.START_OBJECT -> {
                    maybeValue = null
                    nextContext = parserContext.apply {
                        push(DeserializerContext.ReadingObject())
                    }
                }

                JsonToken.FIELD_NAME -> {
                    val head = parserContext.pop()
                    if (head is DeserializerContext.ReadingObject) {
                        parserContext.push(head.setField(jp.currentName))
                        maybeValue = null
                        nextContext = parserContext
                    } else
                        throw ParsingException("We should be reading map, something got wrong")
                }

                JsonToken.END_OBJECT -> {
                    val head = parserContext.pop()
                    if (head is DeserializerContext.ReadingObject) {
                        maybeValue = head.toDataElement()
                        nextContext = parserContext
                    } else
                        throw ParsingException("We should have been reading an object, something got wrong ($head)")
                }

                JsonToken.NOT_AVAILABLE ->
                    throw ParsingException("We should have been reading an object, something got wrong")

                JsonToken.VALUE_EMBEDDED_OBJECT ->
                    throw ParsingException("We should have been reading an object, something got wrong")
            }

            // Read ahead
            jp.nextToken()

            return if (maybeValue != null && nextContext.isEmpty())
                maybeValue
            else {
                val toPass: Stack<DeserializerContext> = maybeValue?.let { v ->
                    val previous: DeserializerContext = nextContext.pop()
                    val p = previous.addValue(v)
                    nextContext.push(p)
                    nextContext
                } ?: nextContext

                deserialize(jp, context, toPass)
            }
        }

        private fun String.toDecimal() = try {
            JsonElement.Decimal(this.toBigDecimal())
        } catch (expected: Exception) {
            throw ParsingException("Invalid number value: $this", expected)
        }

        private sealed class DeserializerContext {

            abstract fun addValue(value: JsonElement): DeserializerContext

            class ReadingList : DeserializerContext() {
                private val builder = JsonElement.Array.Builder()
                override fun addValue(value: JsonElement): DeserializerContext = this.apply { builder.add(value) }
                fun toDataElement(): JsonElement.Array = builder.build()
            }

            class ReadingObject : DeserializerContext() {
                private val builder = JsonElement.Struct.Builder()

                fun setField(fieldName: String): KeyRead = KeyRead(fieldName)

                override fun addValue(value: JsonElement): DeserializerContext =
                    throw ParsingException("Cannot add a value on an object without a key, malformed JSON object!")

                fun toDataElement(): JsonElement.Struct = builder.build()

                inner class KeyRead(val fieldName: String) : DeserializerContext() {
                    override fun addValue(value: JsonElement): DeserializerContext =
                        this@ReadingObject.apply { builder[fieldName] = value }
                }
            }
        }
    }
}
