package io.github.ustudiocompany.uframework.rulesengine.feel

import io.github.ustudiocompany.uframework.json.element.JsonElement
import org.camunda.feel.context.Context.`StaticContext$`
import org.camunda.feel.syntaxtree.Val
import org.camunda.feel.syntaxtree.ValBoolean
import org.camunda.feel.syntaxtree.ValContext
import org.camunda.feel.syntaxtree.ValList
import org.camunda.feel.syntaxtree.`ValNull$`
import org.camunda.feel.syntaxtree.ValNumber
import org.camunda.feel.syntaxtree.ValString
import org.camunda.feel.valuemapper.JavaCustomValueMapper
import scala.Tuple2
import scala.collection.immutable.`$colon$colon`
import scala.collection.immutable.List
import scala.collection.immutable.`List$`
import scala.collection.immutable.`Map$`
import scala.math.BigDecimal
import java.util.*
import java.util.function.Function

internal class FeelValueMapper : JavaCustomValueMapper() {
    override fun unpackValue(value: Val, innerValueMapper: Function<Val, Any>): Optional<Any> =
        value.toJsonElement(innerValueMapper)

    override fun toValue(value: Any, innerValueMapper: Function<Any, Val>): Optional<Val> =
        if (value is JsonElement)
            Optional.of<Val>(value.toVal(innerValueMapper))
        else
            Optional.empty<Val>()

    private fun Val.toJsonElement(innerValueMapper: Function<Val, Any>): Optional<Any> =
        when (this) {
            is ValBoolean -> Optional.of(JsonElement.Bool.valueOf(value()))
            is ValString -> Optional.of(JsonElement.Text(value()))
            is ValNumber -> Optional.of(JsonElement.Decimal(java.math.BigDecimal(value().toString())))
            is ValList -> Optional.of(this.asArray(innerValueMapper))
            is ValContext -> Optional.of(this.asStruct(innerValueMapper))
            is `ValNull$` -> Optional.of(JsonElement.Null)
            else -> Optional.empty()
        }

    private fun ValList.asArray(innerValueMapper: Function<Val, Any>): JsonElement.Array {
        val builder = JsonElement.Array.Builder()
        var items = this.items()
        while (!items.isEmpty) {
            val item = items.head()
            builder.add(innerValueMapper.apply(item) as JsonElement)
            items = items.tail()
        }
        return builder.build()
    }

    private fun ValContext.asStruct(innerValueMapper: Function<Val, Any>): JsonElement.Struct {
        val builder = JsonElement.Struct.Builder()
        val variableProvider = this.context().variableProvider()
        var variables = variableProvider.getVariables()
        while (!variables.isEmpty) {
            val entry = variables.head()
            val key = entry._1
            val value = innerValueMapper.apply(entry._2 as Val) as JsonElement
            builder[key] = value
            variables = variables.tail()
        }
        return builder.build()
    }

    private fun JsonElement.toVal(innerValueMapper: Function<Any, Val>): Val =
        when (this) {
            is JsonElement.Null -> `ValNull$`.`MODULE$`
            is JsonElement.Text -> ValString(get)
            is JsonElement.Bool -> ValBoolean(get)
            is JsonElement.Decimal -> ValNumber(BigDecimal(get))
            is JsonElement.Array -> toVal(innerValueMapper)
            is JsonElement.Struct -> toVal(innerValueMapper)
        }

    private fun JsonElement.Array.toVal(innerValueMapper: Function<Any, Val>): Val {
        var list: List<Val> = `List$`.`MODULE$`.empty()
        forEach { item ->
            val value = innerValueMapper.apply(item)
            list = `$colon$colon`(value, list)
        }
        return ValList(list)
    }

    private fun JsonElement.Struct.toVal(innerValueMapper: Function<Any, Val>): Val {
        val properties = `Map$`.`MODULE$`.newBuilder<String, Any>()
        forEach { (key, value) ->
            properties.addOne(Tuple2(key, value.toVal(innerValueMapper)))
        }
        properties.result()
        val context = `StaticContext$`.`MODULE$`.apply(
            properties.result(),
            `Map$`.`MODULE$`.empty(),
        )
        return ValContext(context)
    }
}
