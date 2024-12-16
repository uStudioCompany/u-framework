package io.github.ustudiocompany.uframework.saga.engine.state

@JvmInline
public value class SerializedData private constructor(public val get: String?) {

    public companion object {
        private val EMPTY = SerializedData(null)

        public fun empty(): SerializedData = EMPTY

        public operator fun invoke(value: String?): SerializedData = value?.let { SerializedData(it) } ?: EMPTY
    }
}
