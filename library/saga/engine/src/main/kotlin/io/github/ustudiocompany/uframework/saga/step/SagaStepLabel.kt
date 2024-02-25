package io.github.ustudiocompany.uframework.saga.step

@JvmInline
public value class SagaStepLabel private constructor(public val get: String) {

    public companion object {

        public fun of(value: String): SagaStepLabel {
            if (value.isBlank()) error("The step label is blank.")
            return SagaStepLabel(value.lowercase())
        }
    }
}
