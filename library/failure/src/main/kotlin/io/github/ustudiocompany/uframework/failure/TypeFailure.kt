package io.github.ustudiocompany.uframework.failure

public interface TypeFailure<T> : Failure {
    public val type: TypeOf<T>

    override val domain: String
        get() = type.name

    override val kind: Failure.Kind
        get() = Failure.Kind.ERROR

    public companion object {
        public const val ACTUAL_VALUE_DETAIL_KEY: String = "actual-value"
        public const val PATTERN_DETAIL_KEY: String = "pattern"
    }
}
