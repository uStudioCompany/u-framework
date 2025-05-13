package io.github.ustudiocompany.uframework.json.element.merge.strategy.parser

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.map
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.json.element.merge.strategy.MergeStrategy
import io.github.ustudiocompany.uframework.json.element.merge.strategy.parser.model.PropertyModel
import io.github.ustudiocompany.uframework.json.element.merge.strategy.parser.model.RuleModel
import io.github.ustudiocompany.uframework.json.element.merge.strategy.parser.model.StrategyModel
import io.github.ustudiocompany.uframework.json.element.merge.strategy.path.AttributePath
import io.github.ustudiocompany.uframework.json.element.merge.strategy.role.MergeRule

public fun mergeStrategyParser(mapper: ObjectMapper): MergeStrategyParser = JacksonMergeStrategyParser(mapper)

private class JacksonMergeStrategyParser(mapper: ObjectMapper) : MergeStrategyParser {
    private val deserializer: MergeStrategyDeserializer = MergeStrategyDeserializer(mapper)

    override fun parse(input: String): ResultK<MergeStrategy, MergeStrategyParser.Errors> =
        deserializer.deserialize(input)
            .map { model -> model.toMergeStrategy() }
            .mapFailure { error -> MergeStrategyParser.Errors.Parsing(error) }

    private fun StrategyModel.toMergeStrategy(): MergeStrategy = properties.toMergeStrategy()

    private fun Map<String, PropertyModel>.toMergeStrategy(
        destination: MutableMap<AttributePath, MergeRule> = mutableMapOf(),
        path: AttributePath = AttributePath.None
    ): MergeStrategy {
        for ((name, property) in this) {
            val currentPath = path.append(name)

            val rule = property.rule
            if (rule != null)
                destination[currentPath] = when (rule) {
                    is RuleModel.MergeByAttribute -> MergeRule.MergeByAttributes(rule.attributes)
                    is RuleModel.WholeListMerge -> MergeRule.WholeListMerge
                }

            val properties = property.properties
            if (properties.isNotEmpty()) properties.toMergeStrategy(destination, currentPath)
        }
        return destination
    }
}
