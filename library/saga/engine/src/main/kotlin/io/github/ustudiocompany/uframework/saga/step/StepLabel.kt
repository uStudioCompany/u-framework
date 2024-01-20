package io.github.ustudiocompany.uframework.saga.step

@JvmInline
public value class StepLabel private constructor(public val get: String) {

    public companion object {

        public fun of(value: String): StepLabel {
            if (value.isBlank()) error("The step label is blank.")
            return StepLabel(value.lowercase())
        }
    }
}
