package io.github.ustudiocompany.uframework.saga

@JvmInline
public value class SagaLabel private constructor(public val get: String) {

    public companion object {
        public fun of(value: String): SagaLabel {
            if (value.isBlank()) error("The saga label is blank.")
            return SagaLabel(value.lowercase())
        }
    }
}
