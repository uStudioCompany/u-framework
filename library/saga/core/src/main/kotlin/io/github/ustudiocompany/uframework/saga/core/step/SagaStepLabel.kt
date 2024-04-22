package io.github.ustudiocompany.uframework.saga.core.step

import io.github.airflux.commons.types.result.Result
import io.github.airflux.commons.types.result.failure
import io.github.airflux.commons.types.result.success
import io.github.ustudiocompany.uframework.failure.TypeFailure
import io.github.ustudiocompany.uframework.failure.TypeOf
import io.github.ustudiocompany.uframework.failure.typeOf

@JvmInline
public value class SagaStepLabel private constructor(public val get: String) {

    public sealed class Errors : TypeFailure<SagaStepLabel> {
        override val type: TypeOf<SagaStepLabel>
            get() = typeOf<SagaStepLabel>()

        public data object IsBlank : Errors() {
            override val number: String = "1"
            override val description: String = "The step label is blank."
        }
    }

    public companion object {

        public fun of(value: String): Result<SagaStepLabel, Errors> =
            if (value.isBlank())
                Errors.IsBlank.failure()
            else
                SagaStepLabel(value).success()
    }
}
