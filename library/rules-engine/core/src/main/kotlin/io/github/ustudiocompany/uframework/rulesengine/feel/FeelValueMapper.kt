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
        if (value is FeelValueWrapper)
            Optional.of<Val>(value.toVal(innerValueMapper))
        else
            Optional.empty<Val>()

    private fun Val.toDataElement(innerValueMapper: Function<Val, Any>): Optional<Any> =
        when (this) {
            is ValBoolean -> Optional.of(FeelValueWrapper.Bool(value()))
            is ValString -> Optional.of(FeelValueWrapper.Text(value()))
            is ValNumber -> Optional.of(FeelValueWrapper.Decimal(java.math.BigDecimal(value().toString())))
            is ValList -> Optional.of(this.asArray(innerValueMapper))
            is ValContext -> Optional.of(this.asStruct(innerValueMapper))
            is `ValNull$` -> Optional.of(FeelValueWrapper.Null)
            else -> Optional.empty()
        }

    private fun ValList.asArray(innerValueMapper: Function<Val, Any>): FeelValueWrapper.Array {
        val list = mutableListOf<FeelValueWrapper>()
        var items = this.items()
        while (!items.isEmpty) {
            val item = items.head()
            list.add(innerValueMapper.apply(item) as FeelValueWrapper)
            items = items.tail()
        }
        return FeelValueWrapper.Array(list)
    }

    private fun ValContext.asStruct(innerValueMapper: Function<Val, Any>): FeelValueWrapper.Struct {
        val map = mutableMapOf<String, FeelValueWrapper>()
        val variableProvider = this.context().variableProvider()
        var variables = variableProvider.getVariables()
        while (!variables.isEmpty) {
            val entry = variables.head()
            val key = entry._1
            val value = innerValueMapper.apply(entry._2 as Val) as FeelValueWrapper
            map[key] = value
            variables = variables.tail()
        }
        return FeelValueWrapper.Struct(map)
    }

    private fun FeelValueWrapper.Array.toVal(innerValueMapper: Function<Any, Val>): Val {
        var list: scala.collection.immutable.List<Val> = scala.collection.immutable.`List$`.`MODULE$`.empty()
        items.forEach { item ->
            val value = innerValueMapper.apply(item)
            list = `$colon$colon`(value, list)
        }
        return ValList(list)
    }

    private fun FeelValueWrapper.Struct.toVal(innerValueMapper: Function<Any, Val>): Val {
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

    private fun FeelValueWrapper.toVal(innerValueMapper: Function<Any, Val>): Val =
        when (this) {
            is FeelValueWrapper.Null -> `ValNull$`.`MODULE$`
            is FeelValueWrapper.Text -> ValString(value)
            is FeelValueWrapper.Bool -> ValBoolean(value)
            is FeelValueWrapper.Decimal -> ValNumber(BigDecimal(value))
            is FeelValueWrapper.Array -> toVal(innerValueMapper)
            is FeelValueWrapper.Struct -> toVal(innerValueMapper)
        }
}
