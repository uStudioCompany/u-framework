package io.github.ustudiocompany.uframework.rulesengine.parser

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.andThen
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.airflux.commons.types.resultk.result
import io.github.airflux.commons.types.resultk.traverse
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.feel.FeelExpression
import io.github.ustudiocompany.uframework.rulesengine.core.path.Path
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Rule
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
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.DataScheme
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.Step
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.StepResult
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.Steps
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.Uri
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.ValidationStep
import io.github.ustudiocompany.uframework.rulesengine.feel.ExpressionParser
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.FeelExpressionModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.PathModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.RuleModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.RulesModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.SourceModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.ValueModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.condition.ConditionModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.condition.PredicateModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.operator.OperatorModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.step.ActionModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.step.ArgModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.step.ArgsModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.step.DataSchemeModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.step.ItemsModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.step.PropertiesModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.step.StepModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.step.StepsModel
import io.github.ustudiocompany.uframework.rulesengine.path.PathParser

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
            condition = condition?.convertCondition()?.bind(),
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
            value = value.convert().bind()
        )
    }

    private fun StepsModel.convertSteps(): ResultK<Steps, Errors.Conversion> =
        traverse { step -> step.convertStep() }
            .andThen { steps -> Steps(steps).asSuccess() }

    private fun StepModel.convertStep(): ResultK<Step, Errors.Conversion> = result {
        val step = this@convertStep
        when (step) {
            is StepModel.Validation -> ValidationStep(
                condition = step.condition?.convertCondition()?.bind(),
                target = step.target.convert().bind(),
                operator = step.operator.convertOperator().bind(),
                value = step.value.convert().bind(),
                errorCode = ValidationStep.ErrorCode(step.errorCode),
            )

            is StepModel.DataRetrieve -> DataRetrieveStep(
                condition = step.condition?.convertCondition()?.bind(),
                uri = Uri(step.uri),
                args = step.args.convertArgs().bind(),
                result = step.result.convert().bind()
            )

            is StepModel.DataBuild -> DataBuildStep(
                condition = step.condition?.convertCondition()?.bind(),
                dataScheme = step.dataScheme.convert().bind(),
                result = step.result.convert().bind()
            )
        }
    }

    private fun ArgsModel.convertArgs(): ResultK<Args, Errors.Conversion> =
        traverse { arg -> arg.convert() }
            .andThen { args -> Args(args).asSuccess() }

    private fun ArgModel.convert(): ResultK<Arg, Errors.Conversion> = result {
        Arg(
            name = name,
            value = value.convert().bind()
        )
    }

    private fun DataSchemeModel.convert(): ResultK<DataScheme, Errors.Conversion> = result {
        val scheme = this@convert
        when (scheme) {
            is DataSchemeModel.Struct -> DataScheme.Struct(
                properties = scheme.properties.convertStructProperties().bind()
            )

            is DataSchemeModel.Array -> DataScheme.Array(
                items = scheme.items.convertArrayItems().bind()
            )
        }
    }

    private fun PropertiesModel.convertStructProperties(): ResultK<List<DataScheme.Property>, Errors.Conversion> =
        traverse { property -> property.convertProperty() }

    private fun DataSchemeModel.Property.convertProperty(): ResultK<DataScheme.Property, Errors.Conversion> = result {
        val property = this@convertProperty
        when (property) {
            is DataSchemeModel.Property.Struct -> DataScheme.Property.Struct(
                name = property.name,
                properties = property.properties.convertStructProperties().bind()
            )

            is DataSchemeModel.Property.Array -> DataScheme.Property.Array(
                name = property.name,
                items = property.items.convertArrayItems().bind()
            )

            is DataSchemeModel.Property.Element -> DataScheme.Property.Element(
                name = property.name,
                value = property.value.convert().bind()
            )
        }
    }

    private fun ItemsModel.convertArrayItems(): ResultK<List<DataScheme.Item>, Errors.Conversion> =
        traverse { item -> item.convertItem() }

    private fun DataSchemeModel.Item.convertItem(): ResultK<DataScheme.Item, Errors.Conversion> = result {
        val item = this@convertItem
        when (item) {
            is DataSchemeModel.Item.Struct -> DataScheme.Item.Struct(
                properties = item.properties.convertStructProperties().bind()
            )

            is DataSchemeModel.Item.Array -> DataScheme.Item.Array(items = item.items.convertArrayItems().bind())
            is DataSchemeModel.Item.Element -> DataScheme.Item.Element(value = item.value.convert().bind())
        }
    }

    private fun StepModel.Result.convert(): ResultK<StepResult, Errors.Conversion> = result {
        StepResult(
            source = source.convertSource(),
            action = action.convertAction().bind()
        )
    }

    private fun ActionModel.convertAction(): ResultK<StepResult.Action, Errors.Conversion> =
        StepResult.Action.orNull(this)
            ?.asSuccess()
            ?: run {
                val expectedActions = StepResult.Action.entries
                    .joinToString(prefix = "[", separator = ", ", postfix = "]") { action -> action.key }
                Errors.Conversion("Invalid action type. Expected one of: $expectedActions, but was: '$this'")
                    .asFailure()
            }

    private fun OperatorModel.convertOperator(): ResultK<Operator<Boolean>, Errors.Conversion> {
        return BooleanOperators.orNull(this)
            ?.asSuccess()
            ?: run {
                val expectedActions = BooleanOperators.entries
                    .joinToString(prefix = "[", separator = ", ", postfix = "]") { action -> action.key }
                Errors.Conversion("Unrecognized operator. Expected one of: $expectedActions, but was: '$this'")
                    .asFailure()
            }
    }

    private fun ValueModel.convert(): ResultK<Value, Errors.Conversion> = result {
        val value = this@convert
        when (value) {
            is ValueModel.Literal -> Value.Literal(fact)

            is ValueModel.Reference -> Value.Reference(
                source = source.convertSource(),
                path = path.convertPath().bind(),
            )

            is ValueModel.Expression -> Value.Expression(
                expression = expression.convertExpression().bind()
            )
        }
    }

    private fun SourceModel.convertSource(): Source = Source(this@convertSource)

    private fun PathModel.convertPath(): ResultK<Path, Errors.Conversion> =
        pathParser.parse(this)
            .mapFailure { error -> Errors.Conversion(error) }

    private fun FeelExpressionModel.convertExpression(): ResultK<FeelExpression, Errors.Conversion> =
        expressionParser.parse(this)
            .mapFailure { error -> Errors.Conversion(error) }

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
