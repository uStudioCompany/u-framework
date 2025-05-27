package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.fold
import io.github.airflux.commons.types.resultk.getOrForward
import io.github.airflux.commons.types.resultk.map
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVars
import io.github.ustudiocompany.uframework.rulesengine.core.rule.ValueComputeErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.compute

internal fun DataSchema.build(envVars: EnvVars, context: Context): ResultK<JsonElement, DataBuildErrors> =
    when (this) {
        is DataSchema.Struct -> properties.toStruct(envVars, context)
        is DataSchema.Array -> items.toArray(envVars, context)
    }

private fun DataSchema.Property.build(
    envVars: EnvVars,
    context: Context
): ResultK<Pair<String, JsonElement>, DataBuildErrors> =
    when (this) {
        is DataSchema.Property.Struct -> properties.toStruct(envVars, context).map { struct -> name to struct }
        is DataSchema.Property.Array -> items.toArray(envVars, context).map { array -> name to array }
        is DataSchema.Property.Element -> value.compute(envVars, context)
            .fold(
                onSuccess = { value -> (name to value).asSuccess() },
                onFailure = { failure -> DataBuildErrors.BuildingStructProperty(cause = failure).asFailure() }
            )
    }

private fun DataSchema.Item.build(envVars: EnvVars, context: Context): ResultK<JsonElement, DataBuildErrors> =
    when (this) {
        is DataSchema.Item.Struct -> properties.toStruct(envVars, context)
        is DataSchema.Item.Array -> items.toArray(envVars, context)
        is DataSchema.Item.Element -> value.compute(envVars, context)
            .mapFailure { failure -> DataBuildErrors.BuildingArrayItem(cause = failure) }
    }

private fun List<DataSchema.Property>.toStruct(
    envVars: EnvVars,
    context: Context
): ResultK<JsonElement.Struct, DataBuildErrors> {
    val builder = JsonElement.Struct.Builder()
    this.forEach { spec ->
        val property = spec.build(envVars, context).getOrForward { return it }
        builder[property.first] = property.second
    }
    return builder.build().asSuccess()
}

private fun List<DataSchema.Item>.toArray(
    envVars: EnvVars,
    context: Context
): ResultK<JsonElement.Array, DataBuildErrors> {
    val builder = JsonElement.Array.Builder()
    this.forEach { spec ->
        val value = spec.build(envVars, context).getOrForward { return it }
        builder.add(value)
    }
    return builder.build().asSuccess()
}

internal sealed interface DataBuildErrors : BasicRulesEngineError {

    class BuildingStructProperty(cause: ValueComputeErrors) : DataBuildErrors {
        override val code: String = PREFIX + "1"
        override val description: String = "Error building struct property."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    class BuildingArrayItem(cause: ValueComputeErrors) : DataBuildErrors {
        override val code: String = PREFIX + "2"
        override val description: String = "Error building array item."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "DATA-BUILD-"
    }
}
