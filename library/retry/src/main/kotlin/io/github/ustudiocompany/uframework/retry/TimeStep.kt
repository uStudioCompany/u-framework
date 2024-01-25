package io.github.ustudiocompany.uframework.retry

import java.time.Duration

public sealed class TimeStep {
    public abstract fun computeNext(currentDelayTime: Duration): Duration

    private class Factor(private val value: Double) : TimeStep() {
        override fun computeNext(currentDelayTime: Duration): Duration =
            Duration.ofMillis((currentDelayTime.toMillis() * value).toLong())
    }

    private class Linear(private val value: Duration) : TimeStep() {
        override fun computeNext(currentDelayTime: Duration): Duration = currentDelayTime.plus(value)
    }

    public companion object {
        public fun factor(value: Double): TimeStep = Factor(value)
        public fun linear(value: Duration): TimeStep = Linear(value)
    }
}
