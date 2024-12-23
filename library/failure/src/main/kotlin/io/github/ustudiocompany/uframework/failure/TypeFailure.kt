package io.github.ustudiocompany.uframework.failure

public interface TypeFailure<T> : Failure {
    public val type: TypeOf<T>

    public companion object {
        public const val ACTUAL_VALUE_DETAIL_KEY: String = "actual-value"
        public const val PATTERN_DETAIL_KEY: String = "pattern"
    }
}
