package io.github.ustudiocompany.uframework.json.path

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectReader
import com.jayway.jsonpath.InvalidJsonException
import com.jayway.jsonpath.JsonPathException
import com.jayway.jsonpath.spi.json.JsonProvider
import io.github.ustudiocompany.uframework.json.element.JsonElement
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.StringWriter
import java.math.BigDecimal
import java.nio.charset.StandardCharsets

@Suppress("TooManyFunctions")
internal class JsonElementProvider(
    private val objectMapper: ObjectMapper,
    private val objectReader: ObjectReader
) : JsonProvider {

    @Throws(InvalidJsonException::class)
    override fun parse(json: String): JsonElement = try {
        objectReader.readValue(json, JsonElement::class.java)
    } catch (expected: IOException) {
        throw InvalidJsonException(expected, json)
    }

    @Throws(InvalidJsonException::class)
    override fun parse(json: ByteArray): JsonElement = try {
        objectReader.readValue(json, JsonElement::class.java)
    } catch (expected: IOException) {
        throw InvalidJsonException(expected, String(json, StandardCharsets.UTF_8))
    }

    @Throws(InvalidJsonException::class)
    override fun parse(jsonStream: InputStream, charset: String): JsonElement = try {
        objectReader.readValue(InputStreamReader(jsonStream, charset), JsonElement::class.java)
    } catch (expected: IOException) {
        throw InvalidJsonException(expected)
    }

    override fun toJson(obj: Any): String {
        val writer = StringWriter()
        try {
            val generator = objectMapper.factory.createGenerator(writer)
            objectMapper.writeValue(generator, obj)
            writer.flush()
            writer.close()
            generator.close()
            return writer.buffer.toString()
        } catch (expected: IOException) {
            throw InvalidJsonException(expected)
        }
    }

    override fun createArray(): JsonElement.Array = JsonElement.Array()

    override fun createMap(): JsonElement.Struct = JsonElement.Struct()

    /**
     * checks if object is an array
     *
     * @param obj object to check
     * @return true if obj is an array
     */
    override fun isArray(obj: Any?): Boolean = obj is JsonElement.Array

    /**
     * Extracts a value from an array
     *
     * @param obj an array
     * @param idx index
     * @return the entry at the given index
     */
    override fun getArrayIndex(obj: Any, idx: Int): Any {
        val list = obj as JsonElement.Array
        return list.getOrNull(idx)
            ?: throw JsonPathException("index '$idx' missing in the array ")
    }

    @Deprecated("This method is deprecated in interface")
    override fun getArrayIndex(obj: Any, idx: Int, unwrap: Boolean): Any = getArrayIndex(obj, idx)

    override fun setArrayIndex(array: Any, index: Int, newValue: Any?) {
        if (isArray(array)) {
            val list = array as JsonElement.Array
            val value = createJsonElement(newValue)
            if (index == list.size)
                list.add(value)
            else
                list[index] = value
        } else
            throw UnsupportedOperationException()
    }

    /**
     * Extracts a value from an map
     *
     * @param obj a map
     * @param key property key
     * @return the map entry or [JsonProvider.UNDEFINED] for missing properties
     */
    override fun getMapValue(obj: Any, key: String): Any =
        if (isMap(obj)) {
            val map = obj as JsonElement.Struct
            map[key] ?: JsonProvider.UNDEFINED
        } else
            throw JsonPathException("getMapValue operation cannot be applied to " + obj.getClassName())

    /**
     * Sets a value in an object
     *
     * @param obj   an object
     * @param key   a String key
     * @param value the value to set
     */
    override fun setProperty(obj: Any, key: Any?, value: Any?) {
        if (isMap(obj))
            setValueInObjectNode(obj as JsonElement.Struct, key!!, value)
        else if (isArray(obj)) {
            val array = obj as JsonElement.Array
            val index = if (key != null)
                key as? Int ?: key.toString().toInt()
            else
                array.size

            if (index == array.size)
                array.add(createJsonElement(value))
            else
                array[index] = createJsonElement(value)
        }
    }

    /**
     * Removes a value in an object or array
     *
     * @param obj   an array or an object
     * @param key   a String key or a numerical index to remove
     */
    override fun removeProperty(obj: Any, key: Any) {
        when {
            isMap(obj) -> {
                val map = obj as JsonElement.Struct
                map.remove(key.toString())
            }

            isArray(obj) -> {
                val list = obj as JsonElement.Array
                val index = if (key is Int) key else key.toString().toInt()
                list.removeAt(index)
            }

            else -> throw JsonPathException("removeProperty operation cannot be applied to " + obj.getClassName())
        }
    }

    /**
     * checks if object is a map (i.e. no array)
     *
     * @param obj object to check
     * @return true if the object is a map
     */
    override fun isMap(obj: Any): Boolean = obj is JsonElement.Struct

    /**
     * Returns the keys from the given object
     *
     * @param obj an object
     * @return the keys for an object
     */
    override fun getPropertyKeys(obj: Any): Collection<String> =
        if (isMap(obj))
            (obj as JsonElement.Struct).keys
        else
            throw UnsupportedOperationException()

    /**
     * Get the length of an array or object
     *
     * @param obj an array or an object
     * @return the number of entries in the array or object
     */
    override fun length(obj: Any): Int = when {
        isArray(obj) -> (obj as JsonElement.Array).size
        isMap(obj) -> getPropertyKeys(obj).size
        obj is String -> obj.length
        else -> throw JsonPathException("length operation cannot be applied to " + obj.getClassName())
    }

    /**
     * Converts given array to an [Iterable]
     *
     * @param obj an array
     * @return an Iterable that iterates over the entries of an array
     */
    override fun toIterable(obj: Any?): Iterable<Any?> {
        if (isArray(obj)) {
            val arr = obj as JsonElement.Array
            val iterator = arr.iterator()
            return object : Iterable<Any?> {
                override fun iterator(): Iterator<Any?> = object : AbstractIterator<Any?>() {
                    override fun computeNext() {
                        if (iterator.hasNext())
                            setNext(unwrap(iterator.next()))
                        else
                            done()
                    }
                }
            }
        } else
            throw JsonPathException("Cannot iterate over " + obj.getClassName())
    }

    @Suppress("ReturnCount")
    override fun unwrap(o: Any?): Any? {
        if (o == null) return null
        if (o !is JsonElement) return o
        return when (o) {
            is JsonElement.Text -> o.get
            is JsonElement.Bool -> o.get
            is JsonElement.Decimal -> o.get
            is JsonElement.Array -> o
            is JsonElement.Struct -> o
            is JsonElement.Null -> null
        }
    }

    private fun Any?.getClassName() = if (this != null) this::class.qualifiedName else "null"

    private fun setValueInObjectNode(objectNode: JsonElement.Struct, key: Any, value: Any?) {
        objectNode[key.toString()] = if (value is JsonElement) value else createJsonElement(value)
    }

    private fun createJsonElement(o: Any?): JsonElement =
        when (o) {
            null -> JsonElement.Null
            is JsonElement -> o
            is String -> JsonElement.Text(o)
            is Boolean -> JsonElement.Bool.valueOf(o)
            is BigDecimal -> JsonElement.Decimal(o)
            else -> JsonElement.Text(o.toString())
        }
}
