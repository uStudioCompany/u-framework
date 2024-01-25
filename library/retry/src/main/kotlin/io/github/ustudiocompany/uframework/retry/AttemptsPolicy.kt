package io.github.ustudiocompany.uframework.retry

public sealed class AttemptsPolicy {
    public abstract fun next(): Boolean

    private data object Unlimited : AttemptsPolicy() {
        override fun next(): Boolean = true
    }

    private class Limited(private var remainder: Int) : AttemptsPolicy() {
        override fun next(): Boolean {
            val hasNext: Boolean = remainder != 0
            if (hasNext)
                remainder = (remainder - 1).coerceAtLeast(0)
            return hasNext
        }
    }

    public companion object {
        public fun limited(count: Int): AttemptsPolicy = Limited(count)
        public fun unlimited(): AttemptsPolicy = Unlimited
    }
}
