package io.github.ustudiocompany.uframework.saga.core.step

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.failure.TypeFailure
import io.github.ustudiocompany.uframework.failure.TypeOf
import io.github.ustudiocompany.uframework.failure.typeOf

@JvmInline
public value class SagaStepLabel private constructor(public val get: String) {

    public sealed class Errors : TypeFailure<SagaStepLabel> {
        override val type: TypeOf<SagaStepLabel>
            get() = typeOf<SagaStepLabel>()

        public data object IsBlank : Errors() {
            override val code: String = type.name + "1"
            override val description: String = "The step label is blank."
        }
    }

    public companion object {

        public fun of(value: String): ResultK<SagaStepLabel, Errors> =
            if (value.isBlank())
                Errors.IsBlank.asFailure()
            else
                SagaStepLabel(value).asSuccess()
    }
}
