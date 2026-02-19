package io.github.ustudiocompany.uframework.retry

import java.time.Duration

public sealed class TimeStep {
    public abstract fun computeNext(currentDelayTime: Duration): Duration

    private class Factor(private val value: Double, private val jitter: Jitter = Jitter.None) : TimeStep() {
        override fun computeNext(currentDelayTime: Duration): Duration =
            Duration.ofMillis((currentDelayTime.toMillis() * value).toLong()).plus(jitter.get(currentDelayTime))
    }

    private class Linear(private val value: Duration, private val jitter: Jitter = Jitter.None) : TimeStep() {
        override fun computeNext(currentDelayTime: Duration): Duration =
            currentDelayTime.plus(value)
                .plus(jitter.get(currentDelayTime))
    }

    public companion object {
        public fun factor(value: Double, jitter: Jitter = Jitter.None): TimeStep = Factor(value, jitter)
        public fun linear(value: Duration, jitter: Jitter = Jitter.None): TimeStep = Linear(value, jitter)
    }
}
