package io.github.ustudiocompany.uframework.rulesengine.feel

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
import scala.math.BigDecimal
import java.util.*
import java.util.function.Function

internal class FeelValueMapper : JavaCustomValueMapper() {
    override fun unpackValue(value: Val, innerValueMapper: Function<Val, Any>): Optional<Any> =
        value.toDataElement(innerValueMapper)

    override fun toValue(value: Any, innerValueMapper: Function<Any, Val>): Optional<Val> =
        if (value is FeelExpressionValue)
            Optional.of<Val>(value.toVal(innerValueMapper))
        else
            Optional.empty<Val>()

    private fun Val.toDataElement(innerValueMapper: Function<Val, Any>): Optional<Any> =
        when (this) {
            is ValBoolean -> Optional.of(FeelExpressionValue.Bool(value()))
            is ValString -> Optional.of(FeelExpressionValue.Text(value()))
            is ValNumber -> Optional.of(FeelExpressionValue.Decimal(java.math.BigDecimal(value().toString())))
            is ValList -> Optional.of(this.asArray(innerValueMapper))
            is ValContext -> Optional.of(this.asStruct(innerValueMapper))
            is `ValNull$` -> Optional.of(FeelExpressionValue.Null)
            else -> Optional.empty()
        }

    private fun ValList.asArray(innerValueMapper: Function<Val, Any>): FeelExpressionValue.Array {
        val list = mutableListOf<FeelExpressionValue>()
        var items = this.items()
        while (!items.isEmpty) {
            val item = items.head()
            list.add(innerValueMapper.apply(item) as FeelExpressionValue)
            items = items.tail()
        }
        return FeelExpressionValue.Array(list)
    }

    private fun ValContext.asStruct(innerValueMapper: Function<Val, Any>): FeelExpressionValue.Struct {
        val map = mutableMapOf<String, FeelExpressionValue>()
        val variableProvider = this.context().variableProvider()
        var variables = variableProvider.getVariables()
        while (!variables.isEmpty) {
            val entry = variables.head()
            val key = entry._1
            val value = innerValueMapper.apply(entry._2 as Val) as FeelExpressionValue
            map[key] = value
            variables = variables.tail()
        }
        return FeelExpressionValue.Struct(map)
    }

    private fun FeelExpressionValue.Array.toVal(innerValueMapper: Function<Any, Val>): Val {
        var list: scala.collection.immutable.List<Val> = scala.collection.immutable.`List$`.`MODULE$`.empty()
        items.forEach { item ->
            val value = innerValueMapper.apply(item)
            list = `$colon$colon`(value, list)
        }
        return ValList(list)
    }

    private fun FeelExpressionValue.Struct.toVal(innerValueMapper: Function<Any, Val>): Val {
        var properties = scala.collection.immutable.`Map$`.`MODULE$`.newBuilder<String, Any>()
        this.properties.forEach { (key, value) ->
            properties.addOne(Tuple2(key, value.toVal(innerValueMapper)))
        }
        properties.result()
        val context = `StaticContext$`.`MODULE$`.apply(
            properties.result(),
            scala.collection.immutable.`Map$`.`MODULE$`.empty(),
        )
        return ValContext(context)
    }

    private fun FeelExpressionValue.toVal(innerValueMapper: Function<Any, Val>): Val =
        when (this) {
            is FeelExpressionValue.Null -> `ValNull$`.`MODULE$`
            is FeelExpressionValue.Text -> ValString(value)
            is FeelExpressionValue.Bool -> ValBoolean(value)
            is FeelExpressionValue.Decimal -> ValNumber(BigDecimal(value))
            is FeelExpressionValue.Array -> toVal(innerValueMapper)
            is FeelExpressionValue.Struct -> toVal(innerValueMapper)
        }
}
