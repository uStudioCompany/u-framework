package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.fold
import io.github.airflux.commons.types.resultk.map
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.airflux.commons.types.resultk.traverseTo
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.ValueComputeErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.compute

internal fun DataSchema.build(context: Context): ResultK<DataElement, DataBuildErrors> =
    when (this) {
        is DataSchema.Struct -> properties.toStruct(context)
        is DataSchema.Array -> items.toArray(context)
    }

private fun DataSchema.Property.build(context: Context): ResultK<Pair<String, DataElement>, DataBuildErrors> =
    when (this) {
        is DataSchema.Property.Struct -> properties.toStruct(context).map { struct -> name to struct }
        is DataSchema.Property.Array -> items.toArray(context).map { array -> name to array }
        is DataSchema.Property.Element -> value.compute(context)
            .fold(
                onSuccess = { value -> (name to value).asSuccess() },
                onFailure = { failure -> DataBuildErrors.BuildingStructProperty(cause = failure).asFailure() }
            )
    }

private fun DataSchema.Item.build(context: Context): ResultK<DataElement, DataBuildErrors> =
    when (this) {
        is DataSchema.Item.Struct -> properties.toStruct(context)
        is DataSchema.Item.Array -> items.toArray(context)
        is DataSchema.Item.Element -> value.compute(context)
            .mapFailure { failure -> DataBuildErrors.BuildingArrayItem(cause = failure) }
    }

private fun List<DataSchema.Property>.toStruct(context: Context): ResultK<DataElement.Struct, DataBuildErrors> =
    this.traverseTo(
        destination = mutableMapOf<String, DataElement>(),
        transform = { property -> property.build(context) }
    ).map { DataElement.Struct(it) }

private fun List<DataSchema.Item>.toArray(context: Context): ResultK<DataElement.Array, DataBuildErrors> =
    this.traverseTo(
        destination = mutableListOf<DataElement>(),
        transform = { item -> item.build(context) }
    ).map { DataElement.Array(it) }

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
