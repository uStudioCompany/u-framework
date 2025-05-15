package io.github.ustudiocompany.uframework.json.element.merge.strategy.parser

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.json.element.merge.strategy.parser.model.StrategyModel

internal class MergeStrategyDeserializer(private val mapper: ObjectMapper) {

    fun deserialize(input: String): ResultK<StrategyModel, Errors> =
        try {
            mapper.readValue(input, StrategyModel::class.java).asSuccess()
        } catch (expected: Exception) {
            Errors.Deserialization(expected).asFailure()
        }

    sealed class Errors : Failure {

        class Deserialization(exception: Exception) : Errors() {
            override val code: String = PREFIX + "1"
            override val description: String =
                "The error of deserialization JSON with merge strategy. " + exception.message
            override val cause: Failure.Cause = Failure.Cause.Exception(exception)
        }

        private companion object {
            private const val PREFIX = "MERGE-STRATEGY-DESERIALIZER-"
        }
    }
}
