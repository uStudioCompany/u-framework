package io.github.ustudiocompany.uframework.rulesengine.parser

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.andThen
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.airflux.commons.types.resultk.result
import io.github.airflux.commons.types.resultk.traverse
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.json.path.Path
import io.github.ustudiocompany.uframework.json.path.PathParser
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVarName
import io.github.ustudiocompany.uframework.rulesengine.core.feel.FeelExpression
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Rule
import io.github.ustudiocompany.uframework.rulesengine.core.rule.RuleId
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Rules
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.Condition
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.Predicate
import io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.operator.BooleanOperators
import io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.operator.Operator
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.Arg
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.Args
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.DataBuildStep
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.DataRetrieveStep
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.DataSchema
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.MessageHeader
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.MessageHeaders
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.MessagePublishStep
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.Step
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.StepId
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.StepResult
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.Steps
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.Uri
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.ValidationStep
import io.github.ustudiocompany.uframework.rulesengine.feel.ExpressionParser
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.EnvVarNameModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.FeelExpressionModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.PathModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.RuleModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.RulesModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.SourceModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.ValueModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.condition.ConditionModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.condition.PredicateModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.operator.OperatorModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.step.ArgModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.step.ArgsModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.step.ArrayItemsModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.step.DataSchemaModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.step.MessageBodyModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.step.MessageHeaderModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.step.MessageHeadersModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.step.MessageRouteKeyModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.step.ResultModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.step.StepModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.step.StepsModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.step.StructPropertiesModel

@Suppress("TooManyFunctions")
internal class Converter(
    private val expressionParser: ExpressionParser,
    private val pathParser: PathParser
) {
    fun convert(model: RulesModel): ResultK<Rules, Errors.Conversion> =
        model.traverse { rule -> rule.convert() }
            .andThen { rules -> Rules(rules).asSuccess() }

    private fun RuleModel.convert(): ResultK<Rule, Errors.Conversion> = result {
        Rule(
            id = RuleId(id),
            condition = condition.convertCondition().bind(),
            steps = steps.convertSteps().bind()
        )
    }

    private fun ConditionModel.convertCondition(): ResultK<Condition, Errors.Conversion> =
        traverse { predicate -> predicate.convert() }
            .andThen { predicates -> Condition(predicates).asSuccess() }

    private fun PredicateModel.convert(): ResultK<Predicate, Errors.Conversion> = result {
        Predicate(
            target = target.convert().bind(),
            operator = operator.convertOperator().bind(),
            value = value?.convert()?.bind()
        )
    }

    private fun StepsModel.convertSteps(): ResultK<Steps, Errors.Conversion> =
        traverse { step -> step.convertStep() }
            .andThen { steps -> Steps(steps).asSuccess() }

    private fun StepModel.convertStep(): ResultK<Step, Errors.Conversion> = result {
        val step = this@convertStep
        when (step) {
            is StepModel.Validation -> ValidationStep(
                id = StepId(step.id),
                condition = step.condition.convertCondition().bind(),
                target = step.target.convert().bind(),
                operator = step.operator.convertOperator().bind(),
                value = step.value?.convert()?.bind(),
                errorCode = ValidationStep.ErrorCode(step.errorCode),
            )

            is StepModel.DataRetrieve -> DataRetrieveStep(
                id = StepId(step.id),
                condition = step.condition.convertCondition().bind(),
                uri = Uri(step.uri),
                args = step.args.convertArgs().bind(),
                result = step.result.convert().bind()
            )

            is StepModel.DataBuild -> DataBuildStep(
                id = StepId(step.id),
                condition = step.condition.convertCondition().bind(),
                dataSchema = step.dataSchema.convert().bind(),
                result = step.result.convert().bind()
            )

            is StepModel.MessagePublish -> MessagePublishStep(
                id = StepId(step.id),
                condition = step.condition.convertCondition().bind(),
                routeKey = step.routeKey?.convertMessageRouteKey()?.bind(),
                headers = step.headers.convertMessageHeaders().bind(),
                body = step.body?.convertMessageBody()?.bind()
            )
        }
    }

    private fun MessageRouteKeyModel.convertMessageRouteKey(): ResultK<Value, Errors.Conversion> = this.convert()

    private fun MessageHeadersModel.convertMessageHeaders(): ResultK<MessageHeaders, Errors.Conversion> =
        traverse { arg -> arg.convertMessageHeader() }
            .andThen { headers -> MessageHeaders(headers).asSuccess() }

    private fun MessageHeaderModel.convertMessageHeader(): ResultK<MessageHeader, Errors.Conversion> = result {
        MessageHeader(
            name = name,
            value = value.convert().bind()
        )
    }

    private fun MessageBodyModel.convertMessageBody(): ResultK<Value, Errors.Conversion> = this.convert()

    private fun ArgsModel.convertArgs(): ResultK<Args, Errors.Conversion> =
        traverse { arg -> arg.convert() }
            .andThen { args -> Args(args).asSuccess() }

    private fun ArgModel.convert(): ResultK<Arg, Errors.Conversion> = result {
        Arg(
            name = name,
            value = value.convert().bind()
        )
    }

    private fun DataSchemaModel.convert(): ResultK<DataSchema, Errors.Conversion> = result {
        val schema = this@convert
        when (schema) {
            is DataSchemaModel.Struct ->
                DataSchema.Struct(properties = schema.properties.convertStructProperties().bind())

            is DataSchemaModel.Array ->
                DataSchema.Array(items = schema.items.convertArrayItems().bind())
        }
    }

    private fun StructPropertiesModel.convertStructProperties(): ResultK<List<DataSchema.Property>, Errors.Conversion> =
        traverse { property -> property.convertProperty() }

    private fun DataSchemaModel.StructProperty.convertProperty(): ResultK<DataSchema.Property, Errors.Conversion> =
        result {
            val property = this@convertProperty
            when (property) {
                is DataSchemaModel.StructProperty.Struct -> DataSchema.Property.Struct(
                    name = property.name,
                    properties = property.properties.convertStructProperties().bind()
                )

                is DataSchemaModel.StructProperty.Array -> DataSchema.Property.Array(
                    name = property.name,
                    items = property.items.convertArrayItems().bind()
                )

                is DataSchemaModel.StructProperty.Element -> DataSchema.Property.Element(
                    name = property.name,
                    value = property.value.convert().bind()
                )
            }
        }

    private fun ArrayItemsModel.convertArrayItems(): ResultK<List<DataSchema.Item>, Errors.Conversion> =
        traverse { item -> item.convertItem() }

    private fun DataSchemaModel.ArrayItem.convertItem(): ResultK<DataSchema.Item, Errors.Conversion> = result {
        val item = this@convertItem
        when (item) {
            is DataSchemaModel.ArrayItem.Struct ->
                DataSchema.Item.Struct(properties = item.properties.convertStructProperties().bind())

            is DataSchemaModel.ArrayItem.Array -> DataSchema.Item.Array(items = item.items.convertArrayItems().bind())

            is DataSchemaModel.ArrayItem.Element -> DataSchema.Item.Element(value = item.value.convert().bind())
        }
    }

    private fun ResultModel.convert(): ResultK<StepResult, Errors.Conversion> = result {
        val result = this@convert
        when (result) {
            is ResultModel.Put -> {
                val source = result.source.convertSource()
                val action = StepResult.Action.Put
                StepResult(source = source, action = action)
            }

            is ResultModel.Replace -> {
                val source = result.source.convertSource()
                val action = StepResult.Action.Replace
                StepResult(source = source, action = action)
            }

            is ResultModel.Merge -> {
                val source = result.source.convertSource()
                val action = StepResult.Action.Merge(
                    strategyCode = StepResult.Action.Merge.StrategyCode(result.mergeStrategyCode)
                )
                StepResult(source = source, action = action)
            }
        }
    }

    private fun OperatorModel.convertOperator(): ResultK<Operator<Boolean>, Errors.Conversion> =
        BooleanOperators.orNull(this)
            ?.asSuccess()
            ?: run {
                val expectedActions = BooleanOperators.entries
                    .joinToString(prefix = "[", separator = ", ", postfix = "]") { action -> action.key }
                Errors.Conversion("Unrecognized operator. Expected one of: $expectedActions, but was: '$this'")
                    .asFailure()
            }

    private fun ValueModel.convert(): ResultK<Value, Errors.Conversion> = result {
        val value = this@convert
        when (value) {
            is ValueModel.Literal -> Value.Literal(fact = fact.get)

            is ValueModel.Reference -> Value.Reference(
                source = source.convertSource(),
                path = path.convertPath().bind()
            )

            is ValueModel.Expression -> Value.Expression(expression = expression.convertExpression().bind())

            is ValueModel.EnvVars -> Value.EnvVars(name = name.convertEnvVarName())
        }
    }

    private fun SourceModel.convertSource(): Source = Source(this@convertSource)

    private fun PathModel.convertPath(): ResultK<Path, Errors.Conversion> =
        pathParser.parse(this)
            .mapFailure { error -> Errors.Conversion(error) }

    private fun FeelExpressionModel.convertExpression(): ResultK<FeelExpression, Errors.Conversion> =
        expressionParser.parse(this)
            .mapFailure { error -> Errors.Conversion(error) }

    private fun EnvVarNameModel.convertEnvVarName(): EnvVarName = EnvVarName(this)

    sealed class Errors : Failure {

        class Conversion private constructor(message: String? = null, override val cause: Failure.Cause) : Errors() {

            constructor(cause: Failure) : this(cause = Failure.Cause.Failure(cause))
            constructor(message: String) : this(message = message, cause = Failure.Cause.None)

            override val code: String = PREFIX + "1"
            override val description: String =
                "The error of conversion rules." + if (message != null) " $message" else ""
        }

        private companion object {
            private const val PREFIX = "RULES-CONVERTER-"
        }
    }
}
