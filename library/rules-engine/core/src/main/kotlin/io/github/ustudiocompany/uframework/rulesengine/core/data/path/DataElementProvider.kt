package io.github.ustudiocompany.uframework.rulesengine.core.data.path

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectReader
import com.jayway.jsonpath.InvalidJsonException
import com.jayway.jsonpath.JsonPathException
import com.jayway.jsonpath.spi.json.JsonProvider
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.StringWriter
import java.math.BigDecimal
import java.nio.charset.StandardCharsets

@Suppress("TooManyFunctions")
internal class DataElementProvider(
    private val objectMapper: ObjectMapper,
    private val objectReader: ObjectReader
) : JsonProvider {

    @Throws(InvalidJsonException::class)
    override fun parse(json: String): DataElement = try {
        objectReader.readValue(json, DataElement::class.java)
    } catch (expected: IOException) {
        throw InvalidJsonException(expected, json)
    }

    @Throws(InvalidJsonException::class)
    override fun parse(json: ByteArray): DataElement = try {
        objectReader.readValue(json, DataElement::class.java)
    } catch (expected: IOException) {
        throw InvalidJsonException(expected, String(json, StandardCharsets.UTF_8))
    }

    @Throws(InvalidJsonException::class)
    override fun parse(jsonStream: InputStream, charset: String): DataElement = try {
        objectReader.readValue(InputStreamReader(jsonStream, charset), DataElement::class.java)
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

    override fun createArray(): DataElement.Array = DataElement.Array()

    override fun createMap(): DataElement.Struct = DataElement.Struct()

    /**
     * checks if object is an array
     *
     * @param obj object to check
     * @return true if obj is an array
     */
    override fun isArray(obj: Any?): Boolean = obj is DataElement.Array

    /**
     * Extracts a value from an array
     *
     * @param obj an array
     * @param idx index
     * @return the entry at the given index
     */
    override fun getArrayIndex(obj: Any, idx: Int): Any {
        val list = obj as DataElement.Array
        return list.getOrNull(idx)
            ?: throw JsonPathException("index `$idx` missing in the array ")
    }

    @Deprecated("")
    override fun getArrayIndex(obj: Any, idx: Int, unwrap: Boolean): Any = getArrayIndex(obj, idx)

    override fun setArrayIndex(array: Any, index: Int, newValue: Any?) {
        if (isArray(array)) {
            val list = array as DataElement.Array
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
            val map = obj as DataElement.Struct
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
            setValueInObjectNode(obj as DataElement.Struct, key!!, value)
        else if (isArray(obj)) {
            val array = obj as DataElement.Array
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
                val map = obj as DataElement.Struct
                map.remove(key.toString())
            }

            isArray(obj) -> {
                val list = obj as DataElement.Array
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
    override fun isMap(obj: Any): Boolean = obj is DataElement.Struct

    /**
     * Returns the keys from the given object
     *
     * @param obj an object
     * @return the keys for an object
     */
    override fun getPropertyKeys(obj: Any): Collection<String> =
        if (isMap(obj))
            (obj as DataElement.Struct).keys
        else
            throw UnsupportedOperationException()

    /**
     * Get the length of an array or object
     *
     * @param obj an array or an object
     * @return the number of entries in the array or object
     */
    override fun length(obj: Any): Int = when {
        isArray(obj) -> (obj as DataElement.Array).size
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
            val arr = obj as DataElement.Array
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
        if (o !is DataElement) return o
        return when (o) {
            is DataElement.Text -> o.get
            is DataElement.Bool -> o.get
            is DataElement.Decimal -> o.get
            is DataElement.Array -> o
            is DataElement.Struct -> o
            is DataElement.Null -> null
        }
    }

    private fun Any?.getClassName() = if (this != null) this::class.qualifiedName else "null"

    private fun setValueInObjectNode(objectNode: DataElement.Struct, key: Any, value: Any?) {
        objectNode[key.toString()] = if (value is DataElement) value else createJsonElement(value)
    }

    private fun createJsonElement(o: Any?): DataElement =
        when (o) {
            null -> DataElement.Null
            is DataElement -> o
            is String -> DataElement.Text(o)
            is Boolean -> DataElement.Bool(o)
            is BigDecimal -> DataElement.Decimal(o)
            else -> DataElement.Text(o.toString())
        }
}
