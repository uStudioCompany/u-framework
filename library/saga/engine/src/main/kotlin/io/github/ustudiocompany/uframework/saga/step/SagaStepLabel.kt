package io.github.ustudiocompany.uframework.saga.step

import io.github.airflux.functional.Result
import io.github.airflux.functional.error
import io.github.airflux.functional.success
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
                Errors.IsBlank.error()
            else
                SagaStepLabel(value).success()
    }
}
