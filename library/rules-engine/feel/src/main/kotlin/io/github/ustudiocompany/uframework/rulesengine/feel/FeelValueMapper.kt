package io.github.ustudiocompany.uframework.rulesengine.feel

import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
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
        value.toDataElement(innerValueMapper)

    override fun toValue(value: Any, innerValueMapper: Function<Any, Val>): Optional<Val> =
        if (value is DataElement)
            Optional.of<Val>(value.toVal(innerValueMapper))
        else
            Optional.empty<Val>()

    private fun Val.toDataElement(innerValueMapper: Function<Val, Any>): Optional<Any> =
        when (this) {
            is ValBoolean -> Optional.of(DataElement.Bool.valueOf(value()))
            is ValString -> Optional.of(DataElement.Text(value()))
            is ValNumber -> Optional.of(DataElement.Decimal(java.math.BigDecimal(value().toString())))
            is ValList -> Optional.of(this.asArray(innerValueMapper))
            is ValContext -> Optional.of(this.asStruct(innerValueMapper))
            is `ValNull$` -> Optional.of(DataElement.Null)
            else -> Optional.empty()
        }

    private fun ValList.asArray(innerValueMapper: Function<Val, Any>): DataElement.Array {
        val builder = DataElement.Array.Builder()
        var items = this.items()
        while (!items.isEmpty) {
            val item = items.head()
            builder.add(innerValueMapper.apply(item) as DataElement)
            items = items.tail()
        }
        return builder.build()
    }

    private fun ValContext.asStruct(innerValueMapper: Function<Val, Any>): DataElement.Struct {
        val builder = DataElement.Struct.Builder()
        val variableProvider = this.context().variableProvider()
        var variables = variableProvider.getVariables()
        while (!variables.isEmpty) {
            val entry = variables.head()
            val key = entry._1
            val value = innerValueMapper.apply(entry._2 as Val) as DataElement
            builder[key] = value
            variables = variables.tail()
        }
        return builder.build()
    }

    private fun DataElement.toVal(innerValueMapper: Function<Any, Val>): Val =
        when (this) {
            is DataElement.Null -> `ValNull$`.`MODULE$`
            is DataElement.Text -> ValString(get)
            is DataElement.Bool -> ValBoolean(get)
            is DataElement.Decimal -> ValNumber(BigDecimal(get))
            is DataElement.Array -> toVal(innerValueMapper)
            is DataElement.Struct -> toVal(innerValueMapper)
        }

    private fun DataElement.Array.toVal(innerValueMapper: Function<Any, Val>): Val {
        var list: List<Val> = `List$`.`MODULE$`.empty()
        forEach { item ->
            val value = innerValueMapper.apply(item)
            list = `$colon$colon`(value, list)
        }
        return ValList(list)
    }

    private fun DataElement.Struct.toVal(innerValueMapper: Function<Any, Val>): Val {
        var properties = `Map$`.`MODULE$`.newBuilder<String, Any>()
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
